package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.Student;

import java.util.List;

public interface IUserService {

    Mentor getMentor() throws UserNotFoundException;
    Mentor getMentor(int id) throws UserNotFoundException;
    Student getStudent(int id) throws UserNotFoundException;
    List<Student> getStudents();
    Student saveStudent(Student student);
    void deleteStudent(int id) throws UserNotFoundException;
}
