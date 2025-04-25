package com.juanma.proyecto_vn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@SpringBootApplication
@EnableAspectJAutoProxy // Habilita el uso de AOP (Aspect Oriented Programming) en la aplicación
public class ProyectoVnApplication {
	// Configuración de logger con SLF4J
	private static final Logger logger = LoggerFactory.getLogger(ProyectoVnApplication.class);

	@Value("${spring.profiles.active:default}")
	private String activeProfile;

	@Value("${server.port:8080}")
	private String serverPort;

	@Value("${cors.allowed-origins:http://localhost:4200}")
	private String[] allowedOrigins;

	public static void main(String[] args) {
		logger.info("Iniciando aplicación Proyecto ViewNext...");
		try {
			SpringApplication.run(ProyectoVnApplication.class, args);
		} catch (Exception e) {
			logger.error("Error durante el arranque de la aplicación: {}", e.getMessage(), e);
			throw e;
		}
	}

	@EventListener(ApplicationStartedEvent.class)
	public void onApplicationStarted() {
		logger.info("Aplicación iniciada con perfil: {}", activeProfile);
		logger.info("Servidor escuchando en puerto: {}", serverPort);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		logger.info("Aplicación lista para recibir peticiones");

		// Log información sobre CORS para facilitar la depuración
		StringBuilder corsConfig = new StringBuilder("Configuración CORS: Orígenes permitidos: ");
		for (String origin : allowedOrigins) {
			corsConfig.append(origin).append(", ");
		}
		logger.info(corsConfig.toString());
	}

	@EventListener(ApplicationFailedEvent.class)
	public void onApplicationFailed(ApplicationFailedEvent event) {
		logger.error("La aplicación no pudo iniciarse correctamente: {}",
				event.getException().getMessage(), event.getException());
	}

	@Bean // Se crea un bean para configurar el CORS
	WebMvcConfigurer corsConfigurer() {
		logger.info("Configurando CORS para la aplicación");
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				// Configuración más segura utilizando orígenes específicos
				registry.addMapping("/**")
						.allowedOrigins(allowedOrigins)
						.allowedMethods("GET", "POST", "PUT", "DELETE")
						.allowedHeaders("Authorization", "Content-Type", "Accept")
						.allowCredentials(true)
						.maxAge(3600); // 1 hora de caché para pre-flight

				logger.info("Configuración CORS aplicada: {} orígenes permitidos", allowedOrigins.length);
			}
		};
	}

	@Bean
	OpenAPI apiInfo() {
		logger.info("Configurando documentación OpenAPI");
		OpenAPI openAPI = new OpenAPI()
				.info(new Info()
						.title("API de Tienda de Productos de Informática")
						.description("Documentación de la API para la tienda de productos de informática")
						.version("1.0.0"));

		logger.debug("Documentación OpenAPI generada correctamente");
		return openAPI;
	}
}