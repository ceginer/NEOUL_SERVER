package com.example.neoul.repository;

import com.example.neoul.entity.user.User;
import com.example.neoul.entity.user.UserLikedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserLikedProductRepository extends JpaRepository<UserLikedProduct, Long> {



    Optional<UserLikedProduct> findByUser(User user); //id가 아니라 객체 자체로 찾기!!!
}
