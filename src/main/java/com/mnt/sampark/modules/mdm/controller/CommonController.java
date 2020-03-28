package com.mnt.sampark.modules.mdm.controller;

import com.itextpdf.text.DocumentException;
import com.opencsv.CSVWriter;
import com.mnt.sampark.core.db.model.User;
import com.mnt.sampark.core.db.model.UserDetail;
import com.mnt.sampark.core.db.repository.UserDetailRepository;
import com.mnt.sampark.core.shiro.service.UserService;
import com.mnt.sampark.modules.mdm.db.domain.*;
import com.mnt.sampark.modules.mdm.db.repository.*;
import com.mnt.sampark.modules.mdm.tools.CitizenService;
import com.mnt.sampark.modules.mdm.tools.HierarchyUploadService;
import com.mnt.sampark.modules.mdm.tools.NotificationService;
import com.mnt.sampark.modules.mdm.tools.VolunteerActionService;
import com.mnt.sampark.modules.mdm.tools.VolunteerService;
import com.mnt.sampark.mvc.utils.*;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.io.IOUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;

@RestController
@RequestMapping("/open/sampark")
@CrossOrigin(origins = "*")
public class CommonController {

    @Value("${filePath}")
    String filePath;

    @Autowired
    BoothRepository boothRepository;

    @Autowired
    NewsFeedRepository newsFeedRepository;

    @Autowired
    SurveyQuestionRepository surveyQuestionRepository;

    @Autowired
    SurveyRepository surveyRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    CitizenRepository citizenRepository;

    @Autowired
    UserDetailRepository userDetailRepository;

    @Autowired
    HierarchyUploadService hierarchyUploadService;

    @Autowired
    CitizenService citizenService;

    @Autowired
    VolunteerService volunteerService;

    @Autowired
    UserService userService;

    @Autowired
    ComplaintRepository complaintRepository;

    @Autowired
    SubDepartmentRepository subDepartmentRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    ParliamentaryConstituencyRepository parliamentaryConstituencyRepository;

    @Autowired
    AssemblyConstituencyRepository assemblyConstituencyRepository;

    @Autowired
    VolunteerRepository volunteerRepository;

    @Autowired
    EmailService emailservice;

    @Autowired
    NotificationService notificationService;

    @Autowired
    ApplicationSettingsRepository applicationSettingsRepository;

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    StateAssemblyRepository stateAssemblyRepository;

    @Autowired
    ComplaintImagesRepository complaintImagesRepository;

    @Autowired
    CSVFileInfoRepository csvFileInfoRepository;

    @Autowired
    ReportService reportService;

    @Autowired
    VolunteerActionService volunteerActionService;

    @Autowired
    VolunteerNotificationRepository volunteerNotificationRepository;

    @Autowired
    PreviousElectionUploadsRepository previousElectionUploadsRepository;

    @Autowired
    PreviousElectionRepository previousElectionRepository;

    @RequestMapping(value = "/api/upload-hierarchy-csv", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper uploadHierarchyFile(@RequestParam("file") MultipartFile receivedFile,
            @RequestParam("stateAssemblyId") Long stateAssemblyId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        File readableFile = new File(filePath + File.separator + receivedFile.getOriginalFilename());
        try {
            receivedFile.transferTo(readableFile);
            Map<String, Object> result = hierarchyUploadService.parse(readableFile, stateAssemblyId);
            wrapper.asData(result).asCode("201");
        } catch (IllegalStateException | IOException e) {
            wrapper.asError();
        }
        return wrapper;
    }

    @RequestMapping(value = "/api/upload-csv", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper uploadDocumnet(@RequestParam("file") MultipartFile receivedFile, @RequestParam("state") String state) {
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            int length = 4;
            boolean useLetters = true;
            boolean useNumbers = false;
            String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

            String csvFilePath = filePath + File.separator + "csvfiles" + File.separator + state;
            new File(csvFilePath).mkdirs();

            File readableFile = new File(csvFilePath + File.separator + generatedString + "_" + receivedFile.getOriginalFilename());
            receivedFile.transferTo(readableFile);
            CSVFileInfo csvFile = new CSVFileInfo();
            csvFile.setPath(readableFile.getAbsolutePath());
            csvFile.setState(state);
            csvFile.setStatus("new");
            csvFileInfoRepository.save(csvFile);
            wrapper.asMessage("File Uploaded Successfully.");
            wrapper.asCode("201");
            wrapper.asData(csvFile);
        } catch (Exception e) {
            wrapper.asCode("401");
            wrapper.asMessage(e.getMessage());
            e.printStackTrace();
        }
        return wrapper;
    }

    public Specification<Citizen> citizenPrediction(HashMap<String, String> map, List<Booth> booths) {
        String voterId = Objects.isNull(map.get("voterId")) || map.get("voterId").trim().isEmpty() ? null : map.get("voterId");
        String srno = Objects.isNull(map.get("srno")) || map.get("srno").trim().isEmpty() ? null : map.get("srno");
        String firstName = Objects.isNull(map.get("firstName")) || map.get("firstName").trim().isEmpty() ? null : map.get("firstName");
        String familyName = Objects.isNull(map.get("familyName")) || map.get("familyName").trim().isEmpty() ? null : map.get("familyName");
        String mobile = Objects.isNull(map.get("mobile")) || map.get("mobile").trim().isEmpty() ? null : map.get("mobile");

        Specification<Citizen> spec = boothsPrediction(booths);
        if (Objects.nonNull(voterId)) {
            spec = Specifications.where(spec).and(voterIdPredicate(voterId));
        }
        if (Objects.nonNull(srno)) {
            spec = Specifications.where(spec).and(srnoPredicate(srno));
        }
        if (Objects.nonNull(familyName)) {
            spec = Specifications.where(spec).and(familyNamePredicate(familyName));
        }
        if (Objects.nonNull(mobile)) {
            spec = Specifications.where(spec).and(mobilePredicate(mobile));
        }
        if (Objects.nonNull(firstName)) {
            spec = Specifications.where(spec).and(firstNamePredicate(firstName));
        }
        return spec;
    }

    private Specification<Citizen> voterIdPredicate(String voterId) {
        return (Root<Citizen> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            return builder.like(root.get("voterId").as(String.class), builder.literal("%" + voterId + "%"));
        };
    }

    private Specification<Citizen> mobilePredicate(String mobile) {
        return (Root<Citizen> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            return builder.like(root.get("mobile").as(String.class), builder.literal("%" + mobile + "%"));
        };
    }

    private Specification<Citizen> srnoPredicate(String srno) {
        return (Root<Citizen> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            return builder.like(root.get("srno").as(String.class), builder.literal("%" + srno + "%"));
        };
    }

    private Specification<Citizen> firstNamePredicate(String firstName) {
        return (Root<Citizen> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            return builder.like(root.get("firstName").as(String.class), builder.literal("%" + firstName + "%"));
        };
    }

    private Specification<Citizen> familyNamePredicate(String familyName) {
        return (Root<Citizen> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            return builder.like(root.get("familyName").as(String.class), builder.literal("%" + familyName + "%"));
        };
    }

    public Specification<Citizen> boothsPrediction(List<Booth> booths) {
        return (Root<Citizen> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            return root.<Booth>get("booth").in(booths);
        };
    }

    @RequestMapping(value = "/api/citizensData/{id}/{filterBy}/{page}/{size}", method = {RequestMethod.POST})
    @ResponseBody
    public ResponseWrapper gridData(@PathVariable("id") @NotNull final Long id, @PathVariable("filterBy") String filterBy, @PathVariable("page") int page,
            @PathVariable("size") int size, @RequestBody HashMap<String, String> filterMap) {
        List<Booth> booths = filterBy.equalsIgnoreCase("ward") ? boothRepository.findAllByWardId(id) : boothRepository.findAllByAssemblyConstituencyId(id);
        HashMap<String, Object> map = new HashMap<>();
        ResponseWrapper wrapper = new ResponseWrapper();
        List<Citizen> citizens = null;
        if (size > 0) {
            Page pageData = citizenRepository.findAll(citizenPrediction(filterMap, booths), new PageRequest(page - 1, size));
            citizens = pageData.getContent();
            map.put("totalElements", pageData.getTotalElements());
            map.put("totalPages", pageData.getTotalPages());
        } else {
            citizens = citizenRepository.findByBoothIn(booths);
        }
        map.put("citizens", citizens);
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/upload-images", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper uploadImages(@RequestParam("files") MultipartFile[] files) {
        ResponseWrapper wrapper = new ResponseWrapper();
        String path = filePath + File.separator + "Complaint Images" + File.separator;
        createDir(path);
        ArrayList<String> returnFiles = new ArrayList<String>();
        for (MultipartFile file : files) {
            try {
                System.out.println(file.getOriginalFilename());
                String uuid = UUID.randomUUID().toString();
                String originalFileName = file.getOriginalFilename();
                String fileName = uuid + originalFileName.substring(originalFileName.lastIndexOf('.'));
//                File emptyFile = new File(path + fileName);
//                file.transferTo(emptyFile);

                InputStream input = file.getInputStream();
                Path pathObj = Paths.get(path + fileName);//check path
                OutputStream output = Files.newOutputStream(pathObj);
                IOUtils.copy(input, output);

                returnFiles.add(fileName);
            } catch (Exception e) {
            }
        }
        wrapper.asData(returnFiles);
        wrapper.asCode("201");
        wrapper.asMessage("Images Upload Successfully.");
        return wrapper;
    }

    @RequestMapping(value = "/api/upload-image", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper uploadImage(@RequestParam("file") MultipartFile file) {
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            String path = filePath + File.separator + "Complaint Images" + File.separator;
            createDir(path);
            String uuid = UUID.randomUUID().toString();
            String originalFileName = file.getOriginalFilename();
            String fileName = uuid + originalFileName.substring(originalFileName.lastIndexOf('.'));
            File emptyFile = new File(path + fileName);
            file.transferTo(emptyFile);
            wrapper.asData(fileName);
            wrapper.asCode("201");
            wrapper.asMessage("Image Upload Successfully.");
        } catch (Exception e) {
            wrapper.asCode("401");
            wrapper.asMessage(e.getMessage());
        }

        return wrapper;
    }

    @RequestMapping(value = "/api/download-image/{filePath:.+}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FileSystemResource downloadFile(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("filePath") String fileName) {
        String path = filePath + File.separator + "Complaint Images" + File.separator + fileName;
        File file = new File(path);
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/api/send-user-notification/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper sendUserCreationMailToUser(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("id") Long id) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        UserDetail userDetail = userService.findByUserDetailId(id);
        String loginUrl = getURLWithContextPath(request) + "/#/login";
        wrapper.asMessage("Notification send sucessfully.");
        emailservice.sendNewUserNotification(userDetail.getEmail(), loginUrl, userDetail);
        return wrapper;
    }

    public void createDir(String path) {
        File fdir = new File(path);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
    }

    public void deleteDirectory(String path) throws IOException {
        File fdir = new File(path);
        if (fdir.exists()) {
            FileUtils.deleteDirectory(fdir);
        }
    }

    public static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath();
    }

