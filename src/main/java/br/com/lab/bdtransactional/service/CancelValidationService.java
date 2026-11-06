package br.com.lab.bdtransactional.service;

import br.com.lab.bdtransactional.dto.CancelRequest;
import br.com.lab.bdtransactional.exception.SaleNotFoundException;
import br.com.lab.bdtransactional.model.Sale;
import br.com.lab.bdtransactional.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CancelValidationService {

    private final SaleRepository saleRepository;

    public CancelValidationService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    public Sale validate(CancelRequest request) {
        if (request.saleId() == null) {
            throw new IllegalArgumentException("O saleId é obrigatório");
        }

        if (request.cancelAmount() == null || request.cancelAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do cancelamento deve ser maior que zero");
        }

        Sale sale = saleRepository.findByIdForUpdate(request.saleId())
                .orElseThrow(() -> new SaleNotFoundException(request.saleId()));

        if (request.cancelAmount().compareTo(sale.amount()) > 0) {
            throw new IllegalArgumentException(
                    "O valor do cancelamento não pode ser maior que o valor da venda");
        }
        return sale;
    }
}
