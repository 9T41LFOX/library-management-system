package com.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Serves uploaded book PDFs straight from disk (outside the jar/classpath)
 * so they survive an application restart. The mapped path is not listed in
 * SecurityConfig's permitAll rules, so it falls under
 * ".anyRequest().authenticated()" - only signed-in users can fetch a PDF,
 * and anonymous requests are redirected to /login like any other page.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String pdfUploadDir;

    public WebConfig(@Value("${library.upload.pdf-dir:uploads/pdf}") String pdfUploadDir) {
        this.pdfUploadDir = pdfUploadDir;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Paths.get(pdfUploadDir).toAbsolutePath().normalize().toUri().toString();
        registry.addResourceHandler("/uploads/pdf/**")
                .addResourceLocations(location);
    }
}
