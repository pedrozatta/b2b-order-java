package br.com.zattaz.common.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI openAPI() {
        final String schemeName = "basicAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("B2B Order API")
                        .description("API REST de gestão de pedidos B2B")
                        .version("v1"))
                .servers(List.of(new Server().url("/").description("Host atual")))
                .components(new Components()
                        .addSecuritySchemes(
                                schemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName));
    }
}
