package com.liangyanbo.airwallex.rpn.domain;

public enum RPNOperation {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    SQRT("sqrt"),
    CLEAR("clear"),
    UNDO("undo");

    public String input;

    RPNOperation(String input) {
        this.input = input;
    }
}
