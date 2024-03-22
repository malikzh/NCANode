package kz.ncanode.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://magnum-ashen.vercel.app", "http://localhost:3000", "https://cors-test.codehappy.dev", "http://89.46.34.50")
                        .allowedMethods("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS") 
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
