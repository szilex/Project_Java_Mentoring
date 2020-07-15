package com.euvic.mentoring.controller;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.SimpleMeeting;
import com.euvic.mentoring.service.IMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    private IMeetingService meetingService;

    @Autowired
    public MeetingController(IMeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/{id}")
    public SimpleMeeting getMeeting(@PathVariable int id) {
        return meetingService.getMeeting(id);
    }

    @GetMapping
    public List<SimpleMeeting> getMeetings() {
        return meetingService.getMeetings();
    }

    @GetMapping("/student/{id}")
    public List<SimpleMeeting> getStudentMeetings(@PathVariable int id) throws UserNotFoundException {
        return meetingService.getStudentMeetings(id);
    }

    @PostMapping
    public SimpleMeeting addMeeting(@RequestBody SimpleMeeting meeting) throws MeetingNotFoundException, UserNotFoundException {
        return meetingService.saveMeeting(meeting);
    }

    @PutMapping
    public SimpleMeeting updateMeeting(@RequestBody SimpleMeeting meeting) throws MeetingNotFoundException, UserNotFoundException {
        return meetingService.saveMeeting(meeting);
    }

    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable int id) {
        meetingService.deleteMeeting(id);
    }
}
