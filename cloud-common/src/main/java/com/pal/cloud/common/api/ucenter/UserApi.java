package com.pal.cloud.common.api.ucenter;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pal.cloud.common.entity.ResultModel;
import com.pal.cloud.common.entity.ucenter.User;

@FeignClient("model")
@RequestMapping("/user")
public interface UserApi {
	
	@GetMapping
	ResultModel<User> queryByUsernamePwd(String username,String password);
}
