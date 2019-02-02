package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.Model;
import com.google.protobuf.Message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiPredicate;

public class FileService {
    private final String BASE_FOLDER = "data";
    private String modelFolder;
    private String modelName;
    public FileOutputStream fileOutput;
    private Path modelFile;
    private BiPredicate rolloverStrategy;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public FileService(String modelName) throws IOException {
        this.modelName = modelName;
        modelFolder = BASE_FOLDER + "/" + modelName;
        Path modelDirectory = Paths.get(modelFolder);
        if(!Files.isDirectory(modelDirectory)) {
            Files.createDirectory(modelDirectory);
        }
        modelFile = modelDirectory.resolve( modelName + ".data");
        if(!Files.isRegularFile(modelFile)) {
            Files.createFile(modelFile);
        }
        fileOutput = new FileOutputStream(modelFolder + "/" + modelName + ".data", true);
    }

    public boolean saveModel(Model model, long timeout) throws IOException {
        Message message = model.getMessage();
        lock.writeLock().lock();
        FileOutputStream fos = getFileOutput(timeout);
        message.writeDelimitedTo(fileOutput);
        lock.writeLock().unlock();

        return true;
    }

    private FileOutputStream getFileOutput(long timeout) throws IOException {
        //lock.writeLock().lock();
        BasicFileAttributes attributes = Files.readAttributes(modelFile, BasicFileAttributes.class);
        Date creationTime =  new Date(attributes.creationTime().toMillis() + timeout);
        boolean createNewFile = creationTime.before(new Date());
        //lock.writeLock().unlock();

        if (createNewFile) {
            //lock.writeLock().lock();

            fileOutput.close();
            Path archivedModelFile = modelFile.resolveSibling(modelName + "_" + attributes.creationTime().toString() + ".data");
            Files.move(modelFile, archivedModelFile);
            Files.createFile(modelFile);
            fileOutput = new FileOutputStream(modelFolder + "/" + modelName + ".data", true);

        }
        //lock.writeLock().unlock();
        return fileOutput;
    }

}
