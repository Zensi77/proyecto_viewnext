// src/main/java/com/tuapp/config/InfinispanConfig.java
package com.juanma.proyecto_vn.infrastructure.config;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ClientIntelligence;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.infinispan.protostream.SerializationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.juanma.proyecto_vn.infrastructure.cache.ProductProtoSchemaInitializer;
import com.juanma.proyecto_vn.infrastructure.cache.ProductProtoSchemaInitializerImpl;

@Configuration
// @EnableCaching // Activa @Cacheable
public class InfinispanConfig {

    @Value("${infinispan.server.host}")
    private String host;

    @Value("${infinispan.server.port}")
    private int port;

    @Bean
    public RemoteCacheManager remoteCacheManager(ProductProtoSchemaInitializer schema) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder
                // Por defecto, el cliente de Infinispan usa la IP que le da el servidor, por lo
                // que no es necesario
                // Al estar en docker se le pone BASIC para que no use la IP del servidor
                .clientIntelligence(ClientIntelligence.BASIC)
                .addServer().host(host).port(port)
                .security().authentication()
                .enabled(true)
                .saslMechanism("SCRAM-SHA-512")
                .username("admin123")
                .password("admin123")
                .marshaller(new ProtoStreamMarshaller())
                .addJavaSerialAllowList("com.juanma.proyecto_vn.Serialization.ModelsProto.*"); // Permite la
                                                                                               // serialización de los
                                                                                               // objetos
        // de la aplicación

        RemoteCacheManager rcm = new RemoteCacheManager(builder.build());

        SerializationContext ctx = MarshallerUtil.getSerializationContext(rcm);
        schema.registerSchema(ctx); // Registra Producto, Categoria, Proveedor, UUID en el contexto de serialización
        schema.registerMarshallers(ctx); // Registra los Marshallers en el contexto de serialización
        return rcm;
    }

    /**
     * Bean para cargar el manejador de cache remota de infinispan en el
     * contexto de spring. Se usa para que spring pueda usar el cache de infinispan
     */
    @Bean
    SpringRemoteCacheManager cacheManager(RemoteCacheManager rcm) {
        return new SpringRemoteCacheManager(rcm);
    }

    /**
     * Bean para inicializar el esquema de protostream de infinispan. Se usa para
     * registrar los marshallers y el esquema de protostream en el contexto de
     * Los marshallers son los que se encargan de serializar y deserializar los
     * objetos en el cache de infinispan. Se usa para registrar los objetos que se
     */
    @Bean
    ProductProtoSchemaInitializer productoSchemaInitializer() {
        return new ProductProtoSchemaInitializerImpl();
    }
}
