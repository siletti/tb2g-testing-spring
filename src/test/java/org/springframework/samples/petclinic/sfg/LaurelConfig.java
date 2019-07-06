package org.springframework.samples.petclinic.sfg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("base-test")
@Configuration
@Primary
public class LaurelConfig {
    @Bean
    LaurelWordProducer laurelWordProducer() {
        return new LaurelWordProducer();
    }

}
