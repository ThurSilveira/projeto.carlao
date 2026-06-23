package br.com.escalaministerial.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entidade, Long id) {
        super(entidade + " com id " + id + " não encontrado(a)");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
