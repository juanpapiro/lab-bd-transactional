package br.com.lab.bdtransactional.dto;

import java.math.BigDecimal;

public record CancelRequest(Long saleId, BigDecimal cancelAmount) {
}
