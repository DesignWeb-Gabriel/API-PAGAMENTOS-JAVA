# API de Pagamentos - Estrutura em Português

API REST para gerenciamento de pagamentos de débitos de pessoas físicas e jurídicas.

## Tecnologias Utilizadas

- **Java 11**
- **Spring Boot 2.7.14**
- **Spring Data JPA**
- **Banco H2** (em memória)
- **Maven** com Maven Wrapper
- **Swagger/OpenAPI 3**
- **Bean Validation**

## Arquitetura do Projeto

```
src/main/java/com/pagamento/
├──  AplicacaoPagamento.java        # Classe principal
├── configuracao/                     # Configurações
│   └── ConfiguracaoOpenApi.java     # Config Swagger
├── controlador/                      # Controllers REST
│   └── ControladorPagamento.java    # Endpoints da API
├── dto/                             # Data Transfer Objects
│   ├── PagamentoRequestDTO.java     # Request de pagamento
│   ├── PagamentoResponseDTO.java    # Response de pagamento
│   ├── AtualizacaoStatusRequestDTO.java # Request atualização
│   └── ErroResponseDTO.java         # Response de erro
├── entidade/                        # Entidades JPA
│   └── Pagamento.java              # Entidade principal
├── enums/                          # Enumerações
│   ├── MetodoPagamento.java        # Métodos de pagamento
│   └── StatusPagamento.java        # Status do pagamento
├── excecao/                        # Tratamento de exceções
│   ├── PagamentoInvalidoException.java
│   ├── PagamentoNaoEncontradoException.java
│   ├── TransicaoStatusInvalidaException.java
│   └── TratadorGlobalExcecoes.java # Handler global
├── repositorio/                    # Camada de dados
│   └── RepositorioPagamento.java   # Repository JPA
└── servico/                        # Lógica de negócio
    └── ServicoPagamento.java       # Serviços principais
```

## Funcionalidades

- **Recebimento de pagamentos** com validações completas
- **Atualização de status** com regras de negócio
- **Listagem e filtros** de busca
- **Exclusão lógica** de pagamentos
- **Validações robustas** de dados
- **Tratamento global de erros** padronizado
- **Documentação Swagger** completa

### Links Importantes

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Console H2**: http://localhost:8081/h2-console
  - **URL**: `jdbc:h2:mem:paymentdb`
  - **Usuário**: `sa`
  - **Senha**: `password`

## Endpoints da API

### 1. Criar Pagamento

```http
POST /api/pagamentos
Content-Type: application/json

{
  "codigoDebito": 12345,
  "cpfCnpj": "12345678901",
  "metodoPagamento": "pix",
  "valorPagamento": 150.50
}
```

### 2. Listar Todos os Pagamentos

```http
GET /api/pagamentos
```

### 3. Buscar com Filtros

```http
GET /api/pagamentos/buscar?codigoDebito=12345&cpfCnpj=12345678901&status=Pendente de Processamento
```

### 4. Buscar por ID

```http
GET /api/pagamentos/{id}
```

### 5. Atualizar Status

```http
PUT /api/pagamentos/{id}/status
Content-Type: application/json

{
  "status": "Processado com Sucesso"
}
```

### 6. Excluir Logicamente

```http
DELETE /api/pagamentos/{id}
```

## Regras de Negócio

### Métodos de Pagamento

- `boleto`: Boleto bancário
- `pix`: PIX
- `cartao_credito`: Cartão de crédito
- `cartao_debito`: Cartão de débito

### Status de Pagamento

- `Pendente de Processamento`: Status inicial do pagamento
- `Processado com Sucesso`: Pagamento aprovado e finalizado
- `Processado com Falha`: Pagamento rejeitado

### Transições de Status Permitidas

- **Pendente de Processamento** → Processado com Sucesso
- **Pendente de Processamento** → Processado com Falha
- **Processado com Sucesso** → (nenhuma transição)
- **Processado com Falha** → Pendente de Processamento

### Validações Implementadas

- **CPF**: Exatamente 11 dígitos
- **CNPJ**: Exatamente 14 dígitos
- **Número do cartão**: Obrigatório apenas para pagamentos com cartão (13-19 dígitos)
- **Valor**: Deve ser positivo e maior que zero
- **Exclusão**: Apenas pagamentos pendentes podem ser excluídos

## Exemplos de Uso

### Exemplo 1: Pagamento PIX

```json
{
  "codigoDebito": 12345,
  "cpfCnpj": "12345678901",
  "metodoPagamento": "pix",
  "valorPagamento": 150.5
}
```

### Exemplo 2: Pagamento com Cartão

```json
{
  "codigoDebito": 67890,
  "cpfCnpj": "98765432000123",
  "metodoPagamento": "cartao_credito",
  "numeroCartao": "1234567890123456",
  "valorPagamento": 300.0
}
```

### Exemplo 3: Atualização de Status

```json
{
  "status": "Processado com Sucesso"
}
```

## Tratamento de Erros Globais

A API possui tratamento global de erros com respostas padronizadas:

```json
{
  "timestamp": "2025-09-19T04:30:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Dados inválidos fornecidos",
  "caminho": "/api/pagamentos",
  "detalhes": [
    "codigoDebito: deve ser um número positivo",
    "cpfCnpj: CPF deve ter 11 dígitos ou CNPJ deve ter 14 dígitos"
  ]
}
```

## Dados de Teste Pré-carregados

A aplicação já vem com dados para facilitar os testes:

| ID  | Tipo | Método         | Valor     | Status   |
| --- | ---- | -------------- | --------- | -------- |
| 1   | CPF  | PIX            | R$ 150,50 | Pendente |
| 2   | CNPJ | Boleto         | R$ 300,00 | Pendente |
| 3   | CPF  | Cartão Crédito | R$ 99,99  | Sucesso  |
| 4   | CPF  | Cartão Débito  | R$ 250,75 | Falha    |
| 5   | CPF  | PIX            | R$ 500,00 | Pendente |

**Desenvolvido por Gabriel Raiol em Java, seguindo as melhores práticas e padrões**
