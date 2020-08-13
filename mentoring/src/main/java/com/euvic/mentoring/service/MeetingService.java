package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.MeetingDTO;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.MeetingRepository;
import org.modelmapper.ModelMapper;
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

    private final MeetingRepository meetingRepository;
    private final IUserService userService;
    private final IMailService mailService;
    private final ModelMapper modelMapper;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, IUserService userService, IMailService mailService, ModelMapper modelMapper) {
        this.meetingRepository = meetingRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.modelMapper = modelMapper;
    }

    @Override
    public MeetingDTO getMeeting(int id) throws MeetingNotFoundException {

        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (meeting.isPresent()) {
            return convertToDTO(meeting.get());
        }

        throw new MeetingNotFoundException(id);
    }

    @Override
    public List<MeetingDTO> getMeetings() {

        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MeetingDTO> getStudentMeetings(int id) throws UserNotFoundException {

        List<Meeting> meetings = meetingRepository.findByStudent(userService.getStudent(id));
        return meetings.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MeetingDTO saveMeeting(MeetingDTO meetingDTO) throws UserNotFoundException {

        if (meetingDTO == null || meetingDTO.getDate() == null || meetingDTO.getStartTime() == null || meetingDTO.getEndTime() == null) {
            throw new IllegalArgumentException("Insufficient argument list");
        }

        if (meetingDTO.getId() != null || meetingDTO.getMentorId() != null || meetingDTO.getStudentId() != null) {
            throw new IllegalArgumentException("Illegal argument specified");
        }

        if (!Duration.between(meetingDTO.getStartTime(), meetingDTO.getEndTime()).equals(Duration.of(15, MINUTES))) {
            throw new IllegalArgumentException("Time interval must be equal to 15 minutes");
        }

        List<Meeting> meetings = meetingRepository.findByDate(meetingDTO.getDate()).stream()
                .filter(x -> {
                    if (x.getStartTime().isBefore(meetingDTO.getStartTime()) && x.getEndTime().isAfter(meetingDTO.getStartTime()))
                        return true;
                    if (x.getStartTime().isBefore(meetingDTO.getEndTime()) && x.getEndTime().isAfter(meetingDTO.getEndTime()))
                        return true;
                    if (x.getStartTime().equals(meetingDTO.getStartTime()))
                        return true;

                    return false;
                })
                .collect(Collectors.toList());
        if (meetings.size() > 0) {
            throw new IllegalArgumentException("Meeting collides with already existing meeting");
        }

        Meeting meeting = convertToEntity(meetingDTO);
        meeting.setMentor(userService.getMentor());
        meeting.setStudent(null);

        Meeting savedMeeting = meetingRepository.save(meeting);

        return convertToDTO(savedMeeting);
    }

    @Override
    @Transactional
    public MeetingDTO updateMeeting(MeetingDTO meetingDTO) throws UserNotFoundException, MeetingNotFoundException {

        if (meetingDTO == null || meetingDTO.getId() == null || meetingDTO.getStudentId() == null) {
            throw new IllegalArgumentException("Insufficient argument list");
        }

        if (meetingDTO.getDate() != null || meetingDTO.getStartTime() != null || meetingDTO.getEndTime() != null || meetingDTO.getMentorId() != null) {
            throw new IllegalArgumentException("Illegal argument specified");
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString();

        User loggedStudent = userService.getStudents().stream()
                .filter(x->x.getMail().equals(username))
                .findFirst()
                .get();
        if (loggedStudent.getId() != meetingDTO.getStudentId()) {
            throw new IllegalArgumentException("Student cannot book a meeting for another student");
        }

        Optional<Meeting> dbMeeting = meetingRepository.findById(meetingDTO.getId());
        if (dbMeeting.isEmpty()) {
            throw new MeetingNotFoundException(meetingDTO.getId());
        }

        Meeting temporaryMeeting = dbMeeting.get();
        if(temporaryMeeting.getStudent() != null) {
            throw new IllegalArgumentException("Meeting with specified id is already booked: " + meetingDTO.getId());
        }

        temporaryMeeting.setStudent(loggedStudent);
        Meeting savedMeeting = meetingRepository.save(temporaryMeeting);

        try {
            sendMessageToStudent(savedMeeting);
            sendMessageToMentor(savedMeeting);
        } catch (MessagingException e) { }

        return convertToDTO(savedMeeting);
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
        String message = createMessage(meeting, "Student");
        mailService.sendMail(meeting.getStudent().getMail(), "Meeting reservation", message, false);
    }

    private void sendMessageToMentor(Meeting meeting) throws MessagingException {
        String message = createMessage(meeting, "Mentor");
        mailService.sendMail(meeting.getMentor().getMail(), "Meeting reservation", message, false);
    }

    private String createMessage(Meeting meeting, String userType) {
        StringBuilder message = new StringBuilder();
        message.append("Hello!\n\n");
        switch(userType) {
            case "Mentor" :
                message.append("Student: ").append(meeting.getStudent().getFirstName()).append(' ').append(meeting.getStudent().getLastName()).append(" booked a meeting at:\n\n");
                break;
            case "Student" :
                message.append("You've booked a meeting at:\n\n");
                break;
            default:
                message.append("Meeting details:");
        }
        message.append("Date: ").append(meeting.getDate().toString()).append('\n');
        message.append("Time: ").append(meeting.getStartTime()).append('-').append(meeting.getEndTime().toString()).append("\n\n");
        message.append("Note: This message was generated automatically by Spring Boot Mentoring Application\n");
        return message.toString();
    }

    private MeetingDTO convertToDTO(Meeting meeting) {
        return modelMapper.map(meeting, MeetingDTO.class);
    }

    private Meeting convertToEntity(MeetingDTO meetingDTO) {
        return modelMapper.map(meetingDTO, Meeting.class);
    }
}
