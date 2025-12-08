package com.example.QuanLySinhVien.controller;

import com.example.QuanLySinhVien.entity.Login;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.LoginRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class LoginController {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginRepository loginRepository;
    @GetMapping("/showloginpage")
    public String show(){
        return "login";
    }
    @PostMapping("/change-password")
    public String changePassword(Principal principal,
                                 @RequestParam String oldPass,
                                 @RequestParam String newPass,
                                 @RequestParam String confirmPass,
                                 Model model, RedirectAttributes redirectAttributes) {
        User user = userRepository.findById(principal.getName()).orElse(null);
        if (user == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản!");
            model.addAttribute("showModal",true);
            model.addAttribute("profile",user);
            model.addAttribute("activePage", "profile");
            return "admin/profile";
        }
        Login login = user.getLogin();
        if (!passwordEncoder.matches(oldPass, login.getPassword())) {
            model.addAttribute("error", "Mật khẩu cũ không đúng!");
            model.addAttribute("showModal",true);
            model.addAttribute("profile",user);
            model.addAttribute("activePage", "profile");
            return "admin/profile";
        }
        if (!newPass.equals(confirmPass)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp!");
            model.addAttribute("showModal",true);
            model.addAttribute("profile",user);
            model.addAttribute("activePage", "profile");
            return "admin/profile";
        }
        login.setPassword(passwordEncoder.encode(newPass));
        loginRepository.save(login);
        model.addAttribute("profile",user);
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
        return "redirect:/showloginpage";
    }

}
