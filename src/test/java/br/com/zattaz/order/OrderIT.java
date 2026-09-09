package br.com.zattaz.order;

import static br.com.zattaz.common.BasicAuthTestClientSupport.getJson;
import static br.com.zattaz.common.BasicAuthTestClientSupport.postEmpty;
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
class OrderIT {

    private static final String PARTNER_ID = "11111111-1111-1111-1111-111111111111";
    private static final String PRODUCT_ID = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createApproveAndCancelOrderWithCreditFlow() throws Exception {
        UUID orderId = UUID.randomUUID();
        String body =
                """
                {
                  "partnerId": "%s",
                  "items": [{"productId": "%s", "quantity": 1}]
                }
                """
                        .formatted(PARTNER_ID, PRODUCT_ID);

        putJson(mockMvc, "/orders/" + orderId, body)
                .andExpect(status().isOk())
                .andExpect(header().exists(TraceId.HEADER))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(3500.00));

        postEmpty(mockMvc, "/orders/" + orderId + "/approve")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));

        getJson(mockMvc, "/partners/" + PARTNER_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCredit").value(6500.00));

        postEmpty(mockMvc, "/orders/" + orderId + "/cancel")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        getJson(mockMvc, "/partners/" + PARTNER_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableCredit").value(10000.00));
    }

    @Test
    void createReturnsValidationErrorForEmptyItems() throws Exception {
        UUID orderId = UUID.randomUUID();
        String body =
                """
                {"partnerId": "%s", "items": []}
                """
                        .formatted(PARTNER_ID);

        putJson(mockMvc, "/orders/" + orderId, body).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void rejectsInsufficientCredit() throws Exception {
        UUID orderId = UUID.randomUUID();
        String body =
                """
                {
                  "partnerId": "22222222-2222-2222-2222-222222222222",
                  "items": [
                    {"productId": "%s", "quantity": 1}
                  ]
                }
                """
                        .formatted(PRODUCT_ID);

        putJson(mockMvc, "/orders/" + orderId, body).andExpect(status().isBadRequest());
    }
}
