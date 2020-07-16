package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.MeetingDetails;

import java.util.List;

public interface IMeetingService {

    MeetingDetails getMeeting(int id) throws MeetingNotFoundException;
    List<MeetingDetails> getMeetings();
    List<MeetingDetails> getStudentMeetings(int id) throws UserNotFoundException;
    MeetingDetails saveMeeting(MeetingDetails meetingDetails) throws UserNotFoundException, MeetingNotFoundException;
    void deleteMeeting(int id) throws MeetingNotFoundException;

}
