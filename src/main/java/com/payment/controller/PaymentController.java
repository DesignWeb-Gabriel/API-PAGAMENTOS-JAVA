package com.payment.controller;

import com.payment.dto.PaymentRequestDTO;
import com.payment.dto.PaymentResponseDTO;
import com.payment.dto.StatusUpdateRequestDTO;
import com.payment.enums.PaymentStatus;
import com.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "API para gerenciamento de pagamentos")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Criar novo pagamento", description = "Cria um novo pagamento com status 'Pendente de Processamento'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pagamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PaymentResponseDTO> createPayment(
            @Valid @RequestBody PaymentRequestDTO request) {
        
        PaymentResponseDTO response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os pagamentos", description = "Lista todos os pagamentos ativos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagamentos recuperada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<PaymentResponseDTO>> listAllPayments() {
        List<PaymentResponseDTO> payments = paymentService.listAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar pagamentos com filtros", description = "Busca pagamentos aplicando filtros opcionais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<PaymentResponseDTO>> searchPayments(
            @Parameter(description = "Código do débito") @RequestParam(required = false) Integer codigoDebito,
            @Parameter(description = "CPF ou CNPJ do pagador") @RequestParam(required = false) String cpfCnpj,
            @Parameter(description = "Status do pagamento") @RequestParam(required = false) PaymentStatus status) {
        
        List<PaymentResponseDTO> payments = paymentService.searchPayments(codigoDebito, cpfCnpj, status);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID", description = "Busca um pagamento específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PaymentResponseDTO> getPaymentById(
            @Parameter(description = "ID do pagamento", required = true) @PathVariable Long id) {
        
        PaymentResponseDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pagamento", description = "Atualiza o status de um pagamento seguindo as regras de transição")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @Parameter(description = "ID do pagamento", required = true) @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequestDTO request) {
        
        PaymentResponseDTO updatedPayment = paymentService.updatePaymentStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pagamento logicamente", description = "Exclui logicamente um pagamento (apenas se estiver pendente)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pagamento excluído com sucesso"),
        @ApiResponse(responseCode = "400", description = "Não é possível excluir o pagamento"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "ID do pagamento", required = true) @PathVariable Long id) {
        
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}

