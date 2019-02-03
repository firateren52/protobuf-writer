package com.eren.noddus.protobufwriter.service;

import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

import static com.eren.noddus.protobufwriter.model.ApplicationConstants.*;

@Slf4j
public class FileService {
    public FileOutputStream outputStream;
    private Path messageFile;
    private String messageType;
    private Predicate<Path> rolloverStrategy;

    public FileService(String messageType, Predicate<Path> rolloverStrategy) {
        this.messageType = messageType;
        this.rolloverStrategy = rolloverStrategy;
        prepareFolderAndFiles();
    }

    private void checkOutputStream() throws IOException {
        // create new file if needed
        if (rolloverStrategy.test(messageFile)) {
            outputStream.close();
            BasicFileAttributes attributes = Files.readAttributes(messageFile, BasicFileAttributes.class);
            Path archivedModelFile = messageFile.resolveSibling(messageType + "_" + attributes.creationTime().toString() + MESSAGE_FILE_EXTENSION);
            Files.move(messageFile, archivedModelFile);
            Files.createFile(messageFile);
            outputStream = new FileOutputStream(getFileName(), true);
        }
    }

    public void writeMessage( Message message) throws IOException {
        checkOutputStream();
        message.writeDelimitedTo(outputStream);
    }

    private void prepareFolderAndFiles() {
        try {
            Path modelDirectory = Paths.get(getFolderName());
            if (!Files.isDirectory(modelDirectory)) {
                Files.createDirectory(modelDirectory);
            }
            messageFile = modelDirectory.resolve(messageType + MESSAGE_FILE_EXTENSION);
            if (!Files.isRegularFile(messageFile)) {
                Files.createFile(messageFile);
            }
            outputStream = new FileOutputStream(getFileName(), true);
        } catch (IOException e) {
            log.error("FileService failed!" + e);
        }
    }

    private String getFolderName() {
        return BASE_FOLDER + SLASH_SYMBOL + messageType;
    }

    private String getFileName() {
        return getFolderName() + SLASH_SYMBOL + messageType + MESSAGE_FILE_EXTENSION;
    }
}
