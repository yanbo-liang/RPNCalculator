package com.liangyanbo.airwallex.rpn.exception;

public class RPNInsufficientParamException extends Exception {

    public RPNInsufficientParamException(String message) {
        super(message);
    }

    public RPNInsufficientParamException(String message, Throwable cause) {
        super(message, cause);
    }
}
