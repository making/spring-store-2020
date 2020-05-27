package lol.maki.dev.store.stock;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public class StockMapper {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Stock> stockRowMapper = (rs, rowNum) -> new StockBuilder()
            .withItemId(rs.getLong("item_id"))
            .withQuantity(rs.getInt("quantity"))
            .withCreatedAt(rs.getTimestamp("created_at").toInstant())
            .withCreatedBy(rs.getString("created_by"))
            .withUpdatedAt(rs.getTimestamp("updated_at").toInstant())
            .withUpdatedBy(rs.getString("created_by"))
            .build();

    public StockMapper(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Stock> findAll() {
        return this.jdbcTemplate.query("SELECT item_id, quantity, created_at, created_by, updated_at, updated_by FROM stock ORDER BY item_id", this.stockRowMapper);
    }

    public List<Stock> findByItemIds(List<Long> itemIds) {
        return this.jdbcTemplate.query("SELECT item_id, quantity, created_at, created_by, updated_at, updated_by FROM stock WHERE item_id IN (:itemIds) ORDER BY item_id", Map.of("itemIds", itemIds), this.stockRowMapper);
    }

    @Transactional
    public int subtractIfPossible(Stock stock) {
        final BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(stock);
        return this.jdbcTemplate.update("update stock set quantity = quantity - :quantity where item_id = :itemId and quantity > :quantity", paramSource);
    }

}
