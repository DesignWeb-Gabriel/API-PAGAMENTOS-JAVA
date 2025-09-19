package com.payment.service;

import com.payment.dto.PaymentRequestDTO;
import com.payment.dto.PaymentResponseDTO;
import com.payment.entity.Payment;
import com.payment.enums.PaymentStatus;
import com.payment.exception.InvalidPaymentException;
import com.payment.exception.InvalidStatusTransitionException;
import com.payment.exception.PaymentNotFoundException;
import com.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * Cria um novo pagamento
     */
    public PaymentResponseDTO createPayment(PaymentRequestDTO request) {
        validatePaymentRequest(request);
        
        Payment payment = new Payment(
            request.getCodigoDebito(),
            request.getCpfCnpj(),
            request.getMetodoPagamento(),
            request.getNumeroCartao(),
            request.getValorPagamento()
        );

        Payment savedPayment = paymentRepository.save(payment);
        return new PaymentResponseDTO(savedPayment);
    }

    /**
     * Lista todos os pagamentos ativos
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> listAllPayments() {
        return paymentRepository.findByAtivoTrue()
                .stream()
                .map(PaymentResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca pagamentos com filtros
     */
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> searchPayments(Integer codigoDebito, String cpfCnpj, PaymentStatus status) {
        return paymentRepository.findByFilters(codigoDebito, cpfCnpj, status)
                .stream()
                .map(PaymentResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Busca pagamento por ID
     */
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return new PaymentResponseDTO(payment);
    }

    /**
     * Atualiza o status de um pagamento
     */
    public PaymentResponseDTO updatePaymentStatus(Long id, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        validateStatusTransition(payment.getStatus(), newStatus);
        
        payment.setStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);
        
        return new PaymentResponseDTO(updatedPayment);
    }

    /**
     * Exclui logicamente um pagamento
     */
    public void deletePayment(Long id) {
        Payment payment = paymentRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        // Só pode excluir se estiver pendente de processamento
        if (payment.getStatus() != PaymentStatus.PENDENTE_PROCESSAMENTO) {
            throw new InvalidPaymentException(
                "Só é possível excluir pagamentos com status 'Pendente de Processamento'"
            );
        }

        payment.setAtivo(false);
        paymentRepository.save(payment);
    }

    /**
     * Valida a requisição de pagamento
     */
    private void validatePaymentRequest(PaymentRequestDTO request) {
        // Valida se número do cartão é obrigatório para pagamentos com cartão
        if (request.getMetodoPagamento().isCardPayment()) {
            if (request.getNumeroCartao() == null || request.getNumeroCartao().trim().isEmpty()) {
                throw new InvalidPaymentException(
                    "Número do cartão é obrigatório para pagamentos com cartão de crédito ou débito"
                );
            }
        } else {
            // Para outros métodos, não deve ter número de cartão
            if (request.getNumeroCartao() != null && !request.getNumeroCartao().trim().isEmpty()) {
                throw new InvalidPaymentException(
                    "Número do cartão não deve ser informado para pagamentos que não sejam com cartão"
                );
            }
        }
    }

    /**
     * Valida transições de status conforme regras de negócio
     */
    private void validateStatusTransition(PaymentStatus currentStatus, PaymentStatus newStatus) {
        switch (currentStatus) {
            case PENDENTE_PROCESSAMENTO:
                if (newStatus != PaymentStatus.PROCESSADO_SUCESSO &&
                    newStatus != PaymentStatus.PROCESSADO_FALHA) {
                    throw new InvalidStatusTransitionException(currentStatus, newStatus);
                }
                break;
                
            case PROCESSADO_SUCESSO:
                // Não pode ser alterado
                throw new InvalidStatusTransitionException(
                    "Pagamentos com status 'Processado com Sucesso' não podem ser alterados"
                );
                
            case PROCESSADO_FALHA:
                if (newStatus != PaymentStatus.PENDENTE_PROCESSAMENTO) {
                    throw new InvalidStatusTransitionException(currentStatus, newStatus);
                }
                break;
                
            default:
                throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }
    }
}

