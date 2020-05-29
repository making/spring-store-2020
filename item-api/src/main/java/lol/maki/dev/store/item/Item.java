package lol.maki.dev.store.item;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;

import org.springframework.util.Assert;

public class Item {
	private final Long id;

	private final String name;

	private final MediaType media;

	private final String auditor;

	private final BigDecimal unitPrice;

	private final LocalDate release;

	private final URI image;

	private final Instant createdAt;

	private final String createdBy;

	private final Instant updatedAt;

	private final String updatedBy;

	public Item(Long id, String name, MediaType media, String auditor, BigDecimal unitPrice, LocalDate release, URI image, Instant createdAt, String createdBy, Instant updatedAt, String updatedBy) {
		this.id = id;
		this.name = name;
		this.media = media;
		this.auditor = auditor;
		this.unitPrice = unitPrice;
		this.release = release;
		this.image = image;
		this.createdAt = createdAt;
		this.createdBy = createdBy;
		this.updatedAt = updatedAt;
		this.updatedBy = updatedBy;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public MediaType getMedia() {
		return media;
	}

	public String getAuditor() {
		return auditor;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public LocalDate getRelease() {
		return release;
	}

	public URI getImage() {
		return image;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public enum MediaType {
		CD, BLU_RAY;

		public static MediaType fromValue(String value) {
			Assert.notNull(value, "'value' must not be null");
			return MediaType.valueOf(value.toUpperCase().replace("-", "_"));
		}
	}
}
