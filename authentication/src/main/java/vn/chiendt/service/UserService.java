package vn.chiendt.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import vn.chiendt.repository.UserRepository;

@Service
public record UserService(UserRepository userRepository) {
    public UserDetailsService userDetailsService(){
        return userRepository::findByUsername;
    }
}
