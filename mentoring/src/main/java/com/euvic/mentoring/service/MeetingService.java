package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.MeetingDetails;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
public class MeetingService implements IMeetingService {

    private MeetingRepository meetingRepository;
    private IUserService userService;
    private IMailService mailService;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, IUserService userService, IMailService mailService) {
        this.meetingRepository = meetingRepository;
        this.userService = userService;
        this.mailService = mailService;
    }

    @Override
    public MeetingDetails getMeeting(int id) throws MeetingNotFoundException {

        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (meeting.isPresent()) {
            return new MeetingDetails(meeting.get());
        }

        throw new MeetingNotFoundException(id);
    }

    @Override
    public List<MeetingDetails> getMeetings() {

        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream()
                .map(meeting -> new MeetingDetails(meeting))
                .collect(Collectors.toList());

    }

    @Override
    public List<MeetingDetails> getStudentMeetings(int id) throws UserNotFoundException {

        List<Meeting> meetings = meetingRepository.findByStudent(userService.getStudent(id));
        return meetings.stream()
                .map(meeting -> new MeetingDetails(meeting))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MeetingDetails saveMeeting(MeetingDetails meetingDetails) throws UserNotFoundException {

        if (meetingDetails.getId() != 0 && meetingDetails.getStudentId() != 0) {
            throw new IllegalArgumentException("Illegal argument specified");
        }

        if (!Duration.between(meetingDetails.getStartTime(), meetingDetails.getEndTime()).equals(Duration.of(15, MINUTES))) {
            throw new IllegalArgumentException("Time interval must be equal to 15 minutes");
        }

        Optional<Meeting> dbMeeting = meetingRepository.findById(meetingDetails.getId());
        if (dbMeeting.isPresent()) {
            throw new IllegalArgumentException("Meeting with specified id already exists: " + meetingDetails.getId());
        }

        User mentor = userService.getMentor();
        Meeting meeting = new Meeting(meetingDetails.getDate(), meetingDetails.getStartTime(), meetingDetails.getEndTime(), mentor, null);
        Meeting savedMeeting = meetingRepository.save(meeting);

        return new MeetingDetails(savedMeeting);
    }

    @Override
    @Transactional
    public MeetingDetails updateMeeting(MeetingDetails meetingDetails) throws UserNotFoundException, MeetingNotFoundException {

        if (meetingDetails.getId() == 0 || meetingDetails.getStudentId() == 0) {
            throw new IllegalArgumentException("Insufficient argument list");
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString();

        Integer loggedStudentId = userService.getStudents().stream()
                .filter(x->x.getMail().equals(username))
                .map(x->x.getId())
                .findFirst()
                .get();
        if (loggedStudentId != meetingDetails.getStudentId()) {
            throw new IllegalArgumentException("Student cannot book a meeting for another student");
        }

        Optional<Meeting> dbMeeting = meetingRepository.findById(meetingDetails.getId());
        if (!dbMeeting.isPresent()) {
            throw new MeetingNotFoundException(meetingDetails.getId());
        }

        Meeting temporaryMeeting = dbMeeting.get();
        if(temporaryMeeting.getStudent() != null) {
            throw new IllegalArgumentException("Meeting with specified id is already reserved: " + meetingDetails.getId());
        }

        User student = userService.getStudent(meetingDetails.getStudentId());
        temporaryMeeting.setStudent(student);
        Meeting savedMeeting = meetingRepository.save(temporaryMeeting);

        try {
            sendMessageToStudent(savedMeeting);
            sendMessageToMentor(savedMeeting);
        } catch (MessagingException e) {

        }

        return new MeetingDetails(savedMeeting);
    }

    @Override
    public void deleteMeeting(int id) throws MeetingNotFoundException {

        Optional<Meeting> meeting = meetingRepository.findById(id);

        if (meeting.isPresent()) {
            meetingRepository.delete(meeting.get());
        } else {
            throw new MeetingNotFoundException();
        }
    }

    private void sendMessageToStudent(Meeting meeting) throws MessagingException {
        StringBuilder message = new StringBuilder();
        message.append("Hello!\n\n");
        message.append("You've booked a meeting at:\n\n");
        message.append("Date: ").append(meeting.getDate().toString()).append('\n');
        message.append("Time: ").append(meeting.getStartTime()).append('-').append(meeting.getEndTime().toString()).append("\n\n");
        message.append("Note: This message was generated automatically by Spring Boot Mentoring Application\n");
        mailService.sendMail(meeting.getStudent().getMail(), "Meeting reservation", message.toString(), false);
    }

    private void sendMessageToMentor(Meeting meeting) throws MessagingException {
        StringBuilder message = new StringBuilder();
        message.append("Hello!\n\n");
        message.append("Student: ").append(meeting.getStudent().getFirstName()).append(' ').append(meeting.getStudent().getLastName()).append(" booked a meeting at:\n\n");
        message.append("Date: ").append(meeting.getDate().toString()).append('\n');
        message.append("Time: ").append(meeting.getStartTime()).append('-').append(meeting.getEndTime().toString()).append("\n\n");
        message.append("Note: This message was generated automatically by Spring Boot Mentoring Application\n");
        mailService.sendMail(meeting.getMentor().getMail(), "Meeting reservation", message.toString(), false);
    }
}
