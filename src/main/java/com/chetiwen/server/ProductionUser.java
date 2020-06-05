package com.chetiwen.server;

import com.chetiwen.rest.service.UserResource;

public class ProductionUser {

    public static void main(String[] args) throws Exception{
        UserResource.userOperation("http://39.100.117.169:8090/user");
    }
}
