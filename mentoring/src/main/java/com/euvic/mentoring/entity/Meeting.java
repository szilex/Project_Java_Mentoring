package com.euvic.mentoring.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "meetings")
public class Meeting {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "mentor_id")
    private User mentor;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "student_id")
    private User student;

    public Meeting() {

    }

    public Meeting(LocalDate date, LocalTime startTime, LocalTime endTime, User mentor) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mentor = mentor;
    }

    public Meeting(LocalDate date, LocalTime startTime, LocalTime endTime, User mentor, User student) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mentor = mentor;
        this.student = student;
    }

    public Meeting(int id, LocalDate date, LocalTime startTime, LocalTime endTime, User mentor) {
        this.id = id;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mentor = mentor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "Meeting{" +
                "date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", mentor=" + mentor +
                ", student=" + student +
                '}';
    }
}
