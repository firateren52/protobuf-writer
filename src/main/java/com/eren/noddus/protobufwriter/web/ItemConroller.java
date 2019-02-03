package com.eren.noddus.protobufwriter.web;

import com.eren.noddus.protobufwriter.model.ItemProto;
import com.eren.noddus.protobufwriter.model.ItemRequest;
import com.eren.noddus.protobufwriter.model.UserRequest;
import com.eren.noddus.protobufwriter.service.ItemWriterService;
import com.eren.noddus.protobufwriter.service.ItemsWriterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemConroller {
    @Autowired
    private ItemWriterService<ItemProto.Item> itemWriterService;

    @Autowired
    private ItemsWriterService<ItemProto.Items> itemsWriterService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveItem (@RequestBody final ItemRequest request) {
        request.validateRequest();
        ItemProto.Item item = ItemProto.Item.newBuilder().setId(request.getId()).setName(request.getName()).build();
        return itemWriterService.saveAsync(item);
    }

    @PostMapping(value = "/all",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveItems (@RequestBody final List<ItemRequest> request) {
        ItemProto.Items.Builder builder = ItemProto.Items.newBuilder();
        request.forEach(r -> {
            r.validateRequest();
            ItemProto.Item item = ItemProto.Item.newBuilder().setId(r.getId()).setName(r.getName()).build();
            builder.addItem(item);
        });
        ItemProto.Items items = builder.build();
        return itemsWriterService.saveAsync(items);
    }


}
