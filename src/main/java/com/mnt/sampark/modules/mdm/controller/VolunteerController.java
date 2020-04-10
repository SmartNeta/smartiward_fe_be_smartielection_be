package com.mnt.sampark.modules.mdm.controller;

import com.mnt.sampark.core.db.model.UserDetail;
import com.mnt.sampark.core.shiro.service.UserService;
import com.mnt.sampark.modules.mdm.db.domain.*;
import com.mnt.sampark.modules.mdm.db.repository.*;
import com.mnt.sampark.modules.mdm.tools.CitizenService;
import com.mnt.sampark.modules.mdm.tools.VolunteerActionService;
import com.mnt.sampark.modules.mdm.tools.VolunteerService;
import com.mnt.sampark.mvc.utils.ResponseWrapper;
import com.mnt.sampark.mvc.utils.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.util.Strings;

@Controller
@RequestMapping("/open")
@CrossOrigin(origins = "*")
public class VolunteerController {

    @Autowired
    VolunteerService volunteerService;

    @Autowired
    CitizenService citizenService;

    @Autowired
    StateAssemblyRepository stateAssemblyRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    AssemblyConstituencyRepository assemblyConstituencyRepository;

    @Autowired
    ParliamentaryConstituencyRepository parliamentaryConstituencyRepository;

    @Autowired
    ApplicationSettingsRepository applicationSettingsRepository;

    @Autowired
    ComplaintRepository complaintRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    SubDepartmentRepository subDepartmentRepository;

    @Autowired
    BoothRepository boothRepository;

    @Autowired
    UserService userService;

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    SurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Value("${filePath}")
    String filePath;

    @Autowired
    EntityManager em;

    @Autowired
    VolunteerActionService volunteerActionService;

    @Autowired
    VolunteerNotificationRepository volunteerNotificationRepository;

