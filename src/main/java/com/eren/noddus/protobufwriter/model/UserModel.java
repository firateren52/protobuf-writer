package com.eren.noddus.protobufwriter.model;

import java.util.Objects;

public class UserModel implements Model {
    private final UserProto.User user;
    private final String metadata = "users";

    public UserModel(UserProto.User user) {
        this.user = user;
    }

    public UserModel(int id, String name) {
        user = UserProto.User.newBuilder().setId(id).setName(name).build();
    }

    public UserProto.User getMessage() {
        return user;
    }

    public String getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserModel userModel = (UserModel) o;
        return Objects.equals(user, userModel.user) &&
                Objects.equals(metadata, userModel.metadata);
    }

    @Override
    public int hashCode() {

        return Objects.hash(user, metadata);
    }
}
