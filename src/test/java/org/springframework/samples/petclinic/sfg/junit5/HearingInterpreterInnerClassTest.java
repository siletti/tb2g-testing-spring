package org.springframework.samples.petclinic.sfg.junit5;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.petclinic.sfg.HearingInterpreter;
import org.springframework.samples.petclinic.sfg.YannyWordProducer;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig(HearingInterpreterInnerClassTest.InnerConfig.class)
class HearingInterpreterInnerClassTest {

    @Autowired
    HearingInterpreter hearingInterpreter;

    @Test
    void whatIHeard() {
        String word = hearingInterpreter.WhatIHeard();
        assertEquals(word, "Yanny");
    }

    @Configuration
    static class InnerConfig {
        @Bean
        HearingInterpreter hearingInterpreter() {
            return new HearingInterpreter(new YannyWordProducer());
        }

    }
}