package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "volunteer")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Volunteer extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "gender", length = 50)
    private String gender;

    @Column(name = "mobile", length = 50, unique = true)
    @NotNull(message = "Mobile can not be empty")
    private String mobile;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "otp", length = 5)
    private String otp;

    @Column(name = "device_id", length = 500)
    private String deviceId;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assembly_constituency_id", unique = true)
    @NotNull(message = "Assembly Constituency can not be empty")
    private AssemblyConstituency assemblyConstituency;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "last_login")
    private Date lastLogin;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public AssemblyConstituency getAssemblyConstituency() {
        return assemblyConstituency;
    }

    public void setAssemblyConstituency(AssemblyConstituency assemblyConstituency) {
        this.assemblyConstituency = assemblyConstituency;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

}
