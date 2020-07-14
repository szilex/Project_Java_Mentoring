package com.euvic.mentoring.service;

import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.Student;
import com.euvic.mentoring.repository.MentorRepository;
import com.euvic.mentoring.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    @Transactional
    public Mentor getMentor() {
        return mentorRepository.findFirstByOrderByIdAsc();
    }

    //TODO: Handle no such element exception thrown by get() method
    @Override
    @Transactional
    public Student getStudent(int id) {
        return studentRepository.findById(id).get();
    }

    @Override
    @Transactional
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }
}
