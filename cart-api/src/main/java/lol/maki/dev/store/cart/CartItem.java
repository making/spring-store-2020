package lol.maki.dev.store.cart;

public class CartItem {
	private final Long itemId;

	private int quantity;

	public CartItem(Long itemId, int quantity) {
		this.itemId = itemId;
		this.quantity = quantity;
	}

	public Long getItemId() {
		return itemId;
	}

	public int getQuantity() {
		return quantity;
	}

	public CartItem incrementQuantity(int increment) {
		this.quantity = this.quantity + increment;
		return this;
	}
}
