package com.eren.noddus.protobufwriter.service;

import com.google.protobuf.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.eren.noddus.protobufwriter.model.ApplicationConstants.BASE_FOLDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

public class FileUtil {

    public static void readAndCompareDeserializedMessages(List<Message> messages, String messageType, Function<InputStream, Message> parseDelimitedFrom) throws IOException, InterruptedException {
        Thread.sleep(4000);

        // Read messages from files
        Path messagePath = Paths.get(BASE_FOLDER + "/" + messageType);
        List<Message> deserializedMessages = new ArrayList<>();
        if (Files.isDirectory(messagePath)) {

            List<File> files = Files.walk(messagePath).filter(Files::isRegularFile)
                    .map(Path::toFile).collect(Collectors.toList());
            files.forEach(file -> {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    while (true) {
                        Message deserializedMessage = parseDelimitedFrom.apply(fis);
                        if (deserializedMessage == null) {
                            break;
                        }
                        deserializedMessages.add(deserializedMessage);
                    }
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            assertThat(messages).containsExactlyInAnyOrder(deserializedMessages.toArray(new Message[deserializedMessages.size()]));
        } else {
            assertFalse("Directory still exists",
                    Files.exists(messagePath));
        }
    }

    public static void cleanDirectories(String messagetype) throws IOException {
        Path messagePath = Paths.get(BASE_FOLDER + "/" + messagetype);
        if (Files.isDirectory(messagePath)) {
            Files.walk(messagePath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            assertFalse("Directory still exists",
                    Files.exists(messagePath));
        }
    }


}
