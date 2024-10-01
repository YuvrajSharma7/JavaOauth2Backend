package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.UserEntity;
import com.example.demo.enums.SocialMedia;

public interface UserRepository extends JpaRepository<UserEntity, Integer>{

	Optional<UserEntity> findByEmail(String email);
	
	Optional<UserEntity> findByEmailAndSocialMediaLogin(String email,SocialMedia socialMedia);
}
