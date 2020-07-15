package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.SimpleMeeting;

import java.util.List;

public interface IMeetingService {

    SimpleMeeting getMeeting(int id) throws MeetingNotFoundException;
    List<SimpleMeeting> getMeetings();
    List<SimpleMeeting> getStudentMeetings(int id) throws UserNotFoundException;
    SimpleMeeting saveMeeting(SimpleMeeting meeting) throws UserNotFoundException, MeetingNotFoundException;
    void deleteMeeting(int id) throws MeetingNotFoundException;

}
