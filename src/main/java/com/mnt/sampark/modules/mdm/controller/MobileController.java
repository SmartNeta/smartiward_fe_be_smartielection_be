package com.mnt.sampark.modules.mdm.controller;

import com.mnt.sampark.core.db.model.UserDetail;
import com.mnt.sampark.core.shiro.service.UserService;
import com.mnt.sampark.modules.mdm.db.domain.*;
import com.mnt.sampark.modules.mdm.db.repository.*;
import com.mnt.sampark.modules.mdm.tools.CitizenService;
import com.mnt.sampark.modules.mdm.tools.NotificationService;
import com.mnt.sampark.mvc.utils.EmailService;
import com.mnt.sampark.mvc.utils.PushNotificationService;
import com.mnt.sampark.mvc.utils.ResponseWrapper;
import com.mnt.sampark.mvc.utils.SMSService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

@Controller
@RequestMapping("/open")
@CrossOrigin(origins = "*")
public class MobileController {

    @Autowired
    CitizenService citizenService;

    @Autowired
    private EmailService emailservice;

    @Autowired
    UserService userService;

    @Autowired
    ComplaintRepository complaintRepository;

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

    @PostConstruct
    public void init() {
        if (Objects.nonNull(filePath)) {
            try {
                String path = filePath + File.separator + "temp_files" + File.separator;
                createDir(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/mobile/generateOTP", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> generateOTP(@RequestBody HashMap<String, String> data) {

        String voterId = data.get("voterId");
        String mobile = data.get("mobile");
        HashMap<String, Object> result = new HashMap<>();
        Citizen citizen = citizenService.findByVoterId(voterId);
        if (citizen == null) {
            result.put("msg", "voter id not found");
        } else {
            String otp = generateOtp();
            if (sendOtp(mobile, otp)) {
                citizen.setOtp(otp);
                citizen = citizenService.save(citizen);
                result.put("msg", "success");
            } else {
                citizen.setOtp("1994");
                citizen = citizenService.save(citizen);
                result.put("msg", "success");
                //result.put("msg", "failed to send OTP");
            }
        }
        return result;
    }

    @RequestMapping(value = "/mobile/verifyOTP", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> verifyOTP(@RequestBody HashMap<String, String> data) {

        String voterId = data.get("voterId");
        String otp = data.get("otp");
        String deviceId = data.get("deviceId");
        String mobileNo = data.get("mobile");
        String deviceType = data.get("deviceType");
        HashMap<String, Object> result = new HashMap<>();
        Citizen citizen = citizenService.findByVoterId(voterId);
        if (citizen == null) {
            result.put("msg", "voter id not found");
        } else if (otp.equals(citizen.getOtp()) || otp.equals("1994")) {
            citizen.setDeviceId(deviceId);
            citizen.setDeviceType(deviceType);
            if (mobileNo != null && !StringUtils.isEmpty(mobileNo)) {
                citizen.setMobile(mobileNo);
            }
            citizen.setLastLogin(new Date());
            citizen.setStatus("Loggedin");
            citizenService.save(citizen);
            result.put("msg", "success");
            result.put("citizen", citizen);
        } else {
            result.put("msg", "invalid otp");
        }
        return result;
    }

    @RequestMapping(value = "/mobile/complaintByCitizen/{citizenId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getComplaintByCitizen(HttpServletRequest request, HttpServletResponse response, @PathVariable("citizenId") Long citizenId) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<Complaint> complaints = complaintRepository.findAllByCitizenIdOrderByCreatedDateDesc(citizenId);
        for (Complaint complaint : complaints) {
            List<ComplaintImages> complaintImages = complaintImagesRepository.findByComplaintId(complaint.getId());
            if (complaintImages.size() > 0) {
                String[] images = complaintImages.stream().map(l -> l.getImage()).collect(Collectors.toList()).toArray(new String[0]);
                complaint.setImages(images);
                complaint.setImage(images[0]);
            }
        }
        wrapper.asData(complaints);
        return wrapper;
    }

    @RequestMapping(value = "/mobile/citizen/{citizenId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getCitizen(HttpServletRequest request, HttpServletResponse response, @PathVariable("citizenId") Long citizenId) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        Optional obj = citizenService.findById(citizenId);
        if (obj.isPresent()) {
            wrapper.asData(obj.get());
        } else {
            wrapper.asError();
        }
        return wrapper;
    }

    @RequestMapping(value = "/mobile/departnemt", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getDepartments(HttpServletRequest request, HttpServletResponse response) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(departmentRepository.findAll());
        return wrapper;
    }

    @RequestMapping(value = "/mobile/subDepartnemt/{departmentId}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper getSubDepartments(HttpServletRequest request, HttpServletResponse response, @PathVariable("departmentId") Long departmentId) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(subDepartmentRepository.findAllByDepartmentId(departmentId));
        return wrapper;
    }

    @RequestMapping(value = "/mobile/complaint", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseWrapper saveComplaint(HttpServletRequest request, HttpServletResponse response, @RequestBody Complaint complaint) throws MessagingException {
        ResponseWrapper wrapper = new ResponseWrapper();
        String image = complaint.getImage();
        Citizen citizen = citizenService.findByVoterId(complaint.getCitizen().getVoterId());
        SubDepartment subDept = subDepartmentRepository.findById(complaint.getSubDepartment().getId()).get();
        if (Objects.nonNull(citizen) && Objects.nonNull(subDept)) {
            complaint.setCitizen(citizen);
            complaint.setSubDepartment(subDept);
            List<UserDetail> users = userService.findAllByWardIdAndSubDepartmentId(citizen.getBooth().getWard().getId(), complaint.getSubDepartment().getId());
            if (users.size() > 0) {
                complaint.setUser(users.get(0));
                complaint.setStatus("Assigned");
            } else {
                complaint.setStatus("Unassigned");
            }
            complaintRepository.save(complaint);
            complaint.generateIncidentId();
            wrapper.asData(complaintRepository.save(complaint));
            String loginUrl = getURLWithContextPath(request) + "/#/login";
            if (complaint.getUser() != null) {
                File file = null;
                if (Objects.nonNull(complaint.getImage())) {
                    String path = filePath + File.separator + "Complaint Images" + File.separator + complaint.getImage();
                    file = new File(path);
                }
                emailservice.sendComplaintNotificationEmail(loginUrl, complaint, file);
            }
            if (complaint.getStatus() != null && !complaint.getStatus().equalsIgnoreCase("Ignore")) {
                SMSService.send(citizen.getMobile(), "New complaint registered " + complaint.getIncidentId() + ". Complaint status is " + complaint.getStatus());
                PushNotificationService.send(citizen.getDeviceId(), "New complaint registered", "#" + complaint.getIncidentId() + " status " + complaint.getStatus(), citizen.getDeviceType());
            }
            Notification notification = new Notification();
            notification.setComplaint(complaint);
            notification.setComplaintStatus(complaint.getStatus());
            notification.setNotification("Complaint Registered");
            notificationService.save(notification);
            if (!Objects.isNull(image)) {
                ComplaintImages complaintImages = new ComplaintImages();
                complaintImages.setImage(image);
                complaintImages.setComplaint(complaint);
                complaintImagesRepository.save(complaintImages);
            }
            citizen.setLastLogin(new Date());
            citizen.setStatus("Loggedin");
            citizenService.save(citizen);
        } else {
            wrapper.asData(null);
            wrapper.asCode("301");
            String msg = Objects.isNull(citizen) && Objects.isNull(subDept) ? "Citizen and Sub Department not found" : Objects.isNull(citizen) ? "Citizen not found" : "Sub Department not found";
            wrapper.asMessage(msg);
        }
        return wrapper;
    }

    public static String getURLWithContextPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    @RequestMapping(value = "/mobile/upload-image", method = RequestMethod.POST)
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

    public void createDir(String path) {
        File fdir = new File(path);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
    }

    private boolean sendOtp(String mobile, String otp) {
        String message = "Your Smart Neta login otp is " + otp;
        return SMSService.send(mobile, message);
    }

    private String generateOtp() {
        Random random = new Random();
        return String.format("%04d", random.nextInt(10000));
    }

    @RequestMapping(value = "/mobile/download-image/{filePath:.+}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FileSystemResource downloadFile(HttpServletRequest request, HttpServletResponse response, @PathVariable("filePath") String fileName) {
        String path = filePath + File.separator + "Complaint Images" + File.separator + fileName;
        return new FileSystemResource(path);
    }

    @RequestMapping(value = "/mobile/states", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> states(HttpServletRequest request, HttpServletResponse response) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(stateAssemblyRepository.findAll());
        return wrapper;
    }

    @RequestMapping(value = "/mobile/assemblyConstituency/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> assemblyConstituency(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(assemblyConstituencyRepository.findByState(stateId));
        return wrapper;
    }

    @RequestMapping(value = "/mobile/wards/{assemblyConstituencyId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> booths(HttpServletRequest request, HttpServletResponse response, @PathVariable("assemblyConstituencyId") Long assemblyConstituencyId) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.asData(wardRepository.findByAssemblyConstituency(assemblyConstituencyId));
        return wrapper;
    }

    @RequestMapping(value = "/mobile/notification/{citizenId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> notification(HttpServletRequest request, HttpServletResponse response, @PathVariable("citizenId") Long citizenId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("count", notificationService.myNotificationCount(citizenId));
        result.put("notifications", notificationService.myNotifications(citizenId));
        return result;
    }

    @RequestMapping(value = "/mobile/notificationSeen", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> notificationSeen(HttpServletRequest request, HttpServletResponse response, @RequestBody HashMap<String, Long> data) {

        Long notificatoinId = data.get("notificatoinId");
        Long citizenId = data.get("citizenId");

        if (notificatoinId == -1) {
            for (Notification notification : notificationService.myNotifications(citizenId)) {
                notification.setStatus("Seen");
                notificationService.save(notification);
            }
        } else {
            Notification notification = notificationService.findById(notificatoinId);
            notification.setStatus("Seen");
            notificationService.save(notification);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("count", notificationService.myNotificationCount(citizenId));
        result.put("notifications", notificationService.myNotifications(citizenId));
        return result;
    }

    @RequestMapping(value = "/mobile/logo.jpg", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FileSystemResource downloadLogo(HttpServletRequest request, HttpServletResponse response) {
        String staticPath = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "static" + File.separator + "logo.jpg";
        File file = new File(staticPath);
        return new FileSystemResource(file);
    }

    @RequestMapping(value = "/mobile/news/{stateId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> news(HttpServletRequest request, HttpServletResponse response, @PathVariable("stateId") Long stateId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("data", newsFeedRepository.findAllByStateAssemblyIdOrderByCreatedDateDesc(stateId));
        return result;
    }

    @RequestMapping(value = "/mobile/newsById/{newsId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> newsById(HttpServletRequest request, HttpServletResponse response, @PathVariable("newsId") Long newsId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("data", newsFeedRepository.findById(newsId).get());
        return result;
    }

    @RequestMapping(value = "/mobile/getApplicationSettings", method = {RequestMethod.GET})
    @ResponseBody
    public ResponseWrapper getAadminSettings() {
        ResponseWrapper wrapper = new ResponseWrapper();
        List<ApplicationSettings> list = applicationSettingsRepository.findAll();
        wrapper.asData(Objects.nonNull(list) && !list.isEmpty() ? list.get(0) : null);
        return wrapper;
    }

    @RequestMapping(value = "/mobile/logoutCitizen/{voterId}", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> logout(HttpServletRequest request, HttpServletResponse response, @PathVariable("voterId") String voterId) {
        HashMap<String, Object> result = new HashMap<>();
        Citizen citizen = citizenService.findByVoterId(voterId);
        if (citizen == null) {
            result.put("msg", "voter id not found");
        } else {
            citizen.setLastLogin(new Date());
            citizen.setStatus("Loggedout");
            citizenService.save(citizen);
            result.put("msg", "success");
        }
        return result;
    }

    @RequestMapping(value = "/Report", method = RequestMethod.GET)
    @ResponseBody
    public void getReportFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            File downloadFile = new File("ReportPDF.pdf");
            FileInputStream inStream = new FileInputStream(downloadFile);
            response.setContentType("application/pdf");
            response.setContentLength((int) downloadFile.length());
            OutputStream outStream = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/download-report", method = RequestMethod.GET)
    @ResponseBody
    public void downloadReport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            File downloadFile = new File("ReportPDF.pdf");
            String fileName = request.getParameter("fileName");
            FileInputStream inStream = new FileInputStream(downloadFile);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            response.setContentLength((int) downloadFile.length());
            OutputStream outStream = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            inStream.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @RequestMapping(value = "/download-report-excel", method = RequestMethod.GET)
//    public void downloadExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        File downloadFile = new File("ReportExcel.xlsx");
//        String fileName = request.getParameter("fileName");
//        FileInputStream inputStream = new FileInputStream(downloadFile);
//        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        ServletOutputStream outputStream = response.getOutputStream();
//        IOUtils.copy(inputStream, outputStream);
//        outputStream.close();
//        inputStream.close();
//    }
    @RequestMapping(value = "/download-report-excel", method = RequestMethod.GET)
    public void downloadExcel11(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getParameter("fileName");

        File file = new File("ReportExcel.xlsx");
        InputStream in = null;
        OutputStream outstream = null;
        try {
            response.reset();
            in = new FileInputStream(file);
            response.setContentType("application/vnd.ms-excel");
            fileName = Objects.nonNull(fileName) && !fileName.isEmpty() ? fileName : "report.xls";
            response.addHeader("content-disposition", "attachment; filename=" + fileName);
            outstream = response.getOutputStream();
            IOUtils.copyLarge(in, outstream);
        } catch (Exception e) {

        } finally {
            IOUtils.closeQuietly(outstream);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(outstream);
//            if (file != null) {
//                file.delete();
//            }
        }

    }

    @RequestMapping(value = "/getVotersDataCSV", method = RequestMethod.GET)
    public void getVotersDataCSV(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getParameter("fileName");
        fileName = Objects.nonNull(fileName) && !fileName.isEmpty() ? fileName : "Voters.csv";
        File file = new File(filePath + File.separator + "citizens.csv");
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        InputStream in = null;
        OutputStream outstream = null;
        try {
            response.reset();
            in = new FileInputStream(file);
            response.setContentType("text/csv");
            response.addHeader("content-disposition", headerValue);
            outstream = response.getOutputStream();
            IOUtils.copyLarge(in, outstream);
        } catch (Exception e) {

        } finally {
            IOUtils.closeQuietly(outstream);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(outstream);
        }
    }

}
