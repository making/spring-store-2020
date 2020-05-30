package lol.maki.dev.store.stock.web;

import java.util.List;
import java.util.stream.Collectors;

import lol.maki.dev.store.stock.Stock;
import lol.maki.dev.store.stock.StockBuilder;
import lol.maki.dev.store.stock.StockMapper;
import lol.maki.dev.store.stock.StockService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StockController.class)
class StockControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StockService stockService;

	@MockBean
	private StockMapper stockMapper;

	private final Stock stock1 = new StockBuilder()
			.withItemId(1L)
			.withQuantity(10)
			.build();

	private final Stock stock2 = new StockBuilder()
			.withItemId(2L)
			.withQuantity(20)
			.build();

	private final Stock stock3 = new StockBuilder()
			.withItemId(3L)
			.withQuantity(30)
			.build();

	@Test
	void getStocks() throws Exception {
		given(this.stockMapper.findAll()).willReturn(List.of(stock1, stock2, stock3));
		this.mockMvc.perform(get("/stocks"))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[0].itemId").value(1L))
				.andExpect(jsonPath("$[0].quantity").value(10))
				.andExpect(jsonPath("$[1].itemId").value(2L))
				.andExpect(jsonPath("$[1].quantity").value(20))
				.andExpect(jsonPath("$[2].itemId").value(3L))
				.andExpect(jsonPath("$[2].quantity").value(30))
		;
	}

	@Test
	void getStocksByItemIds() throws Exception {
		final List<Long> itemIds = List.of(1L, 3L);
		given(this.stockMapper.findByItemIds(itemIds)).willReturn(List.of(stock1, stock3));
		this.mockMvc.perform(get("/stocks/{itemIds}", itemIds.stream().map(Object::toString).collect(Collectors.joining(","))))
				.andExpect(status().isOk())
				.andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].itemId").value(1L))
				.andExpect(jsonPath("$[0].quantity").value(10))
				.andExpect(jsonPath("$[1].itemId").value(3L))
				.andExpect(jsonPath("$[1].quantity").value(30))
		;
	}

	@Test
	void postStocksKeep_ok() throws Exception {
		final Stock s1 = new StockBuilder().withItemId(1L).withQuantity(1).build();
		final Stock s2 = new StockBuilder().withItemId(2L).withQuantity(2).build();
		doNothing().when(this.stockService).keepStocks(List.of(s1, s2));
		this.mockMvc.perform(post("/stocks/keep")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[{\"itemId\":1,\"quantity\":1},{\"itemId\":2,\"quantity\":2}]\n"))
				.andExpect(status().isOk())
		;
	}


	@Test
	void postStocksKeep_conflict() throws Exception {
		final Stock s1 = new StockBuilder().withItemId(1L).withQuantity(1).build();
		final Stock s2 = new StockBuilder().withItemId(2L).withQuantity(2).build();
		doThrow(new StockService.StockNotEnoughException(s2)).when(this.stockService).keepStocks(List.of(s1, s2));
		this.mockMvc.perform(post("/stocks/keep")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[{\"itemId\":1,\"quantity\":1},{\"itemId\":2,\"quantity\":2}]\n"))
				.andExpect(status().isConflict())
				.andExpect(status().reason("Not enough stocks (itemId=2, quantity=2)"))
		;
	}
}