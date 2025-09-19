package com.payment.exception;

public class PaymentNotFoundException extends RuntimeException {
    
    public PaymentNotFoundException(String message) {
        super(message);
    }
    
    public PaymentNotFoundException(Long id) {
        super("Pagamento não encontrado com ID: " + id);
    }
}
