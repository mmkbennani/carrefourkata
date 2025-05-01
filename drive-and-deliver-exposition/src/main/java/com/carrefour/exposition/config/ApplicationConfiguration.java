package com.carrefour.exposition.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurerComposite;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Method;

import static java.util.Optional.ofNullable;

@SuppressWarnings({"SpringFacetCodeInspection"})
@Configuration
public class ApplicationConfiguration {

    private static final String MAPPING_PATTERN = "/**";
    private static final String ALLOWED_ORIGIN = "*";
    private static final String ALLOWED_HEADERS = "*";

    @Bean
    public WebFluxConfigurer corsConfigurer() {
        return new WebFluxConfigurerComposite() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry corsRegistry) {
                corsRegistry
                    .addMapping(MAPPING_PATTERN)
                    .allowedOrigins(ALLOWED_ORIGIN)
                    .allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name())
                    .allowedHeaders(ALLOWED_HEADERS);
            }
        };
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger logger(InjectionPoint injectionPoint) {
        return ofNullable(injectionPoint.getMethodParameter())
            .map(ApplicationConfiguration::getTargetClass)
            .map(LoggerFactory::getLogger)
            .orElseThrow(() -> {
                var constructorMissingParameterError = "Could not inject logger through constructor: missing parameter";
                return new UnsupportedOperationException(constructorMissingParameterError);
            });
    }

    private static Class<?> getTargetClass(MethodParameter methodParameter) {
        var beanProducingMethodReturnType = ofNullable(methodParameter.getMethod())
            .map(Method::getReturnType);
        if (beanProducingMethodReturnType.isPresent()) {
            return beanProducingMethodReturnType.get();
        }
        return methodParameter.getContainingClass();
    }


}
