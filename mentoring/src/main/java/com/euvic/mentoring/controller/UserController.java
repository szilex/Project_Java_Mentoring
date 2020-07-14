package com.euvic.mentoring.controller;

import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.Student;
import com.euvic.mentoring.service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserServiceInterface userService;

    @Autowired
    public UserController(UserServiceInterface userService) {
        this.userService = userService;
    }

    @GetMapping("/mentor")
    public Mentor getMentor() {
        return userService.getMentor();
    }

    @GetMapping("/student/{id}")
    public Student getStudent(@PathVariable int id) {
        return userService.getStudent(id);
    }

    @GetMapping("/student")
    public List<Student> getStudents() {
        return userService.getStudents();
    }


}
