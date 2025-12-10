package com.example.QuanLySinhVien.controller.student;

import com.example.QuanLySinhVien.entity.Cycle;
import com.example.QuanLySinhVien.entity.Department;
import com.example.QuanLySinhVien.entity.Subject;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.CycleRepository;
import com.example.QuanLySinhVien.repository.DepartmentRepository;
import com.example.QuanLySinhVien.repository.SubjectRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequestMapping("/student")
public class StudentSubjectController {
    private UserRepository userRepository;
    private DepartmentRepository departmentRepository;
    private SubjectRepository subjectRepository;
    private CycleRepository cycleRepository;
    @Autowired
    public StudentSubjectController(UserRepository userRepository, DepartmentRepository departmentRepository, SubjectRepository subjectRepository, CycleRepository cycleRepository) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.subjectRepository = subjectRepository;
        this.cycleRepository = cycleRepository;
    }

    @GetMapping("/student-subject")
    public String show(Principal principal, Model model, @RequestParam(required = false) String key,
                       @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "7") int size) {
        User student = userRepository.findById(principal.getName()).orElse(null);
        List<Cycle> cycList = cycleRepository.findAll();
        Department department = departmentRepository.findDepartmentByStudentId(student.getId());
        if(Objects.isNull(department)) {
            Map<String, Object> data = new HashMap<>();
            data.put("profile", student);
            model.addAllAttributes(data);
            return "admin/layout/403";
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Subject> pageSubjects;

        if (key == null || key.equals("") || key.equals("Tất cả học kỳ")) {
            pageSubjects = subjectRepository.findSubjectsByDepartmentAndCycle(department.getId(), null, pageable);
        } else {
            pageSubjects = subjectRepository.findSubjectsByDepartmentAndCycle(department.getId(), Integer.parseInt(key), pageable);
        }
        List<Subject> subjects = pageSubjects.getContent();
        Map<Integer, String> statusMap = new HashMap<>();
        LocalDate today = LocalDate.now();
        int totalSubjects = subjects.size();
        int totalCredits = 0;
        int countFinished = 0;
        int countOngoing = 0;
        int countComing = 0;
        for (Subject s : subjects) {
            LocalDate st = s.getStartDate();
            LocalDate en = s.getEndDate();
            String status;
            totalCredits += s.getCredit();
            if (st == null) {
                status = "coming";
                countComing++;
            } else if (en != null && en.isEqual(today)) {
                status = "finished";
                countFinished++;
            } else if (st.isAfter(today)) {
                status = "coming";
                countComing++;
            } else if (en != null && en.isBefore(today)) {
                status = "finished";
                countFinished++;
            } else {
                status = "ongoing";
                countOngoing++;
            }
            statusMap.put(s.getId(), status);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("totalSubjects", totalSubjects);
        data.put("totalCredits", totalCredits);
        data.put("countFinished", countFinished);
        data.put("countOngoing", countOngoing);
        data.put("countComing", countComing);
        data.put("currentPage", page);
        data.put("totalPages", pageSubjects.getTotalPages());
        data.put("totalItems", pageSubjects.getTotalElements());
        data.put("cycList", cycList);
        data.put("profile", student);
        data.put("subjects", subjects);
        data.put("statusMap", statusMap);
        data.put("activePage", "student-sub");
        model.addAllAttributes(data);
        return "admin/score-student";
    }
}
