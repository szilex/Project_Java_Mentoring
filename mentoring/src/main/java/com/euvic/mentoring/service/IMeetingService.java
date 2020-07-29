package com.euvic.mentoring.service;

import com.euvic.mentoring.aspect.MeetingNotFoundException;
import com.euvic.mentoring.aspect.UserNotFoundException;
import com.euvic.mentoring.entity.MeetingDTO;

import java.util.List;

public interface IMeetingService {

    MeetingDTO getMeeting(int id) throws MeetingNotFoundException;
    List<MeetingDTO> getMeetings();
    List<MeetingDTO> getStudentMeetings(int id) throws UserNotFoundException;
    MeetingDTO saveMeeting(MeetingDTO meetingDetails) throws UserNotFoundException;
    MeetingDTO updateMeeting(MeetingDTO meetingDetails) throws UserNotFoundException, MeetingNotFoundException;
    void deleteMeeting(int id) throws MeetingNotFoundException;

}
