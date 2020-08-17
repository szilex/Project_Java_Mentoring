package com.euvic.mentoring.meeting;

import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.MeetingRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class MeetingRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private MeetingRepository meetingRepository;

    @Test
    void givenNoMeetings_whenGetByStudent_thenReturnEmptyList() {
        User student = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns" );
        testEntityManager.persist(student);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByStudent(student);

        assertThat(found).isEmpty();
    }

    @Test
    void givenNoMeetingsForStudent_whenGetByStudent_thenReturnEmptyList() {
        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith");
        User student = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");
        Meeting meeting = new Meeting(LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor);
        testEntityManager.persist(mentor);
        testEntityManager.persist(student);
        testEntityManager.persist(meeting);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByStudent(student);

        assertThat(found).isEmpty();
    }

    @Test
    void givenOneMeetingForStudent_whenGetByStudent_thenReturnMeetingList() {
        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith");
        User student = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");
        Meeting meeting = new Meeting(LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor, student);
        testEntityManager.persist(mentor);
        testEntityManager.persist(student);
        testEntityManager.persist(meeting);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByStudent(student);

        assertThat(found.size()).isEqualTo(1);
        assertThat(found).contains(meeting);
    }

    @Test
    void givenMultipleMeetingsForStudent_whenGetByStudent_thenReturnMeetingList() {
        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith");
        User student = new User("karenjohns@email.com", "pass123", "ROLE_STUDENT", 1, "Karen", "Johns");
        Meeting meeting1 = new Meeting(LocalDate.now(), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor, student);
        Meeting meeting2 = new Meeting(LocalDate.now().minusDays(5), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor, student);
        testEntityManager.persist(mentor);
        testEntityManager.persist(student);
        testEntityManager.persist(meeting1);
        testEntityManager.persist(meeting2);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByStudent(student);

        assertThat(found.size()).isEqualTo(2);
        assertThat(found).contains(meeting1, meeting2);
    }

    @Test
    void givenNoMeetings_whenGetByDate_thenReturnEmptyList() {

        LocalDate date = LocalDate.of(2020, 8, 21);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByDate(date);

        assertThat(found).isEmpty();
    }

    @Test
    void givenNoMeetingWithSpecifiedDate_whenGetByDate_thenReturnEmptyList() {

        LocalDate date = LocalDate.of(2020, 8, 21);
        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith");
        Meeting meeting = new Meeting(LocalDate.of(2020, 8, 20), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor);
        testEntityManager.persist(mentor);
        testEntityManager.persist(meeting);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByDate(date);

        assertThat(found).isEmpty();
    }

    @Test
    void givenOneMeetingWithSpecifiedDate_whenGetByDate_thenReturnMeetingList() {

        LocalDate date = LocalDate.of(2020, 8, 21);
        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith");
        Meeting meeting = new Meeting(LocalDate.of(2020, 8, 21), LocalTime.now(), LocalTime.now().plusMinutes(15), mentor);
        testEntityManager.persist(mentor);
        testEntityManager.persist(meeting);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByDate(date);

        assertThat(found.size()).isEqualTo(1);
        assertThat(found.get(0)).isEqualTo(meeting);
    }

    @Test
    void givenMultipleMeetingsWithSpecifiedDate_whenGetByDate_thenReturnMeetingList() {

        LocalDate date = LocalDate.of(2020, 8, 21);
        User mentor = new User("johnsmith@email.com", "pass123", "ROLE_MENTOR", 1, "John", "Smith");
        Meeting meeting1 = new Meeting(LocalDate.of(2020, 8, 21), LocalTime.of(10, 00), LocalTime.of(10, 15), mentor);
        Meeting meeting2 = new Meeting(LocalDate.of(2020, 8, 21), LocalTime.of(12, 30), LocalTime.of(12, 45), mentor);
        testEntityManager.persist(mentor);
        testEntityManager.persist(meeting1);
        testEntityManager.persist(meeting2);
        testEntityManager.flush();

        List<Meeting> found = meetingRepository.findByDate(date);

        assertThat(found.size()).isEqualTo(2);
        assertThat(found).isEqualTo(List.of(meeting1, meeting2));
    }
}
