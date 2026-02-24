package br.com.sbsistemas.inventario.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    public NotFoundException(String entity, Long id) {
        super("%s não encontrado(a) com id: %d".formatted(entity, id));
    }

    public NotFoundException(String message) {
        super(message);
    }
}
