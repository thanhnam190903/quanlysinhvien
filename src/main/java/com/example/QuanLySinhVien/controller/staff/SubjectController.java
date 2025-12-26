package com.example.QuanLySinhVien.controller.staff;

import com.example.QuanLySinhVien.entity.*;
import com.example.QuanLySinhVien.repository.CycleRepository;
import com.example.QuanLySinhVien.repository.SubjectRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/staff")
@Controller
public class SubjectController {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private CycleRepository cycleRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/subject")
    public String getSubject(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model, Principal principal) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "cycle"));
        Page<Subject> subjects = subjectRepository.searchSubjects(keyword, pageable);

        List<User> teacherList = userRepository.getAllTeacher();
        List<Cycle> cycleList = cycleRepository.getAllCycle();
        User profile = userRepository.findById(principal.getName()).orElse(null);

        Map<String, Object> data = new HashMap<>();
        data.put("profile", profile);
        data.put("subjectList", subjects);
        data.put("teacherList", teacherList);
        data.put("cycleList", cycleList);
        data.put("subject", new Subject());
        data.put("keyword", keyword);
        data.put("activePage", "subject");
        model.addAllAttributes(data);
        return "admin/subject";
    }
    @PostMapping("/add-subject")
    public String addSubject(@ModelAttribute("subject") Subject subject, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if (subject.getProcessCoefficient() + subject.getExamCoefficient() != 1.0) {
            result.reject("coefficientError", "Tổng hệ số phải bằng 1");
        }
        if (result.hasErrors()){
            List<Subject> subjects = subjectRepository.getAllSubject();
            List<User> list = userRepository.getAllTeacher();
            List<Cycle> cycleList = cycleRepository.getAllCycle();
            Map<String, Object> data = new HashMap<>();
            data.put("subjectList", subjects);
            data.put("teacherList", list);
            data.put("cycleList", cycleList);
            data.put("subject",subject);
            data.put("showModal", true);
            model.addAllAttributes(data);
            return "admin/subject";
        }
        Date today = new Date(System.currentTimeMillis());
        subject.setCreatedAt(today);
        subject.setLastModified(today);
        subject.setDeleted(false);

        subjectRepository.save(subject);
        redirectAttrs.addFlashAttribute("successMessage", "Thêm môn học thành công!");
        return "redirect:/staff/subject";
    }
    @PostMapping("/update-subject")
    public String updateSubject(@ModelAttribute Subject subject, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        Subject subject1 = subjectRepository.findById(subject.getId()).orElse(null);

         if (subject1 != null){
            subject1.setName(subject.getName());
            subject1.setCredit(subject.getCredit());
            subject1.setProcessCoefficient(subject.getProcessCoefficient());
            subject1.setExamCoefficient(subject.getExamCoefficient());
            subject1.setCycle(subject.getCycle());
            subject1.setUser(subject.getUser());
            subject1.setStatus(subject.isStatus());
            Date today = new Date(System.currentTimeMillis());
            subject1.setLastModified(today);
            subjectRepository.save(subject1);
            redirectAttrs.addFlashAttribute("successMessage", "Cập nhật môn học thành công!");
        }else {
             redirectAttrs.addFlashAttribute("errorMessage", "Không tìm thấy môn học!");
         }
        return "redirect:/staff/subject";
    }
    @GetMapping("/delete-subject")
    public String deleteSubject(@RequestParam("id") int id){
        subjectRepository.deleteSubject(id);
        return "redirect:/staff/subject";
    }
    @PostMapping("/add-time-subject")
    public String addTimeSubject(@ModelAttribute Subject subject, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        Subject subject1 = subjectRepository.findById(subject.getId()).orElse(null);
        if (subject1 != null){
            subject1.setStartDate(subject.getStartDate());
            subject1.setEndDate(subject.getEndDate());
            subject1.setRegradeStart(subject.getEndDate());
            subject1.setRegradeEnd(subject.getEndDate().plusDays(10));
            Date today = new Date(System.currentTimeMillis());
            subject1.setLastModified(today);
            subjectRepository.save(subject1);
            redirectAttrs.addFlashAttribute("successMess", "Thêm thời gian thành công!");
        }else {
            redirectAttrs.addFlashAttribute("errorMessage", "Không tìm thấy môn học!");
        }
        return "redirect:/staff/subject";
    }
}
