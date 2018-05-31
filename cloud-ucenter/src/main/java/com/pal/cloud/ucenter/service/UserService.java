package com.pal.cloud.ucenter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pal.cloud.common.entity.ucenter.User;
import com.pal.cloud.ucenter.dao.UserMapper;

@Service
public class UserService {

	@Autowired
	UserMapper userMapper;
	
	public User queryByUsernamePwd(String username,String password){
		return userMapper.selectByUsernamePassword(username, password);
	}
}
