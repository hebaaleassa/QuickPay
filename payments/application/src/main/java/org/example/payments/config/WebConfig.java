package org.example.payments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Serves the Angular files from static/. If no file matches and the URL looks like an Angular
    // page (e.g. /payments/5 or an unknown /abc), answer with index.html: Angular then shows the
    // right page or its own "not found" page. Anything else gets a normal 404.
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws java.io.IOException {
                        Resource file = location.createRelative(resourcePath);
                        if (file.exists() && file.isReadable()) {
                            return file;
                        }
                        return SpaPaths.isPage("/" + resourcePath) ? new ClassPathResource("static/index.html") : null;
                    }
                });
    }
}
