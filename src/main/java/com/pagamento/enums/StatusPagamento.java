package com.pagamento.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusPagamento {
    PENDENTE_PROCESSAMENTO("Pendente de Processamento"),
    PROCESSADO_SUCESSO("Processado com Sucesso"),
    PROCESSADO_FALHA("Processado com Falha");

    private final String descricao;

    StatusPagamento(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    public static StatusPagamento fromDescricao(String descricao) {
        for (StatusPagamento status : StatusPagamento.values()) {
            if (status.descricao.equals(descricao)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de pagamento inválido: " + descricao);
    }
}
