package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.MessageType;
import com.eren.noddus.protobufwriter.model.UserProto;
import com.google.protobuf.Message;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class UserWriterServiceTest {
    private int dataCount = 10000;
    private int treadCount = 1000;
    private String messageType = MessageType.USER.getType();

    @Test
    public void saveModel_givenMultiThreadRequests_thenShouldAppendToFiles() throws IOException, InterruptedException {
        FileUtil.cleanDirectories(messageType);
        MessageWriterService<UserProto.User> messageWriterService = new UserWriterService();
        ExecutorService executor = Executors.newFixedThreadPool(treadCount);
        CountDownLatch latch = new CountDownLatch(dataCount);

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < dataCount; i++) {
            int id = new Random().nextInt();
            String name = "Nick Doe " + id;
            UserProto.User user = UserProto.User.newBuilder().setId(id).setName(name).build();
            messages.add(user);
            executor.execute(new Thread(() -> {
                try {
                    messageWriterService.save(user);
                    latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        latch.await();

        Function<InputStream, Message> parseUserDelimitedFrom = (fis -> {
            try {
                return UserProto.User.parseDelimitedFrom(fis);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        FileUtil.readAndCompareDeserializedMessages(messages, messageType, parseUserDelimitedFrom);
    }

    @Test
    public void addModel_givenMultiThreadRequestsWithQueue_thenShouldAppendToFiles() throws IOException, FileNotFoundException, InterruptedException {
        FileUtil.cleanDirectories(messageType);
        MessageWriterService messageWriterService = new UserWriterService();
        ExecutorService executor = Executors.newFixedThreadPool(treadCount);
        CountDownLatch latch = new CountDownLatch(dataCount);

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < dataCount; i++) {
            int id = new Random().nextInt();
            String name = "John Doe " + id;
            UserProto.User user = UserProto.User.newBuilder().setId(id).setName(name).build();
            messages.add(user);
            executor.execute(new Thread(() -> {
                try {
                    messageWriterService.save(user);
                    latch.countDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
        }

        latch.await();

        Function<InputStream, Message> parseUserDelimitedFrom = (fis -> {
            try {
                return UserProto.User.parseDelimitedFrom(fis);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        FileUtil.readAndCompareDeserializedMessages(messages, messageType, parseUserDelimitedFrom);
    }
}