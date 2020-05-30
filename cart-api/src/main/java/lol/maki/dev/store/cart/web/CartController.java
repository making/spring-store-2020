package lol.maki.dev.store.cart.web;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lol.maki.dev.store.cart.Cart;
import lol.maki.dev.store.cart.CartItem;
import lol.maki.dev.store.cart.CartMapper;
import lol.maki.dev.store.cart.spec.CartResponse;
import lol.maki.dev.store.cart.spec.CartResponseItem;
import lol.maki.dev.store.cart.spec.CartsApi;
import lol.maki.dev.store.cart.spec.UpdateCartRequest;
import lol.maki.dev.store.item.client.ItemApi;
import lol.maki.dev.store.item.client.ItemResponse;
import lol.maki.dev.store.stock.client.StockApi;
import lol.maki.dev.store.stock.client.StockResponse;
import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.util.IdGenerator;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class CartController implements CartsApi {
	private final CartMapper cartMapper;

	private final ItemApi itemApi;

	private final StockApi stockApi;

	private final IdGenerator idGenerator;

	public CartController(CartMapper cartMapper, ItemApi itemApi, StockApi stockApi, IdGenerator idGenerator) {
		this.cartMapper = cartMapper;
		this.itemApi = itemApi;
		this.stockApi = stockApi;
		this.idGenerator = idGenerator;
	}

	@Override
	public ResponseEntity<CartResponse> addItem(UUID cartId, UpdateCartRequest request) {
		final Cart cart = this.findCart(cartId)
				.updateItem(new CartItem(request.getItemId(), request.getQuantity()));
		final Optional<StockResponse> stock = Mono.from(this.stockApi.getStocksByItemIds(List.of(request.getItemId()))).blockOptional();
		stock.map(StockResponse::getQuantity)
				.filter(q -> q >= request.getQuantity())
				.orElseThrow(() -> new ResponseStatusException(CONFLICT, String.format("Not enough stocks (itemId=%d, quantity=%d)", request.getItemId(), request.getQuantity())));
		this.cartMapper.update(cart);
		return ResponseEntity.ok(toResponse(cart));
	}

	@Override
	public ResponseEntity<CartResponse> createCart() {
		final Cart cart = Cart.create(this.idGenerator::generateId);
		this.cartMapper.insert(cart);
		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().replacePath("carts/{cartId}").build(cart.cartId());
		return ResponseEntity.created(location).body(toResponse(cart));
	}

	@Override
	public ResponseEntity<CartResponse> getCartByCartId(UUID cartId) {
		final Cart cart = this.findCart(cartId);
		return ResponseEntity.ok(toResponse(cart));
	}

	@Override
	public ResponseEntity<CartResponse> removeItem(UUID cartId, Long itemId) {
		final Cart cart = this.findCart(cartId).removeItem(itemId);
		this.cartMapper.update(cart);
		return ResponseEntity.ok(toResponse(cart));
	}

	Cart findCart(UUID cartId) {
		return this.cartMapper.findById(cartId)
				.orElseThrow(() -> new ResponseStatusException(NOT_FOUND, String.format("The requested cart is not found. (cartId = %s)", cartId)));
	}

	CartResponse toResponse(Cart cart) {
		final List<Long> itemIds = cart.itemIds();
		if (!itemIds.isEmpty()) {
			final List<ItemResponse> itemResponseList = this.itemApi.getItemsIds(itemIds).collectList().block();
			final Map<Long, ItemResponse> itemMap = itemResponseList.stream().collect(Collectors.toMap(ItemResponse::getId, Function.identity()));
			return cart.detail(itemMap,
					item -> BigDecimal.valueOf(item.getUnitPrice()) /* ItemResponse -> unitPrice */,
					(item, cartItemMap) -> cartItemMap.get(item.getId()) /* ItemResponse -> CartItem */,
					(item, cartItem) -> new CartResponseItem()
							.itemId(item.getId())
							.name(item.getName())
							.image(item.getImage())
							.author(item.getAuthor())
							.release(item.getRelease())
							.unitPrice(item.getUnitPrice())
							.quantity(cartItem.getQuantity()),
					(cartDetailItems, total) -> new CartResponse()
							.cartId(cart.cartId())
							.items(cartDetailItems)
							.total(total.doubleValue()));
		}
		else {
			return new CartResponse()
					.cartId(cart.cartId())
					.items(List.of())
					.total(0.0);
		}
	}
}
