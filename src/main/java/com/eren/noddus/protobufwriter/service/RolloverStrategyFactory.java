package com.eren.noddus.protobufwriter.service;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.function.Predicate;

import static com.eren.noddus.protobufwriter.model.ApplicationConstants.DEFAULT_TIMEOUT;

@Slf4j
public class RolloverStrategyFactory {

    public static Predicate<Path> getRolloverStrategy() {
        //TODO(firat.eren) add file size based rollover strategy
        return getTimeoutRolloverStrategy();
    }

    // retuurn true if current file's creation time exceeds timeout(milliseconds)
    private static Predicate<Path> getTimeoutRolloverStrategy() {
        return (path) -> {
            try {
                BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
                Date creationTime = new Date(attributes.creationTime().toMillis() + DEFAULT_TIMEOUT);
                return creationTime.before(new Date());
            } catch (IOException e) {
                log.error("getTimeoutRolloverStrategy failed!", e);
                return false;
            }
        };
    }
}
