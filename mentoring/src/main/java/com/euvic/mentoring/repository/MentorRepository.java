package com.euvic.mentoring.repository;

import com.euvic.mentoring.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Integer> {

    Optional<Mentor> findFirstByOrderByIdAsc();
}
