package com.payment.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentMethod {
    BOLETO("boleto"),
    PIX("pix"),
    CARTAO_CREDITO("cartao_credito"),
    CARTAO_DEBITO("cartao_debito");

    private final String value;

    PaymentMethod(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Método de pagamento inválido: " + value);
    }

    public boolean isCardPayment() {
        return this == CARTAO_CREDITO || this == CARTAO_DEBITO;
    }
}

