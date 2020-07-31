package com.euvic.mentoring.user;

import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenOneMentor_whenFindFirstByAuthorityMentorOrderByIdAsc_thenReturnMentor() {

        User mentor = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        testEntityManager.persist(mentor);
        testEntityManager.flush();

        Optional<User> found = userRepository.findFirstByAuthorityOrderByIdAsc("MENTOR");

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor);
    }

    @Test
    void givenMultipleMentors_whenFindFirstByAuthorityMentorOrderByIdAsc_thenReturnFirstMentor() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findFirstByAuthorityOrderByIdAsc("MENTOR");

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor1);
    }

    @Test
    void givenMultipleUsers_whenFindFirstByAuthorityMentorOrderByIdAsc_thenReturnFirstMentor() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findFirstByAuthorityOrderByIdAsc("MENTOR");

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor1);
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectIdAndCorrectAuthority_thenReturnUser() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(1, "MENTOR");

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor1);
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectIdAndIncorrectAuthority_thenReturnEmpty() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(3, "MENTOR");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectIdAndNonexistentAuthority_thenReturnEmpty() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(3, "TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentIdAndCorrectAuthority_thenReturnEmpty() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(5, "MENTOR");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentIdAndIncorrectAuthority_thenReturnEmpty() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(5, "TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentIdAndNonexistentAuthority_thenReturnEmpty() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(5, "TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectMailAndCorrectAuthority_thenReturnUser() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority("karenjohns@email.com", "STUDENT");

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(student1);
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectMailAndIncorrectAuthority_thenReturnUser() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority("karenjohns@email.com", "TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentMailAndCorrectAuthority_thenReturnUser() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority("laurenwick@email.com", "STUDENT");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentMailAndIncorrectAuthority_thenReturnUser() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority("laurenwick@email.com", "TEACHER");

        assertThat(found).isEmpty();
    }


    @Test
    void givenMultipleUsers_whenFindByNonexistentMailAndNonexistentAuthority_thenReturnUser() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority("laurenwick@email.com", "TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindAllByAuthorityMentor_thenReturnUserMentorList() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        List<User> mentors = List.of(mentor1, mentor2);

        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("MENTOR");

        assertThat(found).isEqualTo(mentors);
    }

    @Test
    void givenMultipleUsers_whenFindAllByAuthorityStudent_thenReturnUserStudentList() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User(2, "georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User(4, "monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        List<User> students = List.of(student1, student2);

        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("STUDENTS");

        assertThat(found).isEqualTo(students);
    }

    @Test
    void givenStudent_whenFindAllByAuthorityMentor_thenReturnEmptyList() {

        User student1 = new User(3, "karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );

        testEntityManager.persist(student1);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("MENTOR");

        assertThat(found.isEmpty()).isEqualTo(true);
    }

    @Test
    void givenMentor_whenFindAllByAuthorityStudent_thenReturnEmptyList() {

        User mentor1 = new User(1, "johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );

        testEntityManager.persist(mentor1);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("STUDENTS");

        assertThat(found.isEmpty()).isEqualTo(true);
    }

}
