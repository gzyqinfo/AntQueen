package com.chetiwen.rest;

import org.glassfish.jersey.server.ResourceConfig;

public class RestApplication extends ResourceConfig {
    public RestApplication(){
        this.packages("com.chetiwen.rest.service");
        this.register(CorsFilter.class);
    }
}
