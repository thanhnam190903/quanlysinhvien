package com.example.QuanLySinhVien.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (roles.contains("ROLE_admin")) {
            response.sendRedirect("/qlsv/quantri/profile");
        } else if (roles.contains("ROLE_staff")) {
            response.sendRedirect("/qlsv/staff/profile");
        } else if (roles.contains("ROLE_teacher")) {
            response.sendRedirect("/qlsv/teacher/profile");
        } else if (roles.contains("ROLE_student")) {
            response.sendRedirect("/qlsv/student/profile");
        }
    }
}