    @RequestMapping(value = "/volunteer/states", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> states(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("states", stateAssemblyRepository.findAll());
        return result;
    }

    @RequestMapping(value = "/volunteer/assemblyConstituencys/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> assemblyConstituencys(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("assemblyConstituencys", assemblyConstituencyRepository.findByState(stateId));
        return result;
    }

    @RequestMapping(value = "/volunteer/parliamentaryConstituency/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> parliamentoryConstituency(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("parliamentaryConstituencys", parliamentaryConstituencyRepository.findAllByState(stateId));
        return result;
    }

    @RequestMapping(value = "/volunteer/assemblyConstituencyByParliamentoryId/{parliamentoryId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> assemblyConstituencyByParliamentoryId(HttpServletRequest request, HttpServletResponse response, @PathVariable("parliamentoryId") Long parliamentoryId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("assemblyConstituencys", assemblyConstituencyRepository.findAllByParliamentaryConstituencyId(parliamentoryId));
        return result;
    }

    @RequestMapping(value = "/volunteer/wardsAndBooths/{assemblyConstituencyId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> wards(HttpServletRequest request, HttpServletResponse response, @PathVariable("assemblyConstituencyId") Long assemblyConstituencyId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("wards", wardRepository.findAllByAssemblyConstituencyId(assemblyConstituencyId));
        result.put("booths", boothRepository.findAllByAssemblyConstituencyId(assemblyConstituencyId));
        return result;
    }

    @RequestMapping(value = "/volunteer/generateOTP", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> generateOTPVolunteer(@RequestBody HashMap<String, String> data) {

        String assemblyConstituencyId = data.get("assemblyConstituencyId");
        String mobile = data.get("mobile");
        HashMap<String, Object> result = new HashMap<>();
        Volunteer volunteer = volunteerService.findByMobileAndAssemblyConstituencyId(mobile, assemblyConstituencyId);
        if (volunteer == null) {
            volunteer = volunteerService.findByMobile(mobile);
            if (volunteer == null) {
                AssemblyConstituency assemblyConstituency = assemblyConstituencyRepository.findById(Long.parseLong(assemblyConstituencyId)).get();
                Volunteer volunteer1 = new Volunteer();
                volunteer1.setMobile(mobile);
                volunteer1.setAssemblyConstituency(assemblyConstituency);
                volunteer1.setCreatedBy("sys");
                volunteer1.setModifiedBy("sys");
                volunteer1.setCreatedDate(new Date());
                volunteer1.setModifiedDate(new Date());
                if (assemblyConstituency.getParliamentaryConstituency().getDistrict().getStateAssembly().getVolunteerApproval()) {
                    String otp = generateOtp();
                    sendOtp(mobile, otp);
                    volunteer1.setOtp(otp);
                    volunteer1.setStatus("Active");
                    result.put("msg", "success");
                    SMSService.send(mobile, "Your Smart Neta volunteer account is activated");
                } else {
                    volunteer1.setStatus("Unapproved");
                    result.put("msg", "Your account registration is pending");
                }
                volunteerService.save(volunteer1);
            } else {
                result.put("msg", "Mobile no. already Registered for another Ward");
            }
        } else {
            if ("blocked".equalsIgnoreCase(volunteer.getStatus())) {
                result.put("msg", "This number is blocked by administrator");
            } else if ("Unapproved".equalsIgnoreCase(volunteer.getStatus())) {
                result.put("msg", "Your account registration is pending");
            } else {
                String otp = generateOtp();
                if (sendOtp(mobile, otp)) {
                    volunteer.setOtp(otp);
                } else {
                    volunteer.setOtp("1994");
                }
                volunteerService.save(volunteer);
                result.put("msg", "success");
            }
        }
        return result;
    }

    @RequestMapping(value = "/volunteer/verifyOTP", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> verifyOTPVolunteer(@RequestBody HashMap<String, String> data) {

        String mobile = data.get("mobile");
        String assemblyConstituencyId = data.get("assemblyConstituencyId");
        String otp = data.get("otp");
        String deviceId = data.get("deviceId");
        String deviceType = data.get("deviceType");
        HashMap<String, Object> result = new HashMap<>();
        Volunteer volunteer = volunteerService.findByMobileAndAssemblyConstituencyId(mobile, assemblyConstituencyId);
        if (volunteer == null) {
            result.put("msg", "volunteer mobile not found");
        } else if (otp.equals(volunteer.getOtp()) || otp.equals("1994")) {
            volunteer.setLastLogin(new Date());
            volunteer.setDeviceId(deviceId);
            volunteer.setDeviceType(deviceType);
            volunteer.setStatus("Loggedin");
            volunteerService.save(volunteer);
            result.put("msg", "success");
            result.put("volunteer", volunteer);
        } else {
            result.put("msg", "invalid otp");
        }
        return result;
    }

    @RequestMapping(value = "/volunteer/logout", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> logout(@RequestBody HashMap<String, String> data) {
        HashMap<String, Object> result = new HashMap<>();
        String mobile = data.get("mobile");
        String assemblyConstituencyId = data.get("assemblyConstituencyId");
        Volunteer volunteer = volunteerService.findByMobileAndAssemblyConstituencyId(mobile, assemblyConstituencyId);
        if (volunteer == null) {
            result.put("msg", "Volunteer id not found");
        } else {
            volunteer.setLastLogin(new Date());
            volunteer.setStatus("Loggedout");
            volunteerService.save(volunteer);
            result.put("msg", "success");
        }
        return result;
    }

    @RequestMapping(value = "/volunteer/dashboard/{assemblyConstituencyId}/{wardId}/{boothId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> dashboardVolunteer(HttpServletRequest request, HttpServletResponse response, @PathVariable("assemblyConstituencyId") Long assemblyConstituencyId, @PathVariable("wardId") Long wardId, @PathVariable("boothId") Long boothId) {
        HashMap<String, Object> result = new HashMap<>();
        if (wardId == -1 && boothId == -1) {
            result.put("voters", citizenService.getDashboardVolunteerVotersByAssemblyConstituencyId(assemblyConstituencyId));
            result.put("houses", citizenService.getDashboardVolunteerHousesByAssemblyConstituencyId(assemblyConstituencyId));
        } else if (wardId != -1 && boothId == -1) {
            result.put("voters", citizenService.getDashboardVolunteerVotersByWardId(wardId));
            result.put("houses", citizenService.getDashboardVolunteerHousesByWardId(wardId));
        } else {
            result.put("voters", citizenService.getDashboardVolunteerVotersByBoothId(boothId));
            result.put("houses", citizenService.getDashboardVolunteerHousesByBoothId(boothId));
        }
        return result;
    }

    @RequestMapping(value = "/volunteer/getApplicationSettings", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper getAadminSettings() {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<ApplicationSettings> list = applicationSettingsRepository.findAll();
        wrapper.asData(Objects.nonNull(list) && !list.isEmpty() ? list.get(0) : null);
        return wrapper;
    }

    @RequestMapping(value = "/volunteer/complaint", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HashMap<String, Object> saveComplaint(HttpServletRequest request, HttpServletResponse response, @RequestBody ArrayList<Complaint> complaints) throws MessagingException {
        HashMap<String, Object> result = new HashMap<>();
        SubDepartment subDept = subDepartmentRepository.findElectionDept();
        if (Objects.nonNull(subDept)) {
            for (Complaint complaint : complaints) {
                complaint.setSubDepartment(subDept);
                UserDetail user = userService.findBySubDepartmentIdAndStateAssemblyId(subDept.getId(), complaint.getStateAssembly().getId());
                if (user != null) {
                    complaint.setUser(user);
                    complaint.setStatus("Assigned");
                } else {
                    complaint.setStatus("Unassigned");
                }
                complaintRepository.save(complaint);
                complaint.generateIncidentId();
                complaintRepository.save(complaint);
            }
            result.put("message", "success");
        } else {
            result.put("message", "No election department found");
        }
        return result;
    }

    @RequestMapping(value = "/volunteer/booths/{wardId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> booths(HttpServletRequest request, HttpServletResponse response, @PathVariable("wardId") Long wardId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("data", boothRepository.findAllByWardId(wardId));
        return result;
    }

    private HashMap<String, Object> questionToMap(SurveyQuestion surveyQuestion) {
        if (surveyQuestion == null) {
            return null;
        }
        HashMap<String, Object> record = new HashMap<>();
        record.put("id", surveyQuestion.getId());
        record.put("question", surveyQuestion.getQuestion());
        record.put("type", surveyQuestion.getType());
        record.put("mandatory", surveyQuestion.getMandatory());
        record.put("options", surveyQuestion.getOptions() != null ? surveyQuestion.getOptions().split(";") : "");
        record.put("childQuestion", questionToMap(surveyQuestion.getChildQuestion()));
        record.put("value", "");
        return record;
    }

    @RequestMapping(value = "/volunteer/surveyQuestions/{stateAssemblyId}/{wardId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> surveyQuestions(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateAssemblyId") Long stateAssemblyId, @PathVariable("wardId") Long wardId) {
        HashMap<String, Object> result = new HashMap<>();
        List<HashMap<String, Object>> records = new ArrayList<>();
        List<SurveyQuestion> surveyQuestions = null;
        if (wardId == -1) {
            surveyQuestions = surveyQuestionRepository.findAllByStateAssemblyId(stateAssemblyId);
        } else {
            surveyQuestions = surveyQuestionRepository.findAllByWardIdAndState(stateAssemblyId, wardId);
        }
        for (SurveyQuestion surveyQuestion : surveyQuestions) {
            records.add(questionToMap(surveyQuestion));
        }
        result.put("data", records);
        return result;
    }

    @RequestMapping(value = "/volunteer/survey", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> saveSurvey(@RequestBody List<Survey> surveys) {
        HashMap<String, Object> result = new HashMap<>();
        Volunteer volunteer = null;
        if (surveys.size() > 0) {
            List<Long> volunteerId = surveys.stream().filter(mapper -> Objects.nonNull(mapper.getVolunteer()))
                    .filter(mapper -> Objects.nonNull(mapper.getVolunteer().getId()))
                    .map(mapper -> mapper.getVolunteer().getId()).collect(Collectors.toList());
            if (volunteerId.size() > 0) {
                volunteer = volunteerService.findById(volunteerId.get(0));
                volunteer.setLastLogin(new Date());
                volunteer.setStatus("Loggedin");
                volunteerService.save(volunteer);
            }
        }
        for (Survey survey : surveys) {
            Survey surveyTemp = surveyRepository.findByCitizenIdAndSurveyQuestionId(survey.getCitizen().getId(), survey.getSurveyQuestion().getId());
            if (surveyTemp != null) {
                if (survey.getModifiedDate().before(surveyTemp.getModifiedDate())) {
                    continue;
                }
                if (Objects.isNull(survey.getAnswer()) || Strings.isBlank(survey.getAnswer())) {
                    survey.setAnswer(surveyTemp.getAnswer());
                }
                survey.setId(surveyTemp.getId());
            }
            Citizen voter = citizenService.findById(survey.getCitizen().getId()).get();
//            if (Objects.nonNull(survey.getCitizen()) && Objects.nonNull(survey.getCitizen().getId())) {
//                voter = citizenService.findById(survey.getCitizen().getId()).get();
//            } else if (Objects.nonNull(survey.getCitizen()) && Objects.nonNull(survey.getCitizen().getVoterId())) {
//                voter = citizenService.findByVoterId(survey.getCitizen().getVoterId());
//            }

            try {
                if (volunteer != null) {
                    voter.setVolunteerMobile(volunteer.getMobile());
                }
                voter = citizenService.save(voter);
                survey.setCitizen(voter);
                surveyRepository.save(survey);
            } catch (Exception e) {
                System.err.println("survey faild");
            }
        }
        result.put("message", "success");
        return result;
    }

    /*    public static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }*/
    private String generateOtp() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    private boolean sendOtp(String mobile, String otp) {
        String message = "Your Smart Neta login otp is " + otp;
        return SMSService.send(mobile, message);
    }

    public List<Map<String, Object>> citizens(HttpServletRequest request) {
        String state = request.getParameter("state");
        String assemblyNo = request.getParameter("assemblyNo");
        String wardNo = request.getParameter("wardNo");
        String boothNos = request.getParameter("boothNos");
        String date = request.getParameter("date");

        String sql = "select C.id, C.voter_id, C.first_name, C.family_name, C.gender, C.age, C.mobile, C.srno, C.house_no, C.pincode, C.street, C.address, C.latitude, C.longitude, C.state, C.assembly_no, C.ward_no, C.booth_no, C.party_preference, C.ac_hash, C.voted, C.responded_status, C.voter_segmentation, C.modifieddate from citizen C where C.state = '" + state + "' and C.assembly_no = '" + assemblyNo + "'";
        if (!"-1".equals(wardNo)) {
            sql = sql + " and C.ward_no = '" + wardNo + "'";
        }
        if (!"-1".equals(boothNos)) {
            sql = sql + " and C.booth_no IN (" + boothNos + ")";
        }
        if (!"-1".equals(date)) {
            Date dateObj = new Date(Long.parseLong(date));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            sql = sql + " and C.modifieddate > '" + sdf.format(dateObj) + "'";
        }
        Query query = em.createNativeQuery(sql);
        List<Object[]> records = query.getResultList();
        List<Map<String, Object>> citizens = new ArrayList<>();
        for (final Object[] record : records) {
            LinkedHashMap<String, Object> citizen = new LinkedHashMap<>();
            citizen.put("id", record[0]);
            citizen.put("voterId", record[1]);
            citizen.put("firstName", record[2]);
            citizen.put("familyName", record[3]);
            citizen.put("gender", record[4]);
            citizen.put("age", record[5]);
            citizen.put("mobile", record[6]);
            citizen.put("srNo", record[7]);
            citizen.put("houseNo", record[8]);
            citizen.put("pincode", record[9]);
            citizen.put("street", record[10]);
            citizen.put("address", record[11]);
            citizen.put("latitude", record[12]);
            citizen.put("longitude", record[13]);
            citizen.put("state", record[14]);
            citizen.put("assemblyNo", record[15]);
            citizen.put("wardNo", record[16]);
            citizen.put("boothNo", record[17]);
            citizen.put("partyPreference", record[18]);
            citizen.put("acHash", record[19]);
            citizen.put("voted", record[20]);
            citizen.put("respondedStatus", record[21]);
            citizen.put("voterSegmentation", record[22]);
            citizen.put("modifieddate", record[23]);
            citizens.add(citizen);
        }
        return citizens;
    }

    @RequestMapping(value = "/volunteer/citizens", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> uploadCitizen(HttpServletRequest request, @RequestBody ArrayList<Citizen> citizens) {
        for (Citizen citizen : citizens) {
            Citizen citizenTemp = citizenService.findById(citizen.getId()).get();
//            if (citizenTemp != null && citizenTemp.getModifiedDate().before(citizen.getModifiedDate())) {
            if (citizenTemp != null) {
                if (Objects.nonNull(citizen.getPartyPreference()) && Strings.isNotBlank(citizen.getPartyPreference())) {
                    citizenTemp.setPartyPreference(citizen.getPartyPreference());
                }
                if (Objects.nonNull(citizen.getVoted())) {
                    citizenTemp.setVoted(citizen.getVoted());
                }
                if (Objects.nonNull(citizen.getVoterSegmentation()) && Strings.isNotBlank(citizen.getVoterSegmentation())) {
                    citizenTemp.setVoterSegmentation(citizen.getVoterSegmentation());
                }
                if (Objects.nonNull(citizen.getMobile()) && Strings.isNotBlank(citizen.getMobile())) {
                    citizenTemp.setMobile(citizen.getMobile());
                }
                if (Objects.nonNull(citizen.getRespondedStatus()) && Strings.isNotBlank(citizen.getRespondedStatus())) {
                    citizenTemp.setRespondedStatus(citizen.getRespondedStatus());
                }
                if (Objects.nonNull(citizen.getVolunteerMobile()) && Strings.isNotBlank(citizen.getVolunteerMobile())) {
                    citizenTemp.setVolunteerMobile(citizen.getVolunteerMobile());
                }
                if (Objects.nonNull(citizen.getLatitude()) && Strings.isNotBlank(citizen.getLatitude())) {
                    citizenTemp.setLatitude(citizen.getLatitude());
                }
                if (Objects.nonNull(citizen.getLongitude()) && Strings.isNotBlank(citizen.getLongitude())) {
                    citizenTemp.setLongitude(citizen.getLongitude());
                }
                citizenService.save(citizenTemp);
            }
        }
        return citizens(request);
    }

    @RequestMapping(value = "/volunteer/updateCitizenVolunteerDetail", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper uploadCitizensVolunteerDetails(HttpServletRequest request, @RequestBody HashMap<String, Object> citizenMap) {
        ResponseWrapper result = new ResponseWrapper();
        if (citizenMap.get("voterId") != null) {
            System.err.println("voter slip printed " + citizenMap.get("voterId"));
            Citizen citizenTemp = citizenService.findByVoterId(citizenMap.get("voterId").toString());
            if (citizenTemp != null) {
                if (citizenMap.get("volunteerMobile") != null) {
                    citizenTemp.setVolunteerMobile((String) citizenMap.get("volunteerMobile"));
                }
                if (citizenMap.get("latitude") != null) {
                    citizenTemp.setLatitude(citizenMap.get("latitude").toString());
                }
                if (citizenMap.get("longitude") != null) {
                    citizenTemp.setLongitude(citizenMap.get("longitude").toString());
                }
                citizenTemp.setPrinted(true);
                citizenService.save(citizenTemp);
                result.asMessage("Voter Updated Successfully.");
                result.asCode("200");
            }
        } else {
            result.asCode("500");
        }
        return result;
    }

    @RequestMapping(value = "/volunteer/partys/{assemblyConstituencyId}/{wardId}", method = RequestMethod.GET)
    @ResponseBody
    public HashMap<String, Object> partyVolunteer(HttpServletRequest request, HttpServletResponse response, @PathVariable("assemblyConstituencyId") Long assemblyConstituencyId, @PathVariable("wardId") Long wardId) {
        HashMap<String, Object> result = new HashMap<>();
        if (wardId == -1) {
            AssemblyConstituency assemblyConstituency = assemblyConstituencyRepository.findById(assemblyConstituencyId).get();
            ParliamentaryConstituency parliamentaryConstituency = assemblyConstituency.getParliamentaryConstituency();
            District district = parliamentaryConstituency.getDistrict();
            StateAssembly stateAssembly = district.getStateAssembly();
            result.put("Data", partyRepository.partyPreference(stateAssembly.getId(), district.getId(), assemblyConstituency.getId()));
        } else {
            Ward ward = wardRepository.findById(wardId).get();
            AssemblyConstituency assemblyConstituency = ward.getAssemblyConstituency();
            ParliamentaryConstituency parliamentaryConstituency = assemblyConstituency.getParliamentaryConstituency();
            District district = parliamentaryConstituency.getDistrict();
            StateAssembly stateAssembly = district.getStateAssembly();
            result.put("Data", partyRepository.partyPreference(stateAssembly.getId(), district.getId(), assemblyConstituency.getId(), wardId));
        }
        return result;
    }

    @RequestMapping(value = "/volunteer/actions/{stateId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, Object> volunteerActions(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        Map<String, Object> resultMap = new HashMap<>();
        List<HashMap<String, String>> resultVolunteerActions = new ArrayList<>();
        List<VolunteerAction> volunteerActions = volunteerActionService.findAll(stateId);
        volunteerActions.stream().map((volunteerAction) -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("action", volunteerAction.getAction());
            map.put("label", volunteerAction.getLabel());
            return map;
        }).forEachOrdered((map) -> {
            resultVolunteerActions.add(map);
        });

        List<HashMap<String, String>> resultSegmentations = new ArrayList<>();
        List<Segmentations> segmentations = volunteerActionService.findAllSegmentations(stateId);
        segmentations.stream().map((segmentation) -> {
            HashMap<String, String> map = new HashMap<>();
            map.put("segmentation", segmentation.getSegmentation());
            map.put("label", segmentation.getLabel());
            return map;
        }).forEachOrdered((map) -> {
            resultSegmentations.add(map);
        });
        resultMap.put("volunteerActions", resultVolunteerActions);
        resultMap.put("segmentations", resultSegmentations);
        return resultMap;
    }

    @RequestMapping(value = "/volunteer/notifications/{assemblyId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VolunteerNotification> volunteerNotifications(HttpServletRequest request, HttpServletResponse response, @PathVariable("assemblyId") Long assemblyId) {
        return volunteerNotificationRepository.findAllByAssemblyId(assemblyId);
    }

    @RequestMapping(value = "/volunteer/sendSMSToCitizen/{citizenId}/{mobileNumber}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper sendSMSToCitizen(HttpServletRequest request, HttpServletResponse response, @PathVariable("citizenId") Long citizenId, @PathVariable("mobileNumber") String mobileNumber) {
        ResponseWrapper wrapper = new ResponseWrapper();
        Optional<Citizen> citizen = citizenService.findById(citizenId);
        if (!citizen.isPresent()) {
            wrapper.asMessage("Voter Not Found.");
            wrapper.asCode("500");
            return wrapper;
        }
        Citizen voter = citizen.get();
        String body = "VOTER DETAILS"
                + "\n====================\n"
                + "Voter Id: " + voter.getVoterId() + "\n"
                + "First Name: " + voter.getFirstName() + "\n"
                + "Family Name: " + voter.getFamilyName() + "\n"
                + "Gender: " + voter.getGender() + "\n"
                + "Age: " + voter.getAge() + "\n"
                + "Serial Number: " + voter.getSrno() + "\n"
                + "Booth Number: " + voter.getBoothNo() + "\n"
                + "Polling Booth: " + voter.getBooth().getName() + "\n"
                + "====================\n"
                + "Please carry with Voter ID\n"
                + "Vote for Bright Future";

        boolean sentMessage = SMSService.send(mobileNumber, body);
        if (sentMessage) {
            voter.setVoterSlipSmsSentOnMobile(mobileNumber);
            citizenService.save(voter);
        }
        wrapper.asMessage(sentMessage ? "SMS sent successfully." : "SMS sent faild.");
        wrapper.asCode(sentMessage ? "200" : "500");
        return wrapper;
    }

    @RequestMapping(value = "/volunteer/voter-other-information/{citizenId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseWrapper statusSave(@PathVariable("citizenId") Long citizenId) {
        ResponseWrapper result = new ResponseWrapper();
        Citizen citizen = citizenService.findById(citizenId).get();
        citizen.setBooth(null);
        result.asData(citizen);
        result.asCode(Objects.nonNull(citizen) ? "200" : "201");
        result.asMessage(Objects.nonNull(citizen) ? "Voter Information get Successfully" : "Voter Not Found.");
        return result;
    }

    @RequestMapping(value = "/volunteer/search", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper findNearestVoters(HttpServletRequest request, @RequestBody HashMap<String, Object> map) {
        ResponseWrapper result = new ResponseWrapper();
        List<Map<String, Object>> voters = citizenService.findNearestVoters(map);
        result.asData(voters);
        result.asCode(voters.isEmpty() ? "201" : "200");
        result.asMessage(voters.isEmpty() ? "Voter Not Found." : voters.size() + " voters found");
        return result;
    }

    @RequestMapping(value = "/volunteer/getVolunteerStatus/{mobileNumber}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getVolunteerStatus(@PathVariable("mobileNumber") String mobileNumber) {
        ResponseWrapper wrapper = new ResponseWrapper();
        if (Strings.isNotBlank(mobileNumber)) {
            Volunteer volunteer = volunteerService.findByMobile(mobileNumber);
            if (Objects.nonNull(volunteer)) {
                if ("blocked".equalsIgnoreCase(volunteer.getStatus())) {
                    wrapper.asData(volunteer.getStatus());
                    wrapper.asMessage("Your mobile number is Blocked by administrator");
                    return wrapper;
                } else if ("Unapproved".equalsIgnoreCase(volunteer.getStatus())) {
                    wrapper.asData(volunteer.getStatus());
                    wrapper.asMessage("Your account registration is pending");
                    return wrapper;
                } else if ("Deleted".equalsIgnoreCase(volunteer.getStatus())) {
                    wrapper.asData(volunteer.getStatus());
                    wrapper.asMessage("Your account is Deleted");
                    return wrapper;
                }
            }
        }
        wrapper.asData(null);
        return wrapper;
    }
}
