package lol.maki.dev.store.item.web;

import lol.maki.dev.store.item.Item;
import lol.maki.dev.store.item.ItemMapper;
import lol.maki.dev.store.item.spec.ItemResponse;
import lol.maki.dev.store.item.spec.ItemsApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class ItemController implements ItemsApi {
    private final ItemMapper itemMapper;

    public ItemController(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public ResponseEntity<List<ItemResponse>> getItems() {
        final List<Item> items = this.itemMapper.findAll();
        return ResponseEntity.ok(items.stream().map(this::toResponse).collect(Collectors.toUnmodifiableList()));
    }

    @Override
    public ResponseEntity<List<ItemResponse>> getItemsIds(List<Long> ids) {
        final List<Item> items = this.itemMapper.findByIds(ids);
        return ResponseEntity.ok(items.stream().map(this::toResponse).collect(Collectors.toUnmodifiableList()));
    }

    ItemResponse toResponse(Item item) {
        return new ItemResponse()
                .id(item.getId())
                .name(item.getName())
                .media(ItemResponse.MediaEnum.valueOf(item.getMedia().name()))
                .author(item.getAuditor())
                .unitPrice(item.getUnitPrice().doubleValue())
                .image(item.getImage())
                .release(item.getRelease());
    }
}
