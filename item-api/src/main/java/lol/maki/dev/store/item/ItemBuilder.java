package lol.maki.dev.store.item;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

public class ItemBuilder {
    private Long id;
    private String name;
    private Item.MediaType media;
    private String auditor;
    private BigDecimal unitPrice;
    private LocalDate release;
    private URI image;

    public ItemBuilder withId(Long id) {
        this.id = id;
        return this;
    }

    public ItemBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder withMedia(Item.MediaType media) {
        this.media = media;
        return this;
    }

    public ItemBuilder withAuditor(String auditor) {
        this.auditor = auditor;
        return this;
    }

    public ItemBuilder withUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        return this;
    }

    public ItemBuilder withRelease(LocalDate release) {
        this.release = release;
        return this;
    }

    public ItemBuilder withImage(URI image) {
        this.image = image;
        return this;
    }

    public Item build() {
        return new Item(id, name, media, auditor, unitPrice, release, image);
    }
}