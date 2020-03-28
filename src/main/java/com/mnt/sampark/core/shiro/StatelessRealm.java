package com.mnt.sampark.core.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mnt.sampark.core.db.model.User;
import com.mnt.sampark.core.shiro.service.UserService;

@Component("statelessRealm")
public class StatelessRealm extends AuthorizingRealm {
	@Autowired
	private UserService userService;
	
	@Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof JWTAuthenticationToken;
    }
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		return null;
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		JWTAuthenticationToken upToken = (JWTAuthenticationToken) token;
		 User user = userService.findByUsername(upToken.getUserId().toString());
		 SimpleAccount account = new SimpleAccount(user, upToken.getToken(), getName());
		 return account;
	}

}
