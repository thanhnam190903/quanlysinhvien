package com.example.QuanLySinhVien.service.impl;

import com.example.QuanLySinhVien.entity.Login;
import com.example.QuanLySinhVien.entity.Role;
import com.example.QuanLySinhVien.repository.LoginRepository;
import com.example.QuanLySinhVien.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private LoginRepository loginRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Login login = loginRepository.findByUsername(username);
        if (login == null){
            throw new UsernameNotFoundException("Tài khoản của bạn không đúng username hoặc password");
        }
        return new User(login.getUsername(), login.getPassword(), rolesToAuthorities(login.getUsers().getRoles()));
    }

    private Collection<? extends GrantedAuthority> rolesToAuthorities(Collection<Role> roles){
        return roles.stream().map(
                role->new SimpleGrantedAuthority(role.getName())
        ).collect(Collectors.toList());
    }
}
