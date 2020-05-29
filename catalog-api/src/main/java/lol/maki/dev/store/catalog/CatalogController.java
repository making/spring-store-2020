package lol.maki.dev.store.catalog;

import java.util.List;

import lol.maki.dev.store.catalog.spec.CatalogResponse;
import lol.maki.dev.store.catalog.spec.CatalogsApi;
import lol.maki.dev.store.item.client.ItemApi;
import lol.maki.dev.store.item.client.ItemResponse;
import lol.maki.dev.store.stock.client.StockApi;
import lol.maki.dev.store.stock.client.StockResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

@RestController
@CrossOrigin
public class CatalogController implements CatalogsApi {
	private final ItemApi itemApi;

	private final StockApi stockApi;

	public CatalogController(ItemApi itemApi, StockApi stockApi) {
		this.itemApi = itemApi;
		this.stockApi = stockApi;
	}

	@Override
	public Mono<ResponseEntity<CatalogResponse>> getCatalogByItemId(Long itemId, ServerWebExchange exchange) {
		final Mono<ItemResponse> itemMono = Mono.from(this.itemApi.getItemsIds(List.of(itemId)));
		final Mono<StockResponse> stockMono = Mono.from(this.stockApi.getStocksByItemIds(List.of(itemId)));
		final Mono<CatalogResponse> catalogMono = itemMono.zipWith(stockMono)
				.map(tpl -> this.zipItemAndStock(tpl.getT1(), tpl.getT2()));
		return catalogMono.map(ResponseEntity::ok)
				.switchIfEmpty(Mono.error(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("The requested item is not found. (itemId = %d)", itemId))));
	}

	@Override
	public Mono<ResponseEntity<Flux<CatalogResponse>>> getCatalogs(ServerWebExchange exchange) {
		final Flux<ItemResponse> itemFlux = this.itemApi.getItems();
		final Flux<StockResponse> stockResponseFlux = itemFlux.map(ItemResponse::getId).collectList().flatMapMany(this.stockApi::getStocksByItemIds);
		final Flux<CatalogResponse> catalogFlux = itemFlux.zipWith(stockResponseFlux)
				.map(tpl -> this.zipItemAndStock(tpl.getT1(), tpl.getT2()));
		return Mono.just(ResponseEntity.ok(catalogFlux));
	}

	CatalogResponse zipItemAndStock(ItemResponse item, StockResponse stock) {
		return new CatalogResponse()
				.id(item.getId())
				.name(item.getName())
				.media(item.getMedia().getValue())
				.author(item.getAuthor())
				.unitPrice(item.getUnitPrice())
				.release(item.getRelease())
				.image(item.getImage())
				.stock(stock.getQuantity());
	}
}
