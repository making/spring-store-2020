package lol.maki.dev.store.item;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;

public class ItemBuilder {
	private Long id;

	private String name;

	private Item.MediaType media;

	private String auditor;

	private BigDecimal unitPrice;

	private LocalDate release;

	private URI image;

	private Instant createdAt;

	private String createdBy;

	private Instant updatedAt;

	private String updatedBy;

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

	public ItemBuilder withCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public ItemBuilder withCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public ItemBuilder withUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}

	public ItemBuilder withUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
		return this;
	}

	public Item build() {
		return new Item(id, name, media, auditor, unitPrice, release, image, createdAt, createdBy, updatedAt, updatedBy);
	}
}