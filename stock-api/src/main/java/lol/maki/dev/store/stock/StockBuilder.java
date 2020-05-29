package lol.maki.dev.store.stock;

import java.time.Instant;

public class StockBuilder {
	private Long itemId;

	private Integer quantity;

	private Instant createdAt;

	private String createdBy;

	private Instant updatedAt;

	private String updatedBy;

	public StockBuilder withItemId(Long itemId) {
		this.itemId = itemId;
		return this;
	}

	public StockBuilder withQuantity(Integer quantity) {
		this.quantity = quantity;
		return this;
	}

	public StockBuilder withCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public StockBuilder withCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public StockBuilder withUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public StockBuilder withUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		return this;
	}

	public Stock build() {
		return new Stock(itemId, quantity, createdAt, createdBy, updatedAt, updatedBy);
	}
}