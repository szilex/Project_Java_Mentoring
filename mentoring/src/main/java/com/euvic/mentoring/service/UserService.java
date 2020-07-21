package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getMentor() throws UserNotFoundException {

        Optional<User> mentor = userRepository.findFirstByAuthorityOrderByIdAsc("ROLE_MENTOR");
        if (mentor.isPresent()) {
            return mentor.get();
        }

        throw new UserNotFoundException();
    }

    @Override
    public User getMentor(int id) throws UserNotFoundException {

        Optional<User> mentor = userRepository.findByIdAndAuthority(id, "ROLE_MENTOR");
        if (mentor.isPresent()) {
            return mentor.get();
        }

        throw new UserNotFoundException(id);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public User getStudent(int id) throws UserNotFoundException {

        Optional<User> student = userRepository.findByIdAndAuthority(id, "ROLE_STUDENT");
        if (student.isPresent()) {
            return student.get();
        }

        throw new UserNotFoundException(id);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<User> getStudents() {
        return userRepository.findAllByAuthority("ROLE_STUDENT");
    }

    @Override
    @PreAuthorize("not(isAuthenticated())")
    public User saveStudent(User student) {
        student.setAuthority("ROLE_STUDENT");
        student.setEnabled(1);

        return userRepository.save(student);
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public User updateStudent(User student) throws UserNotFoundException {

        Optional<User> dbStudent = userRepository.findByIdAndAuthority(student.getId(), "ROLE_STUDENT");
        if (dbStudent.isPresent()) {

            User temporaryStudent = dbStudent.get();
            if (student.getFirstName() != null) temporaryStudent.setFirstName(student.getFirstName());
            if (student.getLastName() != null) temporaryStudent.setLastName(student.getLastName());
            if (student.getMail() != null) temporaryStudent.setMail(student.getMail());
            if (student.getPassword() != null) temporaryStudent.setPassword(student.getPassword());

            return userRepository.save(temporaryStudent);
        }

        throw new UserNotFoundException(student.getId());
    }

    @Override
    @PreAuthorize("hasRole('STUDENT')")
    public void deleteStudent(int id) throws NoSuchElementException, UserNotFoundException {
        Optional<User> student = userRepository.findByIdAndAuthority(id, "ROLE_STUDENT");
        if (student.isPresent()) {
            userRepository.delete(student.get());
            return;
        }

        throw new UserNotFoundException(id);
    }
}
