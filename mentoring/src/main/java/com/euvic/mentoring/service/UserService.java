package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.Student;
import com.euvic.mentoring.repository.MentorRepository;
import com.euvic.mentoring.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private MentorRepository mentorRepository;
    private StudentRepository studentRepository;

    @Autowired
    public UserService(MentorRepository mentorRepository, StudentRepository studentRepository) {
        this.mentorRepository = mentorRepository;
        this.studentRepository = studentRepository;
    }


    @Override
    public Mentor getMentor() throws UserNotFoundException {

        Optional<Mentor> mentor = mentorRepository.findFirstByOrderByIdAsc();
        if (mentor.isPresent()) {
            return mentor.get();
        }

        throw new UserNotFoundException();
    }

    @Override
    public Mentor getMentor(int id) throws UserNotFoundException {

        Optional<Mentor> mentor = mentorRepository.findById(id);
        if (mentor.isPresent()) {
            return mentor.get();
        }

        throw new UserNotFoundException(id);
    }

    @Override
    public Student getStudent(int id) throws UserNotFoundException {

        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            return student.get();
        }

        throw new UserNotFoundException(id);
    }

    @Override
    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public void deleteStudent(int id) throws NoSuchElementException, UserNotFoundException {
        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            studentRepository.delete(student.get());
            return;
        }

        throw new UserNotFoundException(id);
    }
}
