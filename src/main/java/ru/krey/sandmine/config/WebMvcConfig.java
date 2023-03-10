package ru.krey.sandmine.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "ru.krey.sandmine")
public class WebMvcConfig implements WebMvcConfigurer {
    @Bean
    WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerCustomizer(){
        return factory -> factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,"/"));
    }
}
