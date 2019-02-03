package com.eren.noddus.protobufwriter.service;

import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static com.eren.noddus.protobufwriter.model.ApplicationConstants.DEFAULT_MODEL_QUEUE_SIZE;


/**
 * Abstract Message Writer service.
 */
@Slf4j
public abstract class MessageWriterService<T extends Message> {
    private final FileService fileService;
    private final ReentrantLock lock = new ReentrantLock();
    private final LinkedBlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>(DEFAULT_MODEL_QUEUE_SIZE);

    public MessageWriterService(final String messageType, final Predicate<Path> rolloverStrategy) {
        fileService = new FileService(messageType, rolloverStrategy);

        //start single file writer thread
        new Thread(() -> {
            save();
        }).start();
    }

    public boolean save(T message) throws IOException {
        lock.lock();
        try {
            fileService.writeMessage(message);
        } catch (Exception ex) {
            log.error("save failed! ", ex);
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean saveAsync(T message) {
        try {
            messageBlockingQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void save() {
        while (true) {
            try {
                Message message = messageBlockingQueue.take();
                fileService.writeMessage(message);
            } catch (Exception ex) {
                log.error("writeMessage");
            }
        }
    }

}
