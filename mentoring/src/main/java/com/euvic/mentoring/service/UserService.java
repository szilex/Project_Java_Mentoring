package com.euvic.mentoring.service;

import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.Student;
import com.euvic.mentoring.repository.MentorRepository;
import com.euvic.mentoring.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {

    private MentorRepository mentorRepository;
    private StudentRepository studentRepository;

    @Autowired
    public UserService(MentorRepository mentorRepository, StudentRepository studentRepository) {
        this.mentorRepository = mentorRepository;
        this.studentRepository = studentRepository;
    }


    @Override
    public Mentor getMentor() {
        return mentorRepository.findFirstByOrderByIdAsc();
    }

    @Override
    public Student getStudent(int id) {
        return studentRepository.findById(id).get();          //method throws NoSuchElementException
    }

    @Override
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }
}
