package com.payment.dto;

import com.payment.entity.Payment;
import com.payment.enums.PaymentMethod;
import com.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDTO {

    private Long id;
    private Integer codigoDebito;
    private String cpfCnpj;
    private PaymentMethod metodoPagamento;
    private String numeroCartao;
    private BigDecimal valorPagamento;
    private PaymentStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public PaymentResponseDTO() {}

    public PaymentResponseDTO(Payment payment) {
        this.id = payment.getId();
        this.codigoDebito = payment.getCodigoDebito();
        this.cpfCnpj = payment.getCpfCnpj();
        this.metodoPagamento = payment.getMetodoPagamento();
        this.numeroCartao = maskCardNumber(payment.getNumeroCartao());
        this.valorPagamento = payment.getValorPagamento();
        this.status = payment.getStatus();
        this.dataCriacao = payment.getDataCriacao();
        this.dataAtualizacao = payment.getDataAtualizacao();
    }

    private String maskCardNumber(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.length() < 4) {
            return numeroCartao;
        }
        return "**** **** **** " + numeroCartao.substring(numeroCartao.length() - 4);
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

    public PaymentMethod getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(PaymentMethod metodoPagamento) {
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

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
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
