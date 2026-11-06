package br.com.lab.bdtransactional.repository;

import br.com.lab.bdtransactional.model.Cancel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class CancelRepository {

    private final JdbcTemplate jdbcTemplate;

    public CancelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Cancel insert(Long saleId, BigDecimal cancelAmount, String status) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                    "INSERT INTO cancel (sale_id, cancel_amount, status) VALUES (?, ?, ?)", new String[]{"ID"});
            ps.setLong(1, saleId);
            ps.setBigDecimal(2, cancelAmount);
            ps.setString(3, status);
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        return findById(id).orElseThrow();
    }

    public void updateStatus(Long id, String status) {
        jdbcTemplate.update("UPDATE cancel SET status = ? WHERE id = ?", status, id);
    }

    public BigDecimal sumAmountBySaleIdAndStatuses(Long saleId, String status1, String status2) {
        return jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(cancel_amount), 0) FROM cancel WHERE sale_id = ? AND status IN (?, ?)",
                BigDecimal.class, saleId, status1, status2);
    }

    public Optional<Cancel> findById(Long id) {
        return jdbcTemplate.query(
                "SELECT id, sale_id, cancel_amount, dt_cancel, status FROM cancel WHERE id = ?",
                (rs, rowNum) -> new Cancel(
                        rs.getLong("id"),
                        rs.getLong("sale_id"),
                        rs.getBigDecimal("cancel_amount"),
                        rs.getObject("dt_cancel", Timestamp.class).toLocalDateTime(),
                        rs.getString("status")),
                id).stream().findFirst();
    }
}
