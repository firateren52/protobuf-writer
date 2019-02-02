package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.Model;
import com.eren.noddus.protobufwriter.model.UserModel;
import com.eren.noddus.protobufwriter.model.UserProto;
import com.google.protobuf.UninitializedMessageException;
import org.junit.Before;
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

    @Before
    public void cleanup() throws IOException {
        Path modelPath = Paths.get("data/users");
        if (Files.isDirectory(modelPath)) {
            Files.walk(modelPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            assertFalse("Directory still exists",
                    Files.exists(modelPath));
        }
    }

    @Test
    public void saveModel_givenMultiThreadRequests_thenShouldAppendToFiles() throws IOException, FileNotFoundException {
        FileService fileService = new FileService("users");
        List<Model> models = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            int id = new Random().nextInt();
            String name = "Nick Doe " + i;
            UserModel userModel = new UserModel(i, name);
            models.add(userModel);
            new Thread(() -> {
                try {
                    fileService.saveModel(userModel, 3000);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        readAndCompareDeserializedModels(models);
    }

    private void readAndCompareDeserializedModels(List<Model> models) throws IOException {
        // Read models from files
        Path modelPath = Paths.get("data/users");
        List<Model> deserializedModels = new ArrayList<>();
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
                        deserializedModels.add(new UserModel(deserializedUser));
                    }
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            assertThat(models).containsExactlyInAnyOrder(deserializedModels.toArray(new Model[deserializedModels.size()]));
        } else {
            assertFalse("Directory still exists",
                    Files.exists(modelPath));
        }
    }
}
