package br.com.lab.bdtransactional.exception;

public class SaleNotFoundException extends RuntimeException {

    public SaleNotFoundException(Long saleId) {
        super("Venda não encontrada: " + saleId);
    }
}
