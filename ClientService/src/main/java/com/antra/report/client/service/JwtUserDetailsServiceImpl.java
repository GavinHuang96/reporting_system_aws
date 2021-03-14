package com.antra.report.client.service;

import com.antra.report.client.entity.User;
import com.antra.report.client.repository.UserRepo;
import com.antra.report.client.security.UserPrincipal;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JwtUserDetailsServiceImpl implements JwtUserDetailsService {
    @Autowired
    UserRepo userRepo;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    @Transactional
    public UserDetails loadUserById(int id) throws UsernameNotFoundException {
        User user = userRepo.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id : " + id));
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
