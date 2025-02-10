package com.passwordmanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User handleUser(String username, String password, boolean isLogin) {
        User user = new User(username, password, isLogin); 
        return user;
    }

    public boolean doesUserExist(String username) {
        User user = userRepository.findByUsername(username);
        return user != null;
    }
}
