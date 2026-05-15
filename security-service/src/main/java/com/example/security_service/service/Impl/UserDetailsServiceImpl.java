package com.example.security_service.service.Impl;

import com.example.security_service.dto.UserDetailDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailDTO user;
        try {
            // 1. เรียก API ไปยัง user-service ตรง (8082) เพื่อเลี่ยง Gateway Loop
            user = restTemplate.getForObject(userServiceUrl + "/api/users/details/" + username, UserDetailDTO.class);

            if (user == null) {
                throw new UsernameNotFoundException("User Not Found with username: " + username);
            }

        } catch (HttpClientErrorException.NotFound e) {
            throw new UsernameNotFoundException("User Not Found with username: " + username, e);
        }

        // 2. แปลง Roles ที่ได้จาก DTO เป็น GrantedAuthority
        // แก้ไขการดึงข้อมูลให้ตรงกับโครงสร้างใหม่: User -> UserRoles -> Role -> Name
        List<GrantedAuthority> authorities = user.getUserRoles() == null ? Collections.emptyList() :
                user.getUserRoles().stream()
                        .map(userRole -> userRole.getRole()) // เข้าถึง RoleDTO
                        .filter(roleDto -> roleDto != null)
                        .map(roleDto -> new SimpleGrantedAuthority("ROLE_" + roleDto.getName().toUpperCase()))
                        .collect(Collectors.toList());

        if (authorities.isEmpty()) {
            // ถ้าไม่พบ Role เลย อาจจะให้สิทธิ์พื้นฐาน หรือโยน Error ก็ได้
            // ในที่นี้จะให้สิทธิ์พื้นฐานไปก่อน เพื่อไม่ให้ระบบล่ม
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // 3. สร้าง UserDetails object จากข้อมูลที่ได้
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                authorities);
    }
}
