package com.euvic.mentoring.repository;

import com.euvic.mentoring.entity.Meeting;
import com.euvic.mentoring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Integer> {

    List<Meeting> findByStudent(User student);
}
