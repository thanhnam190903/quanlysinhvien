package com.example.QuanLySinhVien.controller.admin;

import com.example.QuanLySinhVien.entity.Login;
import com.example.QuanLySinhVien.entity.Role;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.RoleRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
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

@RequestMapping("/quantri")
@Controller
public class StaffController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/staff")
    public String getAll(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Model model, Principal principal){

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(page, size);
        User profile = userRepository.findById(principal.getName()).orElse(null);
        Page<User> staffList = userRepository.searchStaff(keyword, pageable);

        model.addAttribute("staffList", staffList);
        model.addAttribute("staff", new User());
        model.addAttribute("profile", profile);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "staff");

        return "admin/staff";
    }
    @PostMapping("/add-staff")
    public String addStaff(@ModelAttribute("staff") User user, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if (result.hasErrors()){
            model.addAttribute("staff", user);
            model.addAttribute("showModal", true);
            return "admin/staff";
        }
        Date today = new Date(System.currentTimeMillis());
        user.setCreatedAt(today);
        user.setLastModified(today);
        user.setDeleted(false);
        user.setStatus(false);
        Role role = roleRepository.findByName("ROLE_staff");
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
        redirectAttrs.addFlashAttribute("successMessage", "Thêm phòng đào tạo thành công!");
        return "redirect:/quantri/staff";
    }
    @PostMapping("/update-staff")
    public String updateStaff(@ModelAttribute User staff, RedirectAttributes redirectAttributes) {
        User existingStaff = userRepository.findById(staff.getId())
                .orElse(null);

        if (existingStaff != null) {
            existingStaff.setName(staff.getName());
            existingStaff.setEmail(staff.getEmail());
            existingStaff.setPhone(staff.getPhone());
            existingStaff.setAddress(staff.getAddress());
            existingStaff.setDateOfBirth(staff.getDateOfBirth());
            Date today = new Date(System.currentTimeMillis());
            existingStaff.setLastModified(today);

            userRepository.save(existingStaff);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng đào tạo thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên phòng đào tạo!");
        }

        return "redirect:/quantri/staff";
    }
    @GetMapping("/delete-staff")
    public String deleteStaff(@RequestParam("id") String id){
        userRepository.deleteUser(id);
        return "redirect:/quantri/staff";
    }
}
