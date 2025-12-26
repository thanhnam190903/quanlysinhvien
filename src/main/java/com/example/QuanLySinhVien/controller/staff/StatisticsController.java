package com.example.QuanLySinhVien.controller.staff;

import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/staff")
@Controller
@RequiredArgsConstructor
public class StatisticsController {
    private final CycleRepository cycleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ScoreSubjectRepository scoreSubjectRepository;
    private final RegradeRequestRepository regradeRequestRepository;

    @GetMapping("/statistics")
    public String getStatistics( Model model, Principal principal) {

        User profile = userRepository.findById(principal.getName()).orElse(null);
        var userRoleStatistics = userRepository.getAllUserRole();
        var userStatus = userRepository.getUserStatus();
        var usersByDept = userRepository.getUserByDept();
        var userAllocations = departmentRepository.getUserAllocation();
        var cycleBySemesters = cycleRepository.getCycleBySemester();
        var subjectsByStudent = subjectRepository.getSubjectsByStudent();
        var departmentsMedium = departmentRepository.getDepartmentsMedium();
        var ratioScoreSubjectsList = scoreSubjectRepository.getRatioScoreSubjects();
        var outstandingStudents = scoreSubjectRepository.getOutstandingStudents();
        var histogramStudents = scoreSubjectRepository.getHistogramStudents();
        var regradeRequests = regradeRequestRepository.getRegradeRequest();
        var regradeRequestRejectAndAccepts = regradeRequestRepository.getRegradeRequestRejectAndAccept();
        var mediumProcessAndAcceptChangeRates = regradeRequestRepository.getMediumProcessAndAcceptChangeRate();
        var regradeRequestByDepartments = regradeRequestRepository.getRegradeRequestByDepartment();

        Map<String, Object> data = new HashMap<>();
        data.put("profile", profile);
        data.put("userRoleStatistics", userRoleStatistics);
        data.put("usersByDept", usersByDept);
        data.put("userStatus", userStatus == null ? new Object(): userStatus.getFirst());
        data.put("userAllocation", userAllocations == null ? new Object(): userAllocations.getFirst());
        data.put("regradeRequest", regradeRequests == null ? new Object(): regradeRequests.getFirst());
        data.put("regradeRequestRejectAndAccept", regradeRequestRejectAndAccepts == null ? new Object(): regradeRequestRejectAndAccepts.getFirst());
        data.put("mediumProcessAndAcceptChangeRate", mediumProcessAndAcceptChangeRates == null ? new Object(): mediumProcessAndAcceptChangeRates.getFirst());
        data.put("regradeRequestByDepartments", regradeRequestByDepartments);
        data.put("cycleBySemesters", cycleBySemesters);
        data.put("subjectsByStudent", subjectsByStudent);
        data.put("departmentsMedium", departmentsMedium);
        data.put("ratioScoreSubjectsList", ratioScoreSubjectsList);
        data.put("outstandingStudents", outstandingStudents);
        data.put("histogramStudents", histogramStudents);
        data.put("activePage","staff-statistics");
        model.addAllAttributes(data);
        return "admin/staff-statistics";
    }
}
