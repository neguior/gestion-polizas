package co.com.segurosbolivar.polizas.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ApiKeyInterceptor apiKeyInterceptor;
    public WebConfig(ApiKeyInterceptor apiKeyInterceptor) { this.apiKeyInterceptor = apiKeyInterceptor; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiKeyInterceptor).addPathPatterns("/**")
                .excludePathPatterns("/h2-console/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Necesario para que la consola H2 funcione en navegador durante desarrollo.
        registry.addResourceHandler("/h2-console/**").addResourceLocations("classpath:/META-INF/resources/");
    }
}
