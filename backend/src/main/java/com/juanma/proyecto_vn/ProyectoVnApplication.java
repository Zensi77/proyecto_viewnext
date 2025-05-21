package com.juanma.proyecto_vn;

import com.juanma.proyecto_vn.infrastructure.persistence.entity.Role;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.RoleType;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.UUID;

/**
 * Clase principal de arranque de la aplicación.
 */
@SpringBootApplication
@EnableAspectJAutoProxy // Habilita el soporte para AspectJ
@EnableJms // Habilita la configuración de JMS(Java Message Service)
public class ProyectoVnApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(ProyectoVnApplication.class);

	@Autowired
	private Environment env;

	@Autowired
	private JpaRoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		if(roleRepository.findByAuthority(RoleType.ROLE_ADMIN) == null) {
			roleRepository.save(Role.builder().authority(RoleType.ROLE_ADMIN).build());
			log.info("Rol ROLE_ADMIN creado");
		}
		if(roleRepository.findByAuthority(RoleType.ROLE_USER) == null) {
			roleRepository.save(Role.builder().authority(RoleType.ROLE_USER).build());
			log.info("Rol ROLE_USER creado");
		}
	}

	public static void main(String[] args) {
		// Desactivar banner y construir la aplicación
		ApplicationContext ctx = new SpringApplicationBuilder(ProyectoVnApplication.class)
				.bannerMode(Banner.Mode.OFF)
				.listeners() // Los listeners son objetos que se ejecutan en ciertos eventos del ciclo de
								// vida de la aplicación
				.build()
				.run(args);

		String appName = ctx.getEnvironment().getProperty("spring.application.name");
		String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();
		log.info("Aplicacion '{}' iniciada con perfiles: {}", appName, Arrays.toString(activeProfiles));
	}

	/**
	 * Método que se ejecuta justo después de arrancar el contexto.
	 * Captura Environment inyectado sin parámetros en el PostConstruct.
	 */
	@PostConstruct
	public void logStartupInfo() {
		String port = env.getProperty("server.port");
		String[] corsOrigins = env.getProperty("cors.allowed-origins", String[].class, new String[] {});

		log.info("Servidor escuchando en puerto: {}", port);
		log.info("Origenes CORS permitidos: {}", String.join(", ", corsOrigins));
		log.info("Instance requestId: {}", UUID.randomUUID());
	}

	/**
	 * Configura CORS de forma centralizada usando valores del Environment.
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		String[] origins = env.getProperty("cors.allowed-origins", String[].class, new String[] {});
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
						.allowedOrigins(origins)
						.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
						.allowedHeaders("*")
						.allowCredentials(true)
						.maxAge(3600);
				log.debug("CORS configurado con origenes: {}", Arrays.toString(origins));
			}
		};
	}

	/**
	 * Configuración de OpenAPI para documentación Swagger.
	 */
	@Bean
	public OpenAPI apiInfo() {
		String appName = env.getProperty("spring.application.name", "API");
		String version = env.getProperty("api.version", "1.0.0");
		OpenAPI openAPI = new OpenAPI()
				.info(new Info()
						.title(appName + " Documentation")
						.version(version)
						.description("Documentacion de la API de " + appName));
		log.debug("OpenAPI configurado: {} v{}", appName, version);
		return openAPI;
	}
}
