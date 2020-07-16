package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.MeetingDetails;
import com.euvic.mentoring.entity.Student;
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
    public MeetingDetails saveMeeting(MeetingDetails meetingDetails) throws MeetingNotFoundException, UserNotFoundException {

        Meeting meeting;
        Mentor mentor = userService.getMentor();
        Student student = null;

        if (meetingDetails.getStudentId() != null) {
            student = userService.getStudent(meetingDetails.getStudentId());
        }

        if (meetingDetails.getId() != null) {
            Optional<Meeting> optionalMeeting = meetingRepository.findById(meetingDetails.getId());
            if (optionalMeeting.isPresent()) {
                meeting = optionalMeeting.get();
                meeting.setDate(meetingDetails.getDate());
                meeting.setStartTime(meetingDetails.getStartTime());
                meeting.setEndTime(meetingDetails.getEndTime());
                meeting.setStudent(student);
            } else {
                throw new MeetingNotFoundException(meetingDetails.getId());
            }
        } else {
            meeting = new Meeting(meetingDetails.getDate(), meetingDetails.getStartTime(), meetingDetails.getEndTime(), mentor, student);
        }

        Meeting savedMeeting = meetingRepository.save(meeting);

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
