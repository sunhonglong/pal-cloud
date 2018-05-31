package com.pal.cloud.gateway;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.pal.cloud.common.api.ucenter.UserApi;
import com.pal.cloud.common.entity.ResultModel;
import com.pal.cloud.common.entity.ucenter.User;

public class AccessFilter extends ZuulFilter {
	private static final String[] ignoredPostfixs = new String[]{"js", "css", "png", "jpg", "gz", "zip", "apk", "gif", "svg", "json", "woff2", "eot", "ttf"};
	private static Log logger = LogFactory.getLog(AccessFilter.class);
	private List<String> excludeUriList = new ArrayList<String>();

	@Autowired
	UserApi userApi;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Override
	public Object run() {
		RequestContext requestContext = RequestContext.getCurrentContext();
		HttpServletRequest request = requestContext.getRequest();
		if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
			return null;
		}
		String authorization = request.getHeader("Authorization");
		String uri = request.getRequestURI();
		String errorMsg = "";
		if (!isStaticUri(uri) && !isExclude(uri) && !isChatopsUri(uri)) {
			boolean isAuth = false;
			String token = getTokenByRequest(request);
			if (authorization != null && authorization.startsWith("Basic")) {
				isAuth = basicAuth(authorization);
				if (!isAuth) {
					errorMsg = "Basic auth failed.";
				}
			} else {
				if (token == null) {
					errorMsg = "Token is missing.";
				} else {
					isAuth = validateToken(token);
					if (!isAuth) {
						errorMsg = "Token is invalid.";
						logger.info("Invalid token:" + token);
					}
				}
			}
			if (!isAuth) {
				requestContext.setResponseBody(errorMsg);
				requestContext.setSendZuulResponse(false);
				requestContext.setResponseStatusCode(401);
			}
		}
		return null;
	}

	private boolean validateToken(String token) {
		boolean isValid = false;
		if (token.startsWith("itsmATK")) {
			String adminToken = stringRedisTemplate.opsForValue().get("admintoken");
			if (adminToken.equals(token)) {
				isValid = true;
			}
		} else if (token.startsWith("itsmTK")) {
			String cacheToken = stringRedisTemplate.opsForValue().get(token);
			if (cacheToken != null) {
				isValid = true;
			}
		}
		return isValid;
	}

	private String getTokenByRequest(HttpServletRequest request) {
		String token = request.getParameter("token");
		if (token == null) {
			token = request.getHeader("token");
			if (token == null) {
				token = request.getHeader("token");
			}
			if (token == null) {
				if (request.getSession().getAttribute("token") != null) {
					token = request.getSession().getAttribute("token").toString();
				}
			}
		}
		return token;
	}

	protected boolean isExclude(String url) {
		for (String excludeUri : excludeUriList) {
			if (url.startsWith(excludeUri))
				return true;
		}
		return false;
	}

	private boolean isStaticUri(String uri) {
		boolean ignored = false;
		if (uri.lastIndexOf(".") != -1) {
			String postfix = uri.substring(uri.lastIndexOf(".") + 1, uri.length());
			for (int i = 0; i < ignoredPostfixs.length; i++) {
				if (ignoredPostfixs[i].equalsIgnoreCase(postfix)) {
					ignored = true;
					break;
				}
			}
		}
		return ignored;
	}

	private boolean isChatopsUri(String uri) {
		if (uri.contains("open/chatops"))
			return true;
		else
			return false;
	}

	@Override
	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int filterOrder() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return "pre";
	}

	private boolean basicAuth(String authCode) {
		try {
			String userPwdCode = authCode.replace("Basic ", "");
			String userPwdStr = new String(Base64.getDecoder().decode(userPwdCode), "utf-8");
			String[] userPwdStrs = userPwdStr.split(":");
			String userName = userPwdStrs[0];
			String password = userPwdStrs[1];
			return authFromUcenter(userName, password);
		} catch (Exception e) {
			logger.info("basic auth faild:", e);
			return false;
		}

	}
	private boolean authFromUcenter(String username, String password) {
		ResultModel<User> resultModel = userApi.queryByUsernamePwd(username, password);
		User user = resultModel.getData();
		if (user != null) {
			return true;
		} else {
			return false;
		}
	}

}