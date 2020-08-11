package com.euvic.mentoring.repository;

import com.euvic.mentoring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findFirstByAuthorityOrderByIdAsc(String authority);
    Optional<User> findByIdAndAuthority(int id, String authority);
    Optional<User> findByMailAndAuthority(String mail, String authority);
    Optional<User> findByMail(String mail);
    List<User> findAllByAuthority(String authority);
}
