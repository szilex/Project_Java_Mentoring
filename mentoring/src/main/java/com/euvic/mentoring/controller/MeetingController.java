package com.euvic.mentoring.controller;

import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.service.MeetingServiceInterface;
import com.euvic.mentoring.service.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    private MeetingServiceInterface meetingService;
    private UserServiceInterface userService;

    @Autowired
    public MeetingController(MeetingServiceInterface meetingService, UserServiceInterface userService) {
        this.meetingService = meetingService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Meeting getMeeting(@PathVariable int id) {
        return meetingService.getMeeting(id);
    }

    @GetMapping
    public List<Meeting> getMeetings() {
        return meetingService.getMeetings();
    }

    @PostMapping
    public Meeting addMeeting(@RequestBody Meeting meeting) {
        return meetingService.saveMeeting(meeting);
    }

    @PutMapping
    public Meeting updateMeeting(@RequestBody Meeting meeting) {
        return meetingService.saveMeeting(meeting);
    }

    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable int id) {
        meetingService.deleteMeeting(id);
    }
}
