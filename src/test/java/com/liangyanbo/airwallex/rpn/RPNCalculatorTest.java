package com.liangyanbo.airwallex.rpn;

import com.liangyanbo.airwallex.rpn.domain.RPNOperation;
import com.liangyanbo.airwallex.rpn.domain.RPNOperator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

public class RPNCalculatorTest {

    RPNCalculator rpnCalculator;

    @Before
    public void setup() {
        rpnCalculator = Mockito.spy(RPNCalculator.class);
    }

    @Test
    public void processInputTest() {
        String line = "   3  4   5.1   6   + 7 - 8 * 9 ";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        Assert.assertEquals(rpnOperators.get(0).getRpnNumber(), new BigDecimal("3"));
        Assert.assertEquals(rpnOperators.get(1).getRpnNumber(), new BigDecimal("4"));
        Assert.assertEquals(rpnOperators.get(2).getRpnNumber(), new BigDecimal("5.1"));
        Assert.assertEquals(rpnOperators.get(3).getRpnNumber(), new BigDecimal("6"));
        Assert.assertEquals(rpnOperators.get(4).getRpnOperation(), RPNOperation.ADD);
        Assert.assertEquals(rpnOperators.get(5).getRpnNumber(), new BigDecimal("7"));
        Assert.assertEquals(rpnOperators.get(6).getRpnOperation(), RPNOperation.SUBTRACT);
        Assert.assertEquals(rpnOperators.get(7).getRpnNumber(), new BigDecimal("8"));
        Assert.assertEquals(rpnOperators.get(8).getRpnOperation(), RPNOperation.MULTIPLY);
        Assert.assertEquals(rpnOperators.get(9).getRpnNumber(), new BigDecimal("9"));
    }

    @Test
    public void processInputFailedTest() {
        String line = "1 2 a 3";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        Assert.assertEquals(rpnOperators.size(), 2);
        Assert.assertEquals(rpnOperators.get(0).getRpnNumber(), new BigDecimal("1"));
        Assert.assertEquals(rpnOperators.get(1).getRpnNumber(), new BigDecimal("2"));
    }

    @Test
    public void handleAddTest() {
        String line = "2 2.1 +";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
        Assert.assertEquals(rpnCalculator.rpnOperatorStack.getLast().getRpnNumber(), new BigDecimal("4.1"));
    }

    @Test
    public void handleSubtract() {
        String line = "2 2.1 -";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
        Assert.assertEquals(rpnCalculator.rpnOperatorStack.getLast().getRpnNumber(), new BigDecimal("-0.1"));
    }

    @Test
    public void handleMultiply() {
        String line = "2 2.1 *";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
        Assert.assertEquals(rpnCalculator.rpnOperatorStack.getLast().getRpnNumber(), new BigDecimal("4.2"));
    }

    @Test
    public void handleDivide() {
        String line = "2.2 2 /";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
        Assert.assertEquals(0, rpnCalculator.rpnOperatorStack.getLast().getRpnNumber().compareTo(new BigDecimal("1.1")));
    }

    @Test
    public void handleDivideZero() {
        String line = "2 0 /";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
    }

    @Test
    public void handleSqrt() {
        String line = "4 sqrt";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
        Assert.assertEquals(rpnCalculator.rpnOperatorStack.getLast().getRpnNumber(), new BigDecimal("2"));
    }

    @Test
    public void handleClear() {
        String line = "1 2 3 clear";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
        Assert.assertEquals(rpnCalculator.rpnOperatorStack.size(), 0);
    }

    @Test
    public void handleUndo() {
        String line = "1 2 3 + + undo undo";
        List<RPNOperator> rpnOperators = rpnCalculator.processInput(line);
        rpnCalculator.process(rpnOperators);
        Assert.assertEquals(rpnCalculator.rpnOperatorStack.getLast().getRpnNumber(), new BigDecimal("3"));
    }
}
