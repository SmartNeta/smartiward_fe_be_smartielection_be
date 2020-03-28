package com.mnt.sampark.core.shiro;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.PermissionsAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mnt.sampark.core.shiro.service.UserService;


//@Component("urlPermissionsFilter")
public class URLPermissionsFilter extends PermissionsAuthorizationFilter{
	@Autowired
	private UserService userService;

	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
		String curUrl = getRequestUrl(request);
		Subject subject = SecurityUtils.getSubject();
		/*if(subject.getPrincipal() == null 
				|| org.apache.commons.lang.StringUtils.endsWithAny(curUrl, new String[]{".js",".css"})
				|| org.apache.commons.lang.StringUtils.endsWithAny(curUrl, new String[]{".jpg",".png",".gif", ".jpeg"})
				|| org.apache.commons.lang.StringUtils.equals(curUrl, "/open/unauthorized")) {
			return true;
		}*/
		List<String> urls = userService.findPermissionUrl(subject.getPrincipal().toString());
		System.out.println(curUrl);
		return urls.contains(curUrl);
	}
	
	/**
	 */
	private String getRequestUrl(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest)request;
		String queryString = req.getQueryString();

		queryString = org.apache.commons.lang.StringUtils.isBlank(queryString)?"": "?"+queryString;
		return req.getRequestURI()+queryString;
	}
}
