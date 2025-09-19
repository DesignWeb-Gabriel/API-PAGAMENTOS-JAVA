package com.pagamento.dto;

import com.pagamento.enums.StatusPagamento;

import javax.validation.constraints.NotNull;

public class AtualizacaoStatusRequestDTO {

    @NotNull(message = "Status é obrigatório")
    private StatusPagamento status;

    public AtualizacaoStatusRequestDTO() {}

    public AtualizacaoStatusRequestDTO(StatusPagamento status) {
        this.status = status;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }
}
