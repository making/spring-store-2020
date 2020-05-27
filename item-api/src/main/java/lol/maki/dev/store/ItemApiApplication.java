package lol.maki.dev.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class ItemApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItemApiApplication.class, args);
    }

}
