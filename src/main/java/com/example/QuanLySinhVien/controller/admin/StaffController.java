package com.example.QuanLySinhVien.controller.admin;

import com.example.QuanLySinhVien.entity.Login;
import com.example.QuanLySinhVien.entity.Role;
import com.example.QuanLySinhVien.entity.User;
import com.example.QuanLySinhVien.repository.RoleRepository;
import com.example.QuanLySinhVien.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@RequestMapping("/quantri")
@Controller
public class StaffController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/staff")
    public String getAll(@RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Model model, Principal principal){

        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }

        Pageable pageable = PageRequest.of(page, size);
        User profile = userRepository.findById(principal.getName()).orElse(null);
        Page<User> staffList = userRepository.searchStaff(keyword, pageable);

        model.addAttribute("staffList", staffList);
        model.addAttribute("staff", new User());
        model.addAttribute("profile", profile);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activePage", "staff");

        return "admin/staff";
    }
    @PostMapping("/add-staff")
    public String addStaff(@ModelAttribute("staff") User user, BindingResult result, RedirectAttributes redirectAttrs, Model model){
        if (result.hasErrors()){
            model.addAttribute("staff", user);
            model.addAttribute("showModal", true);
            return "admin/staff";
        }
        Date today = new Date(System.currentTimeMillis());
        user.setCreatedAt(today);
        user.setLastModified(today);
        user.setDeleted(false);
        user.setStatus(false);
        Role role = roleRepository.findByName("ROLE_staff");
        user.getRoles().add(role);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Login login = new Login();
        login.setUsername(user.getId());
        String hashed = passwordEncoder.encode(user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        login.setPassword(hashed);
        login.setDeleted(false);
        login.setUsers(user);
        user.setLogin(login);
        userRepository.save(user);
        redirectAttrs.addFlashAttribute("successMessage", "Thêm phòng đào tạo thành công!");
        return "redirect:/quantri/staff";
    }
    @PostMapping("/update-staff")
    public String updateStaff(@ModelAttribute User staff, RedirectAttributes redirectAttributes) {
        User existingStaff = userRepository.findById(staff.getId())
                .orElse(null);

        if (existingStaff != null) {
            existingStaff.setName(staff.getName());
            existingStaff.setEmail(staff.getEmail());
            existingStaff.setPhone(staff.getPhone());
            existingStaff.setAddress(staff.getAddress());
            existingStaff.setDateOfBirth(staff.getDateOfBirth());
            Date today = new Date(System.currentTimeMillis());
            existingStaff.setLastModified(today);

            userRepository.save(existingStaff);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật phòng đào tạo thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy nhân viên phòng đào tạo!");
        }

        return "redirect:/quantri/staff";
    }
    @GetMapping("/delete-staff")
    public String deleteStaff(@RequestParam("id") String id){
        userRepository.deleteUser(id);
        return "redirect:/quantri/staff";
    }
    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "File không được để trống");
                return "redirect:/quantri/staff";
            }

            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".xlsx") &&
                    !filename.endsWith(".xls") && !filename.endsWith(".csv"))) {
                redirectAttributes.addFlashAttribute("error", "Chỉ hỗ trợ file .xlsx, .xls, .csv");
                return "redirect:/quantri/staff";
            }

            if (file.getSize() > 10 * 1024 * 1024) {
                redirectAttributes.addFlashAttribute("error", "File vượt quá 10MB");
                return "redirect:/quantri/staff";
            }

            List<User> importedUsers = new ArrayList<>();

            try (InputStream is = file.getInputStream();
                 Workbook workbook = WorkbookFactory.create(is)) {

                Sheet sheet = workbook.getSheetAt(0);

                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    try {
                        String id = getCellValue(row, 0);
                        String name = getCellValue(row, 1);
                        String email = getCellValue(row, 2);
                        String phone = getCellValue(row, 3);
                        String address = getCellValue(row, 4);
                        String dateOfBirth = getCellValue(row, 5);

                        // Kiểm tra dữ liệu bắt buộc
                        if (id == null || id.trim().isEmpty() ||
                                name == null || name.trim().isEmpty()) {
                            continue;
                        }

                        User user = new User();
                        user.setId(id.trim());
                        user.setName(name.trim());
                        user.setEmail(email);
                        user.setPhone(phone);
                        user.setAddress(address);

                        // Parse ngày sinh
                        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                            try {
                                user.setDateOfBirth(LocalDate.parse(dateOfBirth,
                                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            } catch (Exception e) {
                                log.warn("Lỗi parse ngày sinh: {}", dateOfBirth);
                            }
                        }

                        Date today = new Date(System.currentTimeMillis());
                        user.setCreatedAt(today);
                        user.setLastModified(today);
                        user.setDeleted(false);
                        user.setStatus(true);

                        // Thêm role mặc định
                        Role role = roleRepository.findByName("ROLE_staff");
                        if (role != null) {
                            user.getRoles().add(role);
                        }

                        // Tạo Login record
                        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                        Login login = new Login();
                        login.setUsername(user.getId());
                        String hashed = passwordEncoder.encode(user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        login.setPassword(hashed);
                        login.setDeleted(false);
                        login.setUsers(user);
                        user.setLogin(login);

                        importedUsers.add(user);
                    } catch (Exception e) {
                        log.warn("Lỗi dòng {}: {}", i + 1, e.getMessage());
                    }
                }

                // Lưu vào database
                if (!importedUsers.isEmpty()) {
                    userRepository.saveAll(importedUsers);
                    log.info("Import thành công {} bản ghi", importedUsers.size());
                    redirectAttributes.addFlashAttribute("success",
                            "Import thành công " + importedUsers.size() + " bản ghi");
                    redirectAttributes.addFlashAttribute("importedUsers", importedUsers);
                } else {
                    redirectAttributes.addFlashAttribute("error", "Không có dữ liệu hợp lệ trong file");
                }

            } catch (IOException e) {
                log.error("Lỗi đọc file Excel: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("error", "Lỗi đọc file Excel");
            }

            return "redirect:/quantri/staff";

        } catch (Exception e) {
            log.error("Lỗi import: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/quantri/staff";
        }
    }

    private String getCellValue(Row row, int cellIndex) {
        try {
            Cell cell = row.getCell(cellIndex);
            if (cell == null) return "";

            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    }
                    return String.valueOf((long)cell.getNumericCellValue());
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                default:
                    return "";
            }
        } catch (Exception e) {
            return "";
        }
    }
}
