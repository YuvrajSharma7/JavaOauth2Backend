package com.example.demo.entity;

import java.util.List;

import com.example.demo.enums.SocialMedia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String email;
	
	/*
	 * password will be null in case user has logged in through Google
	 */
	@Column
	private String password;
	
	/*
	 * socialMediaLogin will be null if user signup through signup form
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "social_media_login")
	private SocialMedia socialMediaLogin;
	/*
	 * sub column will hold the value of "sub" field received in Google idToken Payload
	 * 
	 * sub is An identifier for the user, unique among all Google accounts and never reused. 
	 * A Google account can have multiple email addresses at different points in time, 
	 * but the sub value is never changed. Use sub within your application as the unique-identifier key for the user. 
	 * Maximum length of 255 case-sensitive ASCII characters.
	 * 
	 * sub will be null in case a user signup through our signup form
	 */
	@Column
	private String sub;
	
	@Column
	@ManyToMany(fetch = FetchType.EAGER)
	private List<RoleEntity> role;
	
}
