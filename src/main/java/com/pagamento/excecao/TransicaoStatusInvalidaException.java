package com.pagamento.excecao;

import com.pagamento.enums.StatusPagamento;

public class TransicaoStatusInvalidaException extends RuntimeException {
    
    public TransicaoStatusInvalidaException(String mensagem) {
        super(mensagem);
    }
    
    public TransicaoStatusInvalidaException(StatusPagamento de, StatusPagamento para) {
        super(String.format("Transição de status inválida: de '%s' para '%s'",
                          de.getDescricao(), para.getDescricao()));
    }
}
