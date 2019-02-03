package com.eren.noddus.protobufwriter.model;

import com.google.protobuf.Message;

import java.util.List;

public interface BufferedModel extends  Model{

    Message getMessages(List<? extends Message> messageList);
}
