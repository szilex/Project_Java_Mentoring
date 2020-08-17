package com.euvic.mentoring.controller;

import com.euvic.mentoring.entity.MeetingDTO;
import com.euvic.mentoring.service.IMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    private final IMeetingService meetingService;

    @Autowired
    public MeetingController(IMeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/{id}")
    public MeetingDTO getMeeting(@PathVariable int id) {
        return meetingService.getMeeting(id);
    }

    @GetMapping
    public List<MeetingDTO> getMeetings() {
        return meetingService.getMeetings();
    }

    @GetMapping("/student/{id}")
    public List<MeetingDTO> getStudentMeetings(@PathVariable int id) {
        return meetingService.getStudentMeetings(id);
    }

    @PostMapping
    public MeetingDTO saveMeeting(@RequestBody MeetingDTO meeting) {
        return meetingService.saveMeeting(meeting);
    }

    @PutMapping
    public MeetingDTO updateMeeting(@RequestBody MeetingDTO meeting) {
        return meetingService.updateMeeting(meeting);
    }

    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable int id) {
        meetingService.deleteMeeting(id);
    }
}
