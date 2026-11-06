package br.com.lab.bdtransactional.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Cancel(Long id, Long saleId, BigDecimal cancelAmount, LocalDateTime dtCancel, String status) {
}
