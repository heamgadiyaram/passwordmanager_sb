package com.passwordmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public boolean doesUserExist(@PathVariable String username) {
        return userService.doesUserExist(username);
    }

    @PostMapping("/handle")
    public User handleUser(@RequestParam String username, @RequestParam String password, @RequestParam boolean isLogin) {
        return userService.handleUser(username, password, isLogin);
    }
}
