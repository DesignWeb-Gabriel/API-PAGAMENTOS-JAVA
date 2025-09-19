package com.payment.dto;

import com.payment.enums.PaymentMethod;

import javax.validation.constraints.*;
import java.math.BigDecimal;

public class PaymentRequestDTO {

    @NotNull(message = "Código do débito é obrigatório")
    @Positive(message = "Código do débito deve ser um número positivo")
    private Integer codigoDebito;

    @NotBlank(message = "CPF/CNPJ é obrigatório")
    @Pattern(regexp = "^\\d{11}$|^\\d{14}$", message = "CPF deve ter 11 dígitos ou CNPJ deve ter 14 dígitos")
    private String cpfCnpj;

    @NotNull(message = "Método de pagamento é obrigatório")
    private PaymentMethod metodoPagamento;

    @Pattern(regexp = "^\\d{13,19}$", message = "Número do cartão deve ter entre 13 e 19 dígitos")
    private String numeroCartao;

    @NotNull(message = "Valor do pagamento é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor do pagamento deve ser maior que zero")
    @Digits(integer = 8, fraction = 2, message = "Valor deve ter no máximo 8 dígitos inteiros e 2 decimais")
    private BigDecimal valorPagamento;

    public PaymentRequestDTO() {}

    public PaymentRequestDTO(Integer codigoDebito, String cpfCnpj, PaymentMethod metodoPagamento,
                           String numeroCartao, BigDecimal valorPagamento) {
        this.codigoDebito = codigoDebito;
        this.cpfCnpj = cpfCnpj;
        this.metodoPagamento = metodoPagamento;
        this.numeroCartao = numeroCartao;
        this.valorPagamento = valorPagamento;
    }

    // Getters e Setters
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
}