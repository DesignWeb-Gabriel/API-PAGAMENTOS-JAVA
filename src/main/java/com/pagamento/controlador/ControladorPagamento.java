package com.pagamento.controlador;

import com.pagamento.dto.PagamentoRequestDTO;
import com.pagamento.dto.PagamentoResponseDTO;
import com.pagamento.dto.AtualizacaoStatusRequestDTO;
import com.pagamento.enums.StatusPagamento;
import com.pagamento.servico.ServicoPagamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "API para gerenciamento de pagamentos")
public class ControladorPagamento {

    private final ServicoPagamento servicoPagamento;

    public ControladorPagamento(ServicoPagamento servicoPagamento) {
        this.servicoPagamento = servicoPagamento;
    }

    @PostMapping
    @Operation(summary = "Criar novo pagamento", description = "Cria um novo pagamento com status 'Pendente de Processamento'")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pagamento criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PagamentoResponseDTO> criarPagamento(
            @Valid @RequestBody PagamentoRequestDTO request) {
        
        PagamentoResponseDTO response = servicoPagamento.criarPagamento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os pagamentos", description = "Lista todos os pagamentos ativos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de pagamentos recuperada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<PagamentoResponseDTO>> listarTodosPagamentos() {
        List<PagamentoResponseDTO> pagamentos = servicoPagamento.listarTodosPagamentos();
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar pagamentos com filtros", description = "Busca pagamentos aplicando filtros opcionais")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<PagamentoResponseDTO>> buscarPagamentos(
            @Parameter(description = "Código do débito") @RequestParam(required = false) Integer codigoDebito,
            @Parameter(description = "CPF ou CNPJ do pagador") @RequestParam(required = false) String cpfCnpj,
            @Parameter(description = "Status do pagamento") @RequestParam(required = false) StatusPagamento status) {
        
        List<PagamentoResponseDTO> pagamentos = servicoPagamento.buscarPagamentos(codigoDebito, cpfCnpj, status);
        return ResponseEntity.ok(pagamentos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pagamento por ID", description = "Busca um pagamento específico pelo seu ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PagamentoResponseDTO> obterPagamentoPorId(
            @Parameter(description = "ID do pagamento", required = true) @PathVariable Long id) {
        
        PagamentoResponseDTO pagamento = servicoPagamento.obterPagamentoPorId(id);
        return ResponseEntity.ok(pagamento);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Atualizar status do pagamento", description = "Atualiza o status de um pagamento seguindo as regras de transição")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Transição de status inválida"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<PagamentoResponseDTO> atualizarStatusPagamento(
            @Parameter(description = "ID do pagamento", required = true) @PathVariable Long id,
            @Valid @RequestBody AtualizacaoStatusRequestDTO request) {
        
        PagamentoResponseDTO pagamentoAtualizado = servicoPagamento.atualizarStatusPagamento(id, request.getStatus());
        return ResponseEntity.ok(pagamentoAtualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir pagamento logicamente", description = "Exclui logicamente um pagamento (apenas se estiver pendente)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Pagamento excluído com sucesso"),
        @ApiResponse(responseCode = "400", description = "Não é possível excluir o pagamento"),
        @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> excluirPagamento(
            @Parameter(description = "ID do pagamento", required = true) @PathVariable Long id) {
        
        servicoPagamento.excluirPagamento(id);
        return ResponseEntity.noContent().build();
    }
}
