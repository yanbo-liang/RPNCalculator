package com.liangyanbo.airwallex.rpn.domain;

import com.liangyanbo.airwallex.rpn.RPNConfig;
import com.liangyanbo.airwallex.rpn.exception.RPNInputFormatException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Arrays;

@Getter
public class RPNOperator {
    private RPNOperation rpnOperation;
    private BigDecimal rpnNumber;

    public RPNOperator(RPNOperation rpnOperation) {
        this.rpnOperation = rpnOperation;
    }

    public RPNOperator(BigDecimal rpnNumber) {
        this.rpnNumber = rpnNumber;
    }

    public boolean isRPNOperation() {
        return rpnOperation != null;
    }

    public boolean isRPNNumber() {
        return rpnNumber != null;
    }

    public static RPNOperator create(String input) throws RPNInputFormatException {
        RPNOperation RPNOperation = inputToOperation(input);
        if (RPNOperation != null) {
            return new RPNOperator(RPNOperation);
        }

        BigDecimal number = inputToBigDecimal(input);
        if (number != null) {
            return new RPNOperator(number);
        }

        throw new RPNInputFormatException("invalid input: " + input);
    }

    static RPNOperation inputToOperation(String input) {
        return Arrays.stream(RPNOperation.values()).filter(x -> x.input.equals(input)).findFirst().orElse(null);
    }

    static BigDecimal inputToBigDecimal(String input) throws RPNInputFormatException {
        char[] chars = input.toCharArray();
        //only allow plain decimal strings
        for (char c : chars) {
            if (!((48 <= c && c <= 57) || c == 45 || c == 46)) {
                return null;
            }
        }
        try {
            return new BigDecimal(input);
        } catch (Exception e) {
            throw new RPNInputFormatException("invalid input: " + input, e);
        }
    }

    @Override
    public String toString() {
        if (rpnOperation != null) {
            return rpnOperation.input;
        } else if (rpnNumber != null) {
            return rpnNumber.setScale(RPNConfig.DISPLAY_SCALE, RPNConfig.ROUNDINGMODE).stripTrailingZeros().toPlainString();
        } else {
            return "null";
        }
    }
}
