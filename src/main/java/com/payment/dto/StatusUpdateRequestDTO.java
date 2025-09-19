package com.payment.dto;

import com.payment.enums.PaymentStatus;

import javax.validation.constraints.NotNull;

public class StatusUpdateRequestDTO {

    @NotNull(message = "Status é obrigatório")
    private PaymentStatus status;

    public StatusUpdateRequestDTO() {}

    public StatusUpdateRequestDTO(PaymentStatus status) {
        this.status = status;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}

