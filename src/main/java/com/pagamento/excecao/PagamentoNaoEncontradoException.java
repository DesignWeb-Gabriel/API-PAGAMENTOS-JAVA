package com.pagamento.excecao;

public class PagamentoNaoEncontradoException extends RuntimeException {
    
    public PagamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
    
    public PagamentoNaoEncontradoException(Long id) {
        super("Pagamento não encontrado com ID: " + id);
    }
}
