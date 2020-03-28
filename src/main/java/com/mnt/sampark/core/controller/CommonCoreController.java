package com.mnt.sampark.core.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mnt.sampark.core.db.model.User;
import com.mnt.sampark.core.db.model.UserDetail;
import com.mnt.sampark.core.shiro.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/open")
@CrossOrigin(origins = "*")
public class CommonCoreController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/rest/login", method = RequestMethod.POST)
    @ResponseBody
    public LoginResponse retsLogin(@RequestParam String username, @RequestParam String password) {
        HashMap<String, Object> userInfo = new HashMap<>();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        //token.setRememberMe(true);
        Subject currentUser = SecurityUtils.getSubject();
        currentUser.login(token);
        User user = (User) currentUser.getPrincipal();
        String jwtToken = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secrEt123seCret");
            jwtToken = JWT.create()
                    .withIssuer("auth0")
                    .withSubject(username)
                    .sign(algorithm);
        } catch (Exception e) {

        }
        UserDetail userDetails = new UserDetail();
        List<Long> roleIds = user.getRoles().stream().map(map -> map.getId()).collect(Collectors.toList());
        userDetails = userService.findByUserId(user.getId());
        userDetails.setLastLogin(new Date());
        userService.saveUserDetail(userDetails);
        List<String> actions = new ArrayList<>();
        if (user.getType().equals("user")) {
            actions = userService.getSecurityActionsBySecurityGroupIds(roleIds);
        } else {
            actions.add("COMPLAINTS");
//            actions.add("MANAGE_COMPLAINT");
            actions.add("MANAGE_ASSIGNED_COMPLAINT");
            actions.add("MANAGE_NEW_COMPLAINT");
            actions.add("MANAGE_EDIT_COMPLAINT");
            actions.add("RESOLVED_MONTH_COMPLAINT");
            actions.add("RESOLVED_DEPARTMENT_COMPLAINT");
        }
        return new LoginResponse(jwtToken, userDetails, user, actions);
    }

    @SuppressWarnings("unused")
    private static class LoginResponse {

        public String token;
        public UserDetail userDetail;
        public User user;
        public List<String> permissions;

        public LoginResponse(final String token, UserDetail userDetail, User user, List<String> permissions) {
            this.token = token;
            this.userDetail = userDetail;
            this.user = user;
            this.permissions = permissions;
        }
    }
}
