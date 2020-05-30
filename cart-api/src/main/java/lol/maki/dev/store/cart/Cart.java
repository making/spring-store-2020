package lol.maki.dev.store.cart;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Cart {
	private final UUID cartId;

	private final Map<Long, CartItem> cartItemMap;

	private final Instant createdAt;

	private final String createdBy;

	private final Instant updatedAt;

	private final String updatedBy;

	public Cart(UUID cartId, Set<CartItem> cartItemMap, Instant createdAt, String createdBy, Instant updatedAt, String updatedBy) {
		this.cartId = cartId;
		this.cartItemMap = cartItemMap.stream().collect(Collectors.toMap(CartItem::getItemId, Function.identity()));
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
	}

	public static Cart create(Supplier<UUID> idGenerator) {
		final Instant now = Instant.now();
		return new CartBuilder().withCartId(idGenerator.get()).withCartItemMap(new LinkedHashSet<>()).withCreatedAt(null).withCreatedBy(null).withUpdatedAt(null).withUpdatedBy(null).build();
	}

	public UUID cartId() {
		return this.cartId;
	}

	public Collection<CartItem> cartItems() {
		return Collections.unmodifiableCollection(this.cartItemMap.values());
	}

	public List<Long> itemIds() {
		return this.cartItemMap.keySet()
				.stream()
				.collect(Collectors.toUnmodifiableList());
	}

	public Cart updateItem(CartItem cartItem) {
		final CartItem updatedItem = this.cartItemMap.computeIfAbsent(cartItem.getItemId(), cartId -> new CartItem(cartId, 0))
				.incrementQuantity(cartItem.getQuantity());
		if (updatedItem.getQuantity() <= 0) {
			this.removeItem(updatedItem.getItemId());
		}
		return this;
	}

	public Cart removeItem(Long itemId) {
		this.cartItemMap.remove(itemId);
		return this;
	}

	public <CART_DETAIL, CART_DETAIL_ITEM, ITEM> CART_DETAIL detail(Map<Long, ITEM> itemMap,
			Function<ITEM, BigDecimal> getUnitPrice,
			BiFunction<ITEM, Map<Long, CartItem>, CartItem> itemToCartItem,
			BiFunction<ITEM, CartItem, CART_DETAIL_ITEM> cartDetailItemSupplier,
			BiFunction<List<CART_DETAIL_ITEM>, BigDecimal, CART_DETAIL> cartDetailSupplier) {
		final List<CART_DETAIL_ITEM> cartDetailItems = this.cartItems().stream()
				.map(cartItem -> {
					final Long itemId = cartItem.getItemId();
					final ITEM item = itemMap.get(itemId);
					return cartDetailItemSupplier.apply(item, cartItem);
				})
				.collect(Collectors.toUnmodifiableList());
		final BigDecimal total = total(itemMap.values(), getUnitPrice, itemToCartItem);
		return cartDetailSupplier.apply(cartDetailItems, total);
	}

	<ITEM> BigDecimal total(Collection<ITEM> items, Function<ITEM, BigDecimal> getUnitPrice, BiFunction<ITEM, Map<Long, CartItem>, CartItem> itemToCartItem) {
		return items.stream()
				.map(item -> {
					final BigDecimal unitPrice = getUnitPrice.apply(item);
					final int quantity = itemToCartItem.apply(item, this.cartItemMap).getQuantity();
					return unitPrice.multiply(BigDecimal.valueOf(quantity));
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.FLOOR);
	}
}
