package com.euvic.mentoring.service;

import com.euvic.mentoring.entity.Meeting;
import java.util.List;

public interface MeetingServiceInterface {

    Meeting getMeeting(int id);
    List<Meeting> getMeetings();
    Meeting saveMeeting(Meeting meeting);
    void deleteMeeting(int id);
}
