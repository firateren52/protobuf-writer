package com.eren.noddus.protobufwriter.service;

import com.eren.noddus.protobufwriter.model.MessageType;
import com.google.protobuf.Message;
import org.springframework.stereotype.Service;

//TODO(firat.eren) make singleton
@Service
public class UserWriterService<T extends Message> extends MessageWriterService<T> {

    public UserWriterService() {
        super(MessageType.USER.getType(), RolloverStrategyFactory.getRolloverStrategy());
    }
}
