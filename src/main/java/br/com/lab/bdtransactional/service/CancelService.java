package br.com.lab.bdtransactional.service;

import br.com.lab.bdtransactional.dto.CancelRequest;
import br.com.lab.bdtransactional.exception.CancelNotFoundException;
import br.com.lab.bdtransactional.model.Cancel;
import br.com.lab.bdtransactional.model.Sale;
import br.com.lab.bdtransactional.repository.CancelRepository;
import br.com.lab.bdtransactional.repository.SaleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CancelService {

    private static final String STATUS_CANCEL_PENDING = "PENDING";
    private static final String STATUS_CANCEL_EFFECTIVE = "EFFECTIVE";
    private static final String STATUS_SALE_CANCELED = "CANCELED";

    private final CancelValidationService cancelValidationService;
    private final CancelRepository cancelRepository;
    private final SaleRepository saleRepository;

    public CancelService(CancelValidationService cancelValidationService,
                         CancelRepository cancelRepository,
                         SaleRepository saleRepository) {
        this.cancelValidationService = cancelValidationService;
        this.cancelRepository = cancelRepository;
        this.saleRepository = saleRepository;
    }

    @Transactional(readOnly = true)
    public Cancel findById(Long id) {
        return cancelRepository.findById(id).orElseThrow(() -> new CancelNotFoundException(id));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Cancel create(CancelRequest request) {
        return processCancel(request);
    }

    private Cancel processCancel(CancelRequest request) {
        Sale sale = cancelValidationService.validate(request);

        Cancel cancel = insertPendingCancel(sale, request.cancelAmount());
        cancelRepository.updateStatus(cancel.id(), STATUS_CANCEL_EFFECTIVE);
        saleRepository.updateStatus(sale.id(), STATUS_SALE_CANCELED);

        return cancelRepository.findById(cancel.id()).orElseThrow();
    }

    private Cancel insertPendingCancel(Sale sale, BigDecimal cancelAmount) {
        BigDecimal alreadyCanceled = cancelRepository.sumAmountBySaleIdAndStatuses(
                sale.id(), STATUS_CANCEL_PENDING, STATUS_CANCEL_EFFECTIVE);

        if (alreadyCanceled.add(cancelAmount).compareTo(sale.amount()) > 0) {
            throw new IllegalArgumentException(
                    "A soma dos cancelamentos não pode ser maior que o valor da venda");
        }
        return cancelRepository.insert(sale.id(), cancelAmount, STATUS_CANCEL_PENDING);
    }
}
