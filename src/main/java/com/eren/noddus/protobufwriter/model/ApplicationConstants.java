package com.eren.noddus.protobufwriter.model;

public class ApplicationConstants {
    //TODO(firat.eren) move these to propeties file
    public static final String BASE_FOLDER = "data";
    public static final String MESSAGE_FILE_EXTENSION = ".data";
    public static final String SLASH_SYMBOL = "/";
    public static final int DEFAULT_TIMEOUT = 10000; // timeout shouldn't be less than 1000(1 second)
    public static final int DEFAULT_MODEL_QUEUE_SIZE = 1000;
}
