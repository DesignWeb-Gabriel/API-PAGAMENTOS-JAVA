package com.pagamento.servico;

import com.pagamento.dto.PagamentoRequestDTO;
import com.pagamento.dto.PagamentoResponseDTO;
import com.pagamento.entidade.Pagamento;
import com.pagamento.enums.MetodoPagamento;
import com.pagamento.enums.StatusPagamento;
import com.pagamento.excecao.PagamentoInvalidoException;
import com.pagamento.excecao.PagamentoNaoEncontradoException;
import com.pagamento.excecao.TransicaoStatusInvalidaException;
import com.pagamento.repositorio.RepositorioPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Pagamento")
class ServicoPagamentoTest {

    @Mock
    private RepositorioPagamento repositorioPagamento;

    @InjectMocks
    private ServicoPagamento servicoPagamento;

    private PagamentoRequestDTO pagamentoRequestDTO;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        pagamentoRequestDTO = new PagamentoRequestDTO();
        pagamentoRequestDTO.setCodigoDebito(12345);
        pagamentoRequestDTO.setCpfCnpj("12345678901");
        pagamentoRequestDTO.setMetodoPagamento(MetodoPagamento.PIX);
        pagamentoRequestDTO.setValorPagamento(new BigDecimal("100.50"));

        pagamento = new Pagamento(12345, "12345678901", MetodoPagamento.PIX, null, new BigDecimal("100.50"));
        pagamento.setId(1L);
    }

    @Nested
    @DisplayName("Testes de Criação de Pagamento")
    class CriacaoPagamentoTests {

        @Test
        @DisplayName("Deve criar pagamento com PIX com sucesso")
        void deveCriarPagamentoComPixComSucesso() {
            // Arrange
            when(repositorioPagamento.save(any(Pagamento.class))).thenReturn(pagamento);

            // Act
            PagamentoResponseDTO resultado = servicoPagamento.criarPagamento(pagamentoRequestDTO);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getCodigoDebito()).isEqualTo(12345);
            assertThat(resultado.getCpfCnpj()).isEqualTo("12345678901");
            assertThat(resultado.getMetodoPagamento()).isEqualTo("pix");
            assertThat(resultado.getValorPagamento()).isEqualByComparingTo(new BigDecimal("100.50"));
            assertThat(resultado.getStatus()).isEqualTo("Pendente de Processamento");

            verify(repositorioPagamento).save(any(Pagamento.class));
        }

        @Test
        @DisplayName("Deve criar pagamento com cartão de crédito quando número do cartão é fornecido")
        void deveCriarPagamentoComCartaoCreditoComSucesso() {
            // Arrange
            pagamentoRequestDTO.setMetodoPagamento(MetodoPagamento.CARTAO_CREDITO);
            pagamentoRequestDTO.setNumeroCartao("1234567890123456");
            
            Pagamento pagamentoCartao = new Pagamento(12345, "12345678901", 
                MetodoPagamento.CARTAO_CREDITO, "1234567890123456", new BigDecimal("100.50"));
            pagamentoCartao.setId(1L);
            
            when(repositorioPagamento.save(any(Pagamento.class))).thenReturn(pagamentoCartao);

            // Act
            PagamentoResponseDTO resultado = servicoPagamento.criarPagamento(pagamentoRequestDTO);

            // Assert
            assertThat(resultado.getMetodoPagamento()).isEqualTo("cartao_credito");
            assertThat(resultado.getNumeroCartao()).isEqualTo("**** **** **** 3456");
        }

        @Test
        @DisplayName("Deve lançar exceção quando número do cartão não é fornecido para pagamento com cartão")
        void deveLancarExcecaoQuandoNumeroCartaoNaoFornecidoParaCartao() {
            // Arrange
            pagamentoRequestDTO.setMetodoPagamento(MetodoPagamento.CARTAO_DEBITO);
            pagamentoRequestDTO.setNumeroCartao(null);

            // Act & Assert
            assertThatThrownBy(() -> servicoPagamento.criarPagamento(pagamentoRequestDTO))
                .isInstanceOf(PagamentoInvalidoException.class)
                .hasMessageContaining("Número do cartão é obrigatório para pagamentos com cartão");
        }

        @Test
        @DisplayName("Deve lançar exceção quando número do cartão é fornecido para pagamento sem cartão")
        void deveLancarExcecaoQuandoNumeroCartaoFornecidoParaPagamentoSemCartao() {
            // Arrange
            pagamentoRequestDTO.setMetodoPagamento(MetodoPagamento.PIX);
            pagamentoRequestDTO.setNumeroCartao("1234567890123456");

            // Act & Assert
            assertThatThrownBy(() -> servicoPagamento.criarPagamento(pagamentoRequestDTO))
                .isInstanceOf(PagamentoInvalidoException.class)
                .hasMessageContaining("Número do cartão não deve ser informado para pagamentos que não sejam com cartão");
        }
    }

    @Nested
    @DisplayName("Testes de Listagem de Pagamentos")
    class ListagemPagamentosTests {

        @Test
        @DisplayName("Deve listar todos os pagamentos ativos")
        void deveListarTodosPagamentosAtivos() {
            // Arrange
            Pagamento pagamento2 = new Pagamento(54321, "98765432100", MetodoPagamento.BOLETO, null, new BigDecimal("200.00"));
            pagamento2.setId(2L);
            
            List<Pagamento> pagamentos = Arrays.asList(pagamento, pagamento2);
            when(repositorioPagamento.findByAtivoTrue()).thenReturn(pagamentos);

            // Act
            List<PagamentoResponseDTO> resultado = servicoPagamento.listarTodosPagamentos();

            // Assert
            assertThat(resultado).hasSize(2);
            assertThat(resultado.get(0).getId()).isEqualTo(1L);
            assertThat(resultado.get(1).getId()).isEqualTo(2L);
            
            verify(repositorioPagamento).findByAtivoTrue();
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não há pagamentos ativos")
        void deveRetornarListaVaziaQuandoNaoHaPagamentosAtivos() {
            // Arrange
            when(repositorioPagamento.findByAtivoTrue()).thenReturn(Arrays.asList());

            // Act
            List<PagamentoResponseDTO> resultado = servicoPagamento.listarTodosPagamentos();

            // Assert
            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Pagamentos")
    class BuscaPagamentosTests {

        @Test
        @DisplayName("Deve buscar pagamentos com filtros")
        void deveBuscarPagamentosComFiltros() {
            // Arrange
            List<Pagamento> pagamentos = Arrays.asList(pagamento);
            when(repositorioPagamento.encontrarComFiltros(12345, "12345678901", StatusPagamento.PENDENTE_PROCESSAMENTO))
                .thenReturn(pagamentos);

            // Act
            List<PagamentoResponseDTO> resultado = servicoPagamento.buscarPagamentos(
                12345, "12345678901", StatusPagamento.PENDENTE_PROCESSAMENTO);

            // Assert
            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).getId()).isEqualTo(1L);
            
            verify(repositorioPagamento).encontrarComFiltros(12345, "12345678901", StatusPagamento.PENDENTE_PROCESSAMENTO);
        }

        @Test
        @DisplayName("Deve obter pagamento por ID")
        void deveObterPagamentoPorId() {
            // Arrange
            when(repositorioPagamento.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(pagamento));

            // Act
            PagamentoResponseDTO resultado = servicoPagamento.obterPagamentoPorId(1L);

            // Assert
            assertThat(resultado).isNotNull();
            assertThat(resultado.getId()).isEqualTo(1L);
            
            verify(repositorioPagamento).findByIdAndAtivoTrue(1L);
        }

        @Test
        @DisplayName("Deve lançar exceção quando pagamento não é encontrado por ID")
        void deveLancarExcecaoQuandoPagamentoNaoEncontradoPorId() {
            // Arrange
            when(repositorioPagamento.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> servicoPagamento.obterPagamentoPorId(999L))
                .isInstanceOf(PagamentoNaoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Status")
    class AtualizacaoStatusTests {

        @Test
        @DisplayName("Deve atualizar status de PENDENTE para PROCESSADO_SUCESSO")
        void deveAtualizarStatusDePendenteParaProcessadoSucesso() {
            // Arrange
            pagamento.setStatus(StatusPagamento.PENDENTE_PROCESSAMENTO);
            Pagamento pagamentoAtualizado = new Pagamento(12345, "12345678901", MetodoPagamento.PIX, null, new BigDecimal("100.50"));
            pagamentoAtualizado.setId(1L);
            pagamentoAtualizado.setStatus(StatusPagamento.PROCESSADO_SUCESSO);
            
            when(repositorioPagamento.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(pagamento));
            when(repositorioPagamento.save(pagamento)).thenReturn(pagamentoAtualizado);

            // Act
            PagamentoResponseDTO resultado = servicoPagamento.atualizarStatusPagamento(1L, StatusPagamento.PROCESSADO_SUCESSO);

            // Assert
            assertThat(resultado.getStatus()).isEqualTo("Processado com Sucesso");
            verify(repositorioPagamento).save(pagamento);
        }

        @Test
        @DisplayName("Deve atualizar status de PROCESSADO_FALHA para PENDENTE_PROCESSAMENTO")
        void deveAtualizarStatusDeProcessadoFalhaParaPendente() {
            // Arrange
            pagamento.setStatus(StatusPagamento.PROCESSADO_FALHA);
            Pagamento pagamentoAtualizado = new Pagamento(12345, "12345678901", MetodoPagamento.PIX, null, new BigDecimal("100.50"));
            pagamentoAtualizado.setId(1L);
            pagamentoAtualizado.setStatus(StatusPagamento.PENDENTE_PROCESSAMENTO);
            
            when(repositorioPagamento.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(pagamento));
            when(repositorioPagamento.save(pagamento)).thenReturn(pagamentoAtualizado);

            // Act
            PagamentoResponseDTO resultado = servicoPagamento.atualizarStatusPagamento(1L, StatusPagamento.PENDENTE_PROCESSAMENTO);

            // Assert
            assertThat(resultado.getStatus()).isEqualTo("Pendente de Processamento");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar alterar status PROCESSADO_SUCESSO")
        void deveLancarExcecaoAoTentarAlterarStatusProcessadoSucesso() {
            // Arrange
            pagamento.setStatus(StatusPagamento.PROCESSADO_SUCESSO);
            when(repositorioPagamento.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(pagamento));

            // Act & Assert
            assertThatThrownBy(() -> servicoPagamento.atualizarStatusPagamento(1L, StatusPagamento.PROCESSADO_FALHA))
                .isInstanceOf(TransicaoStatusInvalidaException.class)
                .hasMessageContaining("Pagamentos com status 'Processado com Sucesso' não podem ser alterados");
        }

        @Test
        @DisplayName("Deve lançar exceção para transição inválida")
        void deveLancarExcecaoParaTransicaoInvalida() {
            // Arrange
            pagamento.setStatus(StatusPagamento.PROCESSADO_FALHA);
            when(repositorioPagamento.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(pagamento));

            // Act & Assert
            assertThatThrownBy(() -> servicoPagamento.atualizarStatusPagamento(1L, StatusPagamento.PROCESSADO_SUCESSO))
                .isInstanceOf(TransicaoStatusInvalidaException.class);
        }
    }

    @Nested
    @DisplayName("Testes de Exclusão de Pagamento")
    class ExclusaoPagamentoTests {

        @Test
        @DisplayName("Deve excluir pagamento pendente com sucesso")
        void deveExcluirPagamentoPendenteComSucesso() {
            // Arrange
            pagamento.setStatus(StatusPagamento.PENDENTE_PROCESSAMENTO);
            when(repositorioPagamento.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(pagamento));
            when(repositorioPagamento.save(pagamento)).thenReturn(pagamento);

            // Act
            servicoPagamento.excluirPagamento(1L);

            // Assert
            assertThat(pagamento.getAtivo()).isFalse();
            verify(repositorioPagamento).save(pagamento);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar excluir pagamento não pendente")
        void deveLancarExcecaoAoTentarExcluirPagamentoNaoPendente() {
            // Arrange
            pagamento.setStatus(StatusPagamento.PROCESSADO_SUCESSO);
            when(repositorioPagamento.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(pagamento));

            // Act & Assert
            assertThatThrownBy(() -> servicoPagamento.excluirPagamento(1L))
                .isInstanceOf(PagamentoInvalidoException.class)
                .hasMessageContaining("Só é possível excluir pagamentos com status 'Pendente de Processamento'");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar excluir pagamento inexistente")
        void deveLancarExcecaoAoTentarExcluirPagamentoInexistente() {
            // Arrange
            when(repositorioPagamento.findByIdAndAtivoTrue(999L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> servicoPagamento.excluirPagamento(999L))
                .isInstanceOf(PagamentoNaoEncontradoException.class);
        }
    }
}
