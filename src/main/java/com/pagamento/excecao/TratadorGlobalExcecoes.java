package com.pagamento.excecao;

import com.pagamento.dto.ErroResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class TratadorGlobalExcecoes {

    @ExceptionHandler(PagamentoNaoEncontradoException.class)
    public ResponseEntity<ErroResponseDTO> tratarPagamentoNaoEncontradoException(
            PagamentoNaoEncontradoException ex, WebRequest request) {
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    @ExceptionHandler(TransicaoStatusInvalidaException.class)
    public ResponseEntity<ErroResponseDTO> tratarTransicaoStatusInvalidaException(
            TransicaoStatusInvalidaException ex, WebRequest request) {
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(PagamentoInvalidoException.class)
    public ResponseEntity<ErroResponseDTO> tratarPagamentoInvalidoException(
            PagamentoInvalidoException ex, WebRequest request) {
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponseDTO> tratarExcecaoValidacao(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        List<String> detalhes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .collect(Collectors.toList());
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Dados inválidos fornecidos",
            request.getDescription(false).replace("uri=", ""),
            detalhes
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErroResponseDTO> tratarExcecaoViolacaoRestricao(
            ConstraintViolationException ex, WebRequest request) {
        
        List<String> detalhes = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Dados inválidos fornecidos",
            request.getDescription(false).replace("uri=", ""),
            detalhes
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponseDTO> tratarExcecaoMensagemNaoLegivel(
            HttpMessageNotReadableException ex, WebRequest request) {
        
        String mensagem = "Formato JSON inválido ou campos com tipos incorretos";
        
        // Tenta extrair informação mais específica do erro
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            String mensagemCausa = ex.getCause().getMessage();
            if (mensagemCausa.contains("MetodoPagamento")) {
                mensagem = "Método de pagamento inválido. Use: boleto, pix, cartao_credito ou cartao_debito";
            } else if (mensagemCausa.contains("StatusPagamento")) {
                mensagem = "Status de pagamento inválido";
            }
        }
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            mensagem,
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponseDTO> tratarExcecaoTipoArgumentoIncorreto(
            MethodArgumentTypeMismatchException ex, WebRequest request) {
        
        Class<?> tipoRequerido = ex.getRequiredType();
        String nomeTipo = tipoRequerido != null ? tipoRequerido.getSimpleName() : "desconhecido";
        String mensagem = String.format("Parâmetro '%s' deve ser do tipo %s", 
                                     ex.getName(), nomeTipo);
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            mensagem,
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponseDTO> tratarExcecaoGenerica(
            Exception ex, WebRequest request) {
        
        ErroResponseDTO erro = new ErroResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Ocorreu um erro interno no servidor",
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
    }
}
