package com.fiap.globalsolution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class InternationalizationConfig {

    /**
     * Configura o resolvedor de Locale para usar o cabeçalho 'Accept-Language' das requisições HTTP.
     * Isso permite que a aplicação responda no idioma solicitado pelo cliente (ex: pt-BR, en-US).
     * @return O LocaleResolver configurado.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        // Define um locale padrão caso o header não seja enviado ou não seja suportado
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }
}
