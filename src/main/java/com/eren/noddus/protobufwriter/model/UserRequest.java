package com.eren.noddus.protobufwriter.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.StringUtils;

@Data
@EqualsAndHashCode
@ToString
public class UserRequest {
    private Integer id;
    private String name;

    public void validateRequest() {
        if (id == null) {
            throw new IllegalArgumentException("id required");
        }
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name required");
        }
    }
}
