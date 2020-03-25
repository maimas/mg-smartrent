package com.mg.smartrent.gatewayapi.config.documentation;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
@EnableAutoConfiguration
@EnableSwagger2
public class DocumentationController implements SwaggerResourcesProvider {

    private ZuulProperties zuulProperties;

    public DocumentationController(ZuulProperties zuulProperties) {
        this.zuulProperties = zuulProperties;
    }


    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        zuulProperties
                .getRoutes()
                .forEach((serviceName, detail) ->
                        resources.add(swaggerResource(serviceName, String.format("/%s/v2/api-docs",serviceName), "2.0")));

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }

}
