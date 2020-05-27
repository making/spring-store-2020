package lol.maki.dev.store.item;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Repository
public class ItemMapper {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<Item> itemRowMapper = (rs, rowNum) -> new ItemBuilder()
            .withId(rs.getLong("id"))
            .withName(rs.getString("name"))
            .withMedia(Item.MediaType.fromValue(rs.getString("media")))
            .withAuditor(rs.getString("author"))
            .withUnitPrice(rs.getBigDecimal("unit_price"))
            .withRelease(rs.getDate("release").toLocalDate())
            .withImage(URI.create(rs.getString("image")))
            .build();

    public ItemMapper(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Item> findAll() {
        return this.jdbcTemplate.query("SELECT id, name, media, author, unit_price, `release`, image FROM item ORDER BY id", itemRowMapper);
    }

    public List<Item> findByIds(List<Long> ids) {
        return this.jdbcTemplate.query("SELECT id, name, media, author, unit_price, `release`, image FROM item WHERE id IN (:ids) ORDER BY id", Map.of("ids", ids), itemRowMapper);
    }
}
