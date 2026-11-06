package br.com.lab.bdtransactional.repository;

import br.com.lab.bdtransactional.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class SaleRepository {

    private static final RowMapper<Sale> SALE_ROW_MAPPER = (rs, rowNum) -> new Sale(
            rs.getLong("id"),
            rs.getBigDecimal("amount"),
            rs.getObject("dt_sale", Timestamp.class).toLocalDateTime(),
            rs.getString("status"));

    private final JdbcTemplate jdbcTemplate;

    public SaleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Sale insert(BigDecimal amount, String status) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO sales (amount, status) VALUES (?, ?)", new String[]{"ID"});
            ps.setBigDecimal(1, amount);
            ps.setString(2, status);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return findById(id).orElseThrow();
    }

    public Optional<Sale> findById(Long id) {
        return jdbcTemplate.query(
                "SELECT id, amount, dt_sale, status FROM sales WHERE id = ?",
                SALE_ROW_MAPPER, id).stream().findFirst();
    }

    /**
     * Busca a venda travando a linha (SELECT ... FOR UPDATE) até o fim da transação.
     * Deve ser chamado dentro de uma transação ativa.
     */
    public Optional<Sale> findByIdForUpdate(Long id) {
        return jdbcTemplate.query(
                "SELECT id, amount, dt_sale, status FROM sales WHERE id = ? FOR UPDATE",
                SALE_ROW_MAPPER, id).stream().findFirst();
    }

    public void updateStatus(Long id, String status) {
        jdbcTemplate.update("UPDATE sales SET status = ? WHERE id = ?", status, id);
    }
}
