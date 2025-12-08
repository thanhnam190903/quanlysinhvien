package com.example.QuanLySinhVien.controller.staff;

import com.example.QuanLySinhVien.entity.RegradeRequest;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.ClassRepository;
import com.example.QuanLySinhVien.repository.RegradeRequestRepository;
import com.example.QuanLySinhVien.repository.ScoreSubjectRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import com.example.QuanLySinhVien.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("/staff")
@Controller
public class RegradeStaffController {
    private UserRepository userRepository;
    private RegradeRequestRepository regradeRequestRepository;
    private ScoreSubjectRepository scoreSubjectRepository;
    private ClassRepository classRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    public RegradeStaffController(UserRepository userRepository, RegradeRequestRepository regradeRequestRepository, ScoreSubjectRepository scoreSubjectRepository, ClassRepository classRepository) {
        this.userRepository = userRepository;
        this.regradeRequestRepository = regradeRequestRepository;
        this.scoreSubjectRepository = scoreSubjectRepository;
        this.classRepository = classRepository;
    }
    @GetMapping("/regrades-staff")
    public String showRegrades(Principal principal, Model model, @RequestParam(required = false) String studentCode,
            @RequestParam(required = false) String subjectName, @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdDate,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
        User staff = userRepository.findById(principal.getName()).orElse(null);
        model.addAttribute("profile", staff);
        Pageable pageable = PageRequest.of(page, size);
        Page<RegradeRequest> requests = regradeRequestRepository.searchRegrades(
                (studentCode == null || studentCode.isEmpty()) ? null : studentCode,
                (subjectName == null || subjectName.isEmpty()) ? null : subjectName,
                createdDate,
                pageable
        );
        model.addAttribute("re", requests);
        model.addAttribute("studentCode", studentCode);
        model.addAttribute("subjectName", subjectName);
        model.addAttribute("createdDate", createdDate);

        model.addAttribute("activePage","regrade-staff");

        return "admin/regrade-staff";
    }
    @PostMapping("/refuse")
    public String refuse(@ModelAttribute RegradeRequest regradeRequest,Principal principal,RedirectAttributes redirectAttributes){
        RegradeRequest request = regradeRequestRepository.findById(regradeRequest.getId()).orElse(null);
        if (request != null){
            request.setStatus("Từ chối");
            request.setTrainingNote(regradeRequest.getTrainingNote());
            request.setUpdatedBy(principal.getName());
            request.setUpdatedAt(LocalDate.now());
            emailService.sendEmail(request.getStudent().getEmail(),
                    "Thông tin từ chối phúc khảo",request.getTrainingNote());
            regradeRequestRepository.save(request);
            redirectAttributes.addFlashAttribute("successMessage","Cập nhật thành công!");
        }
        return "redirect:/staff/regrades-staff";
    }
    @GetMapping("/agree")
    public String agree(@RequestParam("ids") List<Integer> ids, Principal principal, RedirectAttributes attributes){
        for (Integer id : ids) {
            RegradeRequest r = regradeRequestRepository.findById(id).orElse(null);
            if (r != null) {
                r.setStatus("Đã duyệt hồ sơ");
                r.setUpdatedAt(LocalDate.now());
                r.setUpdatedBy(principal.getName());
                regradeRequestRepository.save(r);
            }
        }
        attributes.addFlashAttribute("successMessage","Cập nhật thành công!");
        return "redirect:/staff/regrades-staff";
    }
    @GetMapping("/info-regrade")
    public String agree(@RequestParam("id") Integer ids, Principal principal,Model model){
        User staff = userRepository.findById(principal.getName()).orElse(null);
        RegradeRequest request = regradeRequestRepository.findById(ids).orElse(null);
        model.addAttribute("r",request);
        model.addAttribute("profile",staff);
        model.addAttribute("activePage","regrade-staff");
        return "admin/staff-regrade";
    }
}
