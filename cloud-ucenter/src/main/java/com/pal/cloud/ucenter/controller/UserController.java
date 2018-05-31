package com.pal.cloud.ucenter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pal.cloud.common.api.ucenter.UserApi;
import com.pal.cloud.common.entity.ResultModel;
import com.pal.cloud.common.entity.ucenter.User;
import com.pal.cloud.ucenter.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController implements UserApi{

	
	@Autowired
	UserService userService;
 
	@Override
	@GetMapping
	public ResultModel<User> queryByUsernamePwd(@RequestParam String username, @RequestParam  String password) {
		User user =  userService.queryByUsernamePwd(username, password);
		return ResultModel.createSuccess(user);
	}
}


