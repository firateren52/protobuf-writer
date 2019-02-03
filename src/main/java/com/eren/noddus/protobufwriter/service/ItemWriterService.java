package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.MessageType;
import com.google.protobuf.Message;

public class ItemWriterService<T extends Message> extends MessageWriterService<T> {

    public ItemWriterService() {
        super(MessageType.ITEM.getType(), RolloverStrategyFactory.getRolloverStrategy());
    }
}
