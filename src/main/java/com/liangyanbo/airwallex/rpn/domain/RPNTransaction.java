package com.liangyanbo.airwallex.rpn.domain;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class RPNTransaction {
    private boolean isFromOperation;
    private boolean isFromNumber;
    private final List<RPNOperator> rpnOperators;


    public RPNTransaction(List<RPNOperator> rpnOperators) {
        this.isFromOperation = true;
        this.rpnOperators = rpnOperators;
    }

    public RPNTransaction(RPNOperator RPNOperator) {
        this.isFromNumber = true;
        this.rpnOperators = Collections.singletonList(RPNOperator);
    }
}

