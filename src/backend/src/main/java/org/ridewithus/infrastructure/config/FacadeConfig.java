package org.ridewithus.infrastructure.config;

import org.ridewithus.domain.facade.AuthenticationFacade;
import org.ridewithus.domain.facade.impl.AuthenticationFacadeImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FacadeConfig {
    
    @Bean
    public AuthenticationFacade authenticationFacade() {
        return new AuthenticationFacadeImpl();
    }
}
