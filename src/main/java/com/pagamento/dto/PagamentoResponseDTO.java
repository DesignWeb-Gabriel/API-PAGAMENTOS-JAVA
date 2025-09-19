package com.pagamento.dto;

import com.pagamento.entidade.Pagamento;
import com.pagamento.enums.MetodoPagamento;
import com.pagamento.enums.StatusPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResponseDTO {

    private Long id;
    private Integer codigoDebito;
    private String cpfCnpj;
    private String metodoPagamento;
    private String numeroCartao;
    private BigDecimal valorPagamento;
    private String status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public PagamentoResponseDTO() {}

    public PagamentoResponseDTO(Pagamento pagamento) {
        this.id = pagamento.getId();
        this.codigoDebito = pagamento.getCodigoDebito();
        this.cpfCnpj = pagamento.getCpfCnpj();
        this.metodoPagamento = pagamento.getMetodoPagamento().getValor();
        this.numeroCartao = mascaraNumeroCartao(pagamento.getNumeroCartao());
        this.valorPagamento = pagamento.getValorPagamento();
        this.status = pagamento.getStatus().getDescricao();
        this.dataCriacao = pagamento.getDataCriacao();
        this.dataAtualizacao = pagamento.getDataAtualizacao();
    }

    private String mascaraNumeroCartao(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.trim().isEmpty()) {
            return null;
        }
        
        if (numeroCartao.length() < 4) {
            return numeroCartao;
        }
        
        String ultimosQuatro = numeroCartao.substring(numeroCartao.length() - 4);
        return "**** **** **** " + ultimosQuatro;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigoDebito() {
        return codigoDebito;
    }

    public void setCodigoDebito(Integer codigoDebito) {
        this.codigoDebito = codigoDebito;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public BigDecimal getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(BigDecimal valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
