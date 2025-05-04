package com.juanma.proyecto_vn.infrastructure.cache.adapter;

import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.service.IProductCacheService;
import com.juanma.proyecto_vn.infrastructure.cache.mapper.ProductCacheMapper;
import com.juanma.proyecto_vn.infrastructure.cache.modelsProto.ProductProto;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto de caché de productos usando Infinispan
 */
@Component
@RequiredArgsConstructor
public class InfinispanProductCacheAdapter implements IProductCacheService {
    private static final Logger logger = LoggerFactory.getLogger(InfinispanProductCacheAdapter.class);

    private final RemoteCacheManager remoteCacheManager;
    private final ProductCacheMapper productCacheMapper;

    private RemoteCache<String, ProductProto> remoteCache;

    @PostConstruct
    public void init() {
        try {
            remoteCache = remoteCacheManager.administration().getOrCreateCache("products", DefaultTemplate.DIST_SYNC);
        } catch (Exception e) {
            logger.error("Error al inicializar la caché de productos: {}", e.getMessage(), e);
        }
    }

    @Override
    public Optional<Product> getFromCache(String key) {
        if (remoteCache == null) {
            return Optional.empty();
        }

        try {
            ProductProto cachedProduct = remoteCache.get(key);
            if (cachedProduct != null) {
                return Optional.of(productCacheMapper.toDomain(cachedProduct));
            }
        } catch (Exception e) {
            logger.warn("Error al obtener producto de caché: {}", e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public void saveInCache(String key, Product product) {
        if (remoteCache == null) {
            return;
        }

        try {
            ProductProto productProto = productCacheMapper.toProtobuf(product);
            remoteCache.put(key, productProto);
            logger.debug("Producto guardado en caché: {}", key);
        } catch (Exception e) {
            logger.warn("Error al guardar producto en caché: {}", e.getMessage());
        }
    }

    @Override
    public void removeFromCache(String key) {
        if (remoteCache == null) {
            return;
        }

        try {
            remoteCache.remove(key);
            logger.debug("Producto eliminado de caché: {}", key);
        } catch (Exception e) {
            logger.warn("Error al eliminar producto de caché: {}", e.getMessage());
        }
    }
}