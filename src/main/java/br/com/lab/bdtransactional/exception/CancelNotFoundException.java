package br.com.lab.bdtransactional.exception;

public class CancelNotFoundException extends RuntimeException {

    public CancelNotFoundException(Long cancelId) {
        super("Cancelamento não encontrado: " + cancelId);
    }
}
