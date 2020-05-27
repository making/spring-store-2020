package lol.maki.dev.store.stock.web;

import lol.maki.dev.store.stock.Stock;
import lol.maki.dev.store.stock.StockBuilder;
import lol.maki.dev.store.stock.StockMapper;
import lol.maki.dev.store.stock.StockService;
import lol.maki.dev.store.stock.spec.StockRequest;
import lol.maki.dev.store.stock.spec.StockResponse;
import lol.maki.dev.store.stock.spec.StocksApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class StockController implements StocksApi {
    private final StockMapper stockMapper;
    private final StockService stockService;

    public StockController(StockMapper stockMapper, StockService stockService) {
        this.stockMapper = stockMapper;
        this.stockService = stockService;
    }

    @Override
    public ResponseEntity<List<StockResponse>> getStocks() {
        final List<Stock> stocks = this.stockMapper.findAll();
        return ResponseEntity.ok(stocks.stream().map(this::toResponse).collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public ResponseEntity<List<StockResponse>> getStocksByItemIds(List<Long> itemIds) {
        final List<Stock> stocks = this.stockMapper.findByItemIds(itemIds);
        return ResponseEntity.ok(stocks.stream().map(this::toResponse).collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public ResponseEntity<Void> postStocksKeep(List<StockRequest> stockRequest) {
        final List<Stock> stocks = stockRequest.stream().map(this::fromRequest).collect(Collectors.toUnmodifiableList());
        try {
            this.stockService.keepStocks(stocks);
        } catch (StockService.StockNotEnoughException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    Stock fromRequest(StockRequest request) {
        return new StockBuilder()
                .withItemId(request.getItemId())
                .withQuantity(request.getQuantity())
                .build();
    }

    StockResponse toResponse(Stock stock) {
        return new StockResponse()
                .itemId(stock.getItemId())
                .quantity(stock.getQuantity());
    }

}
