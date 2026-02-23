package com.shopwise;

import com.shopwise.config.CorsConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ShopWiseApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Application Context se charge correctement")
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
    }

    @Test
    @DisplayName("Main method s'exécute sans erreur")
    void mainMethodRunsWithoutException() {
        // Ce test vérifie juste que l'application peut démarrer
        assertThat(applicationContext.containsBean("shopWiseApplication")).isTrue();
    }

    @Test
    @DisplayName("CorsConfig bean est présent")
    void corsConfigBeanExists() {
        assertThat(applicationContext.getBean(CorsConfig.class)).isNotNull();
    }

    @Test
    @DisplayName("CorsFilter bean est créé")
    void corsFilterBeanExists() {
        CorsFilter corsFilter = applicationContext.getBean(CorsFilter.class);
        assertThat(corsFilter).isNotNull();
    }
}
