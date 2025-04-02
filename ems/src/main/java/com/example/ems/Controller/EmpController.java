package com.example.ems.Controller;

import com.example.ems.Entity.Emplyoee;
import com.example.ems.Repository.EmpRepo;
import com.example.ems.util.FileUploader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
public class EmpController {

    @Autowired
    private EmpRepo empRepo;

    @Autowired
    private FileUploader uploader;

    // Designations for dropdown or display
    private static final String[] DESIGNATIONS = {"", "Owner", "Manager", "Team Leader", "Developer", "Tester", "Analyst"};

    // Show all employees
    @GetMapping("/emplist/")
    public String showAllEmployees(Model model ) {
        List<Emplyoee> employeeList = empRepo.findAll();
        model.addAttribute("employeeList", employeeList);
        model.addAttribute("designations", DESIGNATIONS);
        return "emplist";
    }

    // Home pagex
    @GetMapping({"/", "/home/"})
    public String showHomePage(Model model) {
        model.addAttribute("title", "Welcome to the Employee Management System");
        return "home"; // Corresponds to home.html
    }

    // View an employee by ID
    @GetMapping("/emp/{id}/")
    public String showEmployee(@PathVariable long id, Model model) {
        Optional<Emplyoee> employee = empRepo.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            model.addAttribute("designations", DESIGNATIONS);
            return "employeeDetails"; // Corresponds to employeeDetails.html
        } else {
            return "redirect:/emplist/";
        }
    }

    // Add a new employee
    @PostMapping("/addEmp/")
    public String addEmployee(Emplyoee employee, @RequestParam("file") MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName != null && !originalFileName.isEmpty()) {
            String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
            Emplyoee savedEmployee = empRepo.save(employee);
            String newFileName = savedEmployee.getId() + "." + extension;

            // Upload the file and handle the result
            boolean isUploaded = uploader.uploadFile(file, newFileName);
            if (isUploaded) {
                savedEmployee.setExtension(extension);  // Store the extension
                empRepo.save(savedEmployee);  // Save the employee with the new file name
            }
        }
        return "redirect:/emplist/";
    }


    @GetMapping("/addEmp/")
    public String showAddEmployeeForm(Model model) {
        model.addAttribute("Emplyoee", new Emplyoee()); // Pass a new Emplyoee object to the form
        model.addAttribute("designations", DESIGNATIONS); // Pass designations if needed
        return "addEmp"; // Corresponds to addEmp.html
    }



    // Delete an employee
    @PostMapping("/emp/delete/{id}")
    public String deleteEmployee(@PathVariable long id) {
        if (empRepo.existsById(id)) {
            empRepo.deleteById(id);
        }
        return "redirect:/emplist/";
    }

    @PostMapping("/emp/edit/{id}/")
    public String updateEmployee(@PathVariable long id, @ModelAttribute Emplyoee employee, @RequestParam("file") MultipartFile file) {
        Optional<Emplyoee> existingEmployee = empRepo.findById(id);
        if (existingEmployee.isPresent()) {
            Emplyoee savedEmployee = existingEmployee.get();
            savedEmployee.setName(employee.getName());
            savedEmployee.setDob(employee.getDob());
            savedEmployee.setSalary(employee.getSalary());
            savedEmployee.setIdDesig(employee.getIdDesig());

            // Handle file upload (if any)
            String originalFileName = file.getOriginalFilename();
            if (originalFileName != null && !originalFileName.isEmpty()) {
                String extension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
                String newFileName = savedEmployee.getId() + "." + extension;
                boolean isUploaded = uploader.uploadFile(file, newFileName);
                if (isUploaded) {
                    savedEmployee.setExtension(extension);
                }
            }

            empRepo.save(savedEmployee);
        }
        return "redirect:/emplist/";
    }



    // About page
    @GetMapping("/about/")
    public String showAboutPage() {
        return "about"; // Corresponds to about.html
    }


}
