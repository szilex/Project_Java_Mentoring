package com.euvic.mentoring.controller;

import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.service.MeetingServiceInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    private MeetingServiceInterface meetingService;

    public MeetingController(MeetingServiceInterface meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/{id}")
    public Meeting getMeeting(@PathVariable int id) {
        return meetingService.getMeeting(id);
    }

    @GetMapping
    public List<Meeting> getMeetings() {
        return meetingService.getMeetings();
    }
}
