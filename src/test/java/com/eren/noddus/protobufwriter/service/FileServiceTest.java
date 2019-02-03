package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.*;
import com.google.protobuf.Message;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;


public class FileServiceTest {

    @Test
    public void saveModel_givenMultiThreadRequests_thenShouldAppendToFiles() throws IOException {
        cleanDirectories("data/user2");
        FileService fileService = new FileService("user2", 5000);
        List<Message> models = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            int id = new Random().nextInt();
            String name = "Nick Doe " + i;
            UserModel userModel = new UserModel(i, name);
            models.add(userModel.getMessage());
            new Thread(() -> {
                try {
                    fileService.saveModel(userModel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        readAndCompareDeserializedUsers(models, "data/user2");
    }

    @Test
    public void addModel_givenMultiThreadRequestsWithQueue_thenShouldAppendToFiles() throws IOException, FileNotFoundException, InterruptedException {
        cleanDirectories("data/user");
        FileService fileService = new FileService("user", 5000);
        List<Message> models = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            int id = new Random().nextInt();
            String name = "Nick Doe " + i;
            UserModel userModel = new UserModel(i, name);
            models.add(userModel.getMessage());
            new Thread(() -> {
                fileService.addModel(userModel);
            }).start();
        }
        Thread.sleep(1000);

        readAndCompareDeserializedUsers(models, "data/user");
    }

    @Test
    public void addModel_givenMultiThreadRequestsWithBufferedQueue_thenShouldAppendToFiles() throws IOException, FileNotFoundException, InterruptedException {
        cleanDirectories("data/item");
        FileService fileService = new FileService("item", 5000);
        List<Message> models = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            String name = "Nick Doe " + i;
            ItemProto.Item item = ItemProto.Item.newBuilder().setId(i).setName(name).build();
            ItemModel itemModel = new ItemModel(item);
            models.add(item);
            new Thread(() -> {
                fileService.addBufferedModel(itemModel);
            }).start();
        }
        Thread.sleep(1000);

        readAndCompareDeserializedItems(models, "data/item");
    }

    private void readAndCompareDeserializedItems(List<Message> models, String path) throws IOException {
        // Read models from files
        Path modelPath = Paths.get(path);
        List<Message> deserializedModels = new ArrayList<>();
        if (Files.isDirectory(modelPath)) {

            List<File> files = Files.walk(modelPath).filter(Files::isRegularFile)
                    .map(Path::toFile).collect(Collectors.toList());
            files.forEach(file -> {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    while (true) {
                        ItemProto.Items deserializedItems = ItemProto.Items.parseDelimitedFrom(fis);
                        if (deserializedItems == null) {
                            break;
                        }
                        deserializedModels.addAll(deserializedItems.getItemList());
                    }
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            assertThat(models).containsExactlyInAnyOrder(deserializedModels.toArray(new Message[deserializedModels.size()]));
        } else {
            assertFalse("Directory still exists",
                    Files.exists(modelPath));
        }
    }

    private void readAndCompareDeserializedUsers(List<Message> models, String path) throws IOException {
        // Read models from files
        Path modelPath = Paths.get(path);
        List<Message> deserializedModels = new ArrayList<>();
        if (Files.isDirectory(modelPath)) {

            List<File> files = Files.walk(modelPath).filter(Files::isRegularFile)
                    .map(Path::toFile).collect(Collectors.toList());
            files.forEach(file -> {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    while (true) {
                        UserProto.User deserializedUser = UserProto.User.parseDelimitedFrom(fis);
                        if (deserializedUser == null) {
                            break;
                        }
                        deserializedModels.add(deserializedUser);
                    }
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            assertThat(models).containsExactlyInAnyOrder(deserializedModels.toArray(new Message[deserializedModels.size()]));
        } else {
            assertFalse("Directory still exists",
                    Files.exists(modelPath));
        }
    }

    private void cleanDirectories(String path) throws IOException {
        Path modelPath = Paths.get(path);
        if (Files.isDirectory(modelPath)) {
            Files.walk(modelPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            assertFalse("Directory still exists",
                    Files.exists(modelPath));
        }
    }
}
