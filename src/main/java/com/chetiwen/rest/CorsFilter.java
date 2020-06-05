package com.chetiwen.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.container.*;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;

public class CorsFilter implements ContainerResponseFilter {

    public ContainerResponse filter(ContainerRequest creq, ContainerResponse cres) {
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");
        /**
         * 允许的Header值，不支持通配符
         */
        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");

        /**
         * 即使只用其中几种，header和options是不能删除的，因为浏览器通过options请求来获取服务的跨域策略
         */
        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        /**
         * CORS策略的缓存时间
         */
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");

        //可以通过 throw new WebApplicationException(Status.UNAUTHORIZED); 来中断请求

        return cres;
    }

    public void filter(ContainerRequestContext request, ContainerResponseContext cres) throws IOException {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {//浏览器会先通过options请求来确认服务器是否可以正常访问，此时应放行
            cres.setStatus(HttpServletResponse.SC_OK);
        }
        cres.getHeaders().add("Access-Control-Allow-Origin", "*");

        cres.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        cres.getHeaders().add("Access-Control-Allow-Credentials", "true");

        cres.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");

        /**
         * CORS策略的缓存时间
         */
        cres.getHeaders().add("Access-Control-Max-Age", "1209600");

    }

}

