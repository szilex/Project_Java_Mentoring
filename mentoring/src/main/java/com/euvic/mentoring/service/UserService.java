package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private final UserRepository userRepository;

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
    public User getStudent(int id) throws UserNotFoundException {

        Optional<User> student = userRepository.findByIdAndAuthority(id, "ROLE_STUDENT");
        if (student.isPresent()) {
            return student.get();
        }

        throw new UserNotFoundException(id);
    }

    @Override
    public List<User> getStudents() {

        User loggedUser = getCurrentUser();

        switch (loggedUser.getAuthority()) {
            case "ROLE_MENTOR" :
                return userRepository.findAllByAuthority("ROLE_STUDENT");
            case "ROLE_STUDENT" :
                return List.of(loggedUser);
            default:
                throw new UserNotFoundException();
        }
    }

    @Override
    public User saveStudent(User student) {

        if (student == null || student.getMail() == null || student.getPassword() == null || student.getFirstName() == null || student.getLastName() == null) {
            throw new IllegalArgumentException("Insufficient argument list");
        }

        if (student.getId() != 0 || student.getAuthority() != null || student.getEnabled() != 0) {
            throw new IllegalArgumentException("Illegal argument specified");
        }

        Optional<User> dbStudent = userRepository.findByMailAndAuthority(student.getMail(), "ROLE_STUDENT");
        if (dbStudent.isPresent()) {
            throw new IllegalArgumentException("User with specified mail already exists");
        }

        student.setAuthority("ROLE_STUDENT");
        student.setEnabled(1);
        String encodedPassword = new BCryptPasswordEncoder().encode(student.getPassword());
        student.setPassword(encodedPassword);

        return userRepository.save(student);
    }

    @Override
    @Transactional
    public User updateStudent(User student) throws UserNotFoundException {

        if (student == null) {
            throw new IllegalArgumentException("Insufficient argument list");
        }

        if (student.getEnabled() != 0 || student.getAuthority() != null) {
            throw new IllegalArgumentException("Illegal argument specified");
        }

        Optional<User> dbStudent = userRepository.findByIdAndAuthority(student.getId(), "ROLE_STUDENT");
        if (dbStudent.isPresent()) {

            if (isInvokedByIncorrectUser(student.getId())) {
                throw new IllegalArgumentException("Students can only update their own credentials");
            }

            User temporaryStudent = dbStudent.get();
            if (student.getFirstName() != null) temporaryStudent.setFirstName(student.getFirstName());
            if (student.getLastName() != null) temporaryStudent.setLastName(student.getLastName());
            if (student.getMail() != null) temporaryStudent.setMail(student.getMail());
            if (student.getPassword() != null) temporaryStudent.setPassword(new BCryptPasswordEncoder().encode(student.getPassword()));
            temporaryStudent.setEnabled(1);

            return userRepository.save(temporaryStudent);
        }

        throw new UserNotFoundException(student.getId());
    }

    @Override
    @Transactional
    public void deleteStudent(int id) throws NoSuchElementException, UserNotFoundException {
        Optional<User> student = userRepository.findByIdAndAuthority(id, "ROLE_STUDENT");
        if (student.isPresent()) {

            if (isInvokedByIncorrectUser(id)) {
                throw new IllegalArgumentException("Students can only delete their own accounts");
            }

            userRepository.delete(student.get());
            return;
        }

        throw new UserNotFoundException(id);
    }

    private boolean isInvokedByIncorrectUser(int id) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString();

        int currentStudentId = userRepository.findByMailAndAuthority(username, "ROLE_STUDENT").get().getId();

        return currentStudentId != id;
    }

    private User getCurrentUser() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString();

        return userRepository.findByMail(username).get();
    }
}
