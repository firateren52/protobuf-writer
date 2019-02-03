package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.MessageType;
import com.google.protobuf.Message;

//TODO(firat.eren) make singleton
public class UserWriterService<T extends Message> extends MessageWriterService<T> {

    public UserWriterService() {
        super(MessageType.USER.getType(), RolloverStrategyFactory.getRolloverStrategy());
    }
}
