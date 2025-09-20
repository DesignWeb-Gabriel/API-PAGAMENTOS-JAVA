package com.pagamento.controlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagamento.dto.AtualizacaoStatusRequestDTO;
import com.pagamento.dto.PagamentoRequestDTO;
import com.pagamento.dto.PagamentoResponseDTO;
import com.pagamento.enums.MetodoPagamento;
import com.pagamento.enums.StatusPagamento;
import com.pagamento.excecao.PagamentoInvalidoException;
import com.pagamento.excecao.PagamentoNaoEncontradoException;
import com.pagamento.excecao.TransicaoStatusInvalidaException;
import com.pagamento.servico.ServicoPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ControladorPagamento.class)
@DisplayName("Testes do Controlador de Pagamento")
class ControladorPagamentoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicoPagamento servicoPagamento;

    @Autowired
    private ObjectMapper objectMapper;

    private PagamentoRequestDTO pagamentoRequestDTO;
    private PagamentoResponseDTO pagamentoResponseDTO;

    @BeforeEach
    void setUp() {
        pagamentoRequestDTO = new PagamentoRequestDTO();
        pagamentoRequestDTO.setCodigoDebito(12345);
        pagamentoRequestDTO.setCpfCnpj("12345678901");
        pagamentoRequestDTO.setMetodoPagamento(MetodoPagamento.PIX);
        pagamentoRequestDTO.setValorPagamento(new BigDecimal("100.50"));

        pagamentoResponseDTO = new PagamentoResponseDTO();
        pagamentoResponseDTO.setId(1L);
        pagamentoResponseDTO.setCodigoDebito(12345);
        pagamentoResponseDTO.setCpfCnpj("12345678901");
        pagamentoResponseDTO.setMetodoPagamento("pix");
        pagamentoResponseDTO.setValorPagamento(new BigDecimal("100.50"));
        pagamentoResponseDTO.setStatus("Pendente de Processamento");
        pagamentoResponseDTO.setDataCriacao(LocalDateTime.now());
        pagamentoResponseDTO.setDataAtualizacao(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Testes de Criação de Pagamento - POST /api/pagamentos")
    class CriacaoPagamentoTests {

        @Test
        @DisplayName("Deve criar pagamento com sucesso e retornar status 201")
        void deveCriarPagamentoComSucessoERetornarStatus201() throws Exception {
            
            when(servicoPagamento.criarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoResponseDTO);
            
            mockMvc.perform(post("/api/pagamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.codigoDebito").value(12345))
                    .andExpect(jsonPath("$.cpfCnpj").value("12345678901"))
                    .andExpect(jsonPath("$.metodoPagamento").value("pix"))
                    .andExpect(jsonPath("$.valorPagamento").value(100.50))
                    .andExpect(jsonPath("$.status").value("Pendente de Processamento"));

            verify(servicoPagamento).criarPagamento(any(PagamentoRequestDTO.class));
        }

        @Test
        @DisplayName("Deve retornar status 400 quando dados são inválidos")
        void deveRetornarStatus400QuandoDadosSaoInvalidos() throws Exception {
            
            when(servicoPagamento.criarPagamento(any(PagamentoRequestDTO.class)))
                .thenThrow(new PagamentoInvalidoException("Dados inválidos"));

            
            mockMvc.perform(post("/api/pagamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagem").value("Dados inválidos"));
        }

        @Test
        @DisplayName("Deve criar pagamento com cartão de crédito")
        void deveCriarPagamentoComCartaoCredito() throws Exception {
            
            pagamentoRequestDTO.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pagamentoRequestDTO.setNumeroCartao("1234567890123456");
            
            pagamentoResponseDTO.setMetodoPagamento("cartao_credito");
            pagamentoResponseDTO.setNumeroCartao("1234567890123456");
            
            when(servicoPagamento.criarPagamento(any(PagamentoRequestDTO.class)))
                .thenReturn(pagamentoResponseDTO);

            
            mockMvc.perform(post("/api/pagamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(pagamentoRequestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.metodoPagamento").value("cartao_credito"))
                    .andExpect(jsonPath("$.numeroCartao").value("1234567890123456"));
        }
    }

    @Nested
    @DisplayName("Testes de Listagem - GET /api/pagamentos")
    class ListagemPagamentosTests {

        @Test
        @DisplayName("Deve listar todos os pagamentos com sucesso")
        void deveListarTodosPagamentosComSucesso() throws Exception {
            
            PagamentoResponseDTO pagamento2 = new PagamentoResponseDTO();
            pagamento2.setId(2L);
            pagamento2.setCodigoDebito(54321);
            pagamento2.setCpfCnpj("98765432100");
            pagamento2.setMetodoPagamento("boleto");
            pagamento2.setValorPagamento(new BigDecimal("200.00"));
            pagamento2.setStatus("Pendente de Processamento");
            
            List<PagamentoResponseDTO> pagamentos = Arrays.asList(pagamentoResponseDTO, pagamento2);
            when(servicoPagamento.listarTodosPagamentos()).thenReturn(pagamentos);

            
            mockMvc.perform(get("/api/pagamentos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[1].id").value(2L))
                    .andExpect(jsonPath("$[0].metodoPagamento").value("pix"))
                    .andExpect(jsonPath("$[1].metodoPagamento").value("boleto"));
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há pagamentos")
        void deveRetornarListaVaziaQuandoNaoHaPagamentos() throws Exception {
            
            when(servicoPagamento.listarTodosPagamentos()).thenReturn(Arrays.asList());

            
            mockMvc.perform(get("/api/pagamentos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("Testes de Busca com Filtros - GET /api/pagamentos/buscar")
    class BuscaComFiltrosTests {

        @Test
        @DisplayName("Deve buscar pagamentos com filtros")
        void deveBuscarPagamentosComFiltros() throws Exception {
            
            List<PagamentoResponseDTO> pagamentos = Arrays.asList(pagamentoResponseDTO);
            when(servicoPagamento.buscarPagamentos(12345, "12345678901", StatusPagamento.PENDENTE_PROCESSAMENTO))
                .thenReturn(pagamentos);

            
            mockMvc.perform(get("/api/pagamentos/buscar")
                    .param("codigoDebito", "12345")
                    .param("cpfCnpj", "12345678901")
                    .param("status", "PENDENTE_PROCESSAMENTO"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1L));

            verify(servicoPagamento).buscarPagamentos(12345, "12345678901", StatusPagamento.PENDENTE_PROCESSAMENTO);
        }

        @Test
        @DisplayName("Deve buscar pagamentos sem filtros")
        void deveBuscarPagamentosSemFiltros() throws Exception {
            
            List<PagamentoResponseDTO> pagamentos = Arrays.asList(pagamentoResponseDTO);
            when(servicoPagamento.buscarPagamentos(null, null, null))
                .thenReturn(pagamentos);

            
            mockMvc.perform(get("/api/pagamentos/buscar"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));

            verify(servicoPagamento).buscarPagamentos(null, null, null);
        }
    }

    @Nested
    @DisplayName("Testes de Busca por ID - GET /api/pagamentos/{id}")
    class BuscaPorIdTests {

        @Test
        @DisplayName("Deve obter pagamento por ID com sucesso")
        void deveObterPagamentoPorIdComSucesso() throws Exception {
            
            when(servicoPagamento.obterPagamentoPorId(1L)).thenReturn(pagamentoResponseDTO);

            
            mockMvc.perform(get("/api/pagamentos/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.codigoDebito").value(12345));

            verify(servicoPagamento).obterPagamentoPorId(1L);
        }

        @Test
        @DisplayName("Deve retornar status 404 quando pagamento não é encontrado")
        void deveRetornarStatus404QuandoPagamentoNaoEncontrado() throws Exception {
            
            when(servicoPagamento.obterPagamentoPorId(999L))
                .thenThrow(new PagamentoNaoEncontradoException(999L));

            
            mockMvc.perform(get("/api/pagamentos/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Status - PUT /api/pagamentos/{id}/status")
    class AtualizacaoStatusTests {

        @Test
        @DisplayName("Deve atualizar status do pagamento com sucesso")
        void deveAtualizarStatusDoPagamentoComSucesso() throws Exception {
            
            AtualizacaoStatusRequestDTO requestDTO = new AtualizacaoStatusRequestDTO();
            requestDTO.setStatus(StatusPagamento.PROCESSADO_SUCESSO);
            
            pagamentoResponseDTO.setStatus("Processado com Sucesso");
            when(servicoPagamento.atualizarStatusPagamento(1L, StatusPagamento.PROCESSADO_SUCESSO))
                .thenReturn(pagamentoResponseDTO);

            
            mockMvc.perform(put("/api/pagamentos/1/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.status").value("Processado com Sucesso"));

            verify(servicoPagamento).atualizarStatusPagamento(1L, StatusPagamento.PROCESSADO_SUCESSO);
        }

        @Test
        @DisplayName("Deve retornar status 400 para transição inválida")
        void deveRetornarStatus400ParaTransicaoInvalida() throws Exception {
            
            AtualizacaoStatusRequestDTO requestDTO = new AtualizacaoStatusRequestDTO();
            requestDTO.setStatus(StatusPagamento.PROCESSADO_FALHA);
            
            when(servicoPagamento.atualizarStatusPagamento(1L, StatusPagamento.PROCESSADO_FALHA))
                .thenThrow(new TransicaoStatusInvalidaException(StatusPagamento.PROCESSADO_SUCESSO, StatusPagamento.PROCESSADO_FALHA));

            
            mockMvc.perform(put("/api/pagamentos/1/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Deve retornar status 404 quando pagamento não existe para atualização")
        void deveRetornarStatus404QuandoPagamentoNaoExisteParaAtualizacao() throws Exception {
            
            AtualizacaoStatusRequestDTO requestDTO = new AtualizacaoStatusRequestDTO();
            requestDTO.setStatus(StatusPagamento.PROCESSADO_SUCESSO);
            
            when(servicoPagamento.atualizarStatusPagamento(999L, StatusPagamento.PROCESSADO_SUCESSO))
                .thenThrow(new PagamentoNaoEncontradoException(999L));

            
            mockMvc.perform(put("/api/pagamentos/999/status")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Testes de Exclusão - DELETE /api/pagamentos/{id}")
    class ExclusaoTests {

        @Test
        @DisplayName("Deve excluir pagamento com sucesso")
        void deveExcluirPagamentoComSucesso() throws Exception {
            
            doNothing().when(servicoPagamento).excluirPagamento(1L);

            
            mockMvc.perform(delete("/api/pagamentos/1"))
                    .andExpect(status().isNoContent());

            verify(servicoPagamento).excluirPagamento(1L);
        }

        @Test
        @DisplayName("Deve retornar status 400 quando não é possível excluir")
        void deveRetornarStatus400QuandoNaoEPossivelExcluir() throws Exception {
            
            doThrow(new PagamentoInvalidoException("Só é possível excluir pagamentos com status 'Pendente de Processamento'"))
                .when(servicoPagamento).excluirPagamento(1L);

            
            mockMvc.perform(delete("/api/pagamentos/1"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.mensagem").value("Só é possível excluir pagamentos com status 'Pendente de Processamento'"));
        }

        @Test
        @DisplayName("Deve retornar status 404 quando pagamento não existe para exclusão")
        void deveRetornarStatus404QuandoPagamentoNaoExisteParaExclusao() throws Exception {
            
            doThrow(new PagamentoNaoEncontradoException(999L))
                .when(servicoPagamento).excluirPagamento(999L);

            
            mockMvc.perform(delete("/api/pagamentos/999"))
                    .andExpect(status().isNotFound());
        }
    }
}
