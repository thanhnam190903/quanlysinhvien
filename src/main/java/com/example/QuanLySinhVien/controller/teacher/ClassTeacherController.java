package com.example.QuanLySinhVien.controller.teacher;

import com.example.QuanLySinhVien.dto.ScoreSubjectProjection;
import com.example.QuanLySinhVien.entity.Clazz;
import com.example.QuanLySinhVien.entity.ScoreSubject;
import com.example.QuanLySinhVien.entity.Subject;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.ClassRepository;
import com.example.QuanLySinhVien.repository.ScoreSubjectRepository;
import com.example.QuanLySinhVien.repository.SubjectRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.sql.Date;
import java.util.List;
@Controller
@RequestMapping("/teacher")
public class ClassTeacherController {
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScoreSubjectRepository scoreSubjectRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping("/class-teacher")
    public String getClassByTeacher(@RequestParam(value = "keyword", required = false) String keyword, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, Model model, Principal principal) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Clazz> classList = classRepository.searchClassesByTeacherId(principal.getName(), keyword, pageable);
        User profile = userRepository.findById(principal.getName()).orElse(null);

        model.addAttribute("classList", classList);
        model.addAttribute("profile", profile);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "class");

        return "admin/class-teacher";
    }
    @GetMapping("/student-class")
    public String getAllStudent(@RequestParam("classId") int classId,
                                @RequestParam(value = "studentId", required = false) String studentId,
                                Model model,
                                Principal principal) {

        User teacher = userRepository.findById(principal.getName()).orElse(null);
        List<User> studentList = userRepository.findStudentsByClassId(classId);
        Clazz clazz = classRepository.findById(classId).orElse(null);
        model.addAttribute("clazz", clazz);
        model.addAttribute("studentList", studentList);
        model.addAttribute("profile", teacher);
        model.addAttribute("activePage", "class");
        if (studentId != null) {
            List<ScoreSubjectProjection> scores =
                    scoreSubjectRepository.getScoreSubjectRaw(studentId,teacher.getId());

            User student = userRepository.findById(studentId).orElse(null);

            model.addAttribute("student", student);
            model.addAttribute("scores", scores);
            model.addAttribute("showModal", true);
        }

        return "admin/student-class";
    }
    @PostMapping("/add-score-subject")
    public String addScore(@ModelAttribute ScoreSubject scoreSubject,@RequestParam("classId") int classId,
                           RedirectAttributes redirectAttributes){
        Subject sub = subjectRepository.findById(scoreSubject.getSubject().getId()).orElse(null);
        User student = userRepository.findById(scoreSubject.getStudent().getId()).orElse(null);
        if (sub != null && student != null){
            ScoreSubject score = new ScoreSubject();
            Date today = new Date(System.currentTimeMillis());
            score.setScoreProcess(scoreSubject.getScoreProcess());
            score.setScoreFinal(scoreSubject.getScoreFinal());
            score.setTotalScore((scoreSubject.getScoreProcess()*sub.getProcessCoefficient())+
                    (scoreSubject.getScoreFinal()*sub.getExamCoefficient()));
            score.setDeleted(false);
            score.setAttempt(1);
            score.setExamAttempt(1);
            score.setCreatedAt(today);
            score.setLastModified(today);
            if (score.getTotalScore() < 5){
                score.setDescription("Học lại");
            }
            score.setStudent(student);
            score.setSubject(sub);
            scoreSubjectRepository.save(score);
            redirectAttributes.addFlashAttribute("successMess", "Thêm điểm thành công!");
        }else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sinh viên và môn học!");
        }

        return "redirect:/teacher/student-class?classId=" + classId + "&studentId=" + student.getId();
    }
    @PostMapping("/update-score-subject")
    public String updateScore(@ModelAttribute ScoreSubject scoreSubject,@RequestParam("classId") int classId,
                           RedirectAttributes redirectAttributes) {
        ScoreSubject score = scoreSubjectRepository.findById(scoreSubject.getId()).orElse(null);
        if (score != null) {
            Date today = new Date(System.currentTimeMillis());
            score.setScoreProcess(scoreSubject.getScoreProcess());
            score.setScoreFinal(scoreSubject.getScoreFinal());
            score.setTotalScore((scoreSubject.getScoreProcess() * score.getSubject().getProcessCoefficient()) +
                    (scoreSubject.getScoreFinal() * score.getSubject().getExamCoefficient()));
            score.setLastModified(today);
            if (score.getTotalScore() < 5){
                score.setDescription("Học lại");
            }
            scoreSubjectRepository.save(score);
            redirectAttributes.addFlashAttribute("successMess" , "Sửa điểm thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage" , "Không tìm thấy điểm!");
        }

        return "redirect:/teacher/student-class?classId=" + classId + "&studentId=" + score.getStudent().getId();
    }

}
