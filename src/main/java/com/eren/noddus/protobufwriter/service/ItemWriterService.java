package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.MessageType;
import com.google.protobuf.Message;
import org.springframework.stereotype.Service;

@Service
public class ItemWriterService<T extends Message> extends MessageWriterService<T> {

    public ItemWriterService() {
        super(MessageType.ITEM.getType(), RolloverStrategyFactory.getRolloverStrategy());
    }
}
