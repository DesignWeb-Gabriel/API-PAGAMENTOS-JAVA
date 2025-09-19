package com.pagamento.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MetodoPagamento {
    BOLETO("boleto"),
    PIX("pix"),
    CARTAO_CREDITO("cartao_credito"),
    CARTAO_DEBITO("cartao_debito");

    private final String valor;

    MetodoPagamento(String valor) {
        this.valor = valor;
    }

    @JsonValue
    public String getValor() {
        return valor;
    }

    public static MetodoPagamento fromValor(String valor) {
        for (MetodoPagamento metodo : MetodoPagamento.values()) {
            if (metodo.valor.equals(valor)) {
                return metodo;
            }
        }
        throw new IllegalArgumentException("Método de pagamento inválido: " + valor);
    }

    public boolean isPagamentoComCartao() {
        return this == CARTAO_CREDITO || this == CARTAO_DEBITO;
    }
}
