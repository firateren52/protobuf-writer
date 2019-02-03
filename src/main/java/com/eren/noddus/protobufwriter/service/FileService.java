package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.BufferedModel;
import com.eren.noddus.protobufwriter.model.Model;
import com.google.protobuf.Message;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiPredicate;

public class FileService {
    private final String BASE_FOLDER = "data";
    private final int MODEL_BUFFER_SIZE = 10;
    private final int MODEL_QUEUE_SIZE = 1000;
    private final int MIN_TIMEOUT_SIZE = 1000;
    private String modelFolder;
    private String modelName;
    public FileOutputStream fileOutput;
    private Path modelFile;
    private BiPredicate rolloverStrategy;
    boolean createNewFile = false;
    private final ReentrantLock lock = new ReentrantLock();
    private final LinkedBlockingQueue<Model> modelQueue = new LinkedBlockingQueue<>(MODEL_QUEUE_SIZE);
    private final LinkedBlockingQueue<BufferedModel> bufferedModelQueue = new LinkedBlockingQueue<>(MODEL_QUEUE_SIZE);
    private long timeout;
    private int testDataCount = 100000;
    private int i=0,j=0,k=0;
    private long testDateStart;

    public FileService(String modelName, long timeout) throws IOException {
        testDateStart = System.currentTimeMillis();
        this.modelName = modelName;
        this.timeout = Math.max(timeout, MIN_TIMEOUT_SIZE); // timeout cannot be less than 1 second
        modelFolder = BASE_FOLDER + "/" + modelName;
        Path modelDirectory = Paths.get(modelFolder);
        if (!Files.isDirectory(modelDirectory)) {
            Files.createDirectory(modelDirectory);
        }
        modelFile = modelDirectory.resolve(modelName + ".data");
        if (!Files.isRegularFile(modelFile)) {
            Files.createFile(modelFile);
        }
        fileOutput = new FileOutputStream(modelFolder + "/" + modelName + ".data", true);

        new Thread(() -> {
            writeBufferedMessagesToFile();
        }).start();
        new Thread(() -> {
            writeMessageToFile();
        }).start();
    }

    public boolean saveModel(Model model) throws IOException {
        lock.lock();
        try {
            getFileOutput(timeout);
            model.getMessage().writeDelimitedTo(fileOutput);
            i++;
            if(i >= testDataCount) {
                System.out.println("saveModel finished!" + (System.currentTimeMillis() - testDateStart));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean addModel(Model model) {
        try {
            modelQueue.put(model);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addBufferedModel(BufferedModel model) {
        try {
            bufferedModelQueue.put(model);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void writeMessageToFile() {
        while (true) {
            try {
                Model model = modelQueue.take();
                Message message = model.getMessage();
                //lock.lock();
                getFileOutput(timeout);
                message.writeDelimitedTo(fileOutput);
                j++;
                if(j >= testDataCount) {
                    System.out.println("writeMessageToFile finished! " + (System.currentTimeMillis() - testDateStart));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                //lock.unlock();
            }
        }
    }

    public void writeBufferedMessagesToFile() {
        List<Message> bufferedModels = new ArrayList<>();
        while (true) {
            try {
                //System.out.println("size:" + modelQueue.size());
                BufferedModel model = bufferedModelQueue.take();
                bufferedModels.add(model.getMessage());
                if(bufferedModels.size() >= MODEL_BUFFER_SIZE) {
                    Message message = model.getMessages(bufferedModels);
                    //lock.lock();
                    getFileOutput(timeout);
                    message.writeDelimitedTo(fileOutput);
                    k+= bufferedModels.size();
                    if(k >= testDataCount) {
                        System.out.println("writeMessageToFile finished! " + (System.currentTimeMillis() - testDateStart));
                    }
                    bufferedModels = new ArrayList<>();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                //lock.unlock();
            }
        }
    }

    private void getFileOutput(long timeout) throws IOException {
        //lock.writeLock().lock();
        BasicFileAttributes attributes = Files.readAttributes(modelFile, BasicFileAttributes.class);
        Date creationTime = new Date(attributes.creationTime().toMillis() + timeout);
        createNewFile = creationTime.before(new Date());
        //lock.writeLock().unlock();
        if (createNewFile) {
            fileOutput.close();
            Path archivedModelFile = modelFile.resolveSibling(modelName + "_" + attributes.creationTime().toString() + ".data");
            Files.move(modelFile, archivedModelFile);
            Files.createFile(modelFile);
            fileOutput = new FileOutputStream(modelFolder + "/" + modelName + ".data", true);
        }
        //lock.writeLock().unlock();
    }

}
