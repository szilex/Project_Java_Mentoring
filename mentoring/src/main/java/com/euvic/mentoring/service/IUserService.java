package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.User;

import java.util.List;

public interface IUserService {

    User getMentor() throws UserNotFoundException;
    User getStudent(int id) throws UserNotFoundException;
    List<User> getStudents();
    User saveStudent(User student);
    User updateStudent(User student) throws UserNotFoundException;
    void deleteStudent(int id) throws UserNotFoundException;
}
