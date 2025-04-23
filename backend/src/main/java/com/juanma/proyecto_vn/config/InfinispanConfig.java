package com.juanma.proyecto_vn.config;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.client.hotrod.marshall.MarshallerUtil;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.spring.remote.provider.SpringRemoteCacheManager;
import org.infinispan.protostream.SerializationContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.juanma.proyecto_vn.serialization.ProductSchemaInitializer;
import com.juanma.proyecto_vn.serialization.ProductSchemaInitializerImpl;

@Configuration
@EnableCaching
public class InfinispanConfig {

    @Value("${infinispan.hotrod.host}")
    private String host;

    @Value("${infinispan.hotrod.port}")
    private int port;

    @Value("${infinispan.hotrod.username}")
    private String username;

    @Value("${infinispan.hotrod.password}")
    private String password;

    @Bean
    RemoteCacheManager remoteCacheManager(ProductSchemaInitializer schema) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host("127.0.0.1").port(11222).security().authentication().enabled(true)
                .saslMechanism("DIGEST-MD5").username("admin123").password("admin123")
                .marshaller(new ProtoStreamMarshaller());
        RemoteCacheManager rcm = new RemoteCacheManager(builder.build());

        SerializationContext ctx = MarshallerUtil.getSerializationContext(rcm);
        schema.registerSchema(ctx);
        return rcm;
    }

    @Bean
    SpringRemoteCacheManager springCacheManager(RemoteCacheManager rcm) {
        return new SpringRemoteCacheManager(rcm);
    }

    @Bean
    public ProductSchemaInitializer productSchemaInitializer() {
        // Clase generada automaticamente por Infinispan al registrar el esquema de
        return new ProductSchemaInitializerImpl();
    }

}