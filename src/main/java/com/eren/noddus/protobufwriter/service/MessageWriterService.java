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
 *
 * @param <T> the type parameter
 */
@Slf4j
public abstract class MessageWriterService<T extends Message> {
    private final FileService fileService;
    private final ReentrantLock lock = new ReentrantLock();
    private final LinkedBlockingQueue<Message> messageBlockingQueue = new LinkedBlockingQueue<>(DEFAULT_MODEL_QUEUE_SIZE);

    /**
     * Instantiates a new Message writer service.
     *
     * @param messageType      the message type
     * @param rolloverStrategy the rollover strategy
     */
    public MessageWriterService(final String messageType, final Predicate<Path> rolloverStrategy) {
        fileService = new FileService(messageType, rolloverStrategy);

        //start single file writer thread
        new Thread(() -> {
            save();
        }).start();
    }

    /**
     * Save message immediately. Thread-safety guaranteed with locks
     *
     * @param message the message
     * @return the boolean
     * @throws IOException the io exception
     */
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

    /**
     * Add message to queue then save message (faster than save(T message)).
     *
     * @param message the message
     * @return the boolean
     */
    public boolean saveAsync(T message) {
        try {
            messageBlockingQueue.put(message);
        } catch (InterruptedException e) {
            log.error("saveAsync failed!", e);
            return false;
        }
        return true;
    }

    //Read queue and save messages to files. Single thread runs per message type so it's thread-safe
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
