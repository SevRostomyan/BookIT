package com.bookit.bookit.controller.user;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
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
