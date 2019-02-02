package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.UserModel;

import java.io.IOException;

public class UserService {

    public boolean saveUser(int id, String name) throws IOException {
        UserModel userModel = new UserModel(id, name);
        FileService fileService = new FileService("users");
        return true;
    }
}
