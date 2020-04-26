package com.mnt.sampark.modules.mdm.tools;

import com.mnt.sampark.modules.mdm.db.domain.Complaint;
import com.mnt.sampark.modules.mdm.db.domain.ComplaintImages;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mnt.sampark.modules.mdm.db.domain.Notification;
import com.mnt.sampark.modules.mdm.db.repository.ComplaintImagesRepository;
import com.mnt.sampark.modules.mdm.db.repository.NotificationRepository;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    ComplaintImagesRepository complaintImagesRepository;

    public Number myNotificationCount(Long citizenId) {
        Number count = notificationRepository.myNotificationCount(citizenId);
        return count == null ? 0 : count;
    }

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> myNotifications(Long citizenId) {
        List<Notification> notifications = notificationRepository.myNotificationsByCitizenId(citizenId);
        List<Notification> modifiedNotifications = new ArrayList<>();
        notifications.forEach((notification) -> {
            Complaint complaint = new Complaint();
            try {
                complaint = (Complaint) notification.getComplaint().clone();
            } catch (CloneNotSupportedException ex) {
            }
            complaint.setStatus(notification.getComplaintStatus());
            List<ComplaintImages> complaintImages = complaintImagesRepository.findByComplaintId(complaint.getId());
            if (complaintImages.size() > 0) {
                String[] images = complaintImages.stream().map(l -> l.getImage()).collect(Collectors.toList()).toArray(new String[0]);
                complaint.setImages(images);
                complaint.setImage(images[0]);
            }
            notification.setComplaint(complaint);
            modifiedNotifications.add(notification);
        });
        return modifiedNotifications;
    }

    public Notification findById(Long notificatoinId) {
        return notificationRepository.findById(notificatoinId).get();
    }

}
