package com.euvic.mentoring.service;

import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.Student;

import java.util.List;

public interface UserServiceInterface {

    Mentor getMentor();
    Student getStudent(int id);
    List<Student> getStudents();
}
