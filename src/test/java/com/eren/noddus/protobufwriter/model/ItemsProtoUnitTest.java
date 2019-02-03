package com.eren.noddus.protobufwriter.model;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemsProtoUnitTest {
    private final String usersfilePath = "data/items.txt";

    @Before
    public void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(usersfilePath));
    }

    @Test
    public void build_givenGeneratedProtobufClass_thenShouldCreateJavaInstance() {
        //when
        int id = new Random().nextInt();
        ItemProto.Items items = ItemProto.Items.newBuilder().addItem(createNewItem(id)).build();
        //then
        assertThat(items.getItem(0).getName()).isEqualTo("Nick Doe " + id);
        assertThat(items.getItem(0).getId()).isEqualTo(id);
    }

    @Test
    public void saveAsAFile_givenItem_thenShouldLoadFromFileToJavaClass() throws IOException {
        //when
        int id = new Random().nextInt();
        ItemProto.Items items = ItemProto.Items.newBuilder().addItem(createNewItem(id)).addItem(createNewItem(id + 1)).build();

        FileOutputStream fos = new FileOutputStream(usersfilePath);
        items.writeTo(fos);
        fos.close();
        FileInputStream fis = new FileInputStream(usersfilePath);
        ItemProto.Items deserializedItems = ItemProto.Items.newBuilder().mergeFrom(fis).build();
        fis.close();

        //then
        assertThat(items.getItem(0)).isEqualTo(deserializedItems.getItem(0));
        assertThat(items.getItem(1)).isEqualTo(deserializedItems.getItem(1));
    }

    private ItemProto.Item createNewItem(int id) {
        String name = "Nick Doe " + id;
        return ItemProto.Item.newBuilder().setId(id).setName(name).build();
    }

}