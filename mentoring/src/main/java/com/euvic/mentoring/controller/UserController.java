package com.euvic.mentoring.controller;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/mentor")
    public User getMentor() throws UserNotFoundException {
        return userService.getMentor();
    }

    @GetMapping("/student/{id}")
    public User getStudent(@PathVariable int id) throws UserNotFoundException {
        return userService.getStudent(id);
    }

    @GetMapping("/student")
    public List<User> getStudents() {
        return userService.getStudents();
    }

    @PostMapping("/student")
    public User saveStudent(@RequestBody User student) {
        return userService.saveStudent(student);
    }

    @PutMapping("/student")
    public User updateStudent(@RequestBody User student) throws UserNotFoundException {
        return userService.updateStudent(student);
    }

    @DeleteMapping("/student/{id}")
    public void deleteStudent(@PathVariable int id) throws UserNotFoundException {
        userService.deleteStudent(id);
    }
}
