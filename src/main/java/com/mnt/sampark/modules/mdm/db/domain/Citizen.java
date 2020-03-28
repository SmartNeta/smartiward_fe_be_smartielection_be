package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "citizen")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Citizen extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "VoterId", nullable = false, length = 20, unique = true)
    private String voterId;

    @Column(name = "deviceId", length = 500)
    private String deviceId;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "srno", length = 20)
    private String srno;

    @Column(name = "first_name", length = 50)
    @NotNull(message = "First name can not be empty")
    private String firstName;

    @Column(name = "family_name", length = 50)
    private String familyName;

    @Column(name = "gender", length = 50)
    @NotNull(message = "Gender can not be empty")
    private String gender;

    @Column(name = "age")
    @NotNull(message = "Age can not be empty")
    private Integer age;

    @Column(name = "mobile", length = 50)
    private String mobile;

    @Column(name = "assembly_no", length = 50)
    private String assemblyNo;

    @Column(name = "booth_no", length = 50)
    private String boothNo;

    @Column(name = "ward_no", length = 50)
    private String wardNo;

    @Column(name = "address", length = 250)
    private String address;

    @Column(name = "voter_segmentation", length = 20)
    private String voterSegmentation;

    @Column(name = "longitude", length = 100)
    private String longitude;

    @Column(name = "latitude", length = 100)
    private String latitude;

    @Column(name = "party_preference")
    private String partyPreference;

    @Column(name = "voted ", length = 20)
    private Boolean voted = false;

    @Column(name = "otp ", length = 20)
    private String otp;

    @Column(name = "ac_hash ", length = 20)
    private String acHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booth_id")
    @NotNull(message = "Booth can not be empty")
    private Booth booth;

    @Column(name = "cust1", length = 50)
    private String cust1;

    @Column(name = "cust2", length = 50)
    private String cust2;

    @Column(name = "cust3", length = 50)
    private String cust3;

    @Column(name = "cust4", length = 50)
    private String cust4;

    @Column(name = "cust5", length = 50)
    private String cust5;

    @Column(name = "cust6", length = 50)
    private String cust6;

    @Column(name = "cust7", length = 50)
    private String cust7;

    @Column(name = "cust8", length = 50)
    private String cust8;

    @Column(name = "cust9", length = 50)
    private String cust9;

    @Column(name = "cust10", length = 50)
    private String cust10;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "last_login")
    private Date lastLogin;

    @Column(name = "responded_status", length = 50)
    private String respondedStatus;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "volunteer_mobile", length = 100)
    private String volunteerMobile;

    @Column(name = "printed", nullable = false)
    private Boolean printed = false;

    @Column(name = "voter_slip_sms_sent_on_mobile", length = 15)
    private String voterSlipSmsSentOnMobile;

    public String getVoterId() {
        return voterId;
    }

    public void setVoterId(String voterId) {
        this.voterId = voterId;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAssemblyNo() {
        return assemblyNo;
    }

    public void setAssemblyNo(String assemblyNo) {
        this.assemblyNo = assemblyNo;
    }

    public String getBoothNo() {
        return boothNo;
    }

    public void setBoothNo(String boothNo) {
        this.boothNo = boothNo;
    }

    public String getWardNo() {
        return wardNo;
    }

    public void setWardNo(String wardNo) {
        this.wardNo = wardNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getVoterSegmentation() {
        return voterSegmentation;
    }

    public void setVoterSegmentation(String voterSegmentation) {
        this.voterSegmentation = voterSegmentation;
    }

    public String getPartyPreference() {
        return partyPreference;
    }

    public void setPartyPreference(String partyPreference) {
        this.partyPreference = partyPreference;
    }

    public Boolean isVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

    public Booth getBooth() {
        return booth;
    }

    public void setBooth(Booth booth) {
        this.booth = booth;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getAcHash() {
        return acHash;
    }

    public void setAcHash(String acHash) {
        this.acHash = acHash;
    }

    public String getSrno() {
        return srno;
    }

    public void setSrno(String srno) {
        this.srno = srno;
    }

    public String getCust1() {
        return cust1;
    }

    public void setCust1(String cust1) {
        this.cust1 = cust1;
    }

    public String getCust2() {
        return cust2;
    }

    public void setCust2(String cust2) {
        this.cust2 = cust2;
    }

    public String getCust3() {
        return cust3;
    }

    public void setCust3(String cust3) {
        this.cust3 = cust3;
    }

    public String getCust4() {
        return cust4;
    }

    public void setCust4(String cust4) {
        this.cust4 = cust4;
    }

    public String getCust5() {
        return cust5;
    }

    public void setCust5(String cust5) {
        this.cust5 = cust5;
    }

    public String getCust6() {
        return cust6;
    }

    public void setCust6(String cust6) {
        this.cust6 = cust6;
    }

    public String getCust7() {
        return cust7;
    }

    public void setCust7(String cust7) {
        this.cust7 = cust7;
    }

    public String getCust8() {
        return cust8;
    }

    public void setCust8(String cust8) {
        this.cust8 = cust8;
    }

    public String getCust9() {
        return cust9;
    }

    public void setCust9(String cust9) {
        this.cust9 = cust9;
    }

    public String getCust10() {
        return cust10;
    }

    public void setCust10(String cust10) {
        this.cust10 = cust10;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespondedStatus() {
        return respondedStatus;
    }

    public void setRespondedStatus(String respondedStatus) {
        this.respondedStatus = respondedStatus;
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

    public Boolean getVoted() {
        return voted;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getVolunteerMobile() {
        return volunteerMobile;
    }

    public void setVolunteerMobile(String volunteerMobile) {
        this.volunteerMobile = volunteerMobile;
    }

    public Boolean getPrinted() {
        return printed;
    }

    public void setPrinted(Boolean printed) {
        this.printed = printed;
    }

    public String getVoterSlipSmsSentOnMobile() {
        return voterSlipSmsSentOnMobile;
    }

    public void setVoterSlipSmsSentOnMobile(String voterSlipSmsSentOnMobile) {
        this.voterSlipSmsSentOnMobile = voterSlipSmsSentOnMobile;
    }

}
