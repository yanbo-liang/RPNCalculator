package com.liangyanbo.airwallex.rpn;

import com.liangyanbo.airwallex.rpn.domain.RPNOperator;
import com.liangyanbo.airwallex.rpn.domain.RPNTransaction;
import com.liangyanbo.airwallex.rpn.exception.RPNArithmeticException;
import com.liangyanbo.airwallex.rpn.exception.RPNInputFormatException;
import com.liangyanbo.airwallex.rpn.exception.RPNInsufficientParamException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@Component
public class RPNCalculator implements ApplicationRunner {
    //should be bounded but no requirement
    final Deque<RPNOperator> rpnOperatorStack = new ArrayDeque<>();
    final Deque<RPNTransaction> operatorLog = new ArrayDeque<>();

    public void run(ApplicationArguments args) {
        log.info("Welcome to Yanbo's RPN Calculator");
        while (true) {
            try {
                String line = waitUserInput();
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                if ("q".equals(line) || "quit".equals(line)) {
                    break;
                }
                List<RPNOperator> RPNOperators = processInput(line);
                process(RPNOperators);
            } catch (Exception e) {
                log.info("something is wrong, please retry");
                log.error("unhandled exception", e);
            } finally {
                printStack();
            }
        }
    }

    String waitUserInput() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return null;
    }

    List<RPNOperator> processInput(String line) {
        String[] inputs = Arrays.stream(line.split(" ")).filter(StringUtils::isNotBlank).toArray(String[]::new);
        List<RPNOperator> RPNOperators = new ArrayList<>(inputs.length);
        for (String input : inputs) {
            try {
                RPNOperators.add(RPNOperator.create(input));
            } catch (RPNInputFormatException e) {
                log.info(e.getMessage());
                log.error(e.getMessage(), e);
                break;
            }
        }
        return RPNOperators;
    }

    void process(List<RPNOperator> RPNOperators) {
        for (int i = 0; i < RPNOperators.size(); i++) {
            RPNOperator RPNOperator = RPNOperators.get(i);

            try {
                if (RPNOperator.isRPNOperation()) {
                    switch (RPNOperator.getRpnOperation()) {
                        case ADD:
                            handleAdd(RPNOperator);
                            break;
                        case SUBTRACT:
                            handleSubtract(RPNOperator);
                            break;
                        case MULTIPLY:
                            handleMultiply(RPNOperator);
                            break;
                        case DIVIDE:
                            handleDivide(RPNOperator);
                            break;
                        case SQRT:
                            handleSqrt(RPNOperator);
                            break;
                        case CLEAR:
                            handleClear(RPNOperator);
                            break;
                        case UNDO:
                            handleUndo();
                            break;
                    }
                } else if (RPNOperator.isRPNNumber()) {
                    rpnOperatorStack.offerLast(RPNOperator);
                    operatorLog.offerLast(new RPNTransaction(RPNOperator));
                }
            } catch (RPNInsufficientParamException e) {
                log.info("operator {} (position: {}): {}", RPNOperator, i + 1, e.getMessage());
                return;
            } catch (RPNArithmeticException e) {
                log.info(e.getMessage());
            }
        }
    }

    void handleSingleOperator(RPNOperator operation, Function<RPNOperator, RPNOperator> function) throws RPNInsufficientParamException {
        if (rpnOperatorStack.peekLast() == null) {
            throw new RPNInsufficientParamException("insufficient parameters");
        }
        RPNOperator RPNOperator = rpnOperatorStack.pollLast();
        rpnOperatorStack.offerLast(function.apply(RPNOperator));
        operatorLog.offerLast(new RPNTransaction(Arrays.asList(RPNOperator, operation)));
    }

    void handleBiOperator(RPNOperator operation, BiFunction<RPNOperator, RPNOperator, RPNOperator> biFunction) throws RPNInsufficientParamException {
        RPNOperator first = rpnOperatorStack.pollLast();
        RPNOperator second;
        if (first == null) {
            throw new RPNInsufficientParamException("insufficient parameters");
        } else {
            second = rpnOperatorStack.pollLast();
            if (second == null) {
                rpnOperatorStack.offerLast(first);
                throw new RPNInsufficientParamException("insufficient parameters");
            }
        }
        rpnOperatorStack.offerLast(biFunction.apply(first, second));
        operatorLog.offerLast(new RPNTransaction(Arrays.asList(second, first, operation)));
    }

    void handleAdd(RPNOperator operation) throws RPNInsufficientParamException {
        handleBiOperator(operation, (first, second) -> new RPNOperator(second.getRpnNumber().add(first.getRpnNumber())));
    }

    void handleSubtract(RPNOperator operation) throws RPNInsufficientParamException {
        handleBiOperator(operation, (first, second) -> new RPNOperator(second.getRpnNumber().subtract(first.getRpnNumber())));
    }

    void handleMultiply(RPNOperator operation) throws RPNInsufficientParamException {
        handleBiOperator(operation, (first, second) -> new RPNOperator(second.getRpnNumber().multiply(first.getRpnNumber())));
    }

    void handleDivide(RPNOperator operation) throws RPNInsufficientParamException, RPNArithmeticException {
        RPNOperator rpnOperator = rpnOperatorStack.peekLast();
        if (rpnOperator != null && rpnOperator.getRpnNumber().compareTo(BigDecimal.ZERO) == 0) {
            throw new RPNArithmeticException("can't divide by zero");
        }
        handleBiOperator(operation, (first, second) -> new RPNOperator(second.getRpnNumber().divide(first.getRpnNumber(), RPNConfig.SCALE, RPNConfig.ROUNDINGMODE)));
    }

    void handleSqrt(RPNOperator operation) throws RPNInsufficientParamException {
        handleSingleOperator(operation, RPNOperator -> new RPNOperator(RPNOperator.getRpnNumber().sqrt(new MathContext(RPNConfig.SCALE, RPNConfig.ROUNDINGMODE))));
    }

    void handleClear(RPNOperator operation) {
        if (rpnOperatorStack.size() == 0) {
            return;
        }
        ArrayList<RPNOperator> RPNOperators = new ArrayList<>(rpnOperatorStack);
        RPNOperators.add(operation);
        operatorLog.offerLast(new RPNTransaction(RPNOperators));
        rpnOperatorStack.clear();
    }

    void handleUndo() {
        RPNTransaction RPNTransaction = operatorLog.pollLast();
        if (RPNTransaction == null) {
            return;
        }
        if (RPNTransaction.isFromNumber()) {
            rpnOperatorStack.pollLast();
        } else if (RPNTransaction.isFromOperation()) {
            rpnOperatorStack.pollLast();
            for (RPNOperator logRPNOperator : RPNTransaction.getRpnOperators()) {
                if (!logRPNOperator.isRPNOperation()) {
                    rpnOperatorStack.offerLast(logRPNOperator);
                }
            }
        }
    }

    void printStack() {
        StringBuilder builder = new StringBuilder();
        builder.append("stack: ");
        for (RPNOperator RPNOperator : rpnOperatorStack) {
            builder.append(RPNOperator);
            builder.append(" ");
        }
        log.info(builder.toString());
    }
}
