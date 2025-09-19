package com.payment.exception;

import com.payment.enums.PaymentStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    
    public InvalidStatusTransitionException(String message) {
        super(message);
    }
    
    public InvalidStatusTransitionException(PaymentStatus from, PaymentStatus to) {
        super(String.format("Transição de status inválida: de '%s' para '%s'",
                          from.getDescription(), to.getDescription()));
    }
}

