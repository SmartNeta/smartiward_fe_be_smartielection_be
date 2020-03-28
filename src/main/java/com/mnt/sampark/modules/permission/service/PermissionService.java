package com.mnt.sampark.modules.permission.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mnt.sampark.core.db.model.SecurityActions;
import com.mnt.sampark.core.db.model.SecurityGroup;
import com.mnt.sampark.core.db.model.SecurityGroupSecurityActions;
import com.mnt.sampark.core.db.model.SecurityGroupSecurityActionsId;
import com.mnt.sampark.core.db.model.UserAccount;
import com.mnt.sampark.core.db.model.UserAccountSecGroup;
import com.mnt.sampark.core.db.repository.SecurityActionsRepository;
import com.mnt.sampark.core.db.repository.SecurityGroupRepository;
import com.mnt.sampark.core.db.repository.SecurityGroupSecurityActionRepository;
import com.mnt.sampark.core.db.repository.UserAccountRepository;
import com.mnt.sampark.core.db.repository.UserAccountSecurityGroupRepository;
import com.mnt.sampark.modules.permission.dto.PermissionHierarchy;
import com.mnt.sampark.modules.permission.dto.SecurityGroupDto;

@Service
public class PermissionService {

    @Autowired
    SecurityActionsRepository actionsRepository;

    @Autowired
    SecurityGroupSecurityActionRepository groupActionsRepository;

    @Autowired
    SecurityGroupRepository securityGroupRepository;

    @Autowired
    UserAccountSecurityGroupRepository userAccountSecurityGroupRepository;

    @Autowired
    UserAccountRepository userAccountRepository;

    public PermissionHierarchy getApplicationPermissionsAsTree() {
        List<SecurityActions> list = new ArrayList<>();
        actionsRepository.findAll().forEach(list::add);
        PermissionHierarchy root = null, parent = null;
        for (SecurityActions action : list) {
            String[] cells = action.getName().split(":");
            for (int i = 0; i < cells.length; i++) {
                if (i == 0) {
                    if (root == null) {
                        root = new PermissionHierarchy();
                        root.id = action.getId();
                        root.label = action.getDescription();
                        root.name = cells[i];
                    } else {
                        if (!root.name.equals(cells[i])) {
                            //this is error case
                        }
                    }
                    parent = root;
                } else {
                    if (parent.children.size() == 0) {
                        PermissionHierarchy child = new PermissionHierarchy();
                        child.id = action.getId();
                        child.name = cells[i];
                        child.label = action.getDescription();
                        parent.children.add(child);
                        parent = child;
                    } else {
                        boolean found = false;
                        for (int j = 0; j < parent.children.size(); j++) {

                            if (parent.children.get(j).name != null && parent.children.get(j).name.equals(cells[i])) {
                                //already have children
                                parent = parent.children.get(j);
                                found = true;
                                break;
                            } else {
                                //DO nothing
                            }
                        }
                        if (!found) {
                            PermissionHierarchy child = new PermissionHierarchy();
                            child.id = action.getId();
                            child.name = cells[i];
                            child.label = action.getDescription();
                            parent.children.add(child);
                            parent = child;
                        }
                    }
                    if (i == cells.length - 1) {
                        parent.leaf = true;
                    }
                }

            }

        }
        return root;
    }

    public List<BigInteger> getSecurityGroupActionAsTree(Long securityGroupId) throws JsonProcessingException {
        List<Long> selectedAction = new ArrayList<>();
        SecurityGroup securityGroupObj = securityGroupRepository.findById(securityGroupId).get();
        List<BigInteger> securityActions = new ArrayList<>();
        if (securityGroupObj != null) {
            securityActions = groupActionsRepository.findBySecurityActionsIdByGroupId(securityGroupObj.getId());
            PermissionHierarchy root = getApplicationPermissionsAsTree();
            root = updateSelected(root, securityActions, selectedAction);
        }
        return securityActions;
    }

