package com.eren.noddus.protobufwriter.model;

import com.google.protobuf.Message;

import java.util.List;

public class ItemModel implements BufferedModel {
    private final ItemProto.Item item;
    private final String metadata = "item";

    public ItemModel(ItemProto.Item item) {
        this.item = item;
    }

    public ItemModel(int id, String name) {
        item = ItemProto.Item.newBuilder().setId(id).setName(name).build();
    }

    public ItemProto.Item getMessage() {
        return item;
    }

    @Override
    public Message getMessages(List<? extends Message> itemList) {
        Iterable<ItemProto.Item> it = (Iterable<ItemProto.Item>) itemList;
        return ItemProto.Items.newBuilder().addAllItem(it).build();
    }

    public String getMetadata() {
        return metadata;
    }
}
