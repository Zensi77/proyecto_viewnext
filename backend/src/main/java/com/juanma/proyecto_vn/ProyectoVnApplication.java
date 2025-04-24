package com.juanma.proyecto_vn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
@EnableAspectJAutoProxy // Habilita el uso de AOP (Aspect Oriented Programming) en la aplicación
public class ProyectoVnApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoVnApplication.class, args);
	}

	@Bean // Se crea un bean para configurar el CORS
	WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE").allowedHeaders("*");
			}
		};
	}

	@Bean
	public OpenAPI apiInfo() {
		return new OpenAPI()
				.info(new Info()
						.title("API de Tienda de Productos de informatica")
						.description("Documentación de la API para la tienda de productos de informatica")
						.version("1.0.0"));
	}
}
