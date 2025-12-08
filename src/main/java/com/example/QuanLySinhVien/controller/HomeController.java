package com.example.QuanLySinhVien.controller;

import com.example.QuanLySinhVien.entity.Login;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.LoginRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import com.example.QuanLySinhVien.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping(value = {"/quantri","/staff","/teacher","student"})
public class HomeController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String showHome(Principal principal, Model model){
        User user = userRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("profile",user);
        model.addAttribute("activePage","profile");
        return "admin/profile";
    }
    @GetMapping("/reset-pass")
    public String reset(@RequestParam("id") String id, RedirectAttributes redirect) {
        User user = userRepository.findById(id).orElse(null);
        String newPassword = user.getDateOfBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Login login = user.getLogin();
        login.setPassword(passwordEncoder.encode(newPassword));
        loginRepository.save(login);
        emailService.sendEmail(user.getEmail(),"Reset mật khẩu","Mật khẩu mới là ngày sinh của bạn theo đinh dạng dd/mm/yyyy");
        redirect.addFlashAttribute("successMessage", "Reset mật khẩu thành công và đã gửi đến mail của người dùng!");
        String roleName = user.getRoles().get(0).getName();
        switch (roleName) {
            case "ROLE_teacher":
                return "redirect:/quantri/teachers";

            case "ROLE_student":
                return "redirect:/quantri/students";

            case "ROLE_staff":
                return "redirect:/quantri/staff";

            default:
                return "redirect:/showloginpage";
        }
    }
    @GetMapping("/lock-user")
    public String lock(@RequestParam("id") String id, RedirectAttributes redirect) {
        User user = userRepository.findById(id).orElse(null);
        user.setStatus(true);
        userRepository.save(user);
        redirect.addFlashAttribute("successMessage", "Khóa người dùng thành công!");
        String roleName = user.getRoles().get(0).getName();
        switch (roleName) {
            case "ROLE_teacher":
                return "redirect:/quantri/teachers";

            case "ROLE_student":
                return "redirect:/quantri/students";

            case "ROLE_staff":
                return "redirect:/quantri/staff";

            default:
                return "redirect:/showloginpage";
        }
    }
    @GetMapping("/unlock-user")
    public String unlock(@RequestParam("id") String id, RedirectAttributes redirect) {
        User user = userRepository.findById(id).orElse(null);
        user.setStatus(false);
        userRepository.save(user);
        redirect.addFlashAttribute("successMessage", "Mở khóa người dùng thành công!");
        String roleName = user.getRoles().get(0).getName();
        switch (roleName) {
            case "ROLE_teacher":
                return "redirect:/quantri/teachers";

            case "ROLE_student":
                return "redirect:/quantri/students";

            case "ROLE_staff":
                return "redirect:/quantri/staff";

            default:
                return "redirect:/showloginpage";
        }
    }
}
