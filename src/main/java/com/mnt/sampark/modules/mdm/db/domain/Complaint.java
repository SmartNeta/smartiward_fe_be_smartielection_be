package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;
import com.mnt.sampark.core.db.model.UserDetail;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "complaint")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Complaint extends AuditableLongBaseEntity implements Cloneable {

    private static final long serialVersionUID = 1L;

    @Transient
    private String[] images;

    @Transient
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citizen_id")
    private Citizen citizen;

    @Column(name = "voter_id", length = 1000)
    private String voterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_department_id")
    @NotNull(message = "Sub-Department can not be empty")
    private SubDepartment subDepartment;

    @Column(name = "longitude", length = 100)
    private String longitude;

    @Column(name = "latitude", length = 100)
    private String latitude;

    @Column(name = "complaint", length = 1000)
    private String complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserDetail user;

    @Column(name = "date_handed_over_to_responsible_department")
    private Date dateHandedOverToResponsibleDepartment;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "comments_from_department", length = 250)
    private String commentsFromDepartment;

    @Column(name = "tentative_date_of_completion")
    private Date tentativeDateOfCompletion;

    @Column(name = "compliant_source", length = 100)
    private String compliantSource;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "incident_id", length = 30)
    private String incidentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_assembly_id")
    @NotNull(message = "State assembly can not be empty")
    private StateAssembly stateAssembly;

    public String[] getImages() {
        return images;
    }

    public void setImages(String[] images) {
        this.images = images;
    }

    public SubDepartment getSubDepartment() {
        return subDepartment;
    }

    public void setSubDepartment(SubDepartment subDepartment) {
        this.subDepartment = subDepartment;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public Citizen getCitizen() {
        return citizen;
    }

    public void setCitizen(Citizen citizen) {
        this.citizen = citizen;
    }

    public UserDetail getUser() {
        return user;
    }

    public void setUser(UserDetail user) {
        this.user = user;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Date getDateHandedOverToResponsibleDepartment() {
        return dateHandedOverToResponsibleDepartment;
    }

    public void setDateHandedOverToResponsibleDepartment(Date dateHandedOverToResponsibleDepartment) {
        this.dateHandedOverToResponsibleDepartment = dateHandedOverToResponsibleDepartment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCommentsFromDepartment() {
        return commentsFromDepartment;
    }

    public void setCommentsFromDepartment(String commentsFromDepartment) {
        this.commentsFromDepartment = commentsFromDepartment;
    }

    public Date getTentativeDateOfCompletion() {
        return tentativeDateOfCompletion;
    }

    public void setTentativeDateOfCompletion(Date tentativeDateOfCompletion) {
        this.tentativeDateOfCompletion = tentativeDateOfCompletion;
    }

    public String getCompliantSource() {
        return compliantSource;
    }

    public void setCompliantSource(String compliantSource) {
        this.compliantSource = compliantSource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIncidentId() {
        return incidentId;
    }

    public void setIncidentId(String incidentId) {
        this.incidentId = incidentId;
    }

    public StateAssembly getStateAssembly() {
        return stateAssembly;
    }

    public void setStateAssembly(StateAssembly stateAssembly) {
        this.stateAssembly = stateAssembly;
    }

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
    }

    public void generateIncidentId() {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        String incidentId = "";
        if (compliantSource != null && !compliantSource.isEmpty()) {
            incidentId = incidentId + compliantSource.charAt(0);
        }
        incidentId = incidentId + id;
        if (subDepartment != null && subDepartment.getDepartment() != null) {
            incidentId = incidentId + subDepartment.getDepartment().getCode();
        }
        incidentId = incidentId + sdf.format(createdDate);
        this.incidentId = incidentId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
