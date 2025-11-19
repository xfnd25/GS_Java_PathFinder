package com.fiap.globalsolution.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Habilita o suporte a cache na aplicação.
 * A configuração do provedor de cache (ex: Caffeine) é feita no arquivo application.yml.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
