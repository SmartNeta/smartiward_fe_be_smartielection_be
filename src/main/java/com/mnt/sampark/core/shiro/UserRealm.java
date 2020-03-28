package com.mnt.sampark.core.shiro;

import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.permission.WildcardPermission;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mnt.sampark.core.db.model.SecurityActions;
import com.mnt.sampark.core.db.model.SecurityGroup;
import com.mnt.sampark.core.db.model.User;
import com.mnt.sampark.core.shiro.service.UserService;

/**
 * 
 * @author Administrator
 */
@Component("userRealm")
public class UserRealm extends AuthorizingRealm {
	@Autowired
	private UserService userService;

	public UserRealm() {
		setName("UserRealm");
		//setCredentialsMatcher(new HashedCredentialsMatcher("MD5"));
	}
	
	@Override
    public boolean supports(AuthenticationToken token) {
        return token != null && token instanceof UsernamePasswordToken;
    }

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		/*String username = (String) principals.getPrimaryPrincipal();*/
		User user = (User)principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
		/*String[] name_account = username.split("::");
		Set<SecurityGroup> roles = userService.getSecurityGroupByUsernameAndAccount(name_account[0],name_account[1]);
        */
		Set<SecurityGroup> roles = userService.getSecurityGroupByUsernameAndAccount(user.getUsername(),user.getDefaultAccountId());
        
        roles.forEach(role -> {
        	info.addRole(role.getName());
        	Set<SecurityActions> permissions = userService.getSecurityActionsBySecurityGroup(role);
            permissions.forEach(perm -> {
            	info.addObjectPermission(new WildcardPermission(perm.getName()));
            });

        });
		return info;
	}
	
	
	
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upt = (UsernamePasswordToken) token;
		String userName = null;
		User user = null;
		userName = upt.getUsername();
	    user = userService.findByUsername(userName);
	    
		if (user == null) {
			throw new UnknownAccountException();
		}
		
		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user/*upt.getUsername()*/, user.getPassword(), getName());
		return info;
	}
}
