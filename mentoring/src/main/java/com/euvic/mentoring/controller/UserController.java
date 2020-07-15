package com.euvic.mentoring.controller;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.Student;
import com.euvic.mentoring.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mentor")
    public Mentor getMentor() throws UserNotFoundException {
        return userService.getMentor();
    }

    @GetMapping("/student/{id}")
    public Student getStudent(@PathVariable int id) throws UserNotFoundException {
        return userService.getStudent(id);
    }

    @GetMapping("/student")
    public List<Student> getStudents() {
        return userService.getStudents();
    }

    @PostMapping("/student")
    public Student saveStudent(@RequestBody Student student) {
        return userService.saveStudent(student);
    }

    @PutMapping("/student")
    public Student updateStudent(@RequestBody Student student) {
        return userService.saveStudent(student);
    }

    @DeleteMapping("/student/{id}")
    public void deleteStudent(@PathVariable int id) throws UserNotFoundException {
        userService.deleteStudent(id);
    }


}