    @RequestMapping(value = "/api/complaintByUser/{userId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaintByUser(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("userId") Long userId) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(complaintRepository.findAllByUserId(userId));
        return wrapper;
    }

    @RequestMapping(value = "/api/complaintByState", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaintByState(HttpServletRequest request, HttpServletResponse response)
            throws MessagingException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ResponseWrapper wrapper = new ResponseWrapper();
        if (user.getType().equals("user")) {
            wrapper.asData(complaintRepository.findAllByUserStateId(user.getId()));
        } else if (user.getType().equals("dept user")) {
            wrapper.asData(complaintRepository.getComplaintByDepartment(user.getId()));
        }
        return wrapper;
    }

    @RequestMapping(value = "/api/userByState", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getUserByState(HttpServletRequest request, HttpServletResponse response)
            throws MessagingException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(userDetailRepository.findAllByUserStateId(user.getId()));
        return wrapper;
    }

    @RequestMapping(value = "/api/getComplaintChart/{year}/{deptId}/{subDeptId}/{wardId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaintChart(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("year") Long year, @PathVariable("deptId") Long deptId,
            @PathVariable("subDeptId") Long subDeptId, @PathVariable("wardId") Long wardId) throws MessagingException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ResponseWrapper wrapper = new ResponseWrapper();
        Long state_id = userDetailRepository.findStateByUserId(user.getId());
        HashMap<String, Object> map = new HashMap<>();
        List<Long> totals = new ArrayList<>();
        List<Long> resolved = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            List<Object[]> resultData = new ArrayList<>();
            //filter data by using ward id
            if (wardId != -1) {
                if (subDeptId != 0) {
                    resultData = complaintRepository.getComplaintChartByMonthAndYearAndDeptSubDeptAndWard(i, year, subDeptId, state_id, wardId);
                } else if (deptId != 0) {
                    resultData = complaintRepository.getComplaintChartByMonthAndYearAndDeptAndWard(i, year, deptId, state_id, wardId);
                } else {
                    resultData = complaintRepository.getComplaintChartByMonthAndYearAndWard(i, year, state_id, wardId);
                }
            } else {
                if (subDeptId != 0) {
                    resultData = complaintRepository.getComplaintChartByMonthAndYearAndDeptSubDept(i, year, subDeptId, state_id);
                } else if (deptId != 0) {
                    resultData = complaintRepository.getComplaintChartByMonthAndYearAndDept(i, year, deptId, state_id);
                } else {
                    resultData = complaintRepository.getComplaintChartByMonthAndYear(i, year, state_id);
                }
            }

