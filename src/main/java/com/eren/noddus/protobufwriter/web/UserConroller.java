package com.eren.noddus.protobufwriter.web;

import com.eren.noddus.protobufwriter.model.UserRequest;
import com.eren.noddus.protobufwriter.model.UserProto;
import com.eren.noddus.protobufwriter.service.UserWriterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserConroller {
    @Autowired
    private UserWriterService<UserProto.User> userWriterService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveUser (@RequestBody final UserRequest request) {
        request.validateRequest();
        UserProto.User user = UserProto.User.newBuilder().setId(request.getId()).setName(request.getName()).build();
        return userWriterService.saveAsync(user);
    }
}
