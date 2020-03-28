package com.mnt.sampark.modules.permission.controller;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mnt.sampark.core.db.model.Account;
import com.mnt.sampark.core.db.repository.AccountRepository;
import com.mnt.sampark.core.db.repository.SecurityGroupRepository;
import com.mnt.sampark.modules.mdm.tools.MdmFactory;
import com.mnt.sampark.modules.permission.dto.PermissionHierarchy;
import com.mnt.sampark.modules.permission.dto.SecurityGroupDto;
import com.mnt.sampark.modules.permission.service.PermissionService;
import com.mnt.sampark.mvc.utils.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/open/permission")
@CrossOrigin(origins = "*")
public class PermissionController {

    @Autowired
    PermissionService permissionService;

    @Autowired
    SecurityGroupRepository securityGroupRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    MdmFactory factory;

    @GetMapping(value = "/tree")
    public String renderPermissions(Model map) throws JsonProcessingException {
        PermissionHierarchy permissionHierarchy = permissionService.getApplicationPermissionsAsTree();
        String json = new ObjectMapper().writeValueAsString(permissionHierarchy);
        System.out.println("This is Object : " + json);
        map.addAttribute("permissionHierarchy", json);
        return "/security/Permissions";
    }

    @GetMapping(value = "/security-group")
    public String renderSecurityGroup(Model map) throws JsonProcessingException {
        PermissionHierarchy permissionHierarchy = permissionService.getApplicationPermissionsAsTree();
        String json = new ObjectMapper().writeValueAsString(permissionHierarchy.children);
        map.addAttribute("permissionHierarchy", json);
        map.addAttribute("classType", "SecurityGroupTable");
        map.addAttribute("mdm", factory);
        return "/security/SecurityGroup/index";
    }

    @GetMapping(value = "/account")
    public String renderAccounts(Model map) throws JsonProcessingException {
        List<SecurityGroupDto> securityGroups = permissionService.findAllSecurityGroups();
        String json = new ObjectMapper().writeValueAsString(securityGroups);
        map.addAttribute("securityGroupList", json);
        map.addAttribute("classType", "AccountTable");
        map.addAttribute("mdm", factory);
        return "/security/Account/index";
    }

    @GetMapping(value = "/user")
    public String renderUsers(Model map) throws JsonProcessingException {
        List<Account> accountList = (List<Account>) accountRepository.findAll();
        String json = new ObjectMapper().writeValueAsString(accountList);
        map.addAttribute("classType", "UserTable");
        map.addAttribute("mdm", factory);
        map.addAttribute("accountList", json);
        return "/security/User/index";
    }

    @GetMapping(value = "/security-group/permissionList/{securityGroupId}")
    @ResponseBody
    public HashMap<String, Object> getPermissionList(@PathVariable("securityGroupId") Long securityGroupId) throws JsonProcessingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<BigInteger> selectedAction = permissionService.getSecurityGroupActionAsTree(securityGroupId);
        wrapper.asCode("201").asMessage("success").asData(selectedAction);
        return wrapper.get();
    }

    @GetMapping(value = "/user/accountList/{userId}")
    @ResponseBody
    public HashMap<String, Object> getAccountList(@PathVariable("userId") Long userId) throws JsonProcessingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<BigInteger> accuontList = permissionService.getAccountList(userId);
        wrapper.asCode("201").asMessage("success").asData(accuontList);
        return wrapper.get();
    }

    @GetMapping(value = "/account/securityGroupList/{accountId}")
    @ResponseBody
    public HashMap<String, Object> getSecurityGroupList(@PathVariable("accountId") Long accountId) throws JsonProcessingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<Long> accuontList = permissionService.getSecurityGropsByUserAccountId(accountId);
        wrapper.asCode("201").asMessage("success").asData(accuontList);
        return wrapper.get();
    }

    @GetMapping(value = "/security-group/update/{security-group}/{securityActionId}/{checked}")
    @ResponseBody
    public HashMap<String, Object> updateSecurityGroup(Model map, @PathVariable("security-group") Long security_group, @PathVariable("checked") Boolean checked, @PathVariable("securityActionId") Long securityActionId) throws JsonProcessingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        permissionService.updateSecurityGroupActionPermissions(security_group, securityActionId, checked);
        wrapper.asCode("201").asMessage("success");
        return wrapper.get();
    }

    @GetMapping(value = "/user/update/{userId}/{accountId}/{checked}")
    @ResponseBody
    public HashMap<String, Object> updateAccountList(Model map, @PathVariable("userId") Long userId, @PathVariable("checked") Boolean checked, @PathVariable("accountId") Long accountId) throws JsonProcessingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        permissionService.updateAccountList(userId, accountId, checked);
        wrapper.asCode("201").asMessage("success");
        return wrapper.get();
    }

    @GetMapping(value = "/account/update/{accountId}/{secGroupId}/{checked}")
    @ResponseBody
    public HashMap<String, Object> updateSecGroups(Model map, @PathVariable("accountId") Long accountId, @PathVariable("checked") Boolean checked, @PathVariable("secGroupId") Long secGroupId) throws JsonProcessingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        permissionService.updateUserSecurityGroups(accountId, secGroupId, checked);
        wrapper.asCode("201").asMessage("success");
        return wrapper.get();
    }

    @GetMapping(value = "/user-security-group/update/{security-group}/{userId}/{checked}")
    @ResponseBody
    public HashMap<String, Object> updateUserSecurityGroup(Model map, @PathVariable("security-group") Long security_group, @PathVariable("checked") Boolean checked, @PathVariable("userId") Long userId) throws JsonProcessingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        permissionService.updateUserSecurityGroups(userId, security_group, checked);
        wrapper.asCode("201").asMessage("success");
        return wrapper.get();
    }

    @GetMapping(value = "/security-group/user/{userId}")
    public String renderUserSecurityGroup(Model map, @PathVariable("userId") Long userId) throws JsonProcessingException {
        List<SecurityGroupDto> securityGroups = permissionService.findAllSecurityGroups();
        String json = new ObjectMapper().writeValueAsString(securityGroups);
        map.addAttribute("securityGroups", json);
        map.addAttribute("userId", userId);
        List<Long> selectedSecurityGroups = permissionService.getSecurityGropsByUserAccountId(userId);
        json = new ObjectMapper().writeValueAsString(selectedSecurityGroups);
        map.addAttribute("selectedSecurityGroups", json);
        return "/security/UserSecurity";
    }

}
