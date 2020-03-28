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
@Component("accountRealm")
public class AccountRealm extends AuthorizingRealm {
	@Autowired
	private UserService userService;

	public AccountRealm() {
		setName("AccountRealm");
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		User user = (User) principals.getPrimaryPrincipal();
		SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
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
		if (upt.getUsername().contains("::")) {
			userName = upt.getUsername().split("::")[0];
		} else {
			throw new AuthenticationException();
		}
		user = userService.findByUsername(userName);
		if (user == null) {
			throw new UnknownAccountException();
		}
		user.setDefaultAccountId(Long.valueOf(upt.getUsername().split("::")[1]));

		SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, user.getPassword(), getName());
		return info;
	}
}
