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

    private MeetingRepository meetingRepository;
    private IUserService userService;
    private IMailService mailService;
    private ModelMapper modelMapper;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, IUserService userService, IMailService mailService, ModelMapper modelMapper) {
        this.meetingRepository = meetingRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.modelMapper = modelMapper;

        this.modelMapper.typeMap(Meeting.class, MeetingDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getMentor().getId(), MeetingDTO::setMentorId);
            mapper.map(src -> src.getStudent().getId(), MeetingDTO::setStudentId);
        });
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
                .map(meeting -> convertToDTO(meeting))
                .collect(Collectors.toList());

    }

    @Override
    public List<MeetingDTO> getStudentMeetings(int id) throws UserNotFoundException {

        List<Meeting> meetings = meetingRepository.findByStudent(userService.getStudent(id));
        return meetings.stream()
                .map(meeting -> convertToDTO(meeting))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MeetingDTO saveMeeting(MeetingDTO meetingDTO) throws UserNotFoundException {

        if (meetingDTO.getId() != 0 || meetingDTO.getMentorId() != 0 || meetingDTO.getStudentId() != 0) {
            throw new IllegalArgumentException("Illegal argument specified");
        }

        if (!Duration.between(meetingDTO.getStartTime(), meetingDTO.getEndTime()).equals(Duration.of(15, MINUTES))) {
            throw new IllegalArgumentException("Time interval must be equal to 15 minutes");
        }

        //Meeting meeting = new Meeting(meetingDTO.getDate(), meetingDTO.getStartTime(), meetingDTO.getEndTime(), userService.getMentor(), null);
        Meeting meeting = convertToEntity(meetingDTO);
        meeting.setMentor(userService.getMentor());
        meeting.setStudent(null);

        Meeting savedMeeting = meetingRepository.save(meeting);

        return new MeetingDTO(savedMeeting);
    }

    @Override
    @Transactional
    public MeetingDTO updateMeeting(MeetingDTO meetingDetails) throws UserNotFoundException, MeetingNotFoundException {

        if (meetingDetails.getId() == 0 || meetingDetails.getStudentId() == 0) {
            throw new IllegalArgumentException("Insufficient argument list");
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails)principal).getUsername() : principal.toString();

        User loggedStudent = userService.getStudents().stream()
                .filter(x->x.getMail().equals(username))
                .findFirst()
                .get();
        if (loggedStudent.getId() != meetingDetails.getStudentId()) {
            throw new IllegalArgumentException("Student cannot book a meeting for another student");
        }

        Optional<Meeting> dbMeeting = meetingRepository.findById(meetingDetails.getId());
        if (!dbMeeting.isPresent()) {
            throw new MeetingNotFoundException(meetingDetails.getId());
        }

        Meeting temporaryMeeting = dbMeeting.get();
        if(temporaryMeeting.getStudent() != null) {
            throw new IllegalArgumentException("Meeting with specified id is already booked: " + meetingDetails.getId());
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

    private MeetingDTO convertToDTO(Meeting meeting) {
        MeetingDTO meetingDTO = modelMapper.map(meeting, MeetingDTO.class);

        return meetingDTO;
    }

    private Meeting convertToEntity(MeetingDTO meetingDTO) {
        Meeting meeting = modelMapper.map(meetingDTO, Meeting.class);

        return meeting;
    }
}
