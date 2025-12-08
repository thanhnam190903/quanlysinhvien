package com.example.QuanLySinhVien.controller.staff;

import com.example.QuanLySinhVien.entity.Cycle;
import com.example.QuanLySinhVien.entity.Department;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.CycleRepository;
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
public class CycleController {
    @Autowired
    private CycleRepository cycleRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/cycles")
    public String show(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model, Principal principal) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Cycle> cycles = cycleRepository.searchCycles(keyword, pageable);
        User profile = userRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("profile", profile);
        model.addAttribute("cycles", cycles);
        model.addAttribute("cycle", new Cycle());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "cycle");
        return "admin/cycle";
    }
    @PostMapping("/add-cycle")
    public String addCycle(@ModelAttribute("cycle") Cycle cycle, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if (result.hasErrors()){
            model.addAttribute("cycle", cycle);
            model.addAttribute("showModal", true);
            return "admin/cycle";
        }
        Date today = new Date(System.currentTimeMillis());
        cycle.setCreatedAt(today);
        cycle.setLastModified(today);
        cycle.setDeleted(false);

        cycleRepository.save(cycle);
        redirectAttrs.addFlashAttribute("successMessage", "Thêm học kỳ thành công!");
        return "redirect:/staff/cycles";
    }
    @PostMapping("/update-cycle")
    public String updateCycle(@ModelAttribute Cycle cycle, RedirectAttributes redirectAttributes) {
        Cycle existingCycle = cycleRepository.findById(cycle.getId())
                .orElse(null);

        if (existingCycle != null) {
            existingCycle.setName(cycle.getName());
            existingCycle.setStatus(cycle.isStatus());
            existingCycle.setStartDate(cycle.getStartDate());
            existingCycle.setEndDate(cycle.getEndDate());
            Date today = new Date(System.currentTimeMillis());
            existingCycle.setLastModified(today);
            cycleRepository.save(existingCycle);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật học kỳ thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy học kỳ!");
        }

        return "redirect:/staff/cycles";
    }
    @GetMapping("/delete-cycle")
    public String deleteCycle(@RequestParam("id") int id){
        Cycle cycle = cycleRepository.findById(id).orElse(null);
        cycle.setDeleted(true);
        cycleRepository.save(cycle);
        return "redirect:/staff/cycles";
    }
}
