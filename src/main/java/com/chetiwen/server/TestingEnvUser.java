package com.chetiwen.server;

import com.chetiwen.rest.service.UserResource;

public class TestingEnvUser {
    public static void main(String[] args) throws Exception{
        UserResource.userOperation("http://localhost:8090/user");
    }
}
