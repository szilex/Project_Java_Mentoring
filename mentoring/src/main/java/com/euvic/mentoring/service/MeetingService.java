package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.Mentor;
import com.euvic.mentoring.entity.SimpleMeeting;
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
    public SimpleMeeting getMeeting(int id) throws MeetingNotFoundException {

        Optional<Meeting> meeting = meetingRepository.findById(id);
        if (meeting.isPresent()) {
            return new SimpleMeeting(meeting.get());
        }

        throw new MeetingNotFoundException(id);
    }

    @Override
    public List<SimpleMeeting> getMeetings() {

        List<Meeting> meetings = meetingRepository.findAll();
        return meetings.stream()
                .map(meeting -> new SimpleMeeting(meeting))
                .collect(Collectors.toList());

    }

    @Override
    public List<SimpleMeeting> getStudentMeetings(int id) throws UserNotFoundException {

        List<Meeting> meetings = meetingRepository.findByStudent(userService.getStudent(id));
        return meetings.stream()
                .map(meeting -> new SimpleMeeting(meeting))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SimpleMeeting saveMeeting(SimpleMeeting simpleMeeting) throws MeetingNotFoundException, UserNotFoundException {

        Meeting meeting;
        Mentor mentor = userService.getMentor();
        Student student = null;

        if (simpleMeeting.getStudentId() != null) {
            student = userService.getStudent(simpleMeeting.getStudentId());
        }

        if (simpleMeeting.getId() != null) {
            Optional<Meeting> optionalMeeting = meetingRepository.findById(simpleMeeting.getId());
            if (optionalMeeting.isPresent()) {
                meeting = optionalMeeting.get();
                meeting.setDate(simpleMeeting.getDate());
                meeting.setStartTime(simpleMeeting.getStartTime());
                meeting.setEndTime(simpleMeeting.getEndTime());
                meeting.setStudent(student);
            } else {
                throw new MeetingNotFoundException(simpleMeeting.getId());
            }
        } else {
            meeting = new Meeting(simpleMeeting.getDate(), simpleMeeting.getStartTime(), simpleMeeting.getEndTime(), mentor, student);
        }

        Meeting savedMeeting = meetingRepository.save(meeting);

        return new SimpleMeeting(savedMeeting);
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
