package com.example.QuanLySinhVien.controller.staff;

import com.example.QuanLySinhVien.entity.Cycle;
import com.example.QuanLySinhVien.entity.ScoreSubject;
import com.example.QuanLySinhVien.entity.Subject;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.CycleRepository;
import com.example.QuanLySinhVien.repository.RegradeRequestRepository;
import com.example.QuanLySinhVien.repository.ScoreSubjectRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@RequestMapping("/staff")
@Controller
public class ScoreStudentController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ScoreSubjectRepository scoreSubjectRepository;
    @Autowired
    private RegradeRequestRepository regradeRequestRepository;
    @Autowired
    private CycleRepository cycleRepository;

    @GetMapping("/score-student")
    public String show(@RequestParam("id") String id, Principal principal, Model model){
        User profile = userRepository.findById(principal.getName()).orElse(null);
        List<ScoreSubject> allScores = scoreSubjectRepository.findByStudentId(id);
        List<Cycle> cycles = cycleRepository.findCyclesByStudent(id);
        Long totalSubjects = scoreSubjectRepository.countAllSubjects(id);
        Double overallAvg = scoreSubjectRepository.getOverallAverage(id);
        Double avgScore4 = scoreFour(overallAvg);
        String status = calculateStatus(avgScore4);

        Map<Integer, List<ScoreSubject>> scoresByCycle = new HashMap<>();
        Map<Integer, Double> avgScore10ByCycle = new HashMap<>();
        Map<Integer, Double> avgScore4ByCycle = new HashMap<>();
        Map<Integer, Long> subjectCountByCycle = new HashMap<>();
        Map<Integer, Long> regradeCountByCycle = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        for (Cycle cycle : cycles) {
            List<ScoreSubject> cycleScores = scoreSubjectRepository
                    .findByStudentAndCycle(id, cycle.getId());
            scoresByCycle.put(cycle.getId(), cycleScores);

            Double avg10 = scoreSubjectRepository
                    .getAverageScoreByCycle(id, cycle.getId());
            avgScore10ByCycle.put(cycle.getId(),
                    avg10 != null ? Math.round(avg10 * 100.0) / 100.0 : 0.0);

            avgScore4ByCycle.put(cycle.getId(),
                    avg10 != null ? Math.round(scoreFour(avg10) * 100.0) / 100.0 : 0.0);

            Long count = scoreSubjectRepository.countByCycle(id, cycle.getId());
            subjectCountByCycle.put(cycle.getId(), count);

            Long regradeCount = regradeRequestRepository
                    .countByCycle(id, cycle.getId());
            regradeCountByCycle.put(cycle.getId(), regradeCount);
        }

        Map<Integer, Boolean> hasRegradeMap = new HashMap<>();
        Map<Integer, Boolean> canRegradeMap = new HashMap<>();

        for (ScoreSubject score : allScores) {
            Long regradeExists = regradeRequestRepository
                    .checkRegradeExists(id, score.getSubject().getId());
            hasRegradeMap.put(score.getSubject().getId(), regradeExists > 0);

            boolean canRegrade = canRequestRegrade(score.getSubject());
            canRegradeMap.put(score.getSubject().getId(), canRegrade);
        }

        data.put("totalSubjects", totalSubjects);
        data.put("overallStatus", status);
        data.put("overallAvgScore4",
                overallAvg != null ? Math.round(avgScore4 * 100.0) / 100.0 : 0.0);
        data.put("overallAvgScore10",
                overallAvg != null ? Math.round(overallAvg * 100.0) / 100.0 : 0.0);

        data.put("cycles", cycles);
        data.put("scoresByCycle", scoresByCycle);
        data.put("avgScore10ByCycle", avgScore10ByCycle);
        data.put("avgScore4ByCycle", avgScore4ByCycle);
        data.put("subjectCountByCycle", subjectCountByCycle);
        data.put("regradeCountByCycle", regradeCountByCycle);

        data.put("hasRegradeMap", hasRegradeMap);
        data.put("canRegradeMap", canRegradeMap);

        data.put("studentName", principal.getName());
        data.put("profile", profile);
        data.put("activePage","department");
        model.addAllAttributes(data);
        return "admin/staff-student";
    }
    private double scoreFour(Double score) {
        if (score == null) return 0;
        if (score >= 8.5) return 4.0;
        if (score >= 8.0) return 3.5;
        if (score >= 7.0) return 3.0;
        if (score >= 6.5) return 2.5;
        if (score >= 5.5) return 2.0;
        if (score >= 5.0) return 1.5;
        if (score >= 4.0) return 1.0;
        return 0;
    }
    private String calculateStatus(Double score) {
        if (score == null) return "";
        if (score >= 3.6) return "GIỎI";
        if (score >= 3.2) return "KHÁ";
        if (score >= 2.5) return "TRUNG BÌNH";
        return "YẾU";
    }
    private boolean canRequestRegrade(Subject subject) {
        LocalDate now = LocalDate.now();
        return subject.getRegradeStart() != null &&
                subject.getRegradeEnd() != null &&
                !now.isBefore(subject.getRegradeStart()) &&
                !now.isAfter(subject.getRegradeEnd());
    }
}
