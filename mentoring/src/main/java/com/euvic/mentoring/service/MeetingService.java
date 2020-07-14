package com.euvic.mentoring.service;

import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MeetingService implements MeetingServiceInterface{

    private MeetingRepository meetingRepository;
    private UserServiceInterface userService;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository, UserServiceInterface userService) {
        this.meetingRepository = meetingRepository;
        this.userService = userService;
    }

    @Override
    public Meeting getMeeting(int id) throws NoSuchElementException {
        return meetingRepository.findById(id).get();
    }

    @Override
    public List<Meeting> getMeetings() {
        return meetingRepository.findAll();
    }

    @Override
    public Meeting saveMeeting(Meeting meeting) {

        meeting.setMentor(userService.getMentor());
        meeting.setStudent(null);

        return meetingRepository.save(meeting);
    }

    @Override
    public void deleteMeeting(int id) throws NoSuchElementException {

        Optional<Meeting> meeting = meetingRepository.findById(id);

        if (meeting.isPresent()) {
            meetingRepository.delete(meeting.get());
        } else {
            throw new NoSuchElementException();
        }
    }
}
