package com.mnt.sampark.modules.mdm.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.google.common.collect.Lists;
import com.mnt.sampark.core.db.model.UserDetail;
import com.mnt.sampark.core.shiro.service.UserService;
import com.mnt.sampark.modules.mdm.db.domain.Citizen;
import com.mnt.sampark.modules.mdm.db.domain.Complaint;
import com.mnt.sampark.modules.mdm.db.domain.SubDepartment;
import com.mnt.sampark.modules.mdm.db.repository.ApplicationSettingsRepository;
import com.mnt.sampark.modules.mdm.db.repository.AssemblyConstituencyRepository;
import com.mnt.sampark.modules.mdm.db.repository.BoothRepository;
import com.mnt.sampark.modules.mdm.db.repository.CitizenRepository;
import com.mnt.sampark.modules.mdm.db.repository.ComplaintImagesRepository;
import com.mnt.sampark.modules.mdm.db.repository.ComplaintRepository;
import com.mnt.sampark.modules.mdm.db.repository.DepartmentRepository;
import com.mnt.sampark.modules.mdm.db.repository.NewsFeedRepository;
import com.mnt.sampark.modules.mdm.db.repository.StateAssemblyRepository;
import com.mnt.sampark.modules.mdm.db.repository.SubDepartmentRepository;
import com.mnt.sampark.modules.mdm.db.repository.WardRepository;
import com.mnt.sampark.modules.mdm.tools.CitizenService;
import com.mnt.sampark.modules.mdm.tools.NotificationService;

@Controller
@RequestMapping("/open")
@CrossOrigin(origins = "*")
public class DataGeneratorController {

    @Autowired
    CitizenService citizenService;

    @Autowired
    UserService userService;

    @Autowired
    ComplaintRepository complaintRepository;

    @Autowired
    CitizenRepository citizenRepository;

    @Autowired
    ComplaintImagesRepository complaintImagesRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    SubDepartmentRepository subDepartmentRepository;

    @Autowired
    StateAssemblyRepository stateAssemblyRepository;

    @Autowired
    AssemblyConstituencyRepository assemblyConstituencyRepository;

    @Autowired
    BoothRepository boothRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    NewsFeedRepository newsFeedRepository;

    @Autowired
    ApplicationSettingsRepository applicationSettingsRepository;

    @Value("${filePath}")
    String filePath;

    @RequestMapping(value = "/mobile/test", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response) {
    	List<Complaint> complaints = new ArrayList<Complaint>();
    	List<Citizen> citizens = citizenRepository.findByBoothIn(boothRepository.findAll());
    	List<SubDepartment> subDepartments = (List<SubDepartment>) subDepartmentRepository.findAll();
    	String status[] = {"Assigned", "Inprogress", "Resolved", "Ignore", "Out Of Scope", "Under Review"}; 
    	Random rand = new Random();
    	for(int index = 0 ; index < 100000 ; index ++) {
    		Complaint complaint = new Complaint();
    		SubDepartment subDepartment = subDepartments.get(rand.nextInt(subDepartments.size() - 1));
    		Citizen citizen = citizens.get(rand.nextInt(citizens.size() - 1));
    		complaint.setCitizen(citizen);
    		complaint.setSubDepartment(subDepartment);
            List<UserDetail> users = userService.findAllByWardIdAndSubDepartmentId(citizen.getBooth().getWard().getId(), complaint.getSubDepartment().getId());
            if (users.size() > 0) {
                complaint.setUser(users.get(0));
                complaint.setStatus(status[rand.nextInt(status.length - 1)]);
            } else {
                complaint.setStatus("Unassigned");
            }
            complaint.setStateAssembly(citizen.getBooth().getWard().getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getStateAssembly());
            complaintRepository.save(complaint);
            complaint.generateIncidentId();
            complaint.setComplaint("This is complaint text complaint id " + complaint.getIncidentId());
            complaint.setName("complaint id " + complaint.getIncidentId());
            complaintRepository.save(complaint);
            complaints.add(complaint);
    	}
    	
    	HashMap<String, Object> result = new HashMap<>();
    	result.put("complaints", complaints);
        return result;
    }

}
