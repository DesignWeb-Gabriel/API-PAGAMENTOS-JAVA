package com.pagamento.servico;

import com.pagamento.dto.PagamentoRequestDTO;
import com.pagamento.dto.PagamentoResponseDTO;
import com.pagamento.entidade.Pagamento;
import com.pagamento.enums.StatusPagamento;
import com.pagamento.excecao.PagamentoInvalidoException;
import com.pagamento.excecao.PagamentoNaoEncontradoException;
import com.pagamento.excecao.TransicaoStatusInvalidaException;
import com.pagamento.repositorio.RepositorioPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ServicoPagamento {

    @Autowired
    private RepositorioPagamento repositorioPagamento;

    /**
     * Cria um novo pagamento
     */
    public PagamentoResponseDTO criarPagamento(PagamentoRequestDTO request) {
        validarRequisicaoPagamento(request);
        
        Pagamento pagamento = new Pagamento(
            request.getCodigoDebito(),
            request.getCpfCnpj(),
            request.getMetodoPagamento(),
            request.getNumeroCartao(),
            request.getValorPagamento()
        );

        Pagamento pagamentoSalvo = repositorioPagamento.save(pagamento);
        return new PagamentoResponseDTO(pagamentoSalvo);
    }

    /**
     * Lista todos os pagamentos ativos
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> listarTodosPagamentos() {
        return repositorioPagamento.findByAtivoTrue()
                .stream()
                .map(PagamentoResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca pagamentos com filtros
     */
    @Transactional(readOnly = true)
    public List<PagamentoResponseDTO> buscarPagamentos(Integer codigoDebito, String cpfCnpj, StatusPagamento status) {
        return repositorioPagamento.encontrarComFiltros(codigoDebito, cpfCnpj, status)
                .stream()
                .map(PagamentoResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca pagamento por ID
     */
    @Transactional(readOnly = true)
    public PagamentoResponseDTO obterPagamentoPorId(Long id) {
        Pagamento pagamento = repositorioPagamento.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new PagamentoNaoEncontradoException(id));
        return new PagamentoResponseDTO(pagamento);
    }

    /**
     * Atualiza o status de um pagamento
     */
    public PagamentoResponseDTO atualizarStatusPagamento(Long id, StatusPagamento novoStatus) {
        Pagamento pagamento = repositorioPagamento.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new PagamentoNaoEncontradoException(id));

        validarTransicaoStatus(pagamento.getStatus(), novoStatus);
        
        pagamento.setStatus(novoStatus);
        Pagamento pagamentoAtualizado = repositorioPagamento.save(pagamento);
        
        return new PagamentoResponseDTO(pagamentoAtualizado);
    }

    /**
     * Exclui logicamente um pagamento
     */
    public void excluirPagamento(Long id) {
        Pagamento pagamento = repositorioPagamento.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new PagamentoNaoEncontradoException(id));

        // Só pode excluir se estiver pendente de processamento
        if (pagamento.getStatus() != StatusPagamento.PENDENTE_PROCESSAMENTO) {
            throw new PagamentoInvalidoException(
                "Só é possível excluir pagamentos com status 'Pendente de Processamento'"
            );
        }

        pagamento.setAtivo(false);
        repositorioPagamento.save(pagamento);
    }

    /**
     * Valida a requisição de pagamento
     */
    private void validarRequisicaoPagamento(PagamentoRequestDTO request) {
        // Valida se número do cartão é obrigatório para pagamentos com cartão
        if (request.getMetodoPagamento().isPagamentoComCartao()) {
            if (request.getNumeroCartao() == null || request.getNumeroCartao().trim().isEmpty()) {
                throw new PagamentoInvalidoException(
                    "Número do cartão é obrigatório para pagamentos com cartão de crédito ou débito"
                );
            }
        } else {
            // Para outros métodos, não deve ter número de cartão
            if (request.getNumeroCartao() != null && !request.getNumeroCartao().trim().isEmpty()) {
                throw new PagamentoInvalidoException(
                    "Número do cartão não deve ser informado para pagamentos que não sejam com cartão"
                );
            }
        }
    }

    /**
     * Valida transições de status conforme regras de negócio
     */
    private void validarTransicaoStatus(StatusPagamento statusAtual, StatusPagamento novoStatus) {
        switch (statusAtual) {
            case PENDENTE_PROCESSAMENTO:
                if (novoStatus != StatusPagamento.PROCESSADO_SUCESSO &&
                    novoStatus != StatusPagamento.PROCESSADO_FALHA) {
                    throw new TransicaoStatusInvalidaException(statusAtual, novoStatus);
                }
                break;
                
            case PROCESSADO_SUCESSO:
                // Não pode ser alterado
                throw new TransicaoStatusInvalidaException(
                    "Pagamentos com status 'Processado com Sucesso' não podem ser alterados"
                );
                
            case PROCESSADO_FALHA:
                if (novoStatus != StatusPagamento.PENDENTE_PROCESSAMENTO) {
                    throw new TransicaoStatusInvalidaException(statusAtual, novoStatus);
                }
                break;
                
            default:
                throw new TransicaoStatusInvalidaException(statusAtual, novoStatus);
        }
    }
}
