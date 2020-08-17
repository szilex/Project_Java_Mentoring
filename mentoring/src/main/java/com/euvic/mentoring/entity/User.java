package com.euvic.mentoring.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "mail")
    private String mail;

    @Column(name = "password")
    private String password;

    @Column(name = "authority")
    private String authority;

    @Column(name = "enabled")
    private int enabled;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(mappedBy = "student",
                cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH, CascadeType.PERSIST},
                fetch = FetchType.LAZY)
    private List<Meeting> meetings;

    public User() {

    }

    public User(String mail, String password, String firstName, String lastName) {
        this.mail = mail;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String mail, String password, String authority, int enabled, String firstName, String lastName) {
        this.id = id;
        this.mail = mail;
        this.password = password;
        this.authority = authority;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(int id, String mail, String password, String authority, int enabled, String firstName, String lastName) {
        this.id = id;
        this.mail = mail;
        this.password = password;
        this.authority = authority;
        this.enabled = enabled;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonIgnore
    public List<Meeting> getMeetings() {
        return meetings;
    }

    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", mail='" + mail + '\'' +
                ", password='" + password + '\'' +
                ", authority='" + authority + '\'' +
                ", enabled=" + enabled +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
