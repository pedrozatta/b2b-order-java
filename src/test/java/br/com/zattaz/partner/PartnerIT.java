package br.com.zattaz.partner;

import static br.com.zattaz.common.BasicAuthTestClientSupport.getJson;
import static br.com.zattaz.common.BasicAuthTestClientSupport.putJson;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PartnerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void upsertAndGetPartnerWithCredits() throws Exception {
        UUID id = UUID.randomUUID();
        String body =
                """
                {"name":"Partner Gamma","availableCredit":2500.00}
                """;

        putJson(mockMvc, "/partners/" + id, body)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.availableCredit").value(2500.00));

        getJson(mockMvc, "/partners/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Partner Gamma"));
    }

    @Test
    void getSeedPartner() throws Exception {
        getJson(mockMvc, "/partners/11111111-1111-1111-1111-111111111111")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Partner Alpha"));
    }

    @Test
    void listPartnersReturnsPaginatedResponse() throws Exception {
        getJson(mockMvc, "/partners?page=0&size=10")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10));
    }
}
