package com.liangyanbo.airwallex.rpn.domain;

import com.liangyanbo.airwallex.rpn.exception.RPNInputFormatException;
import com.liangyanbo.airwallex.rpn.domain.RPNOperation;
import com.liangyanbo.airwallex.rpn.domain.RPNOperator;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class RPNOperatorTest {

    @Test
    public void createOperationTest() throws RPNInputFormatException {
        RPNOperator rpnOperator = RPNOperator.create("+");
        Assert.assertTrue(rpnOperator.isRPNOperation());
        Assert.assertEquals(rpnOperator.getRpnOperation(), RPNOperation.ADD);

        rpnOperator = RPNOperator.create("-");
        Assert.assertTrue(rpnOperator.isRPNOperation());
        Assert.assertEquals(rpnOperator.getRpnOperation(), RPNOperation.SUBTRACT);

        rpnOperator = RPNOperator.create("*");
        Assert.assertTrue(rpnOperator.isRPNOperation());
        Assert.assertEquals(rpnOperator.getRpnOperation(), RPNOperation.MULTIPLY);

        rpnOperator = RPNOperator.create("/");
        Assert.assertTrue(rpnOperator.isRPNOperation());
        Assert.assertEquals(rpnOperator.getRpnOperation(), RPNOperation.DIVIDE);

        rpnOperator = RPNOperator.create("sqrt");
        Assert.assertTrue(rpnOperator.isRPNOperation());
        Assert.assertEquals(rpnOperator.getRpnOperation(), RPNOperation.SQRT);

        rpnOperator = RPNOperator.create("clear");
        Assert.assertTrue(rpnOperator.isRPNOperation());
        Assert.assertEquals(rpnOperator.getRpnOperation(), RPNOperation.CLEAR);

        rpnOperator = RPNOperator.create("undo");
        Assert.assertTrue(rpnOperator.isRPNOperation());
        Assert.assertEquals(rpnOperator.getRpnOperation(), RPNOperation.UNDO);
    }

    @Test
    public void createNumberSuccessTest() throws RPNInputFormatException {
        RPNOperator rpnOperator = RPNOperator.create("1");
        Assert.assertTrue(rpnOperator.isRPNNumber());

        rpnOperator = RPNOperator.create("1.");
        Assert.assertTrue(rpnOperator.isRPNNumber());

        rpnOperator = RPNOperator.create("1.0");
        Assert.assertTrue(rpnOperator.isRPNNumber());

        rpnOperator = RPNOperator.create("-1.23");
        Assert.assertTrue(rpnOperator.isRPNNumber());

        rpnOperator = RPNOperator.create("1.23");
        Assert.assertTrue(rpnOperator.isRPNNumber());
    }

    @Test(expected = RPNInputFormatException.class)
    public void createNumberFailTest1() throws RPNInputFormatException {
        RPNOperator.create("abc");
    }

    @Test(expected = RPNInputFormatException.class)
    public void createNumberFailTest2() throws RPNInputFormatException {
        new BigDecimal("12.3E3");
        RPNOperator.create("12.3E3");
    }

    @Test(expected = RPNInputFormatException.class)
    public void createNumberFailTest3() throws RPNInputFormatException {
        RPNOperator.create("a+1");
    }
}
