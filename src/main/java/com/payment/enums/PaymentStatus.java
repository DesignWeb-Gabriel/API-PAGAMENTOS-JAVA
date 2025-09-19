package com.payment.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentStatus {
    PENDENTE_PROCESSAMENTO("Pendente de Processamento"),
    PROCESSADO_SUCESSO("Processado com Sucesso"),
    PROCESSADO_FALHA("Processado com Falha");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static PaymentStatus fromDescription(String description) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.description.equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de pagamento inválido: " + description);
    }
}

