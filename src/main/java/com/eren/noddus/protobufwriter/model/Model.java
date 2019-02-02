package com.eren.noddus.protobufwriter.model;

import com.google.protobuf.Message;

public interface Model {

    public String getMetadata();

    public Message getMessage();
}
