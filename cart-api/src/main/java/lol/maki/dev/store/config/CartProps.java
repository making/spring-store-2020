package lol.maki.dev.store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "store")
public class CartProps {
	private final String itemUrl;
	private final String stockUrl;

	@ConstructorBinding
	public CartProps(String itemUrl, String stockUrl) {
		this.itemUrl = itemUrl;
		this.stockUrl = stockUrl;
	}

	public String getItemUrl() {
		return itemUrl;
	}

	public String getStockUrl() {
		return stockUrl;
	}
}
