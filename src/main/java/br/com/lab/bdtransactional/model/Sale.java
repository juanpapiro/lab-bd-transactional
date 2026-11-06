package br.com.lab.bdtransactional.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Sale(Long id, BigDecimal amount, LocalDateTime dtSale, String status) {
}
