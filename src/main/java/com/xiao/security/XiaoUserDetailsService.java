package com.xiao.security;

import com.xiao.users.entity.User;
import com.xiao.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class XiaoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmailAddress(email);
        if(user != null){
            return new XiaoUserDetails(user);
        }
        throw new UsernameNotFoundException("Could not find user with email: "+ email);
    }
}
