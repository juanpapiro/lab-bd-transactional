package br.com.lab.bdtransactional.service;

import br.com.lab.bdtransactional.dto.SaleRequest;
import br.com.lab.bdtransactional.model.Sale;
import br.com.lab.bdtransactional.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class SaleService {

    private static final String STATUS_CREATED = "CREATED";

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Transactional
    public Sale create(SaleRequest request) {
        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da venda deve ser maior que zero");
        }
        return saleRepository.insert(request.amount(), STATUS_CREATED);
    }
}
