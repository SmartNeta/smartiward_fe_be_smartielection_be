package com.mnt.sampark.core.shiro.service;

import com.mnt.sampark.core.db.model.*;
import com.mnt.sampark.core.db.repository.*;
import com.mnt.sampark.modules.mdm.db.domain.Ward;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UserService {

    @Autowired
    private UserAccountSecurityGroupRepository userAccountSecurityrepository;

    @Autowired
    private SecurityGroupRepository securityGroupRepository;

    @Autowired
    private SecurityGroupSecurityActionRepository securityGroupSecurityActionRepository;

    @Autowired
    private SecurityActionsRepository securityActionsRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailRepository userDetailRepository;

    public User findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }

    public List<String> findPermissionUrl(String account) {
        //TODO: This is dummy implementation
        List<String> s = new ArrayList<String>();
        s.add("/inventory/index.html");
        return s;
    }

    public Set<SecurityGroup> getSecurityGroupByUsernameAndAccount(String username, Long account) {
        User user = findByUsername(username);
        UserAccount userAccount = userAccountRepository.findOneByAccountIdAndUserId(account, user.getId());
        List<UserAccountSecGroup> list = userAccountSecurityrepository.findByUserAccountId(userAccount.getAccountId());
        Set<SecurityGroup> s = new HashSet<SecurityGroup>();
        list.forEach(secGroup -> {
            s.add(securityGroupRepository.findById(secGroup.getSecurityGroupId()).get());
        });
        return s;
    }

    public Set<SecurityActions> getSecurityActionsBySecurityGroup(SecurityGroup role) {
        List<SecurityGroupSecurityActions> actionMappings = securityGroupSecurityActionRepository.findBySecurityGroupId(role.getId());
        Set<SecurityActions> s = new HashSet<SecurityActions>();
        actionMappings.forEach(secAction -> {
            s.add(securityActionsRepository.findById(secAction.getId().getSecurityActionsId()).get());
        });
        return s;
    }

    public UserDetail findByUserId(Long userId) {
        return userDetailRepository.findByUserId(userId);
    }

    public void saveUserDetail(UserDetail userDetail) {
        userDetailRepository.save(userDetail);
    }

    public Long getActiveMember(Long state_id) {
        return userDetailRepository.findActiveMember(state_id);
    }

    public Long getActiveMemberByWardIn(List<Long> wardIds) {
        return userDetailRepository.findActiveMemberByWardIn(wardIds);
    }

    public Long getAllMember(Long state_id) {
        return userDetailRepository.findAllMember(state_id);
    }

    public Long findAllMemberByWardIn(List<Long> wardIds) {
        return userDetailRepository.findAllMemberByWardIn(wardIds);
    }

    public UserDetail findByUserDetailId(Long userDetailId) {
        return userDetailRepository.findByUserDetailId(userDetailId);
    }

    public List<UserDetail> findAllByWardIdAndSubDepartmentId(Long id, Long id2) {
        return userDetailRepository.findAllByWardIdAndSubDepartmentId(id, id2);
    }

    public List<String> getSecurityActionsBySecurityGroupIds(List<Long> roleIds) {
        return securityGroupSecurityActionRepository.findAllSecurityActionsBySecurityGroupIn(roleIds);
    }

    public UserDetail findBySubDepartmentIdAndStateAssemblyId(Long subDeptId, Long stateAssemblyId) {
        return userDetailRepository.findBySubDepartmentIdAndStateAssemblyId(subDeptId, stateAssemblyId);
    }

}
