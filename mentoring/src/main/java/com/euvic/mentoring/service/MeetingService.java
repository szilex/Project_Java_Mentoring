package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.MeetingDetails;
import com.euvic.mentoring.entity.User;
import com.euvic.mentoring.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetingService implements IMeetingService {

    private MeetingRepository meetingRepository;
    private IUserService userService;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, IUserService userService) {
        this.meetingRepository = meetingRepository;
        this.userService = userService;
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
}
