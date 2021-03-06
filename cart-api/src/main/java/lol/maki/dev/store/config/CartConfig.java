package lol.maki.dev.store.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lol.maki.dev.store.item.client.ItemApi;
import lol.maki.dev.store.stock.client.StockApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration(proxyBeanMethods = false)
public class CartConfig {
	private final CartProps cartProps;

	public CartConfig(CartProps cartProps) {
		this.cartProps = cartProps;
	}

	@Bean
	public IdGenerator idGenerator() {
		return new JdkIdGenerator();
	}

	@Bean
	public ItemApi itemApi(WebClient.Builder builder, ObjectMapper objectMapper) {
		return new ItemApi(new lol.maki.dev.store.item.client.ApiClient(builder.build(), objectMapper, StdDateFormat.instance)
				.setBasePath(this.cartProps.getItemUrl()));
	}

	@Bean
	public StockApi stockApi(WebClient.Builder builder, ObjectMapper objectMapper) {
		return new StockApi(new lol.maki.dev.store.stock.client.ApiClient(builder.build(), objectMapper, StdDateFormat.instance)
				.setBasePath(this.cartProps.getStockUrl()));
	}
}
