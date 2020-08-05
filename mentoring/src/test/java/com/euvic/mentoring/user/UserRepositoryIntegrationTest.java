package com.euvic.mentoring.user;

import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
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

    @Before
    public void setup() {
        testEntityManager.clear();
    }

    @Test
    void givenOneMentor_whenFindFirstByAuthorityMentorOrderByIdAsc_thenReturnMentor() {

        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        testEntityManager.persist(mentor);
        testEntityManager.flush();

        Optional<User> found = userRepository.findFirstByAuthorityOrderByIdAsc(mentor.getAuthority());

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor);
    }

    @Test
    void givenMultipleMentors_whenFindFirstByAuthorityMentorOrderByIdAsc_thenReturnFirstMentor() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findFirstByAuthorityOrderByIdAsc(mentor1.getAuthority());

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor1);
    }

    @Test
    void givenMultipleUsers_whenFindFirstByAuthorityMentorOrderByIdAsc_thenReturnFirstMentor() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findFirstByAuthorityOrderByIdAsc(mentor1.getAuthority());

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor1);
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectIdAndCorrectAuthority_thenReturnUser() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        int mentorId = (int) testEntityManager.persistAndGetId(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(mentorId, mentor1.getAuthority());

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(mentor1);
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectIdAndIncorrectAuthority_thenReturnEmpty() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        int studentId = (int) testEntityManager.persistAndGetId(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(studentId, mentor1.getAuthority());

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectIdAndNonexistentAuthority_thenReturnEmpty() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        int studentId = (int) testEntityManager.persistAndGetId(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(studentId, "ROLE_TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentIdAndCorrectAuthority_thenReturnEmpty() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(5489, mentor1.getAuthority());

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentIdAndNonexistentAuthority_thenReturnEmpty() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByIdAndAuthority(5489, "ROLE_TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectMailAndCorrectAuthority_thenReturnUser() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority(student1.getMail(), student1.getAuthority());

        assertThat(found).isNotEmpty();
        assertThat(found.get()).isEqualTo(student1);
    }

    @Test
    void givenMultipleUsers_whenFindByCorrectMailAndNonexistentAuthority_thenReturnUser() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority(student1.getMail(), "ROLE_TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentMailAndCorrectAuthority_thenReturnUser() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority("laurenwick@email.com", student1.getAuthority());

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindByNonexistentMailAndNonexistentAuthority_thenReturnUser() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        Optional<User> found = userRepository.findByMailAndAuthority("laurenwick@email.com", "ROLE_TEACHER");

        assertThat(found).isEmpty();
    }

    @Test
    void givenMultipleUsers_whenFindAllByAuthorityMentor_thenReturnUserMentorList() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        List<User> mentors = List.of(mentor1, mentor2);
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("ROLE_MENTOR");

        assertThat(found).isEqualTo(mentors);
    }

    @Test
    void givenMultipleUsers_whenFindAllByAuthorityStudent_thenReturnUserStudentList() {

        User mentor1 = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );
        User mentor2 = new User("georgeadams@email.com", "pass123", "ROLE_MENTOR", 1, "George", "Adams" );
        User student1 = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        User student2 = new User("monicadaniels@email.com", "pass123", "ROLE_STUDENT", 1, "Monica", "Daniels" );
        List<User> students = List.of(student1, student2);
        testEntityManager.persist(mentor1);
        testEntityManager.persist(mentor2);
        testEntityManager.persist(student1);
        testEntityManager.persist(student2);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("ROLE_STUDENT");

        assertThat(found).isEqualTo(students);
    }

    @Test
    void givenStudent_whenFindAllByAuthorityMentor_thenReturnEmptyList() {

        User student = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );

        testEntityManager.persist(student);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("ROLE_MENTOR");

        assertThat(found.isEmpty()).isEqualTo(true);
    }

    @Test
    void givenMentor_whenFindAllByAuthorityStudent_thenReturnEmptyList() {

        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith" );

        testEntityManager.persist(mentor);
        testEntityManager.flush();

        List<User> found = userRepository.findAllByAuthority("ROLE_STUDENT");

        assertThat(found.isEmpty()).isEqualTo(true);
    }
}
