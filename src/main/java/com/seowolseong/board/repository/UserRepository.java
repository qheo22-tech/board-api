package com.seowolseong.board.repository;

import com.seowolseong.board.domain.Admin;
import com.seowolseong.board.domain.Post;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}
