// src/main/java/com/tuapp/config/InfinispanConfig.java
package com.juanma.proyecto_vn.config;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.infinispan.protostream.SerializationContext;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.juanma.proyecto_vn.serialization.ProductoSchemaInitializer;
import com.juanma.proyecto_vn.serialization.ProductoSchemaInitializerImpl;

@Configuration
@EnableCaching // Activa @Cacheable
public class InfinispanConfig {

    @Bean
    public RemoteCacheManager remoteCacheManager(ProductoSchemaInitializer schema) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
                // Por defecto, el cliente de Infinispan usa la IP que le da el servidor, por lo
                // que no es necesario
                // Al estar en docker se le pone BASIC para que no use la IP del servidor
                .clientIntelligence(ClientIntelligence.BASIC)
                .addServer().host("localhost").port(11222)
                .security().authentication()
                .enabled(true)
                .saslMechanism("SCRAM-SHA-512")
                .username("admin123")
                .password("admin123").marshaller(new ProtoStreamMarshaller());

        RemoteCacheManager rcm = new RemoteCacheManager(builder.build());

        SerializationContext ctx = MarshallerUtil.getSerializationContext(rcm);
        schema.registerSchema(ctx); // Registra Producto, Categoria, Proveedor, UUID en el contexto de serialización
        schema.registerMarshallers(ctx); // Registra los Marshallers en el contexto de serialización
        return rcm;
    }

    @Bean
    public SpringRemoteCacheManager cacheManager(RemoteCacheManager rcm) {
        return new SpringRemoteCacheManager(rcm);
    }

    @Bean
    public ProductoSchemaInitializer productoSchemaInitializer() {
        return new ProductoSchemaInitializerImpl();
    }
}
