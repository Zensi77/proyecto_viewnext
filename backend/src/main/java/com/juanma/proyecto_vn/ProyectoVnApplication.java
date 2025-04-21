package com.juanma.proyecto_vn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableAspectJAutoProxy // Habilita el uso de AOP (Aspect Oriented Programming) en la aplicación
public class ProyectoVnApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoVnApplication.class, args);
	}

	@Bean // Se crea un bean para configurar el CORS
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("*")
						.allowedMethods("GET", "POST", "PUT", "DELETE").allowedHeaders("*");
			}
		};
	}
}
