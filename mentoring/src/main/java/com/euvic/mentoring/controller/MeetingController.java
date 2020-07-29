package com.euvic.mentoring.controller;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
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
    public MeetingDTO getMeeting(@PathVariable int id) throws MeetingNotFoundException {
        return meetingService.getMeeting(id);
    }

    @GetMapping
    public List<MeetingDTO> getMeetings() {
        return meetingService.getMeetings();
    }

    @GetMapping("/student/{id}")
    public List<MeetingDTO> getStudentMeetings(@PathVariable int id) throws UserNotFoundException {
        return meetingService.getStudentMeetings(id);
    }

    @PostMapping
    public MeetingDTO saveMeeting(@RequestBody MeetingDTO meeting) throws UserNotFoundException {
        return meetingService.saveMeeting(meeting);
    }

    @PutMapping
    public MeetingDTO updateMeeting(@RequestBody MeetingDTO meeting) throws MeetingNotFoundException, UserNotFoundException {
        return meetingService.updateMeeting(meeting);
    }

    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable int id) throws MeetingNotFoundException {
        meetingService.deleteMeeting(id);
    }
}
