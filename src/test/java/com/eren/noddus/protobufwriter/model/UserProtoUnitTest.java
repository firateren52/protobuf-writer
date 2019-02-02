package com.eren.noddus.protobufwriter.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.assertj.core.api.Assertions.*;

public class UserProtoUnitTest {
    private final String usersfilePath = "data/users.txt";

    @Before
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(usersfilePath));
    }

    @Test
    public void build_givenGeneratedProtobufClass_thenShouldCreateJavaInstance() {
        //when
        int id = new Random().nextInt();
        String name = "Nick Doe";

        UserProto.User user = UserProto.User.newBuilder().setId(id).setName(name).build();

        //then
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getId()).isEqualTo(id);
    }

    @Test
    public void saveAsAFile_givenUser_thenShouldLoadFromFileToJavaClass() throws IOException {
        //when
        int id = new Random().nextInt();
        String name = "John Doe";

        UserProto.User user = UserProto.User.newBuilder().setId(id).setName(name).build();
        FileOutputStream fos = new FileOutputStream(usersfilePath);
        user.writeTo(fos);
        fos.close();
        FileInputStream fis = new FileInputStream(usersfilePath);
        UserProto.User deserializedUser = UserProto.User.newBuilder().mergeFrom(fis).build();
        fis.close();

        //then
        assertThat(user.getName()).isEqualTo(deserializedUser.getName());
        assertThat(user.getId()).isEqualTo(deserializedUser.getId());
    }

    @Test
    public void equals_givenUsersWithSameValues_thenShouldReturnTrue() throws IOException {
        int id = new Random().nextInt();
        String name = "Nick Doe " + id;
        UserModel userModel = new UserModel(id, name);
        UserModel userModel2 = new UserModel(id, name);
        Assert.assertTrue(userModel.equals(userModel2));
    }

}