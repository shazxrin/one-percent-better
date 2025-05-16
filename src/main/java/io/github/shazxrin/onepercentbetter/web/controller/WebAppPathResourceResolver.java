package io.github.shazxrin.onepercentbetter.web.controller;

import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.PathResourceResolver;

public class WebAppPathResourceResolver extends PathResourceResolver {
    private static final ClassPathResource WEB_APP_ROOT_RESOURCE = new ClassPathResource("/public/index.html");

    @Override
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        Resource requestedResource = location.createRelative(resourcePath);

        // Return static resources
        if (requestedResource.exists() && requestedResource.isReadable()) {
            return requestedResource;
        }

        // Return index page for SPA to handle
        return WEB_APP_ROOT_RESOURCE;
    }
}
