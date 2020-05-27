package lol.maki.dev.store.stock;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.BDDMockito.given;

class StockServiceTest {
    private final StockMapper stockMapper = Mockito.mock(StockMapper.class);
    private final Stock stock1 = new StockBuilder().withItemId(1L).withQuantity(1).build();
    private final Stock stock2 = new StockBuilder().withItemId(2L).withQuantity(2).build();

    @Test
    void keepStocks() {
        final StockService stockService = new StockService(this.stockMapper);
        given(this.stockMapper.subtractIfPossible(stock1)).willReturn(9);
        given(this.stockMapper.subtractIfPossible(stock2)).willReturn(18);
        stockService.keepStocks(List.of(stock1, stock2));
    }

    @Test
    void keepStocks_notEnough() {
        final StockService stockService = new StockService(this.stockMapper);
        given(this.stockMapper.subtractIfPossible(stock1)).willReturn(9);
        given(this.stockMapper.subtractIfPossible(stock2)).willReturn(0);
        Assertions.assertThatThrownBy(() -> stockService.keepStocks(List.of(stock1, stock2)))
                .isInstanceOf(StockService.StockNotEnoughException.class)
                .hasMessageContaining("Not enough stocks (itemId=2, quantity=2)");

    }
}