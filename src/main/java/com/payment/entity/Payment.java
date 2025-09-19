package com.payment.entity;

import com.payment.enums.PaymentMethod;
import com.payment.enums.PaymentStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "codigo_debito")
    private Integer codigoDebito;

    @NotNull
    @Column(name = "cpf_cnpj", length = 14)
    private String cpfCnpj;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pagamento")
    private PaymentMethod metodoPagamento;

    @Column(name = "numero_cartao", length = 20)
    private String numeroCartao;

    @NotNull
    @Positive
    @Column(name = "valor_pagamento", precision = 10, scale = 2)
    private BigDecimal valorPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "ativo")
    private Boolean ativo = true;

    public Payment() {
        this.status = PaymentStatus.PENDENTE_PROCESSAMENTO;
        this.dataCriacao = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
        this.ativo = true;
    }

    public Payment(Integer codigoDebito, String cpfCnpj, PaymentMethod metodoPagamento, 
                   String numeroCartao, BigDecimal valorPagamento) {
        this();
        this.codigoDebito = codigoDebito;
        this.cpfCnpj = cpfCnpj;
        this.metodoPagamento = metodoPagamento;
        this.numeroCartao = numeroCartao;
        this.valorPagamento = valorPagamento;
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
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
        this.dataAtualizacao = LocalDateTime.now();
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

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
        this.dataAtualizacao = LocalDateTime.now();
    }
}
