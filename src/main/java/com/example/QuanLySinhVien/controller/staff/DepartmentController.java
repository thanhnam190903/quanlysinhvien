package com.example.QuanLySinhVien.controller.staff;

import com.example.QuanLySinhVien.entity.Department;
import com.example.QuanLySinhVien.entity.Role;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.DepartmentRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Date;
import java.util.List;

@RequestMapping("/staff")
@Controller
public class DepartmentController {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/departments")
    public String getAll(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model, Principal principal) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Department> departmentList = departmentRepository.searchDepartments(keyword, pageable);

        User profile = userRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("profile", profile);
        model.addAttribute("departmentList", departmentList);
        model.addAttribute("department", new Department());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "department");

        return "admin/department";
    }
    @PostMapping("/add-department")
    public String addDepartment(@ModelAttribute("department") Department department, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if (result.hasErrors()){
            model.addAttribute("department", department);
            model.addAttribute("showModal", true);
            return "admin/department";
        }
        Date today = new Date(System.currentTimeMillis());
        department.setCreatedAt(today);
        department.setLastModified(today);
        department.setDeleted(false);

        departmentRepository.save(department);
        redirectAttrs.addFlashAttribute("successMessage", "Thêm khoa thành công!");
        return "redirect:/staff/departments";
    }
    @PostMapping("/update-department")
    public String updateDepartment(@ModelAttribute Department department, RedirectAttributes redirectAttributes) {
        Department existingDepartment = departmentRepository.findById(department.getId())
                .orElse(null);

        if (existingDepartment != null) {
            existingDepartment.setName(department.getName());
            existingDepartment.setStatus(department.isStatus());
            Date today = new Date(System.currentTimeMillis());
            existingDepartment.setLastModified(today);
            departmentRepository.save(existingDepartment);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khoa thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khoa!");
        }

        return "redirect:/staff/departments";
    }
    @GetMapping("/delete-department")
    public String deleteDepartment(@RequestParam("id") int id){
        departmentRepository.deleteDepartment(id);
        return "redirect:/staff/departments";
    }
}