    public List<BigInteger> getAccountList(Long userId) throws JsonProcessingException {
        List<BigInteger> accountList = userAccountRepository.findByUserId(userId);
        return accountList;
    }

    public void updateSecurityGroupActionPermissions(Long securityGroupId, Long securityActionId, Boolean checked) {
        SecurityGroup securityGroupObj = securityGroupRepository.findById(securityGroupId).get();
        if (securityGroupObj != null) {

            SecurityGroupSecurityActions groupAction = new SecurityGroupSecurityActions(new SecurityGroupSecurityActionsId(securityGroupObj.getId(), securityActionId));
            if (checked) {
                groupActionsRepository.save(groupAction);
            } else {
                groupActionsRepository.delete(groupAction);
            }
        }
    }

    public void updateAccountList(Long userId, Long accountId, Boolean checked) {
        UserAccount userAccount = userAccountRepository.findOneByAccountIdAndUserId(accountId, userId);
        Boolean recordPresent = false;
        if (userAccount != null) {
            recordPresent = true;
            System.out.println("record present");
        } else {

            recordPresent = false;
            System.out.println("record Not present");
        }

        if (checked) {
            if (!recordPresent) {
                UserAccount newUserAccount = new UserAccount();
                newUserAccount.setAccountId(accountId);
                newUserAccount.setUserId(userId);
                userAccountRepository.save(newUserAccount);
            }
        } else {
            if (recordPresent) {
                userAccountRepository.delete(userAccount);
            }
        }
    }

    public PermissionHierarchy updateSelected(PermissionHierarchy root, List<BigInteger> securityActions, List<Long> selectedAction) {
        if (root.leaf && securityActions.contains(BigInteger.valueOf(root.id))) {
            root.selected = true;
            selectedAction.add(root.id);
        }
        List<PermissionHierarchy> newChildList = new ArrayList<>();
        for (PermissionHierarchy child : root.children) {
            child = updateSelected(child, securityActions, selectedAction);
            newChildList.add(child);
            root.children = newChildList;
        }
        return root;
    }

    public void updateUserSecurityGroups(Long accountId, Long securityGroupId, Boolean checked) {
        UserAccountSecGroup userAccountSecGroup = userAccountSecurityGroupRepository.findByUserAccountIdAndSecurityGroupId(accountId, securityGroupId);
        Boolean recordPresent = false;
        if (userAccountSecGroup != null) {
            recordPresent = true;
            System.out.println("record present");
        } else {

            recordPresent = false;
            System.out.println("record Not present");
        }

        if (checked) {
            if (!recordPresent) {
                UserAccountSecGroup UserAccountSecGroupNew = new UserAccountSecGroup(accountId, securityGroupId);
                userAccountSecurityGroupRepository.save(UserAccountSecGroupNew);
            }
        } else {
            if (recordPresent) {
                userAccountSecurityGroupRepository.delete(userAccountSecGroup);
            }
        }
    }

    public List<SecurityGroupDto> findAllSecurityGroups() {
        List<SecurityGroup> securityGroups = (List<SecurityGroup>) securityGroupRepository.findAll();
        List<SecurityGroupDto> securityGroupsDto = new ArrayList<>();
        for (SecurityGroup securityGoup : securityGroups) {
            SecurityGroupDto securityGroupDto = new SecurityGroupDto();
            securityGroupDto.id = securityGoup.getId();
            securityGroupDto.label = securityGoup.getName();
            securityGroupsDto.add(securityGroupDto);
        }
        return securityGroupsDto;
    }

    public List<Long> getSecurityGropsByUserAccountId(Long userAccountId) {
        List<UserAccountSecGroup> userAccountSecGroups = userAccountSecurityGroupRepository.findByUserAccountId(userAccountId);
        List<Long> securityGroups = new ArrayList<>();
        for (UserAccountSecGroup userAccountSecGroup : userAccountSecGroups) {
            securityGroups.add(userAccountSecGroup.getSecurityGroupId());
        }
        return securityGroups;
    }

}
