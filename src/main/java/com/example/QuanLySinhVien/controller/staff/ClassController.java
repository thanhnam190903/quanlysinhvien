package com.example.QuanLySinhVien.controller.staff;

import com.example.QuanLySinhVien.entity.Clazz;
import com.example.QuanLySinhVien.entity.Department;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.ClassRepository;
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
import java.util.*;

@RequestMapping("/staff")
@Controller
public class ClassController {
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/classes")
    public String getAllClass(
            @RequestParam("id") int id,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            Model model, Principal principal) {

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Clazz> classList = classRepository.searchClassesByDepartment(id, keyword, pageable);

        List<User> teachers = userRepository.getAllTeacher();
        Department department = departmentRepository.findById(id).orElse(null);
        User profile = userRepository.findById(principal.getName()).orElse(null);

        Clazz clazz = new Clazz();
        clazz.setDepartment(department);

        Map<String, Object> data = new HashMap<>();
        data.put("profile", profile);
        data.put("teacherList", teachers);
        data.put("classList", classList);
        data.put("clazz", clazz);
        data.put("keyword", keyword);
        data.put("departmentId", id);
        data.put("activePage", "department");
        data.put("de", department);
        model.addAllAttributes(data);
        return "admin/class";
    }
    @PostMapping("/add-class")
    public String addDepartment(@ModelAttribute("clazz") Clazz clazz, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        int departmentId = clazz.getDepartment().getId();
        if (result.hasErrors()){
            Department department = departmentRepository.findById(departmentId).orElse(null);
            List<User> teachers = userRepository.getAllTeacher();
            Map<String, Object> data = new HashMap<>();
            data.put("teacherList", teachers);
            data.put("clazz", clazz);
            data.put("de",department);
            data.put("showModal",true);
            model.addAllAttributes(data);
            return "admin/class";
        }
        Date today = new Date(System.currentTimeMillis());
        clazz.setCreatedAt(today);
        clazz.setLastModified(today);
        clazz.setDeleted(false);

        classRepository.save(clazz);
        redirectAttrs.addFlashAttribute("successMessage", "Thêm lớp thành công!");
        return "redirect:/staff/classes?id="+departmentId;
    }
    @PostMapping("/update-class")
    public String updateClass(@ModelAttribute Clazz clazz, RedirectAttributes redirectAttributes) {
        Clazz existingClass = classRepository.findById(clazz.getId())
                .orElse(null);

        if (existingClass != null) {
            existingClass.setName(clazz.getName());
            existingClass.setStatus(clazz.isStatus());
            existingClass.setDepartment(clazz.getDepartment());
            existingClass.setTeacher(clazz.getTeacher());
            Date today = new Date(System.currentTimeMillis());
            existingClass.setLastModified(today);
            classRepository.save(existingClass);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật lớp học thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy lớp học!");
        }

        return "redirect:/staff/classes?id="+clazz.getDepartment().getId();
    }
    @GetMapping("/delete-class")
    public String deleteClass(@RequestParam("id") int id){
        Clazz clazz = classRepository.findById(id).orElse(null);
        classRepository.deleteclass(id);
        return "redirect:/staff/classes?id="+clazz.getDepartment().getId();
    }
    @GetMapping("/class-student")
    public String getAllStudentByClass(@RequestParam("classId") int id, @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size, Model model, Principal principal) {
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<User> studentList = userRepository.searchStudentsByClassId(id, keyword, pageable);

        Clazz clazz = classRepository.findById(id).orElse(null);
        List<User> students = userRepository.findAllStudentsNotInAnyClass();
        long totalStudentNotClass = userRepository.countStudentsNotInAnyClass();
        User profile = userRepository.findById(principal.getName()).orElse(null);

        Map<String, Object> data = new HashMap<>();
        data.put("profile", profile);
        data.put("studentList", studentList);
        data.put("clazz", clazz);
        data.put("classId", id);
        data.put("keyword", keyword);
        data.put("activePage", "department");
        data.put("students", students);
        data.put("total", totalStudentNotClass);

        model.addAllAttributes(data);
        return "admin/class-student";
    }
    @GetMapping("/students/not-in-class")
    @ResponseBody
    public List<User> searchStudentsNotInClass(@RequestParam(name="key", required=false) String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return userRepository.findAllStudentsNotInAnyClass();
        }

        return userRepository.findStudentsNotInClassByKeyword("%" + keyword + "%");
    }
    @PostMapping("/add-studentclass")
    public String addStudentsToClass(@RequestParam("classId") int classId,
                                     @RequestParam(value = "studentIds") List<String> studentIds,
                                     RedirectAttributes redirectAttributes) {
        System.out.println("StudentIds: " + studentIds);
        Clazz clazz = classRepository.findById(classId).orElseThrow();

        for (String studentId : studentIds) {
            User student = userRepository.findById(studentId).orElseThrow();
            if (student.getClasses() == null) student.setClasses(new ArrayList<>());
            if (!student.getClasses().contains(clazz)) {
                student.getClasses().add(clazz);
            }
        }
        userRepository.saveAll(userRepository.findAllById(Arrays.asList(String.valueOf(studentIds))));
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sinh viên vào lớp thành công!");
        return "redirect:/staff/class-student?classId=" + classId;
    }
    @GetMapping("/delete-student-class")
    public String deleteStudentClass(@RequestParam("classId") int id,@RequestParam("studentId") String studentId){
        Clazz clazz = classRepository.findById(id).orElseThrow();
        User student = userRepository.findById(studentId).orElseThrow();

        if (student.getClasses() != null) {
            student.getClasses().remove(clazz);
        }
        userRepository.save(student);
        return "redirect:/staff/class-student?classId="+id;
    }
}
