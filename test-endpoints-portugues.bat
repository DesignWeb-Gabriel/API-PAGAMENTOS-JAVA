@echo off
echo ========================================
echo API de Pagamentos - Estrutura em Portugues
echo ========================================
echo.
echo Servidor rodando em: http://localhost:8081
echo.

echo 1. Listando todos os pagamentos:
curl -X GET http://localhost:8081/api/pagamentos
echo.
echo.

echo 2. Criando um novo pagamento PIX:
curl -X POST http://localhost:8081/api/pagamentos -H "Content-Type: application/json" -d "{\"codigoDebito\": 99999, \"cpfCnpj\": \"12345678901\", \"metodoPagamento\": \"pix\", \"valorPagamento\": 350.00}"
echo.
echo.

echo 3. Criando um pagamento com cartao de credito:
curl -X POST http://localhost:8081/api/pagamentos -H "Content-Type: application/json" -d "{\"codigoDebito\": 77777, \"cpfCnpj\": \"98765432000123\", \"metodoPagamento\": \"cartao_credito\", \"numeroCartao\": \"1234567890123456\", \"valorPagamento\": 175.50}"
echo.
echo.

echo 4. Buscando pagamento por ID (ID = 1):
curl -X GET http://localhost:8081/api/pagamentos/1
echo.
echo.

echo 5. Buscando pagamentos com filtro por CPF:
curl -X GET "http://localhost:8081/api/pagamentos/buscar?cpfCnpj=12345678901"
echo.
echo.

echo 6. Buscando pagamentos com status Pendente:
curl -X GET "http://localhost:8081/api/pagamentos/buscar?status=Pendente de Processamento"
echo.
echo.

echo 7. Atualizando status de pagamento (ID = 7):
curl -X PUT http://localhost:8081/api/pagamentos/7/status -H "Content-Type: application/json" -d "{\"status\": \"Processado com Sucesso\"}"
echo.
echo.

echo 8. Tentativa de criacao de pagamento com cartao sem numero (deve falhar):
curl -X POST http://localhost:8081/api/pagamentos -H "Content-Type: application/json" -d "{\"codigoDebito\": 55555, \"cpfCnpj\": \"12345678901\", \"metodoPagamento\": \"cartao_credito\", \"valorPagamento\": 100.00}"
echo.
echo.

echo 9. Exclusao de pagamento pendente (deve funcionar):
curl -X DELETE http://localhost:8081/api/pagamentos/8
echo.
echo.

echo ========================================
echo Estrutura 100%% em Portugues!
echo ========================================
echo Classes principais:
echo - AplicacaoPagamento (main)
echo - ControladorPagamento
echo - ServicoPagamento  
echo - RepositorioPagamento
echo - Entidade: Pagamento
echo - Enums: MetodoPagamento, StatusPagamento
echo - DTOs: PagamentoRequestDTO, PagamentoResponseDTO
echo - Excecoes: PagamentoInvalidoException, etc.
echo ========================================
echo Acesse o Swagger UI em: http://localhost:8081/swagger-ui.html
echo Acesse o Console H2 em: http://localhost:8081/h2-console
echo ========================================
pause
