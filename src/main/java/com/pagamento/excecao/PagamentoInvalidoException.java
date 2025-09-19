package com.pagamento.excecao;

public class PagamentoInvalidoException extends RuntimeException {
    
    public PagamentoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
