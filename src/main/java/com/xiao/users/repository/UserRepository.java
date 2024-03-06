package com.xiao.users.repository;

import com.xiao.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE (emailAddress = ?3 OR username = ?2) AND id <> ?1")
    public Optional<User> findUserByEmailOrUserNameWithId(Long userId, String username, String email);
}