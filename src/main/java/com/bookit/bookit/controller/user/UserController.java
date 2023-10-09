package com.bookit.bookit.controller.user;

import com.bookit.bookit.dto.UpdateUserDTO;
import com.bookit.bookit.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public class UserController {
/*
    private final UserService userService;

    @Autowired  //Kopplat controller med userService
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/name")
    public ResponseEntity<Void> setUserNameAndPassword(@RequestHeader("UserId") Integer userId, @RequestBody UpdateUserDTO requestDTO) {
        userService.UpdateUserNameAndPassword(userId, requestDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }*/

}
