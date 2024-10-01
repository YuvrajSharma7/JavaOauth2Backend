package com.example.demo.enums;

import com.example.demo.entity.RoleEntity;

public enum RoleEnum {

	USER(2),ADMIN(1);
	
	private Integer id;
	
	RoleEnum(Integer id){
		this.id = id;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public RoleEntity getEntity() {
		RoleEntity roleEntity = new RoleEntity();
		roleEntity.setId(id);
		roleEntity.setName(this.toString());
		return roleEntity;
	}
	
	public RoleEnum getById(Integer id) {
		for(RoleEnum roleEnum :RoleEnum.values()) {
			if(roleEnum.getId().equals(id)) {
				return roleEnum;
			}
		}
		return null;
	}
}
