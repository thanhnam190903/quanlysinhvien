package com.example.QuanLySinhVien.security;

import com.example.QuanLySinhVien.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,LoginSuccessHandler loginSuccessHandler) throws Exception {
        httpSecurity.authorizeHttpRequests(
                req -> req
                        .requestMatchers("/qlsv/quantri/**").hasAuthority("ROLE_admin")
                        .requestMatchers("/qlsv/staff/**").hasAuthority("ROLE_staff")
                        .requestMatchers("/qlsv/teacher/**").hasAuthority("ROLE_teacher")
                        .requestMatchers("/qlsv/student/**").hasAuthority("ROLE_student")
                        .requestMatchers("/assets/**").permitAll()
                        .anyRequest().authenticated()
        ).formLogin(
                login -> login.loginPage("/showloginpage")
                        .loginProcessingUrl("/authenticateTheUser")
                        .successHandler(loginSuccessHandler)
                        .permitAll()
        ).logout(
                logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/showloginpage")
        ).exceptionHandling(
                exception->exception.accessDeniedPage("/showPage403")

        );
        httpSecurity.csrf(csrf->csrf.disable());
        return httpSecurity.build();
    }
}
