package com.pagamento.configuracao;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoOpenApi {

    @Bean
    public OpenAPI openAPIPersonalizado() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Pagamentos")
                        .description("API REST para gerenciamento de pagamentos de débitos de pessoas físicas e jurídicas")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe API Pagamentos")
                                .email("contato@apipagamentos.com"))
                        .license(new License()
                                .name("Apache License Version 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }
}
