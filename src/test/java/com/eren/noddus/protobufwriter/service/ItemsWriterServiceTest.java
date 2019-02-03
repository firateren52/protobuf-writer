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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class ItemsWriterServiceTest {
    private int dataCount = 10000;
    private int treadCount = 1000;
    private String messageType = MessageType.ITEMS.getType();

    @Test
    public void addModel_givenMultiThreadRequestsWithItemsQueue_thenShouldAppendToFiles() throws IOException, FileNotFoundException, InterruptedException {
        FileUtil.cleanDirectories(messageType);
        MessageWriterService<ItemProto.Items> messageWriterService = new ItemsWriterService();
        ExecutorService executor = Executors.newFixedThreadPool(treadCount);

        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < dataCount; i++) {
            int id = new Random().nextInt();
            String name = "item " + i;
            ItemProto.Item item = ItemProto.Item.newBuilder().setId(i).setName(name).build();
            ItemProto.Items items = ItemProto.Items.newBuilder().addItem(item).build();
            messages.add(items);
            executor.execute(new Thread(() -> {
                messageWriterService.saveAsync(items);
            }));
        }

        Function<InputStream, Message> parseItemsDelimitedFrom = (fis -> {
            try {
                return ItemProto.Items.parseDelimitedFrom(fis);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        });

        FileUtil.readAndCompareDeserializedMessages(messages, messageType, parseItemsDelimitedFrom);
    }
}