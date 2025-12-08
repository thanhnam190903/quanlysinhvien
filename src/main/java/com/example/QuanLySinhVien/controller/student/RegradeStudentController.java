package com.example.QuanLySinhVien.controller.student;

import com.example.QuanLySinhVien.entity.Clazz;
import com.example.QuanLySinhVien.entity.RegradeRequest;
import com.example.QuanLySinhVien.entity.ScoreSubject;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.*;
import com.example.QuanLySinhVien.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@RequestMapping("/student")
@Controller
public class RegradeStudentController {

    private UserRepository userRepository;
    private RegradeRequestRepository regradeRequestRepository;
    private ScoreSubjectRepository scoreSubjectRepository;
    private ClassRepository classRepository;
    private StorageService storageService;

    @Autowired
    public RegradeStudentController(UserRepository userRepository, RegradeRequestRepository regradeRequestRepository, ScoreSubjectRepository scoreSubjectRepository, ClassRepository classRepository, StorageService storageService) {
        this.userRepository = userRepository;
        this.regradeRequestRepository = regradeRequestRepository;
        this.scoreSubjectRepository = scoreSubjectRepository;
        this.classRepository = classRepository;
        this.storageService = storageService;
    }

    @GetMapping("/regrade-student")
    public String show(@RequestParam("scoreId") String subId, @RequestParam(value = "rId", required = false) Integer rId,
            Principal principal, Model model) {
        User student = userRepository.findById(principal.getName()).orElse(null);
        ScoreSubject scoreSubject = scoreSubjectRepository.findById(Integer.parseInt(subId)).orElse(null);
        Clazz clazz = classRepository.findClassByStudentId(student.getId());
        RegradeRequest request = null;
        if (rId != null) {
            request = regradeRequestRepository.findById(rId).orElse(null);
        }
        model.addAttribute("req", request);
        model.addAttribute("clazz", clazz);
        model.addAttribute("profile", student);
        model.addAttribute("sco", scoreSubject);
        model.addAttribute("activePage", "regrade-student");

        return "admin/regrade-student";
    }
    @GetMapping("/regrades")
    public String shows(Principal principal, Model model){
        User student = userRepository.findById(principal.getName()).orElse(null);
        List<RegradeRequest> requests = regradeRequestRepository.findByStudentCode(student.getId());
        model.addAttribute("re",requests);
        model.addAttribute("profile",student);
        model.addAttribute("activePage","regrade-student");
        return "admin/regrade";
    }
    @PostMapping("/add-regrade")
    public String add(@ModelAttribute RegradeRequest regradeRequest, @RequestParam("payment") MultipartFile file,
                      Model model, RedirectAttributes redirectAttributes){
        this.storageService.store(file);
        RegradeRequest request = new RegradeRequest();
        if (regradeRequest.getId() != null){
            request.setId(regradeRequest.getId());
        }
        request.setStudent(regradeRequest.getStudent());
        request.setSubject(regradeRequest.getSubject());
        if (file != null){
            String fileImage = file.getOriginalFilename();
            request.setPaymentImage(fileImage);
        }
        if (regradeRequest.getStatus().equals("Lưu nháp")){
            request.setStatus("Nháp");
        }else{
            request.setStatus("Chờ xét duyệt");
            request.setRequestDate(LocalDate.now());
            request.setCreatedBy(regradeRequest.getStudent().getId());
        }
        request.setRequestReason(regradeRequest.getRequestReason());
        request.setOldScore(regradeRequest.getOldScore());
        request.setNewScore(request.getOldScore());
        request.setDeleted(false);
        request.setCreatedAt(LocalDate.now());
        regradeRequestRepository.save(request);
        redirectAttributes.addFlashAttribute("successMessage", "Đã lưu đơn phúc khảo!");
        return "redirect:/student/regrades";
    }
    @GetMapping("/delete-reg")
    public String delete(@RequestParam("id") int id){
        RegradeRequest regradeRequest = regradeRequestRepository.findById(id).orElse(null);
        regradeRequest.setDeleted(true);
        regradeRequestRepository.save(regradeRequest);
        return "redirect:/student/regrades";
    }
}
