package com.euvic.mentoring.entity;

import java.time.LocalDate;
import java.time.LocalTime;

public class SimpleMeeting {

    private static final long serialVersionUID = 1L;
    
    private Integer id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer mentorId;
    private Integer studentId;

    public SimpleMeeting() { }

    public SimpleMeeting(LocalDate date, LocalTime startTime, LocalTime endTime, Integer mentorId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mentorId = mentorId;
    }

    public SimpleMeeting(LocalDate date, LocalTime startTime, LocalTime endTime, Integer mentorId, Integer studentId) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mentorId = mentorId;
        this.studentId = studentId;
    }
    
    public SimpleMeeting(Meeting meeting) {
        this(meeting.getDate(), meeting.getStartTime(), meeting.getEndTime(), meeting.getMentor().getId());
        this.id = meeting.getId();
        if (meeting.getStudent() != null) {
            studentId = meeting.getStudent().getId();
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getMentorId() {
        return mentorId;
    }

    public void setMentorId(Integer mentorId) {
        this.mentorId = mentorId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    @Override
    public String toString() {
        return "SimpleMeeting{" +
                "id=" + id +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", mentorId=" + mentorId +
                ", studentId=" + studentId +
                '}';
    }
}
