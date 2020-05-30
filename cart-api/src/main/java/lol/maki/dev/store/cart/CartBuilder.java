package lol.maki.dev.store.cart;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class CartBuilder {
	private UUID cartId;

	private Set<CartItem> cartItemMap;

	private Instant createdAt;

	private String createdBy;

	private Instant updatedAt;

	private String updatedBy;

	public CartBuilder withCartId(UUID cartId) {
		this.cartId = cartId;
		return this;
	}

	public CartBuilder withCartItemMap(Set<CartItem> cartItemMap) {
		this.cartItemMap = cartItemMap;
		return this;
	}

	public CartBuilder withCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public CartBuilder withCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public CartBuilder withUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public CartBuilder withUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		return this;
	}

	public Cart build() {
		return new Cart(cartId, cartItemMap, createdAt, createdBy, updatedAt, updatedBy);
	}
}