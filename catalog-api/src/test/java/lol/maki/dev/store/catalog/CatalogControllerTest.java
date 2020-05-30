package lol.maki.dev.store.catalog;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import lol.maki.dev.store.catalog.web.CatalogController;
import lol.maki.dev.store.item.client.ItemApi;
import lol.maki.dev.store.item.client.ItemResponse;
import lol.maki.dev.store.stock.client.StockApi;
import lol.maki.dev.store.stock.client.StockResponse;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.BDDMockito.given;

@WebFluxTest(CatalogController.class)
class CatalogControllerTest {
	@MockBean
	private ItemApi itemApi;

	@MockBean
	private StockApi stockApi;

	@Autowired
	private WebTestClient webClient;

	private final ItemResponse item1 = new ItemResponse()
			.id(1L)
			.name("CD1")
			.media(ItemResponse.MediaEnum.CD)
			.author("making")
			.image(URI.create("https://example.com/cd1.jpg"))
			.unitPrice(100.0)
			.release(LocalDate.of(2020, 1, 1));

	private final ItemResponse item2 = new ItemResponse()
			.id(2L)
			.name("CD2")
			.media(ItemResponse.MediaEnum.CD)
			.author("making")
			.image(URI.create("https://example.com/cd2.jpg"))
			.unitPrice(150.0)
			.release(LocalDate.of(2020, 2, 1));

	private final ItemResponse item3 = new ItemResponse()
			.id(3L)
			.name("BR1")
			.media(ItemResponse.MediaEnum.BLU_RAY)
			.author("making")
			.image(URI.create("https://example.com/br1.jpg"))
			.unitPrice(200.0)
			.release(LocalDate.of(2020, 3, 1));

	private final StockResponse stock1 = new StockResponse().itemId(1L).quantity(10);

	private final StockResponse stock2 = new StockResponse().itemId(2L).quantity(20);

	private final StockResponse stock3 = new StockResponse().itemId(3L).quantity(30);

	@Test
	void getCatalogByItemId() {
		given(this.itemApi.getItemsIds(List.of(2L))).willReturn(Flux.just(item2));
		given(this.stockApi.getStocksByItemIds(List.of(2L))).willReturn(Flux.just(stock2));
		this.webClient.get().uri("/catalogs/{itemId}", 2L)
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(2L)
				.jsonPath("$.name").isEqualTo("CD2")
				.jsonPath("$.media").isEqualTo("CD")
				.jsonPath("$.author").isEqualTo("making")
				.jsonPath("$.image").isEqualTo("https://example.com/cd2.jpg")
				.jsonPath("$.unitPrice").isEqualTo(150.0)
				.jsonPath("$.release").isEqualTo("2020-02-01");
	}

	@Test
	void getCatalogs() {
		given(this.itemApi.getItems()).willReturn(Flux.just(item1, item2, item3));
		given(this.stockApi.getStocksByItemIds(List.of(item1.getId(), item2.getId(), item3.getId()))).willReturn(Flux.just(stock1, stock2, stock3));
		this.webClient.get().uri("/catalogs")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[0].id").isEqualTo(1L)
				.jsonPath("$[0].name").isEqualTo("CD1")
				.jsonPath("$[0].media").isEqualTo("CD")
				.jsonPath("$[0].author").isEqualTo("making")
				.jsonPath("$[0].image").isEqualTo("https://example.com/cd1.jpg")
				.jsonPath("$[0].unitPrice").isEqualTo(100.0)
				.jsonPath("$[0].release").isEqualTo("2020-01-01")
				.jsonPath("$[1].id").isEqualTo(2L)
				.jsonPath("$[1].name").isEqualTo("CD2")
				.jsonPath("$[1].media").isEqualTo("CD")
				.jsonPath("$[1].author").isEqualTo("making")
				.jsonPath("$[1].image").isEqualTo("https://example.com/cd2.jpg")
				.jsonPath("$[1].unitPrice").isEqualTo(150.0)
				.jsonPath("$[1].release").isEqualTo("2020-02-01")
				.jsonPath("$[2].id").isEqualTo(3L)
				.jsonPath("$[2].name").isEqualTo("BR1")
				.jsonPath("$[2].media").isEqualTo("Blu-ray")
				.jsonPath("$[2].author").isEqualTo("making")
				.jsonPath("$[2].image").isEqualTo("https://example.com/br1.jpg")
				.jsonPath("$[2].unitPrice").isEqualTo(200.0)
				.jsonPath("$[2].release").isEqualTo("2020-03-01");
	}
}