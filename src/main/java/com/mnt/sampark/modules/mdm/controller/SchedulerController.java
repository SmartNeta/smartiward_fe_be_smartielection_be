package com.mnt.sampark.modules.mdm.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.mnt.sampark.modules.mdm.db.domain.Complaint;
import com.mnt.sampark.modules.mdm.db.repository.ComplaintRepository;
import com.mnt.sampark.mvc.utils.SMSService;

@Controller
public class SchedulerController {

    @Autowired
    ComplaintRepository complaintRepository;

    //@Scheduled(fixedRate = 3600000)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void complaints() {
        List<Complaint> todaysDueComplaints = complaintRepository.findAllTodaysDueComplaints(); 
        
        for(Complaint complaint : todaysDueComplaints)  {
        	if(!Objects.isNull(complaint.getUser())) {
                String message = "Complaint " + complaint.getIncidentId() + " is due on today";
            	SMSService.send(complaint.getUser().getPhone(), message);
        	}
        }
        List<Complaint> pendingComplaints = complaintRepository.findAllPendingComplaints();
        for(Complaint complaint : pendingComplaints)  {
        	if(!Objects.isNull(complaint.getUser())) {
                String message = "Complaint " + complaint.getIncidentId() + " is still pending";
            	SMSService.send(complaint.getUser().getPhone(), message);
        	}
        }
    }
}
