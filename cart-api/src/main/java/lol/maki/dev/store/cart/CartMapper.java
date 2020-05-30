package lol.maki.dev.store.cart;

import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CartMapper {
	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final ObjectMapper objectMapper;

	private final RowMapper<Cart> cartRowMapper = (rs, rowNum) -> new CartBuilder()
			.withCartId(UUID.fromString(rs.getString("cart_id")))
			.withCartItemMap(jsonToCartItems(rs.getString("items")))
			.withCreatedAt(rs.getTimestamp("created_at").toInstant())
			.withCreatedBy(rs.getString("created_by"))
			.withUpdatedAt(rs.getTimestamp("updated_at").toInstant())
			.withUpdatedBy(rs.getString("created_by"))
			.build();

	public CartMapper(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.objectMapper = objectMapper;
	}

	public Optional<Cart> findById(UUID cartId) {
		try {
			final Cart cart = this.jdbcTemplate.queryForObject("SELECT cart_id, items, created_at, created_by, updated_at, updated_by FROM cart WHERE cart_id = :cartId", Map.of("cartId", cartId.toString()), cartRowMapper);
			return Optional.ofNullable(cart);
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Transactional
	public int insert(Cart cart) {
		final String items = cartItemsToJson(cart.cartItems());
		return this.jdbcTemplate.update("INSERT INTO cart(cart_id, items) VALUES (:cartId, :items)", Map.of("cartId", cart.cartId().toString(), "items", items));
	}

	@Transactional
	public int update(Cart cart) {
		final String items = cartItemsToJson(cart.cartItems());
		return this.jdbcTemplate.update("UPDATE cart set items = :items WHERE cart_id = :cartId", Map.of("cartId", cart.cartId().toString(), "items", items));
	}

	@Transactional
	public int delete(UUID cartId) {
		return this.jdbcTemplate.update("DELETE FROM cart WHERE cart_id = :cartId", Map.of("cartId", cartId.toString()));
	}

	private String cartItemsToJson(Collection<CartItem> cartItems) {
		try {
			return this.objectMapper.writeValueAsString(cartItems);
		}
		catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}

	private Set<CartItem> jsonToCartItems(String json) {
		try {
			return this.objectMapper.readValue(json, new TypeReference<>() {
			});
		}
		catch (JsonProcessingException e) {
			throw new UncheckedIOException(e);
		}
	}
}