            Object[] obj = resultData.get(0);
            totals.add(obj[0] == null ? 0 : Long.parseLong(obj[0] + ""));
            resolved.add(obj[1] == null ? 0 : Long.parseLong(obj[1] + ""));
        }
        map.put("total", totals);
        map.put("resolved", resolved);
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/getComplaintChartByDept/{deptId}/{wardId}/{startDate}/{endDate}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaintChartByDept(@PathVariable("deptId") Long deptId, @PathVariable("wardId") Long wardId, @PathVariable("startDate") Date startDate,
            @PathVariable("endDate") Date endDate) throws MessagingException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ResponseWrapper wrapper = new ResponseWrapper();
        HashMap<String, Object> map = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Long> totals = new ArrayList<>();
        List<Long> resolved = new ArrayList<>();
        if (deptId == -1) {
            List<Object[]> resultData = null;
            if (wardId == -1) {
                resultData = complaintRepository.getComplaintByCreatedBeetween(startDate, endDate, user.getId());
            } else {
                resultData = complaintRepository.getComplaintByCreatedBeetweenByWard(startDate, endDate, wardId);
            }
            Object[] obj = resultData.get(0);
            labels.add("All Departments");
            totals.add(obj[0] == null ? 0 : Long.parseLong(obj[0] + ""));
            resolved.add(obj[1] == null ? 0 : Long.parseLong(obj[1] + ""));
        } else {
            List<SubDepartment> subDepartments = subDepartmentRepository.findAllByDepartmentId(deptId);
            for (int i = 0; i < subDepartments.size(); i++) {
                List<Object[]> resultData = null;
                if (wardId == -1) {
                    resultData = complaintRepository.getComplaintBySubdepartmentAndCreatedBeetween(subDepartments.get(i).getId(), startDate, endDate, user.getId());
                } else {
                    resultData = complaintRepository.getComplaintBySubdepartmentAndCreatedBeetweenByWard(subDepartments.get(i).getId(), startDate, endDate, wardId);
                }
                labels.add(subDepartments.get(i).getName());
                Object[] obj = resultData.get(0);
                totals.add(obj[0] == null ? 0 : Long.parseLong(obj[0] + ""));
                resolved.add(obj[1] == null ? 0 : Long.parseLong(obj[1] + ""));
            }
        }
        map.put("labels", labels);
        map.put("total", totals);
        map.put("resolved", resolved);
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/getComplaintChartByDept/{deptId}/{wardId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaintChartByDept(@PathVariable("deptId") Long deptId, @PathVariable("wardId") Long wardId) throws MessagingException {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        ResponseWrapper wrapper = new ResponseWrapper();
        HashMap<String, Object> map = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Long> totals = new ArrayList<>();
        List<Long> resolved = new ArrayList<>();
        List<SubDepartment> subDepartments = new ArrayList<>();
        if (deptId == -1) {
            Object[] obj = new Object[2];
            if (wardId == -1) {
                obj = complaintRepository.getAllResolvedComplaintsSummary(user.getId()).get(0);
            } else {
                obj = complaintRepository.getAllResolvedComplaintsSummaryByWard(wardId).get(0);
            }
            labels.add("All Departments");
            totals.add(obj[0] == null ? 0 : Long.parseLong(obj[0] + ""));
            resolved.add(obj[1] == null ? 0 : Long.parseLong(obj[1] + ""));
        } else {
            subDepartments = subDepartmentRepository.findAllByDepartmentId(deptId);
            for (int i = 0; i < subDepartments.size(); i++) {
                List<Object[]> resultData = null;
                if (wardId == -1) {
                    resultData = complaintRepository.getComplaintBySubdepartment(subDepartments.get(i).getId(), user.getId());
                } else {
                    resultData = complaintRepository.getComplaintBySubdepartmentByWard(subDepartments.get(i).getId(), wardId);
                }
                labels.add(subDepartments.get(i).getName());
                Object[] obj = resultData.get(0);
                totals.add(obj[0] == null ? 0 : Long.parseLong(obj[0] + ""));
                resolved.add(obj[1] == null ? 0 : Long.parseLong(obj[1] + ""));
            }
        }
        map.put("labels", labels);
        map.put("total", totals);
        map.put("resolved", resolved);
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/getComplaintsAverage/{id}/{filterBy}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaitAverage(@PathVariable("id") Long id, @PathVariable("filterBy") String filterBy) {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        UserDetail userDetail = userDetailRepository.findByUser(user);
        Long stateId = filterBy.equalsIgnoreCase("state") ? id : null;
        Long pcId = filterBy.equalsIgnoreCase("PC") ? id : null;
        Long assemblyId = filterBy.equalsIgnoreCase("assembly") ? id : null;
        Long wardId = filterBy.equalsIgnoreCase("ward") ? id : null;

        HashMap<String, Object> map = new HashMap<>();
        List<Long> wardIds = new ArrayList<>();
        if (pcId != null) {
            List<Long> assemblyConstituencyIds = assemblyConstituencyRepository.findIdsAllByParliamentaryConstituencyId(id);
            wardIds = wardRepository.findWardIdsByAssemblyConstituencyIdsIn(assemblyConstituencyIds);
        } else if (assemblyId != null) {
            List<Long> assemblyConstituencyIds = new ArrayList<>();
            assemblyConstituencyIds.add(id);
            wardIds = wardRepository.findWardIdsByAssemblyConstituencyIdsIn(assemblyConstituencyIds);
        } else if (wardId != null) {
            wardIds.add(id);
        }
        Object[] sqlResultTotalUnresolved = null;
        Object[] sqlResultTotalDayAvg = null;
        if ("user".equals(user.getType())) {
            if (stateId != null) {
                sqlResultTotalUnresolved = complaintRepository.getUnresolvedComplaints(stateId);
                sqlResultTotalDayAvg = complaintRepository.getComplaintAverage(stateId);
            } else if (wardIds.size() > 0) {
                sqlResultTotalUnresolved = complaintRepository.getUnresolvedComplaints(wardIds);
                sqlResultTotalDayAvg = complaintRepository.getComplaintAverage(wardIds);
            }
        } else {
            Long subDepId = userDetail.getSubDepartment().getId();
            if (stateId != null) {
                sqlResultTotalUnresolved = complaintRepository.getUnresolvedComplaintsByDep(stateId, subDepId);
                sqlResultTotalDayAvg = complaintRepository.getComplaintAverageByDep(stateId, subDepId);
            } else if (wardIds.size() > 0) {
                sqlResultTotalUnresolved = complaintRepository.getUnresolvedComplaintsByWardAndDep(wardIds, subDepId);
                sqlResultTotalDayAvg = complaintRepository.getComplaintAverageByWardAndDep(wardIds, subDepId);
            }
        }
        map.put("totalUnresolvedComplaints", sqlResultTotalUnresolved != null && sqlResultTotalUnresolved[0] == null ? 0 : sqlResultTotalUnresolved[0]);
        map.put("totalAverageDays", sqlResultTotalDayAvg != null && sqlResultTotalDayAvg[0] == null ? 0 : sqlResultTotalDayAvg[0]);
        return new ResponseWrapper().asData(map);
    }

    @RequestMapping(value = "/api/saveComplaint", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper assignComplaintToUser(HttpServletRequest request, @RequestBody Complaint complaint) {
        ResponseWrapper wrapper = new ResponseWrapper();
        String[] images = complaint.getImages();
        Citizen citizen = complaint.getCitizen();
        String loginUrl = getURLWithContextPath(request) + "/#/login";
        File file = null;
        if (Objects.nonNull(complaint.getImage())) {
            String path = filePath + File.separator + "Complaint Images" + File.separator + complaint.getImage();
            file = new File(path);
        }
        if (complaint.getId() == null) {
            complaintRepository.save(complaint);
            complaint.generateIncidentId();
            complaint = complaintRepository.save(complaint);
            if (citizen != null) {
                List<UserDetail> users = userService.findAllByWardIdAndSubDepartmentId(citizen.getBooth().getWard().getId(), complaint.getSubDepartment().getId());
                if (users.size() > 0) {
                    complaint.setUser(users.get(0));
                    complaint.setStatus("Assigned");
                } else {
                    complaint.setStatus("Unassigned");
                }
                complaintRepository.save(complaint);
                if (complaint.getUser() != null) {
                    emailservice.sendComplaintUpdatedNotificationEmail(loginUrl, complaint, file);
                }
                if (!complaint.getStatus().equalsIgnoreCase("Ignore")) {
                    SMSService.send(citizen.getMobile(), "New complaint registered " + complaint.getIncidentId()
                            + ". Complaint status is " + complaint.getStatus());
                    PushNotificationService.send(citizen.getDeviceId(), "New complaint registered", "#" + complaint.getIncidentId() + " Status " + complaint.getStatus(), citizen.getDeviceType());
                }
                Notification notification = new Notification();
                notification.setComplaint(complaint);
                notification.setComplaintStatus(complaint.getStatus());
                notification.setNotification("Complaint Registered");
                notificationService.save(notification);
            }

        } else {
            Complaint complaintOld = complaintRepository.findByComplaintId(complaint.getId());
            String oldStatus = complaintOld.getStatus();
            if (complaint.getSubDepartment().getId() != complaintOld.getSubDepartment().getId()) {
                if (citizen != null) {
                    List<UserDetail> users = userService.findAllByWardIdAndSubDepartmentId(citizen.getBooth().getWard().getId(), complaint.getSubDepartment().getId());
                    if (users.size() > 0) {
                        complaint.setUser(users.get(0));
                        complaint.setStatus("Assigned");
                    } else {
                        complaint.setUser(null);
                        complaint.setStatus("Unassigned");
                    }
                    PushNotificationService.send(citizen.getDeviceId(), "Complaint updated", "#" + complaint.getIncidentId() + " status " + complaint.getStatus(), citizen.getDeviceType());
                }
            }
            complaintRepository.save(complaint);
            complaint.generateIncidentId();
            complaint = complaintRepository.save(complaint);
            if (complaint.getUser() != null) {
                emailservice.sendComplaintUpdatedNotificationEmail(loginUrl, complaint, file);
            }
            if (citizen != null) {
                if ((oldStatus != null) && (oldStatus.equals(complaint.getStatus()))) {
                    if (!complaint.getStatus().equalsIgnoreCase("Ignore")) {
                        SMSService.send(citizen.getMobile(), "Complaint " + complaint.getIncidentId() + " updated.");
                        PushNotificationService.send(citizen.getDeviceId(), "Complaint updated", "#" + complaint.getIncidentId() + " status " + complaint.getStatus(), citizen.getDeviceType());
                    }
                    Notification notification = new Notification();
                    notification.setComplaint(complaint);
                    notification.setComplaintStatus(complaint.getStatus());
                    notification.setNotification("Complaint updated");
                    notificationService.save(notification);
                } else {
                    if (!complaint.getStatus().equalsIgnoreCase("Ignore")) {
                        SMSService.send(citizen.getMobile(), "Complaint " + complaint.getIncidentId() + " updated. Complaint status is " + complaint.getStatus());
                        PushNotificationService.send(citizen.getDeviceId(), "Complaint updated", "#" + complaint.getIncidentId() + " status " + complaint.getStatus(), citizen.getDeviceType());
                    }
                    if (Objects.nonNull(complaint.getUser()) && Objects.nonNull(complaint.getUser().getPhone())) {
                        SMSService.send(complaint.getUser().getPhone(), "Complaint " + complaint.getIncidentId() + " updated. Complaint status is " + complaint.getStatus());
                    }
                    Notification notification = new Notification();
                    notification.setComplaint(complaint);
                    notification.setComplaintStatus(complaint.getStatus());
                    notification.setNotification("Complaint " + complaint.getStatus());
                    notificationService.save(notification);
                }
            }
        }

        if (!Objects.isNull(images)) {
            for (String image : images) {
                ComplaintImages complaintImages = new ComplaintImages();
                complaintImages.setImage(image);
                complaintImages.setComplaint(complaint);
                complaintImagesRepository.save(complaintImages);
            }
        }

        wrapper.asData(complaint);
        wrapper.asMessage("Complaint Saved Successfully");
        wrapper.asCode("201");
        return wrapper;
    }

    @RequestMapping(value = "/api/getFilteredData/{className}/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getFilteredData(@PathVariable("className") String className, @PathVariable("id") Long id) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        if (className.equalsIgnoreCase("District")) {
            wrapper.asData(districtRepository.findAllByStateAssemblyId(id));
        } else if (className.equalsIgnoreCase("ParliamentaryConstituency")) {
            wrapper.asData(parliamentaryConstituencyRepository.findAllByDistrictId(id));
        } else if (className.equalsIgnoreCase("AssemblyConstituency")) {
            wrapper.asData(assemblyConstituencyRepository.findAllByParliamentaryConstituencyId(id));
        } else if (className.equalsIgnoreCase("Ward")) {
            wrapper.asData(wardRepository.findAllByAssemblyConstituencyId(id));
        } else if (className.equalsIgnoreCase("Booth")) {
            wrapper.asData(boothRepository.findAllByWardId(id));
        } else if (className.equalsIgnoreCase("NewsFeed")) {
            wrapper.asData(newsFeedRepository.findAllByStateAssemblyIdOrderByCreatedDateDesc(id));
        } else if (className.equalsIgnoreCase("surveyQuestion")) {
            wrapper.asData(surveyQuestionRepository.findAllByWardId(id));
        } else if (className.equalsIgnoreCase("ComplaintImages")) {
            wrapper.asData(complaintImagesRepository.findByComplaintId(id));
        } else {
            wrapper.asData(null);
        }
        return wrapper;
    }

    @RequestMapping(value = "/api/getFilteredSurveyQuestion/{stateId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getFilteredQution(@PathVariable("stateId") Long stateId) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(surveyQuestionRepository.findAllByStateAssemblyId(stateId));
        return wrapper;
    }

    @RequestMapping(value = "/api/getFilteredSubDept/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getSubdepartmentsFromDept(@PathVariable("id") Long id) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(subDepartmentRepository.findAllByDepartmentId(id));
        return wrapper;
    }

    @RequestMapping(value = "/api/getComplaintsHistory/{deptId}/{subDeptId}/{id}/{filterBy}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaintsHistory(@PathVariable("deptId") Long deptId, @PathVariable("subDeptId") Long subDeptId, @PathVariable("id") Long id, @PathVariable("filterBy") @NotNull String filterBy)
            throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        HashMap<String, Object> map = new HashMap<>();

        Long stateId = filterBy.equalsIgnoreCase("state") ? id : null;
        Long pcId = filterBy.equalsIgnoreCase("PC") ? id : null;
        Long assemblyId = filterBy.equalsIgnoreCase("assembly") ? id : null;
        Long wardId = filterBy.equalsIgnoreCase("ward") ? id : null;

        List<Long> wardIds = new ArrayList<>();
        if (pcId != null) {
            List<Long> assemblyConstituencyIds = assemblyConstituencyRepository.findIdsAllByParliamentaryConstituencyId(id);
            wardIds = wardRepository.findWardIdsByAssemblyConstituencyIdsIn(assemblyConstituencyIds);
        } else if (assemblyId != null) {
            List<Long> assemblyConstituencyIds = new ArrayList<>();
            assemblyConstituencyIds.add(id);
            wardIds = wardRepository.findWardIdsByAssemblyConstituencyIdsIn(assemblyConstituencyIds);
        } else if (wardId != null) {
            wardIds.add(id);
        }

        if (stateId != null) {
            if (deptId == -1 && subDeptId == -1) {
                map.put("todaysCreatedComplaints", complaintRepository.findAllTodaysCreatedComplaints(stateId));
                map.put("todaysDueComplaints", complaintRepository.findAllTodaysDueComplaints(stateId));
                map.put("pendingComplaints", complaintRepository.findAllPendingComplaints(stateId));
            } else if (subDeptId == -1) {
                map.put("todaysCreatedComplaints", complaintRepository.findAllTodaysCreatedComplaintsByDept(stateId, deptId));
                map.put("todaysDueComplaints", complaintRepository.findAllTodaysDueComplaintsByDept(stateId, deptId));
                map.put("pendingComplaints", complaintRepository.findAllPendingComplaintsByDept(stateId, deptId));
            } else {
                map.put("todaysCreatedComplaints", complaintRepository.findAllTodaysCreatedComplaintsBySubDept(stateId, subDeptId));
                map.put("todaysDueComplaints", complaintRepository.findAllTodaysDueComplaintsBySubDept(stateId, subDeptId));
                map.put("pendingComplaints", complaintRepository.findAllPendingComplaintsBySubDept(stateId, subDeptId));
            }
        } else {
            if (deptId == -1 && subDeptId == -1) {
                map.put("todaysCreatedComplaints", complaintRepository.findAllTodaysCreatedComplaintsBY(wardIds));
                map.put("todaysDueComplaints", complaintRepository.findAllTodaysDueComplaintsByWard(wardIds));
                map.put("pendingComplaints", complaintRepository.findAllPendingComplaintsByWard(wardIds));
            } else if (subDeptId == -1) {
                map.put("todaysCreatedComplaints", complaintRepository.findAllTodaysCreatedComplaintsByDeptByWard(wardIds, deptId));
                map.put("todaysDueComplaints", complaintRepository.findAllTodaysDueComplaintsByDeptByWard(wardIds, deptId));
                map.put("pendingComplaints", complaintRepository.findAllPendingComplaintsByDeptByWard(wardIds, deptId));
            } else {
                map.put("todaysCreatedComplaints", complaintRepository.findAllTodaysCreatedComplaintsBySubDeptByWard(wardIds, subDeptId));
                map.put("todaysDueComplaints", complaintRepository.findAllTodaysDueComplaintsBySubDeptByWard(wardIds, subDeptId));
                map.put("pendingComplaints", complaintRepository.findAllPendingComplaintsBySubDeptByWard(wardIds, subDeptId));
            }
        }
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/getWard/{page}/{size}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getWard(@PathVariable("page") int page1, @PathVariable("size") int size1) {
        List<Ward> wards = null;
        if (size1 > 0) {
            PageRequest pageRequest = new PageRequest(page1 - 1, size1);
            wards = wardRepository.findAll(pageRequest);
        } else {
            wards = wardRepository.findAll();
        }
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(wards);
        return wrapper;
    }

    @RequestMapping(value = "/api/getBooth/{page}/{size}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getBooth(@PathVariable("page") int page1, @PathVariable("size") int size1) {
        List<Booth> booth = null;
        if (size1 < 0) {
            booth = boothRepository.findAll();
        } else {
            PageRequest pageRequest = new PageRequest(page1 - 1, size1);
            booth = boothRepository.findAll(pageRequest);
        }
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(booth);
        return wrapper;
    }

    @RequestMapping(value = "/api/getAssemblyConstituency/{page}/{size}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getAssemblyConstituency(@PathVariable("page") int page1, @PathVariable("size") int size1) {
        List<AssemblyConstituency> assemblyConstituencies = null;
        if (size1 < 0) {
            assemblyConstituencies = assemblyConstituencyRepository.findAll();
        } else {
            PageRequest pageRequest = new PageRequest(page1 - 1, size1);
            assemblyConstituencies = assemblyConstituencyRepository.findAll(pageRequest);
        }
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(assemblyConstituencies);
        return wrapper;
    }

    @RequestMapping(value = "/api/citizensByVoterId/{voterId}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper citizenVoterId(@PathVariable("voterId") @NotNull final String voterId) {
        Citizen citizen = citizenRepository.findByVoterId(voterId);
        ResponseWrapper wrapper = new ResponseWrapper();
        if (citizen != null) {
            wrapper.asCode("201");
            wrapper.asData(citizen);
        } else {
            wrapper.asCode("404");
            wrapper.asMessage("Entity does not exist");
        }
        return wrapper;
    }

    @RequestMapping(value = "/api/{id}/getVolunteersData", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper volunteerByAssemblyConstituency(HttpServletRequest request, @PathVariable("id") @NotNull final Long id) {
        HashMap<String, Object> map = new HashMap<>();
        ResponseWrapper wrapper = new ResponseWrapper();
        List<Volunteer> volunteers = new ArrayList<>();
        if (request.getParameter("filterDataBy").equalsIgnoreCase("State")) {
            volunteers = volunteerRepository.findByStateIdOrderByStatusDesc(id);
        } else if (request.getParameter("filterDataBy").equalsIgnoreCase("ParliamentaryConstituency")) {
            volunteers = volunteerRepository.findByParliamentaryConstituencyIdOrderByStatusDesc(id);
        } else {
            volunteers = volunteerRepository.findByAssemblyConstituencyIdOrderByStatusDesc(id);
        }
        map.put("Volunteers", volunteers);
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/exportCSV", method = {RequestMethod.GET})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FileSystemResource cvs() {
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        Long state_id = userDetailRepository.findStateByUserId(user.getId());
        List<Complaint> complaints = complaintRepository.findAllByStateAssemblyId(state_id);
        File file = null;
        try {
            file = writeDataAtOnce.writeDataAtOnce1(filePath + "/abc.csv", complaints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/api/getUserLoginDetail/{id}/{filterBy}", method = {RequestMethod.GET})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getLastLoginUser(@PathVariable("id") @NotNull final Long id, @PathVariable("filterBy") @NotNull String filterBy) {
        HashMap<String, Object> map = new HashMap<>();
        Long stateId = filterBy.equalsIgnoreCase("state") ? id : null;
        Long pcId = filterBy.equalsIgnoreCase("PC") ? id : null;
        Long assemblyId = filterBy.equalsIgnoreCase("assembly") ? id : null;
        Long wardId = filterBy.equalsIgnoreCase("ward") ? id : null;
        List<Long> wardIds = new ArrayList<>();
        ResponseWrapper wrapper = new ResponseWrapper();
        if (stateId != null) {
            map.put("TotalActiveUser", userService.getActiveMember(stateId));
            map.put("TotalUser", userService.getAllMember(stateId));
            map.put("TotalActiveVolunteer", volunteerRepository.findActiveCount(stateId));
            map.put("TotalVolunteer", volunteerRepository.findAllCount(stateId));
            map.put("TotalCitizen", citizenRepository.getAllMember(stateId));
            map.put("TotalActiveCitizen", citizenRepository.getActiveMember(stateId));
        } else {
            if (pcId != null) {
                List<Long> assemblyConstituencyIds = assemblyConstituencyRepository.findIdsAllByParliamentaryConstituencyId(id);
                wardIds = wardRepository.findWardIdsByAssemblyConstituencyIdsIn(assemblyConstituencyIds);
                map.put("TotalActiveUser", userDetailRepository.findActiveMemberByPC(pcId));
                map.put("TotalUser", userDetailRepository.findAllMemberByPC(pcId));
            } else if (assemblyId != null) {
                List<Long> assemblyConstituencyIds = new ArrayList<>();
                assemblyConstituencyIds.add(id);
                wardIds = wardRepository.findWardIdsByAssemblyConstituencyIdsIn(assemblyConstituencyIds);
                map.put("TotalActiveUser", userDetailRepository.findActiveMemberByAssembly(assemblyId));
                map.put("TotalUser", userDetailRepository.findAllMemberByAssembly(assemblyId));
            } else if (wardId != null) {
                wardIds.add(id);
                map.put("TotalActiveUser", userService.getActiveMemberByWardIn(wardIds));
                map.put("TotalUser", userService.findAllMemberByWardIn(wardIds));
            }
            if (wardIds.size() > 0) {
                map.put("TotalActiveVolunteer", volunteerRepository.findActiveCountByWardIn(wardIds));
                map.put("TotalVolunteer", volunteerRepository.findAllCountByWardIn(wardIds));
                map.put("TotalCitizen", citizenRepository.getAllMemberByWardIn(wardIds));
                map.put("TotalActiveCitizen", citizenRepository.getActiveMemberByWardIn(wardIds));
            } else {
                map.put("TotalActiveUser", 0);
                map.put("TotalUser", 0);
                map.put("TotalActiveVolunteer", 0);
                map.put("TotalVolunteer", 0);
                map.put("TotalCitizen", 0);
                map.put("TotalActiveCitizen", 0);
            }
        }
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/boothByCitizenCount/{boothId}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper citizenDataByHouse(@PathVariable("boothId") @NotNull final Long boothId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(citizenService.getDashboardData(boothId));
        return wrapper;
    }

    @RequestMapping(value = "/api/number-of-voters-per-house/{id}/{filterBy}/{page}/{size}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper noOfPersonsPerHouse(@PathVariable("id") @NotNull final Long id, @PathVariable("filterBy") @NotNull final String filterBy,
            @PathVariable("page") int page, @PathVariable("size") int size) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(citizenService.getDashboardData(filterBy, id, page, size));
        return wrapper;
    }

    @RequestMapping(value = "/api/number-of-voters-per-house-csv/{id}/{filterBy}", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper noOfPersonsPerHouseCSV(@PathVariable("id") @NotNull final Long id, @PathVariable("filterBy") @NotNull final String filterBy) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(citizenService.getNumberOfVotersPerHouseCSV(filterBy, id));
        return wrapper;
    }

    @RequestMapping(value = "/api/assemblyConstituencyByState/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> assemblyConstituency(@PathVariable("stateId") Long stateId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(assemblyConstituencyRepository.findByState(stateId));
        return wrapper;
    }

    @RequestMapping(value = "/api/parliamentaryConstituencyByState/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> parliamentaryConstituencyByState(@PathVariable("stateId") @NotNull final Long stateId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(parliamentaryConstituencyRepository.findAllByState(stateId));
        return wrapper;
    }

    @RequestMapping(value = "/api/upload-logo", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper uploadLogo(@RequestParam("file") MultipartFile file) {
        ResponseWrapper wrapper = new ResponseWrapper();
        try {
            String staticPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator
                    + "static" + File.separator + "logo.jpg";
            File staticFile = new File(staticPath);
            FileOutputStream fos = new FileOutputStream(staticFile);
            fos.write(file.getBytes());
            fos.close();
            wrapper.asCode("201");
            wrapper.asData(staticFile.getAbsolutePath());
            wrapper.asMessage("Logo Upload Successfully.");
        } catch (Exception e) {
            wrapper.asCode("401");
            wrapper.asMessage(e.getMessage());
        }

        return wrapper;
    }

    @RequestMapping(value = "/api/save-application-settings", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper saveApplicationSettings(@RequestBody ApplicationSettings adminSetttings) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(applicationSettingsRepository.save(adminSetttings));
        return wrapper;
    }

    @RequestMapping(value = "/api/getApplicationSettings", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper getApplicationSettings() {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<ApplicationSettings> list = applicationSettingsRepository.findAll();
        wrapper.asCode("201");
        wrapper.asData(Objects.nonNull(list) && !list.isEmpty() ? list.get(0) : null);
        return wrapper;
    }

    @RequestMapping(value = "/api/volunteer/update", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper updateVolunteer(@RequestBody Volunteer volunteer) {
        ResponseWrapper wrapper = new ResponseWrapper();
        Volunteer volunteerOld = volunteerService.findByMobile(volunteer.getMobile());
        if (volunteerOld != null) {
            if (Objects.equals(volunteerOld.getId(), volunteer.getId())) {
                volunteerService.save(volunteer);
                if ("New".equalsIgnoreCase(volunteerOld.getStatus()) && "Active".equalsIgnoreCase(volunteer.getStatus())) {
                    SMSService.send(volunteer.getMobile(), "Your Smart Neta volunteer account is activated");
                }
            } else {
                wrapper.asCode("133");
                return wrapper;
            }
        } else {
            volunteerService.save(volunteer);
        }
        wrapper.asCode("201");
        return wrapper;
    }

    @RequestMapping(value = "/api/partyByState/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> partyByState(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        Optional<StateAssembly> stateAssembly = stateAssemblyRepository.findById(stateId);
        wrapper.asData(partyRepository.findAllByStateAssembly(stateAssembly.get()));
        return wrapper;
    }

    @RequestMapping(value = "/api/getPieChartCitizenMobile", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, Number> getPieChartFirst(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, String> filterData) {
        Map<String, Number> map = new HashMap<>();
        BigInteger withMobile = BigInteger.ZERO;
        BigInteger withoutMobile = BigInteger.ZERO;
        if (!filterData.get("state").isEmpty()) {
            withMobile = citizenService.getCitizenCountWithMobile(filterData);
            withoutMobile = citizenService.getCitizenCountWithoutMobile(filterData);
        }
        map.put("code", 201);
        map.put("withMobile", withMobile);
        map.put("withoutMobile", withoutMobile);
        return map;
    }

    @RequestMapping(value = "/api/getPieChartCitizenSegmentation", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getPieChartFirstBySegmentation(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, String> filterData) {
        Map<String, Object> map = new HashMap<>();
        Long stateId = filterData.get("stateId") == null ? null : Long.parseLong(filterData.get("stateId"));
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (Objects.isNull(stateId)) {
            String arr[] = {"A+", "A", "B", "C"};
            for (String action : arr) {
                Map<String, Object> mapSegmentation = new HashMap<>();
                mapSegmentation.put("label", action + "");
                mapSegmentation.put("segmentation", action + "");
                resultList.add(mapSegmentation);
            }
            map.put("segmentations", resultList);
        } else {
            map.put("segmentations", volunteerActionService.getSegmentationLabelsByState(stateId));
        }
        map.put("code", 201);
        map.put("AP", Objects.isNull(stateId) ? 0 : citizenService.getCitizenCountSegmentetionWise(filterData, "A+", "a+"));
        map.put("A", Objects.isNull(stateId) ? 0 : citizenService.getCitizenCountSegmentetionWise(filterData, "A", "a"));
        map.put("B", Objects.isNull(stateId) ? 0 : citizenService.getCitizenCountSegmentetionWise(filterData, "B", "b"));
        map.put("C", Objects.isNull(stateId) ? 0 : citizenService.getCitizenCountSegmentetionWise(filterData, "C", "c"));

        return map;
    }

    @RequestMapping(value = "/api/getPieChartCitizenMaleFemale", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Number> getPieChartFirstMaleFemale(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, String> filterData) {
        Map<String, Number> map = new HashMap<>();
        Number male = 0;
        Number female = 0;
        if (!filterData.get("state").isEmpty()) {
            male = citizenService.getCitizenCountMale(filterData);
            female = citizenService.getCitizenCountFemale(filterData);
        }
        map.put("code", 201);
        map.put("male", male);
        map.put("female", female);
        return map;
    }

    @RequestMapping(value = "/api/getPieChartPartyPreference", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> getPieChartFirstPsrtyPreference(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, String> filterData) {
        StateAssembly stateAssembly = stateAssemblyRepository.findByState(filterData.get("state"));
        List<Party> parties = new ArrayList<>();
        if (filterData.get("assembly") != null) {
            Long aid = Long.parseLong(filterData.get("assmblyId"));
            Long wid = null;
            if (filterData.get("ward") != null) {
                wid = Long.parseLong(filterData.get("wardId"));
            }
            AssemblyConstituency assemblyConstituency = assemblyConstituencyRepository.findById(aid).get();
            Long did = assemblyConstituency.getParliamentaryConstituency().getDistrict().getId();
            parties = partyRepository.partyPreference(stateAssembly.getId(), did, aid, wid);
        } else if (stateAssembly != null) {
            parties = partyRepository.partyPreference(stateAssembly.getId(), null, null, null);
        }

        Map<String, Object> map = new HashMap<>();
        List<Long> citizen = new ArrayList<>();
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < parties.size(); i++) {
            Party party = parties.get(i);
            Number number = citizenService.getCitizenPartyPref(filterData, party.getCode());
            String code = party.getCode();
            parts.add(code);
            citizen.add(number.longValue());
        }
        map.put("citizen", citizen);
        map.put("parties", parts);
        return map;
    }

    @RequestMapping(value = "/api/getScatterChartByState", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> getScatterChartByState(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, String> filterData) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (!filterData.get("state").isEmpty()) {
            filterData.put("ageGroup", "");
            String qs = "SELECT ";
            for (int i = 15; i < 100; i = i + 5) {
                qs = qs + " SUM(IF(C.age BETWEEN " + i + " and " + (i + 4) + ",1,0)), ";
            }
            qs = qs + " SUM(IF(age > 100 ,1,0)) FROM citizen C where C.age is not null  ";
            Object[] result = citizenService.getCitizenAge(filterData, qs);
            if (Objects.nonNull(result)) {
                int mid = 12;
                for (int i = 0; i < 18; i++) {
                    Map<String, Object> map1 = new HashMap<>();
                    mid += 5;
                    map1.put("x", mid);
                    map1.put("y", result[i]);
                    map1.put("r", 5);
                    list.add(map1);
                }
            }
        }
        return list;

//        List<Map<String, String>> list = new ArrayList<>();
//        if (!filterData.get("state").isEmpty()) {
//            filterData.put("ageGroup", "");
//            StateAssembly stateAssembly = stateAssemblyRepository.findByState(filterData.get("state"));
//            Number res = Objects.isNull(stateAssembly) ? null : citizenRepository.getMaxAgeByState(stateAssembly.getState());
//            int maxAge;
//            if (res == null) {
//                maxAge = 100;
//            } else {
//                maxAge = res.intValue();
//            }
//            for (int i = 15; i < maxAge; i = i + 5) {
//                Map<String, String> map1 = new HashMap<>();
//                map1.put("y", "" + citizenService.getCitizenAge(filterData, i, i + 5));
//                if (i >= maxAge) {
//                    map1.put("x", "" + maxAge);
//                    map1.put("r", "" + 0);
//                } else {
//                    if (i == 15) {
//                        map1.put("x", "" + (((i + (i + 5)) / 2) + 1));
//                    }
//                    if (i == maxAge) {
//                        map1.put("x", "" + maxAge);
//                    } else {
//                        map1.put("x", "" + ((i + (i + 5)) / 2));
//                    }
//                    map1.put("r", "" + 5);
//                }
//                list.add(map1);
//            }
//        }
//        return list;
    }

    @RequestMapping(value = "/api/getPieChartVolunteerBystate/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Number> getPieChartVolunteer(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        Map<String, Number> map = new HashMap<>();
        map.put("code", 201);
        map.put("ActiveVolunteer", volunteerRepository.findActiveCountOnChartByState(stateId));
        map.put("InactiveVolunteer", volunteerRepository.findInactiveCountOnChartByState(stateId));
        return map;
    }

    @RequestMapping(value = "/api/getPieChartVolunteerByAssembky/{assembly}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Number> getPieChartVolunteerByWard(HttpServletRequest request, HttpServletResponse response, @PathVariable("assembly") Long assembly) {
        Map<String, Number> map = new HashMap<>();
        map.put("code", 201);
        map.put("ActiveVolunteer", volunteerRepository.findActiveCountByAssemblyOnPieChart(assembly));
        map.put("InactiveVolunteer", volunteerRepository.findInactiveCountByAssemblyOnPieChart(assembly));
        return map;
    }

    @RequestMapping(value = "/api/getQuestionSurveyChart/{id}/{filterBy}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseWrapper getQuestionSurvey(@PathVariable("id") Long id, @PathVariable("filterBy") String filterBy) {
        ResponseWrapper wrapper = new ResponseWrapper();

        List<String> totalQuestions = new ArrayList<>();
        List<SurveyQuestion> questions = new ArrayList<>();
        List<Long> respondedCitizensCount = new ArrayList<>();

        Long stateId = filterBy.equalsIgnoreCase("state") ? id : null;
        Long pcId = filterBy.equalsIgnoreCase("PC") ? id : null;
        Long assemblyId = filterBy.equalsIgnoreCase("assembly") ? id : null;
        Long wardId = filterBy.equalsIgnoreCase("ward") ? id : null;
        Long boothId = filterBy.equalsIgnoreCase("booth") ? id : null;

        if (Objects.nonNull(stateId)) {
            questions = surveyQuestionRepository.findAllQuestionByStateAssemblyId(stateId);
            questions.forEach((action) -> {
                totalQuestions.add(action.getQuestion());
                respondedCitizensCount.add(surveyRepository.findAllIdsByStateId(stateId, action.getId()));
            });
        }

        if (Objects.nonNull(pcId)) {
            List<Long> ids = assemblyConstituencyRepository.findIdsAllByParliamentaryConstituencyId(pcId);
            questions = surveyQuestionRepository.findAllByAssemblyConstituencyIdIn(ids);
            questions.forEach((action) -> {
                totalQuestions.add(action.getQuestion());
                respondedCitizensCount.add(surveyRepository.findAllIdsByAssemblyConstituencyIdIn(ids, action.getId()));
            });
        }

        if (Objects.nonNull(assemblyId)) {
            questions = surveyQuestionRepository.findAllByAssemblyConstituencyId(assemblyId);
            questions.forEach((action) -> {
                totalQuestions.add(action.getQuestion());
                respondedCitizensCount.add(surveyRepository.findAllIdsByAssemblyConstituencyId(assemblyId, action.getId()));
            });
        }

        if (Objects.nonNull(wardId)) {
            questions = surveyQuestionRepository.findAllByWardId(wardId);
            questions.forEach((action) -> {
                totalQuestions.add(action.getQuestion());
                respondedCitizensCount.add(surveyRepository.findAllIdsByWardId(wardId, action.getId()));
            });
        }

        if (Objects.nonNull(boothId)) {
            questions = surveyQuestionRepository.findAllByBoothId(boothId);
            questions.forEach((action) -> {
                totalQuestions.add(action.getQuestion());
                respondedCitizensCount.add(surveyRepository.findAllIdsByBoothId(boothId, action.getId()));
            });
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("totalQuestions", totalQuestions);
        map.put("citizenCount", respondedCitizensCount);
        wrapper.asCode("201");
        wrapper.asData(map);
        return wrapper;
    }

    @RequestMapping(value = "/api/getStateReport/{stateId}", method = {RequestMethod.GET})
    public Map<String, Object> getStateReport(@PathVariable("stateId") Long stateId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        return null; // reportService.getStateReport(stateId);
    }

    @RequestMapping(value = "/api/getParlimentoryReport/{parlimentoryId}", method = {RequestMethod.GET})
    public Map<String, Object> getParlimentoryReport(@PathVariable("parlimentoryId") Long parlimentoryId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        return reportService.getParlimentoryReport(parlimentoryId);
    }

    @RequestMapping(value = "/api/getAssemblyReport/{assemblyId}", method = {RequestMethod.GET})
    public Map<String, Object> getAssemblyReport(@PathVariable("assemblyId") Long assemblyId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        return reportService.getAssemblyReport(assemblyId);
    }

    @RequestMapping(value = "/api/getWardReport/{wardId}", method = {RequestMethod.GET})
    public Map<String, Object> getWardReport(@PathVariable("wardId") Long wardId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        return reportService.getWardReport(wardId);
    }

    @RequestMapping(value = "/api/getBoothReport/{boothId}", method = {RequestMethod.GET})
    public Map<String, Object> generateBoothReport(@PathVariable("boothId") Long boothId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        return reportService.generateBoothReport(boothId);
    }

    @RequestMapping(value = "/api/getComplaintsSummaryReport/{wardId}", method = {RequestMethod.GET})
    public Map<String, Object> getComplaintsSummaryReport(@PathVariable("wardId") Long wardId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        return reportService.generateComplaintsSummaryReport(wardId);
    }

    @RequestMapping(value = "/api/getVotersMobileReport/{id}", method = {RequestMethod.GET})
    public Map<String, Object> getVotersMobileReport(HttpServletRequest request, @PathVariable("id") Long id) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        if (Objects.nonNull(request.getParameter("boothId"))) {
            return reportService.generateVotersMobileReport("Booth", id);
        }
        if (Objects.nonNull(request.getParameter("wardId"))) {
            return reportService.generateVotersMobileReport("Ward", id);
        }
        if (Objects.nonNull(request.getParameter("assemblyId"))) {
            return reportService.generateVotersMobileReport("Assembly", id);
        }
        if (Objects.nonNull(request.getParameter("parlimentoryId"))) {
            return reportService.generateVotersMobileReport("Parlimentory", id);
        }
        return reportService.generateVotersMobileReport("State", id);
    }

    @RequestMapping(value = "/api/getClientWiseReport/{id}", method = {RequestMethod.GET})
    public Map<String, Object> getClientWiseReport(@PathVariable("id") Long wardId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        return reportService.getClientWiseReport();
    }

    @RequestMapping(value = "/api/sendMail-chart", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper sendMailChart(@RequestBody HashMap<String, String> fileData) {
        ResponseWrapper wrapper = new ResponseWrapper();
        String file[] = new String[7];
        for (int i = 0; i < 7; i++) {
            file[i] = fileData.get("image" + i);
        }
        String emailId = fileData.get("emailId");
        try {
            byte[][] imgBytes = new byte[7][];
            for (int i = 0; i < file.length; i++) {
                imgBytes[i] = new sun.misc.BASE64Decoder().decodeBuffer(file[i]);
            }
            File imgOutFile[] = new File[7];
            String FileName[] = {"Mobile-Vs-withoutMobile.png", "Segmentation.png", "Active Volunteer Vs Inactive Volunteer.png", "Party Preference.png", "Citizen Male Vs Female.png", "Age Of Citizen.png", "Location Of Voters On GIS Map.png"};
            FileOutputStream fos = null;
            for (int i = 0; i < imgBytes.length; i++) {
                imgOutFile[i] = new File(FileName[i]);
                fos = new FileOutputStream(imgOutFile[i]);
                fos.write((imgBytes[i]));
                fos.flush();
            }
            EmailService.sendMailChartFile(emailId, imgOutFile);
        } catch (Exception e) {
            wrapper.asCode("401");
            wrapper.asMessage(e.getMessage());
        }
        wrapper.asCode("201");
        return wrapper;
    }

    @RequestMapping(value = "/api/getCitizenCSVFilesByState/{state}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseWrapper getCitizenCSVFiles(@PathVariable("state") String state) {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<CSVFileInfo> files = csvFileInfoRepository.findAllByStateOrderByStatusDesc(state);
        wrapper.asData(files);
        wrapper.asCode("201");
        return wrapper;
    }

    @RequestMapping(value = "/api/download-log-file/{fileId}", method = RequestMethod.GET)
    @ResponseBody
    public FileSystemResource downloadFile(@PathVariable("fileId") Long fileId) {
        String errorLogFilePath = csvFileInfoRepository.findById(fileId).get().getPath().replace(".csv", "-errorLog.txt");
        return new FileSystemResource(new File(errorLogFilePath));
    }

    @RequestMapping(value = "/api/sendVolunteerNotification", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Map<String, Number> sendVolunteerNotification(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, String> data) {
        String header = data.get("header");
        String body = data.get("body");
        String webLink = data.get("webLink");
        String parliamentaryConstituencyId = data.get("parliamentaryConstituencyId");
        String assemblyConstituencyId = data.get("assemblyConstituencyId");
        List<Object[]> deviceIds = new ArrayList<>();
        VolunteerNotification volunteerNotification = new VolunteerNotification();
        volunteerNotification.setBody(body);
        volunteerNotification.setHeader(header);
        volunteerNotification.setWebLink(webLink);
        volunteerNotification.setParliamentaryConstituencyId(Long.parseLong(parliamentaryConstituencyId));
        if (assemblyConstituencyId == null || assemblyConstituencyId.isEmpty()) {
            deviceIds = volunteerRepository.findDeviceIdByParliamentaryConstituencyId(parliamentaryConstituencyId);
        } else {
            volunteerNotification.setAssemblyConstituencyId(Long.parseLong(assemblyConstituencyId));
            deviceIds = volunteerRepository.findDeviceIdByAssemblyConstituencyId(assemblyConstituencyId);
        }
        volunteerNotificationRepository.save(volunteerNotification);
        for (Object[] deviceId : deviceIds) {
            PushNotificationService.send(Objects.nonNull(deviceId[0]) ? deviceId[0].toString() : null, header, body, Objects.nonNull(deviceId[1]) ? deviceId[1].toString() : null);
        }
        Map<String, Number> map = new HashMap<>();
        return map;
    }

    @RequestMapping(value = "/api/volunteerActions/{stateId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<VolunteerAction> volunteerActions(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        return volunteerActionService.getActions(stateId);
    }

    @RequestMapping(value = "/api/getSegmentations/{stateId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Segmentations> getSegmentations(@PathVariable("stateId") Long stateId) {
        return volunteerActionService.getSegmentations(stateId);
    }

    @RequestMapping(value = "/api/getOverviewByState/{stateId}", method = {RequestMethod.GET})
    public Map<String, Object> getOverviewByParlimentory(@PathVariable("stateId") Long stateId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("reportFor", "State# " + stateAssemblyRepository.findById(stateId).get().getState());
        reportData.put("stateId", stateId);
        reportData.put("reportName", "State Report");
        reportData.put("tableFirstColumnName", "Parliamentary Constituency#");
        reportData.put("stateId", stateId);
        return reportService.getOverviewReportBy(stateId, reportData);
    }

    @RequestMapping(value = "/api/getOverviewByParlimentory/{parlimentoryId}", method = {RequestMethod.GET})
    public Map<String, Object> getOverviewByAssembly(@PathVariable("parlimentoryId") Long parlimentoryId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        Map<String, Object> reportData = new HashMap<>();
        String sqlQueryForBoothIds = "where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in (select a.id from assembly_constituency a where a.parliamentary_constituency_id = " + parlimentoryId + " ))) group by c.assembly_no order by c.assembly_no + 0 asc";
        ParliamentaryConstituency pc = parliamentaryConstituencyRepository.findById(parlimentoryId).get();
        reportData.put("stateId", pc.getDistrict().getStateAssembly().getId());
        reportData.put("reportFor", "Parliamentary Constituency# " + pc.getName());
        reportData.put("reportName", "Parliamentary Constituency Report");
        reportData.put("tableFirstColumnName", "Assembly Constituency#");
        reportData.put("sqlQueryForBoothIds", sqlQueryForBoothIds);
        return reportService.getOverviewReportBy(parlimentoryId, reportData);
    }

    @RequestMapping(value = "/api/getOverviewByAssembly/{assemblyId}", method = {RequestMethod.GET})
    public Map<String, Object> getOverviewByWard(@PathVariable("assemblyId") Long assemblyId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        String sqlQueryForBoothIds = "where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id  = " + assemblyId + " )) group by c.ward_no order by c.ward_no + 0 asc";
        Map<String, Object> reportData = new HashMap<>();
        AssemblyConstituency ac = assemblyConstituencyRepository.findById(assemblyId).get();
        reportData.put("stateId", ac.getParliamentaryConstituency().getDistrict().getStateAssembly().getId());
        reportData.put("reportFor", "Assembly Constituency# " + ac.getNo());
        reportData.put("reportName", "Assembly Constituency Report");
        reportData.put("tableFirstColumnName", "Ward#");
        reportData.put("sqlQueryForBoothIds", sqlQueryForBoothIds);
        return reportService.getOverviewReportBy(assemblyId, reportData);
    }

    @RequestMapping(value = "/api/getOverviewByWard/{wardId}", method = {RequestMethod.GET})
    public Map<String, Object> getOverviewByBooth(@PathVariable("wardId") Long wardId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        Map<String, Object> reportData = new HashMap<>();
        String sqlQueryForBoothIds = "where c.booth_id in (select b.id from booth b where b.ward_id = " + wardId + " ) group by c.booth_no order by c.booth_no + 0 asc";
        Ward ward = wardRepository.findById(wardId).get();
        reportData.put("stateId", ward.getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getStateAssembly().getId());
        reportData.put("reportFor", "Ward# " + ward.getNo());
        reportData.put("reportName", "Ward Report");
        reportData.put("tableFirstColumnName", "Booth#");
        reportData.put("sqlQueryForBoothIds", sqlQueryForBoothIds);
        return reportService.getOverviewReportBy(wardId, reportData);
    }

    @RequestMapping(value = "/api/getTemplate/{templateType}", method = RequestMethod.GET)
    public FileSystemResource getTemplate(@PathVariable("templateType") String templateType) throws FileNotFoundException {
        String staticPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "templates" + File.separator + "reports" + File.separator;
        if (templateType.equalsIgnoreCase("Voter_Upload_Template")) {
            File file = new File(staticPath + "voter_upload_template.csv");
            return new FileSystemResource(file);
        } else if (templateType.equalsIgnoreCase("Hierarchy_Upload_Template")) {
            File file = new File(staticPath + "Samparka_Hierarchy_upload_template.xlsx");
            return new FileSystemResource(file);
        } else if (templateType.equalsIgnoreCase("Previous_Election_Data_Template")) {
            File file = new File(staticPath + "Previous_Election_Data_Template.csv");
            return new FileSystemResource(file);
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/api/getVotersGoogleMapData", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, Object>> getVotersGoogleMapData(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, String> filterData) {
        return citizenService.getVotersGoogleMapData(filterData);
    }

    @RequestMapping(value = "/api/approveVolunteer", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper approveVolunteer(HttpServletRequest request, HttpServletResponse response, @RequestBody Volunteer volunteer) {
        ResponseWrapper wrapper = new ResponseWrapper();
        volunteer.setStatus("Active");
        SMSService.send(volunteer.getMobile(), "Your Smart Neta volunteer account is activated");
        if (Objects.nonNull(volunteer.getDeviceId())) {
            PushNotificationService.send(volunteer.getDeviceId(), "Volunteer Account Activated", "Your Smart Neta volunteer account is activated", volunteer.getDeviceType());
        }
        volunteerService.save(volunteer);
        return wrapper;
    }

    @RequestMapping(value = "/api/getVotersCSV/{id}/{filterBy}", method = RequestMethod.GET)
    public void getVotersCSV(@PathVariable("id") Long id, @PathVariable("filterBy") String filterBy) {
        File file = new File(filePath + File.separator + "citizens.csv");
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            List<Object[]> data = citizenService.getVotersCSV(id, filterBy);
            writer.writeNext(new String[]{"Assembly Constituency number", "Ward No", "Booth Number", "SL_No", "Pin code", "Address", "Voter ID", "First name", "Family name", "Age", "Gender", "Mobile Number", "Segmentation", "Party Preference", "Voted", "Status", "Latitude", "Longitude", "Volunteer Mobile", "Voter Slip Printed", "Voter Details Slip SMS Sent On Mobile Number"});
            for (int i = 0; i < data.size(); i++) {
                Object[] obj = data.get(i);
                writer.writeNext(new String[]{"" + obj[0], obj[1] + "", obj[2] + "", obj[3] + "", obj[4] + "", obj[5] + "", obj[6] + "", obj[7] + "", obj[8] + "", obj[9] + "", obj[10] + "", (Objects.nonNull(obj[11]) && !(obj[11] + "").equalsIgnoreCase("null") ? obj[11] + "" : ""), (Objects.nonNull(obj[12]) && !(obj[12] + "").equalsIgnoreCase("null") ? obj[12] + "" : ""), (Objects.nonNull(obj[13]) && !(obj[13] + "").equalsIgnoreCase("null") ? obj[13] + "" : ""), (Objects.nonNull(obj[14]) && (boolean) obj[14] ? "Yes" : "No"), obj[15] + "", (Objects.nonNull(obj[16]) && !(obj[16] + "").equalsIgnoreCase("-1") ? obj[16] + "" : ""), (Objects.nonNull(obj[17]) && !(obj[17] + "").equalsIgnoreCase("-1") ? obj[17] + "" : ""), (Objects.nonNull(obj[18]) && !(obj[18] + "").equalsIgnoreCase("null") ? obj[18] + "" : ""), (Objects.nonNull(obj[19]) && (Boolean) obj[19] ? "Yes" : "No"), (Objects.nonNull(obj[20]) && !(obj[20] + "").equalsIgnoreCase("null") ? obj[20] + "" : "")});
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/api/getPreviousUploadsByWard/{wardId}", method = RequestMethod.GET)
    @ResponseBody
    public List<PreviousElectionUploads> getPreviousUploadsByWard(@PathVariable("wardId") Long wardId) throws IOException {
        return previousElectionUploadsRepository.findAllByWardId(wardId);
    }

    @RequestMapping(value = "/api/getPreviousUploadsByAssembly/{assemblyId}", method = RequestMethod.GET)
    @ResponseBody
    public List<PreviousElectionUploads> getPreviousUploadsByAssembly(@PathVariable("assemblyId") Long assemblyId) throws IOException {
        return previousElectionUploadsRepository.findAllByAssemblyConstituencyId(assemblyId);
    }

    @RequestMapping(value = "/api/uploadPreviousElectionData/{id}/{uploadBasedOn}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseWrapper uploadPreviousElectionData(@RequestParam("file") MultipartFile csvFile, @PathVariable("id") Long id, @PathVariable String uploadBasedOn) throws IOException {
        ResponseWrapper wrapper = new ResponseWrapper();
        Ward ward = uploadBasedOn.equalsIgnoreCase("Ward") ? wardRepository.findById(id).get() : null;
        AssemblyConstituency assemblyConstituency = uploadBasedOn.equalsIgnoreCase("AssemblyConstituency") ? assemblyConstituencyRepository.findById(id).get() : null;
        String path = filePath + File.separator + "Previous Election Files" + File.separator;
        createDir(path);
        File readableFile = new File(path + csvFile.getOriginalFilename());
        csvFile.transferTo(readableFile);
        PreviousElectionUploads previousElectionUploads = new PreviousElectionUploads();
        Reader reader = Files.newBufferedReader(readableFile.toPath());
        CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(0).build();
        previousElectionUploads.setFileName(csvFile.getOriginalFilename());
        previousElectionUploads.setFilePath(readableFile.getAbsolutePath());
        previousElectionUploads.setWard(ward);
        previousElectionUploads.setAssemblyConstituency(assemblyConstituency);
        List<String[]> allData = csvReader.readAll();
        Map<Integer, String> partyCodes = new HashMap<>();
        //reading all Parties
        List<Party> parties = new ArrayList<>();
        for (int i = 4; i < allData.get(0).length; i++) {
            String partyCode = (allData.get(0)[i]).trim();
            partyCodes.put(i, partyCode);
            Party partyByWard = null;
            if (ward != null) {
                partyByWard = partyRepository.findByCodeAndWardId(partyCode, ward.getId());
            } else if (assemblyConstituency != null) {
                partyByWard = partyRepository.findByCodeAndAssemblyId(partyCode, id);
            }
            Party partyByAssembly = null;;
            Party partyByState = null;
            if (partyByWard == null && ward != null) {
                partyByAssembly = partyRepository.findByCodeAndAssemblyId(partyCode, ward.getAssemblyConstituency().getId());
            } else if (partyByWard != null) {
                parties.add(partyByWard);
            }
            if (partyByWard == null && partyByAssembly == null) {
                if (ward != null) {
                    partyByState = partyRepository.findByCodeAndStateId(partyCode, ward.getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getStateAssembly().getId());
                } else if (assemblyConstituency != null) {
                    partyByState = partyRepository.findByCodeAndStateId(partyCode, assemblyConstituency.getParliamentaryConstituency().getDistrict().getStateAssembly().getId());
                }
            }
            if (partyByAssembly != null) {
                parties.add(partyByAssembly);
            } else if (partyByState != null) {
                parties.add(partyByState);
            }
        }

        for (int i = 1; i < allData.size(); i++) {
            String[] row = allData.get(i);
            partyCodes.forEach((k, v) -> {
                List<String> parties2 = parties.stream().map(mapper -> mapper.getCode()).collect(Collectors.toList());
                if (parties2.contains(v)) {
                    PreviousElection previousElection = new PreviousElection();
                    previousElection.setYear(Long.valueOf(row[0]));
                    previousElection.setAssemblyNumber(row[1] + "");
                    previousElection.setWardNumber(row[2] + "");
                    previousElection.setBoothNumber(row[3] + "");
                    previousElection.setTotalVoters(Long.valueOf(row[4]));
                    previousElection.setTotalPolled(Long.valueOf(row[5]));
                    Party partyEntity = parties.stream().filter(predicate -> predicate.getCode().equalsIgnoreCase(v)).findAny().get();
                    previousElection.setParty(partyEntity);
                    if (ward != null) {
                        previousElection.setParliamentaryConstituency(ward.getAssemblyConstituency().getParliamentaryConstituency());
                    } else if (assemblyConstituency != null) {
                        previousElection.setParliamentaryConstituency(assemblyConstituency.getParliamentaryConstituency());
                    }
                    previousElection.setTotalPartyVoted(Long.valueOf(row[k]));
                    PreviousElection previousElectionEntity = previousElectionRepository.findByBoothNumberAndWardNumberAndAssemblyNumberAndPartyAndYearAndParliamentaryConstituency(previousElection.getBoothNumber(), previousElection.getWardNumber(), previousElection.getAssemblyNumber(), previousElection.getParty(), previousElection.getYear(), previousElection.getParliamentaryConstituency());
                    if (Objects.nonNull(previousElectionEntity)) {
                        previousElection.setId(previousElectionEntity.getId());
                    }
                    previousElectionRepository.save(previousElection);
                }
            });
        }
        previousElectionUploadsRepository.save(previousElectionUploads);
        wrapper.asCode("201");
        wrapper.asData(uploadBasedOn.equalsIgnoreCase("Ward") ? previousElectionUploadsRepository.findAllByWardId(id) : previousElectionUploadsRepository.findAllByAssemblyConstituencyId(id));
        wrapper.asMessage("Previous Election Data Upload Successfully");
        return wrapper;
    }

    @RequestMapping(value = "/api/download-previous-election-file/{filePath:.+}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FileSystemResource downloadPreviousElectionFile(@PathVariable("filePath") String fileName) {
        String path = filePath + File.separator + "Previous Election Files" + File.separator + fileName;
        File file = new File(path);
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/api/getSurveyQuestionsCSV/{filterdOn}/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Object> getSurveyQuestionsCSV(HttpServletRequest request, @PathVariable("filterdOn") String filterdOn, @PathVariable("id") Long id) {
        String firstQuery = "select c.state, pc.name, c.assembly_no, c.ward_no, c.booth_no, v.mobile, c.voter_id, c.latitude, c.longitude ";
        String secondQuery = "";
        String thirdQuery = " from citizen c inner join booth b on c.booth_id = b.id inner join ward w on w.id = b.ward_id inner join assembly_constituency ac on ac.id = w.assembly_constituency_id inner join parliamentary_constituency pc on pc.id = ac.parliamentary_constituency_id inner join district d on d.id = pc.district_id inner join survey s on s.citizen_id = c.id inner join volunteer v on v.id = s.volunteer_id ";
        String fourthQuery = "";
        String filterQuery = "";
        String pc = "-";
        String ac = "-";
        String wrd = "-";
        String bth = "-";

        List<SurveyQuestion> sarveyQuestions = new ArrayList<>();
        if (filterdOn.equals("state")) {
            fourthQuery = " where d.state_assembly_id = " + id + " group by c.voter_id ";
            sarveyQuestions = surveyQuestionRepository.findAllByStateAssemblyId(id);
        } else if (filterdOn.equals("PC")) {
            pc = parliamentaryConstituencyRepository.findById(id).get().getName();
            List<Long> ids = assemblyConstituencyRepository.findIdsAllByParliamentaryConstituencyId(id);
            fourthQuery = " where ac.id in (select acs.id from assembly_constituency acs where acs.parliamentary_constituency_id = " + id + " ) group by c.voter_id ";
            sarveyQuestions = surveyQuestionRepository.findAllByAssemblyConstituencyIdIn(ids);
            filterQuery = " and c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in (select a.id from assembly_constituency a where a.parliamentary_constituency_id = " + id + " ))) ";
        } else if (filterdOn.equals("assembly")) {
            AssemblyConstituency acEntity = assemblyConstituencyRepository.findById(id).get();
            ac = acEntity.getNo();
            pc = acEntity.getParliamentaryConstituency().getName();
            fourthQuery = " where ac.id = " + id + " group by c.voter_id ";
            sarveyQuestions = surveyQuestionRepository.findAllByAssemblyConstituencyId(id);
            filterQuery = " and c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id  = " + id + " )) ";
        } else if (filterdOn.equals("ward")) {
            Ward ward = wardRepository.findById(id).get();
            wrd = ward.getNo();
            ac = ward.getAssemblyConstituency().getNo();
            pc = ward.getAssemblyConstituency().getParliamentaryConstituency().getName();
            fourthQuery = " where w.id = " + id + " group by c.voter_id ";
            sarveyQuestions = surveyQuestionRepository.findAllByWardId(id);
            filterQuery = " and c.booth_id in (select b.id from booth b where b.ward_id = " + id + " ) ";
        } else if (filterdOn.equals("booth")) {
            Booth booth = boothRepository.findById(id).get();
            bth = booth.getNo();
            wrd = booth.getWard().getNo();
            ac = booth.getWard().getAssemblyConstituency().getNo();
            pc = booth.getWard().getAssemblyConstituency().getParliamentaryConstituency().getName();
            fourthQuery = " where b.id = " + id + " group by c.voter_id ";
            sarveyQuestions = surveyQuestionRepository.findAllByBoothId(id);
            filterQuery = " and c.booth_id = " + id;
        }
        List<Object> result = new ArrayList<>();
        if (request.getParameter("dowloadType").equalsIgnoreCase("ExportAllQuestions")) {
            String[] strHeder = {" State ", " Parliamentary Constituency ", " Assembly Constituency ", " Ward ", " Booth ", "  Quetions ", " Total Resposed Voters ", " Option 1 ", " Option 2 ", " Option 3 ", " Option 4 ", " Option 5 ", " Option 6 "};
            result.add(strHeder);
            for (int i = 0; i < sarveyQuestions.size(); i++) {
                SurveyQuestion surveyQuestion = sarveyQuestions.get(i);
                String anser = surveyQuestion.getOptions();
                String ansrs[] = new String[6];
                String temp[] = anser.split(";");
                for (int j = 0; j < 6; j++) {
                    if (temp.length > j) {
                        ansrs[j] = temp[j];
                    } else {
                        ansrs[j] = "";
                    }
                }
                result.add(citizenService.getReportCsv(surveyQuestion.getId(), ansrs[0], ansrs[1], ansrs[2], ansrs[3], ansrs[4], ansrs[5], filterQuery, pc, ac, wrd, bth)[0]);
            }
        } else if (request.getParameter("dowloadType").equalsIgnoreCase("ExportVotersSurveyData")) {
            String[] strHeader = new String[9 + (sarveyQuestions.size() * 2)];
            strHeader[0] = " State ";
            strHeader[1] = " Parliamentary Constituency ";
            strHeader[2] = " Assembly Constituency ";
            strHeader[3] = " Ward ";
            strHeader[4] = " Booth ";
            strHeader[5] = " Volunteer Mobile ";
            strHeader[6] = " Voter Id ";
            strHeader[7] = " Latitude ";
            strHeader[8] = " Longitude ";
            int index = 9;
            for (int i = 0; i < sarveyQuestions.size(); i++) {
                strHeader[index] = "Question_" + (i + 1) + "";
                index += 1;
                strHeader[index] = "Answer_" + (i + 1) + "";
                index += 1;
                SurveyQuestion surveyQuestion = sarveyQuestions.get(i);
                secondQuery += ",(select s1.question from survey_question s1 where s1.id = " + surveyQuestion.getId() + " limit 1) as '" + surveyQuestion.getId() + "_Q', (select s12.answer from survey s12 where s12.survey_question_id = " + surveyQuestion.getId() + " and s12.citizen_id = c.id  limit 1) as '" + surveyQuestion.getId() + "_A' ";
            }
            result.add(strHeader);
            StringBuilder query = new StringBuilder();
            query.append(firstQuery).append(secondQuery).append(thirdQuery).append(fourthQuery);
            result.addAll(citizenService.getQuestionAnswersDumpVoterWise(query));
        } else {
            String[] strHeder = {" State ", " Parliamentary Constituency ", " Assembly Constituency ", " Ward ", " Booth ", "  Quetions ", " Answer ", "Count"};
            result.add(strHeder);
            sarveyQuestions = sarveyQuestions.stream().filter(predicate -> predicate.getType().equalsIgnoreCase("Input")).collect(Collectors.toList());
            for (SurveyQuestion surveyQuestion : sarveyQuestions) {
                String[] ques = {surveyQuestion.getStateAssembly().getState(), pc, ac, wrd, bth, surveyQuestion.getQuestion(), "-", "-"};
                result.add(ques);
                result.addAll(citizenService.getQuestionAnswers(surveyQuestion.getId(), filterQuery));
            }
        }
        return result;
    }

    @RequestMapping(value = "/api/pushIOS/{deviceId}/{deviceType}", method = RequestMethod.GET)
    @ResponseBody
    public boolean pushToIOS(HttpServletRequest request, HttpServletResponse response, @PathVariable("deviceId") String deviceId, @PathVariable("deviceType") String deviceType) {
        return PushNotificationService.send(deviceId, "My Title", "My first notification\nHello, I'm push notification", deviceType);
    }

    @RequestMapping(value = "/api/deleteVolunteerById/{volunteerId}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseWrapper deleteVolunteerById(@PathVariable("volunteerId") Long volunteerId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        Volunteer volunteer = volunteerRepository.findById(volunteerId).get();
        String volunteerMobile = volunteer.getMobile();
        volunteer.setMobile(volunteerMobile + "(Deleted)");
        if (Objects.nonNull(volunteerRepository.findByMobile(volunteer.getMobile()))) {
            volunteer.setMobile(volunteerMobile + "(Deleted)-1");
        }
        if (Objects.nonNull(volunteerRepository.findByMobile(volunteer.getMobile()))) {
            volunteer.setMobile(volunteerMobile + "(Deleted)-2");
        }
        if (Objects.nonNull(volunteerRepository.findByMobile(volunteer.getMobile()))) {
            volunteer.setMobile(volunteerMobile + "(Deleted)-3");
        }
        if (Objects.nonNull(volunteerRepository.findByMobile(volunteer.getMobile()))) {
            volunteer.setMobile(volunteerMobile + "(Deleted)-4");
        }
        if (Objects.nonNull(volunteerRepository.findByMobile(volunteer.getMobile()))) {
            volunteer.setMobile(volunteerMobile + "(Deleted)-5");
        }
        if (Objects.nonNull(volunteerRepository.findByMobile(volunteer.getMobile()))) {
            volunteer.setMobile(volunteerMobile + "(Deleted)-" + RandomStringUtils.random(2, false, true));
        }
        if (Objects.nonNull(volunteerRepository.findByMobile(volunteer.getMobile()))) {
            volunteer.setMobile(volunteerMobile + "(Deleted)-" + RandomStringUtils.random(2, false, true));
        }
        volunteer.setStatus("Deleted");
        volunteerRepository.save(volunteer);
        wrapper.asCode("201");
        return wrapper;
    }

    @RequestMapping(value = "/api/getSegmentationsList/{stateId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<Map<String, Object>> getSegmentations1(@PathVariable("stateId") Long stateId) {
        List<Map<String, Object>> resultList = volunteerActionService.findAllSegmentationsList(stateId);
        if (resultList.isEmpty()) {
            volunteerActionService.getSegmentations(stateId);
            resultList = volunteerActionService.findAllSegmentationsList(stateId);
        }
        return resultList;
    }

}
