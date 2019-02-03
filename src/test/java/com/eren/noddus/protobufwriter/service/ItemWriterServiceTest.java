package com.eren.noddus.protobufwriter.service;


import com.eren.noddus.protobufwriter.model.ItemProto;
import com.eren.noddus.protobufwriter.model.MessageType;
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

public class ItemWriterServiceTest {
    private int dataCount = 100000;
    private int treadCount = 100;
    private String messageType = MessageType.ITEM.getType();

    @Test
    public void addModel_givenMultiThreadRequestsWithItemsQueue_thenShouldAppendToFiles() throws IOException, FileNotFoundException, InterruptedException {
        FileUtil.cleanDirectories(messageType);
        MessageWriterService<ItemProto.Item> messageWriterService = new ItemWriterService();
        ExecutorService executor = Executors.newFixedThreadPool(treadCount);

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < dataCount; i++) {
            int id = new Random().nextInt();
            String name = "item " + id;
            ItemProto.Item item = ItemProto.Item.newBuilder().setId(id).setName(name).build();
            messages.add(item);
            executor.execute(new Thread(() -> {
                messageWriterService.saveAsync(item);
            }));
        }

        Function<InputStream, Message> parseItemsDelimitedFrom = (fis -> {
            try {
                return ItemProto.Item.parseDelimitedFrom(fis);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        FileUtil.readAndCompareDeserializedMessages(messages, messageType, parseItemsDelimitedFrom);
    }
}