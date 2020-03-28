package com.mnt.sampark.modules.commons;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mnt.sampark.core.db.model.User;

@Controller
@RequestMapping("/commons")
public class CommonsController {

	@RequiresAuthentication
	@RequestMapping(value="/switch", method=RequestMethod.GET)
	public String switchAccount(@RequestParam String accountId, Model model) {
		Subject currentUser = SecurityUtils.getSubject();
		User user = (User)currentUser.getPrincipal();
		currentUser.logout();
		UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername() + "::" + accountId, user.getPassword());
        currentUser.login(token);
        model.addAttribute("redirectUrl", "/router/index.html");
        // Since we are doing silent login after logout , we are doing client side redirection to place latest cookie on browser
		return "/open/redirect";
	}
}
