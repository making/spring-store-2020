package lol.maki.dev.store.stock;

import java.time.Instant;
import java.util.Objects;

public class Stock {
	private final Long itemId;

	private final Integer quantity;

	private final Instant createdAt;

	private final String createdBy;

	private final Instant updatedAt;

	private final String updatedBy;

	public Stock(Long itemId, Integer quantity, Instant createdAt, String createdBy, Instant updatedAt, String updatedBy) {
		this.itemId = itemId;
		this.quantity = quantity;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
	}

	public Long getItemId() {
		return itemId;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final Stock stock = (Stock) o;
		return Objects.equals(itemId, stock.itemId) &&
				Objects.equals(quantity, stock.quantity);
	}

	@Override
	public int hashCode() {
		return Objects.hash(itemId, quantity);
	}
}
