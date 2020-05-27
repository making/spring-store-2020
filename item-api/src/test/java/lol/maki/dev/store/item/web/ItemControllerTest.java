package lol.maki.dev.store.item.web;

import lol.maki.dev.store.item.Item;
import lol.maki.dev.store.item.ItemBuilder;
import lol.maki.dev.store.item.ItemMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.atlassian.oai.validator.mockmvc.OpenApiValidationMatchers.openApi;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemMapper itemMapper;

    private final Item item1 = new ItemBuilder()
            .withId(1L)
            .withName("CD1")
            .withMedia(Item.MediaType.CD)
            .withAuditor("making")
            .withImage(URI.create("https://example.com/cd1.jpg"))
            .withUnitPrice(BigDecimal.valueOf(100))
            .withRelease(LocalDate.of(2020, 1, 1))
            .build();
    private final Item item2 = new ItemBuilder()
            .withId(2L)
            .withName("CD2")
            .withMedia(Item.MediaType.CD)
            .withAuditor("making")
            .withImage(URI.create("https://example.com/cd2.jpg"))
            .withUnitPrice(BigDecimal.valueOf(150))
            .withRelease(LocalDate.of(2020, 2, 1))
            .build();
    private final Item item3 = new ItemBuilder()
            .withId(3L)
            .withName("BR1")
            .withMedia(Item.MediaType.BLU_RAY)
            .withAuditor("making")
            .withImage(URI.create("https://example.com/br1.jpg"))
            .withUnitPrice(BigDecimal.valueOf(200))
            .withRelease(LocalDate.of(2020, 3, 1))
            .build();

    @Test
    void getItems() throws Exception {
        given(this.itemMapper.findAll()).willReturn(List.of(item1, item2, item3));
        this.mockMvc.perform(get("/items"))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("CD1"))
                .andExpect(jsonPath("$[0].media").value("CD"))
                .andExpect(jsonPath("$[0].author").value("making"))
                .andExpect(jsonPath("$[0].image").value("https://example.com/cd1.jpg"))
                .andExpect(jsonPath("$[0].unitPrice").value(100.0))
                .andExpect(jsonPath("$[0].release").value("2020-01-01"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("CD2"))
                .andExpect(jsonPath("$[1].media").value("CD"))
                .andExpect(jsonPath("$[1].author").value("making"))
                .andExpect(jsonPath("$[1].image").value("https://example.com/cd2.jpg"))
                .andExpect(jsonPath("$[1].unitPrice").value(150.0))
                .andExpect(jsonPath("$[1].release").value("2020-02-01"))
                .andExpect(jsonPath("$[2].id").value(3L))
                .andExpect(jsonPath("$[2].name").value("BR1"))
                .andExpect(jsonPath("$[2].media").value("Blu-ray"))
                .andExpect(jsonPath("$[2].author").value("making"))
                .andExpect(jsonPath("$[2].image").value("https://example.com/br1.jpg"))
                .andExpect(jsonPath("$[2].unitPrice").value(200.0))
                .andExpect(jsonPath("$[2].release").value("2020-03-01"))
        ;
    }

    @Test
    void getItemsIds() throws Exception {
        final List<Long> ids = List.of(1L, 3L);
        given(this.itemMapper.findByIds(ids)).willReturn(List.of(item1, item3));
        this.mockMvc.perform(get("/items/{ids}", ids.stream().map(Object::toString).collect(Collectors.joining(","))))
                .andExpect(status().isOk())
                .andExpect(openApi().isValid("META-INF/resources/openapi/doc.yml"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("CD1"))
                .andExpect(jsonPath("$[0].media").value("CD"))
                .andExpect(jsonPath("$[0].author").value("making"))
                .andExpect(jsonPath("$[0].image").value("https://example.com/cd1.jpg"))
                .andExpect(jsonPath("$[0].unitPrice").value(100.0))
                .andExpect(jsonPath("$[0].release").value("2020-01-01"))
                .andExpect(jsonPath("$[1].id").value(3L))
                .andExpect(jsonPath("$[1].name").value("BR1"))
                .andExpect(jsonPath("$[1].media").value("Blu-ray"))
                .andExpect(jsonPath("$[1].author").value("making"))
                .andExpect(jsonPath("$[1].image").value("https://example.com/br1.jpg"))
                .andExpect(jsonPath("$[1].unitPrice").value(200.0))
                .andExpect(jsonPath("$[1].release").value("2020-03-01"))
        ;
    }
}