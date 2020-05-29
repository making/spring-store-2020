package lol.maki.dev.store.stock;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
	private final StockMapper stockMapper;

	public StockService(StockMapper stockMapper) {
		this.stockMapper = stockMapper;
	}

	@Transactional
	public void keepStocks(List<Stock> stocks) {
		stocks.forEach(stock -> {
			final int count = this.stockMapper.subtractIfPossible(stock);
			if (count == 0) {
				throw new StockNotEnoughException(stock);
			}
		});
	}

	public static class StockNotEnoughException extends RuntimeException {
		public StockNotEnoughException(Stock stock) {
			super(String.format("Not enough stocks (itemId=%d, quantity=%d)", stock.getItemId(), stock.getQuantity()));
		}
	}
}
