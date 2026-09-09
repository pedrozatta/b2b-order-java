package br.com.zattaz.product;

import static br.com.zattaz.common.BasicAuthTestClientSupport.deleteResource;
import static br.com.zattaz.common.BasicAuthTestClientSupport.getJson;
import static br.com.zattaz.common.BasicAuthTestClientSupport.putJson;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.zattaz.common.trace.TraceId;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void upsertAndGetProduct() throws Exception {
        UUID id = UUID.randomUUID();
        String body =
                """
                {"name":"Headset","sku":"SKU-HS-%s","unitPrice":199.90}
                """
                        .formatted(id.toString().substring(0, 8));

        putJson(mockMvc, "/products/" + id, body)
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceId.HEADER))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Headset"));

        getJson(mockMvc, "/products/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sku").value("SKU-HS-" + id.toString().substring(0, 8)));
    }

    @Test
    void getReturnsNotFound() throws Exception {
        getJson(mockMvc, "/products/" + UUID.randomUUID()).andExpect(status().isNotFound());
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        String body =
                """
                {"name":"Cabo","sku":"SKU-C-%s","unitPrice":20.00}
                """
                        .formatted(id.toString().substring(0, 8));
        putJson(mockMvc, "/products/" + id, body).andExpect(status().isOk());
        deleteResource(mockMvc, "/products/" + id).andExpect(status().isNoContent());
        getJson(mockMvc, "/products/" + id).andExpect(status().isNotFound());
    }
}
