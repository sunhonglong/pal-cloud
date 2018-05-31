package com.pal.cloud.ucenter.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.pal.cloud.common.entity.ucenter.User;

@Mapper
public interface UserMapper {

	@Select("SELECT * FROM user WHERE username=#{username} AND password=#{password} LIMIT 1")
	User selectByUsernamePassword(@Param("username") String username, @Param("password") String password);

}
