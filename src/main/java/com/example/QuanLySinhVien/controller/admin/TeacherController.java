package com.example.QuanLySinhVien.controller.admin;

import com.example.QuanLySinhVien.entity.Login;
import com.example.QuanLySinhVien.entity.Role;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.RoleRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/quantri")
public class TeacherController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/teachers")
    public String getAllTeachers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model, Principal principal) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> teacherList = userRepository.searchTeachers(keyword, pageable);
        User profile = userRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("profile", profile);
        model.addAttribute("teacherList", teacherList);
        model.addAttribute("teacher", new User());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "teacher");
        return "admin/teachers";
    }
    @PostMapping("/add-teacher")
    public String addTeacher(@ModelAttribute("teacher") User user, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if (result.hasErrors()){
            model.addAttribute("teachers", user);
            model.addAttribute("showModal", true);
            return "admin/teachers";
        }
        Date today = new Date(System.currentTimeMillis());
        user.setCreatedAt(today);
        user.setLastModified(today);
        user.setDeleted(false);
        user.setStatus(false);
        Role role = roleRepository.findByName("ROLE_teacher");
        user.getRoles().add(role);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Login login = new Login();
        login.setUsername(user.getId());
        String hashed = passwordEncoder.encode(user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        login.setPassword(hashed);
        login.setDeleted(false);
        login.setUsers(user);
        user.setLogin(login);
        userRepository.save(user);
        redirectAttrs.addFlashAttribute("successMessage", "Thêm giáo viên thành công!");
        return "redirect:/quantri/teachers";
    }
    @PostMapping("/update-teacher")
    public String updateTeacher(@ModelAttribute User teacher, RedirectAttributes redirectAttributes) {
        User existingTeacher = userRepository.findById(teacher.getId())
                .orElse(null);

        if (existingTeacher != null) {
            existingTeacher.setName(teacher.getName());
            existingTeacher.setEmail(teacher.getEmail());
            existingTeacher.setPhone(teacher.getPhone());
            existingTeacher.setAddress(teacher.getAddress());
            existingTeacher.setDateOfBirth(teacher.getDateOfBirth());
            Date today = new Date(System.currentTimeMillis());
            existingTeacher.setLastModified(today);

            userRepository.save(existingTeacher);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật giáo viên thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy giáo viên!");
        }

        return "redirect:/quantri/teachers";
    }
    @GetMapping("/delete-teacher")
    public String deleteTeacher(@RequestParam("id") String id){
        userRepository.deleteUser(id);
        return "redirect:/quantri/teachers";
    }
}
