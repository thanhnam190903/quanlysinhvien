package com.example.QuanLySinhVien.controller.teacher;

import com.example.QuanLySinhVien.entity.RegradeRequest;
import com.example.QuanLySinhVien.entity.ScoreSubject;
import com.example.QuanLySinhVien.entity.User;
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

@Controller
@RequestMapping("/teacher")
public class RegradeTeacherControlle {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegradeRequestRepository requestRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private ScoreSubjectRepository scoreSubjectRepository;

    @GetMapping("/regrades-teacher")
    public String show(@RequestParam(required = false) String studentCode, @RequestParam(required = false) String status,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAt,
                       @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
                       Principal principal, Model model){
        User teacher = userRepository.findById(principal.getName()).orElse(null);
        Pageable pageable = PageRequest.of(page, size);
        Page<RegradeRequest> requests = requestRepository.findByTeacherId(teacher.getId(),
                (studentCode == null || studentCode.isEmpty()) ? null : studentCode,
                (status == null || status.isEmpty()) ? null : status,createdAt,pageable);
        model.addAttribute("re",requests);
        model.addAttribute("studentCode", studentCode);
        model.addAttribute("status", status);
        model.addAttribute("createdAt", createdAt);
        model.addAttribute("profile",teacher);
        model.addAttribute("activePage","regrades-teacher");
        return "admin/regrade-teacher";
    }
    @PostMapping("/refuse")
    public String refuse(@ModelAttribute RegradeRequest regradeRequest, Principal principal, RedirectAttributes redirectAttributes) {
        RegradeRequest request = requestRepository.findById(regradeRequest.getId()).orElse(null);

        if (request != null) {
            request.setStatus("Từ chối");
            request.setTeacherNote(regradeRequest.getTrainingNote());
            request.setUpdatedBy(principal.getName());
            request.setUpdatedAt(LocalDate.now());
            emailService.sendEmail(request.getStudent().getEmail(),
                    "Thông tin từ chối phúc khảo",request.getTeacherNote());
            requestRepository.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
        }
        return "redirect:/teacher/regrades-teacher";
    }
    @PostMapping("/agree")
    public String agree(@ModelAttribute RegradeRequest regradeRequest, Principal principal, RedirectAttributes redirectAttributes) {
        RegradeRequest request = requestRepository.findById(regradeRequest.getId()).orElse(null);

        if (request != null) {
            request.setStatus("Đã cập nhật điểm");
            request.setNewScore(regradeRequest.getNewScore());
            request.setUpdatedBy(principal.getName());
            request.setUpdatedAt(LocalDate.now());
            emailService.sendEmail(request.getStudent().getEmail(),
                    "Thông tin phúc khảo","Đã cập nhật lại điểm của bạn sau phúc khảo!");
            ScoreSubject scoreSubject = scoreSubjectRepository.findScoreByStudentAndSubject(request.getStudent().getId(),request.getSubject().getId());
            scoreSubject.setScoreFinal(regradeRequest.getNewScore());
            scoreSubject.setTotalScore((scoreSubject.getScoreProcess() * request.getSubject().getProcessCoefficient()) +
                    (scoreSubject.getScoreFinal() * request.getSubject().getExamCoefficient()));
            scoreSubject.setDescription("Đã phúc khảo điểm");
            scoreSubjectRepository.save(scoreSubject);
            requestRepository.save(request);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
        }
        return "redirect:/teacher/regrades-teacher";
    }
    @GetMapping("/infomation-regrade")
    public String agree(@RequestParam("id") Integer ids, Principal principal,Model model){
        User teacher = userRepository.findById(principal.getName()).orElse(null);
        RegradeRequest request = requestRepository.findById(ids).orElse(null);
        model.addAttribute("r",request);
        model.addAttribute("profile",teacher);
        model.addAttribute("activePage","regrades-teacher");
        return "admin/teacher-regrade";
    }
}
