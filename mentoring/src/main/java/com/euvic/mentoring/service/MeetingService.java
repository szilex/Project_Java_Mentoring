package com.euvic.mentoring.service;

import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MeetingService implements MeetingServiceInterface{

    private MeetingRepository meetingRepository;

    @Autowired
    public MeetingService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

    @Override
    public Meeting getMeeting(int id) {
        return meetingRepository.findById(id).get();
    }

    @Override
    public List<Meeting> getMeetings() {
        return meetingRepository.findAll();
    }
}
