package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.MessageType;
import com.google.protobuf.Message;

public class ItemsWriterService<T extends Message> extends MessageWriterService<T> {

    public ItemsWriterService() {
        super(MessageType.ITEMS.getType(), RolloverStrategyFactory.getRolloverStrategy());
    }
}
