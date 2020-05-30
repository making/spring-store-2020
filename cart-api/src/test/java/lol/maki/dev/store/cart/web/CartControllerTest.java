package lol.maki.dev.store.cart.web;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lol.maki.dev.store.cart.Cart;
import lol.maki.dev.store.cart.CartItem;
import lol.maki.dev.store.cart.CartMapper;
import lol.maki.dev.store.item.client.ItemApi;
import lol.maki.dev.store.item.client.ItemResponse;
import lol.maki.dev.store.item.client.ItemResponse.MediaEnum;
import lol.maki.dev.store.stock.client.StockApi;
import lol.maki.dev.store.stock.client.StockResponse;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.IdGenerator;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ItemApi itemApi;

	@MockBean
	private StockApi stockApi;

	@MockBean
	private CartMapper cartMapper;

	@MockBean
	private IdGenerator idGenerator;

	private final ItemResponse item1 = new ItemResponse()
			.id(1L)
			.name("Item1")
			.author("demo1")
			.image(URI.create("https://example.com/item1.jpg"))
			.release(LocalDate.of(2020, 1, 1))
			.unitPrice(30.0)
			.media(MediaEnum.CD);

	private final ItemResponse item2 = new ItemResponse()
			.id(2L)
			.name("Item2")
			.author("demo2")
			.image(URI.create("https://example.com/item2.jpg"))
			.release(LocalDate.of(2020, 2, 1))
			.unitPrice(33.0)
			.media(MediaEnum.CD);

	private final StockResponse stock1 = new StockResponse()
			.itemId(1L)
			.quantity(10);

	private final StockResponse stock2 = new StockResponse()
			.itemId(2L)
			.quantity(10);

	private final UUID cartId1 = UUID.fromString("8e31709b-ab87-45a3-9a28-f3e636384989");

	private final Cart cart1 = Cart.create(() -> cartId1);

	@Test
	void addItem() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1));
		given(this.stockApi.getStocksByItemIds(List.of(1L))).willReturn(Flux.just(this.stock1));
		given(this.itemApi.getItemsIds(List.of(1L))).willReturn(Flux.just(this.item1));
		this.mockMvc.perform(post("/carts/{cartId}", this.cartId1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content("{\n"
						+ "  \"itemId\": 1,\n"
						+ "  \"quantity\": 1\n"
						+ "}"))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(30.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].itemId").value(1))
				.andExpect(jsonPath("$.items[0].name").value("Item1"))
				.andExpect(jsonPath("$.items[0].author").value("demo1"))
				.andExpect(jsonPath("$.items[0].image").value("https://example.com/item1.jpg"))
				.andExpect(jsonPath("$.items[0].release").value("2020-01-01"))
				.andExpect(jsonPath("$.items[0].unitPrice").value(30.0))
				.andExpect(jsonPath("$.items[0].quantity").value(1));
	}

	@Test
	void addItem_increase() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1.updateItem(new CartItem(1L, 1))));
		given(this.stockApi.getStocksByItemIds(List.of(1L))).willReturn(Flux.just(this.stock1));
		given(this.itemApi.getItemsIds(List.of(1L))).willReturn(Flux.just(this.item1));
		this.mockMvc.perform(post("/carts/{cartId}", this.cartId1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content("{\n"
						+ "  \"itemId\": 1,\n"
						+ "  \"quantity\": 1\n"
						+ "}"))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(60.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].itemId").value(1))
				.andExpect(jsonPath("$.items[0].name").value("Item1"))
				.andExpect(jsonPath("$.items[0].author").value("demo1"))
				.andExpect(jsonPath("$.items[0].image").value("https://example.com/item1.jpg"))
				.andExpect(jsonPath("$.items[0].release").value("2020-01-01"))
				.andExpect(jsonPath("$.items[0].unitPrice").value(30.0))
				.andExpect(jsonPath("$.items[0].quantity").value(2));
	}

	@Test
	void addItem_decrease() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1.updateItem(new CartItem(1L, 3))));
		given(this.stockApi.getStocksByItemIds(List.of(1L))).willReturn(Flux.just(this.stock1));
		given(this.itemApi.getItemsIds(List.of(1L))).willReturn(Flux.just(this.item1));
		this.mockMvc.perform(post("/carts/{cartId}", this.cartId1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content("{\n"
						+ "  \"itemId\": 1,\n"
						+ "  \"quantity\": -1\n"
						+ "}"))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(60.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].itemId").value(1))
				.andExpect(jsonPath("$.items[0].name").value("Item1"))
				.andExpect(jsonPath("$.items[0].author").value("demo1"))
				.andExpect(jsonPath("$.items[0].image").value("https://example.com/item1.jpg"))
				.andExpect(jsonPath("$.items[0].release").value("2020-01-01"))
				.andExpect(jsonPath("$.items[0].unitPrice").value(30.0))
				.andExpect(jsonPath("$.items[0].quantity").value(2));
	}

	@Test
	void addItem_decrease_to_0() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1.updateItem(new CartItem(1L, 3))));
		given(this.stockApi.getStocksByItemIds(List.of(1L))).willReturn(Flux.just(this.stock1));
		given(this.itemApi.getItemsIds(List.of(1L))).willReturn(Flux.just(this.item1));
		this.mockMvc.perform(post("/carts/{cartId}", this.cartId1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content("{\n"
						+ "  \"itemId\": 1,\n"
						+ "  \"quantity\": -3\n"
						+ "}"))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(0.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(0));
	}

	@Test
	void addItem_newItem() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1.updateItem(new CartItem(1L, 1))));
		given(this.stockApi.getStocksByItemIds(List.of(2L))).willReturn(Flux.just(this.stock2));
		given(this.itemApi.getItemsIds(List.of(1L, 2L))).willReturn(Flux.just(this.item1, this.item2));
		this.mockMvc.perform(post("/carts/{cartId}", this.cartId1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content("{\n"
						+ "  \"itemId\": 2,\n"
						+ "  \"quantity\": 1\n"
						+ "}"))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(63.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.items[0].itemId").value(1))
				.andExpect(jsonPath("$.items[0].name").value("Item1"))
				.andExpect(jsonPath("$.items[0].author").value("demo1"))
				.andExpect(jsonPath("$.items[0].image").value("https://example.com/item1.jpg"))
				.andExpect(jsonPath("$.items[0].release").value("2020-01-01"))
				.andExpect(jsonPath("$.items[0].unitPrice").value(30.0))
				.andExpect(jsonPath("$.items[0].quantity").value(1))
				.andExpect(jsonPath("$.items[1].itemId").value(2))
				.andExpect(jsonPath("$.items[1].name").value("Item2"))
				.andExpect(jsonPath("$.items[1].author").value("demo2"))
				.andExpect(jsonPath("$.items[1].image").value("https://example.com/item2.jpg"))
				.andExpect(jsonPath("$.items[1].release").value("2020-02-01"))
				.andExpect(jsonPath("$.items[1].unitPrice").value(33.0))
				.andExpect(jsonPath("$.items[1].quantity").value(1));
	}

	@Test
	void addItem_notEnoughStock() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1));
		given(this.stockApi.getStocksByItemIds(List.of(1L))).willReturn(Flux.just(this.stock1));
		this.mockMvc.perform(post("/carts/{cartId}", this.cartId1)
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content("{\n"
						+ "  \"itemId\": 1,\n"
						+ "  \"quantity\": 11\n"
						+ "}"))
				.andExpect(status().isConflict())
				.andExpect(status().reason("Not enough stocks (itemId=1, quantity=11)"))
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"));
	}

	@Test
	void createCart() throws Exception {
		given(this.idGenerator.generateId()).willReturn(this.cartId1);
		this.mockMvc.perform(post("/carts"))
				.andExpect(status().isCreated())
				.andExpect(header().string(LOCATION, String.format("http://localhost/carts/%s", this.cartId1)))
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(0.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(0));
	}

	@Test
	void getCartByCartId() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1
				.updateItem(new CartItem(1L, 1))
				.updateItem(new CartItem(2L, 2))));
		given(this.itemApi.getItemsIds(List.of(1L, 2L))).willReturn(Flux.just(this.item1, this.item2));
		this.mockMvc.perform(get("/carts/{cartId}", this.cartId1))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(96.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(2))
				.andExpect(jsonPath("$.items[0].itemId").value(1))
				.andExpect(jsonPath("$.items[0].name").value("Item1"))
				.andExpect(jsonPath("$.items[0].author").value("demo1"))
				.andExpect(jsonPath("$.items[0].image").value("https://example.com/item1.jpg"))
				.andExpect(jsonPath("$.items[0].release").value("2020-01-01"))
				.andExpect(jsonPath("$.items[0].unitPrice").value(30.0))
				.andExpect(jsonPath("$.items[0].quantity").value(1))
				.andExpect(jsonPath("$.items[1].itemId").value(2))
				.andExpect(jsonPath("$.items[1].name").value("Item2"))
				.andExpect(jsonPath("$.items[1].author").value("demo2"))
				.andExpect(jsonPath("$.items[1].image").value("https://example.com/item2.jpg"))
				.andExpect(jsonPath("$.items[1].release").value("2020-02-01"))
				.andExpect(jsonPath("$.items[1].unitPrice").value(33.0))
				.andExpect(jsonPath("$.items[1].quantity").value(2));
	}

	@Test
	void getCartByCartId_notFound() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.empty());
		this.mockMvc.perform(get("/carts/{cartId}", this.cartId1))
				.andExpect(status().isNotFound())
				.andExpect(status().reason(String.format("The requested cart is not found. (cartId = %s)", this.cartId1)))
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"));
	}

	@Test
	void removeItem() throws Exception {
		given(this.cartMapper.findById(this.cartId1)).willReturn(Optional.of(this.cart1
				.updateItem(new CartItem(1L, 1))
				.updateItem(new CartItem(2L, 2))));
		given(this.itemApi.getItemsIds(List.of(1L))).willReturn(Flux.just(this.item1));
		this.mockMvc.perform(delete("/carts/{cartId}/items/{itemId}", this.cartId1, 2L))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.cartId").value(this.cartId1.toString()))
				.andExpect(jsonPath("$.totalPrice").value(30.0))
				.andExpect(jsonPath("$.items").isArray())
				.andExpect(jsonPath("$.items.length()").value(1))
				.andExpect(jsonPath("$.items[0].itemId").value(1))
				.andExpect(jsonPath("$.items[0].name").value("Item1"))
				.andExpect(jsonPath("$.items[0].author").value("demo1"))
				.andExpect(jsonPath("$.items[0].image").value("https://example.com/item1.jpg"))
				.andExpect(jsonPath("$.items[0].release").value("2020-01-01"))
				.andExpect(jsonPath("$.items[0].unitPrice").value(30.0))
				.andExpect(jsonPath("$.items[0].quantity").value(1));
	}
}