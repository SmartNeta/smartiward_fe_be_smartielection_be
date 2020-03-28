package com.mnt.sampark;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mnt.sampark.modules.mdm.db.domain.Booth;
import com.mnt.sampark.modules.mdm.db.domain.CSVFileInfo;
import com.mnt.sampark.modules.mdm.db.domain.Citizen;
import com.mnt.sampark.modules.mdm.db.domain.StateAssembly;
import com.mnt.sampark.modules.mdm.db.repository.BoothRepository;
import com.mnt.sampark.modules.mdm.db.repository.CSVFileInfoRepository;
import com.mnt.sampark.modules.mdm.db.repository.CitizenRepository;
import com.mnt.sampark.modules.mdm.db.repository.StateAssemblyRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import org.json.JSONArray;

@Component
public class Scheduler {

    @Autowired
    CSVFileInfoRepository csvFileInfoRepository;

    @Value("${spring.datasource.url}")
    String dbUrl;

    @Value("${spring.datasource.driver-class-name}")
    String dbDriver;

    @Value("${spring.datasource.password}")
    String dbPassword;

    @Value("${spring.datasource.username}")
    String dbUserName;

    @Autowired
    CitizenRepository citizenRepository;

    @Autowired
    BoothRepository boothRepository;

    @Autowired
    StateAssemblyRepository stateAssemblyRepository;

    @Scheduled(fixedRate = 300000)
    public void reportCurrentTime() {
        System.out.println("In Scheduler.........");

//        CSVFileInfo inProgressFile = csvFileInfoRepository.findByStatus("inprogress");
        List<CSVFileInfo> inProgressFile = csvFileInfoRepository.findAllByStatus("inprogress");
        if (inProgressFile.isEmpty()) {
            List<CSVFileInfo> csvInfoList = csvFileInfoRepository.findAllByStatus("new");
            if (csvInfoList.size() > 0) {
                Long tempcount = 0l;
                Long successCount = 0l;
                Long count = 0l;
                System.out.println("new" + csvInfoList.get(0).getStatus());
                csvInfoList.get(0).setStatus("inprogress");
                csvFileInfoRepository.save(csvInfoList.get(0));
                StateAssembly state = stateAssemblyRepository.findByState(csvInfoList.get(0).getState());
                String DB_URL = dbUrl;

                //  Database credentials
                String USER = dbUserName;
                String PASS = dbPassword;

                Connection conn = null;
                Statement stmt = null;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    conn = null;
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    stmt = conn.createStatement();

                    StringBuilder sb = new StringBuilder();
                    sb.append("insert into citizen (assembly_no, ward_no, booth_no, srno, pincode, address, voter_id, first_name, family_name, age, gender,  booth_id, state, createddate, modifieddate, createdby, modifiedby) values ");
                    System.out.println("start" + new Date());
                    File readableFile = new File(csvInfoList.get(0).getPath());

                    java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
                    String errorLogFilePath = csvInfoList.get(0).getPath().replace(".csv", "-errorLog.txt");

                    try (Reader reader = Files.newBufferedReader(readableFile.toPath());
                            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()) {

                        String[] nextRecord;

                        while ((nextRecord = csvReader.readNext()) != null) {
                            count++;

                            Citizen citizenPresent = citizenRepository.findByVoterId(nextRecord[6]);
                            if (Objects.isNull(citizenPresent)) {
                                Booth booth = null;
                                if (Objects.isNull(state) || Objects.isNull(state.getId())) {
                                    booth = boothRepository.findByBoothNumberAndWardNoAndAssemblyConstituencyNo(nextRecord[2], nextRecord[1], nextRecord[0]);
                                } else {
                                    booth = boothRepository.findByBoothNumberAndWardNoAndAssemblyConstituencyNoAndState(nextRecord[2], nextRecord[1], nextRecord[0], state.getId());
                                }
                                if (Objects.isNull(booth)) {
                                    booth = boothRepository.findByBoothNumberAndWardNoAndAssemblyConstituencyNo(nextRecord[2], nextRecord[1], nextRecord[0]);
                                }
//                                if (Objects.isNull(booth)) {
//                                    booth = boothRepository.findByAssemblyNumberAndBoothNo(nextRecord[0], nextRecord[2]);
//                                }
                                if (Objects.nonNull(booth)) {
                                    tempcount++;
                                    successCount++;

                                    String address = nextRecord[5].replaceAll("\\(", " ").replaceAll("\\)", " ").replaceAll("\"", "\\\\\"").replaceAll("'", "\\\\'");
                                    String gender = (nextRecord[10].trim().equalsIgnoreCase("M") || nextRecord[10].trim().equalsIgnoreCase("Male")) ? "Male" : (nextRecord[10].trim().equalsIgnoreCase("F") || nextRecord[10].trim().equalsIgnoreCase("Female")) ? "Female" : "Other";
                                    //                    assembly_no,         ward_no,               booth_no,                srno,               pincode,                 address,               voter_id,              first_name,                family_name,                           age,                                                         gender,                                                                                                      ,  booth_id,                      state                  , createddate,    modifieddate,    createdby, modifiedby
                                    sb.append(" ('" + nextRecord[0] + "','" + nextRecord[1] + "','" + nextRecord[2] + "','" + nextRecord[3] + "','" + nextRecord[4] + "',\"" + address + "\",'" + nextRecord[6] + "','" + nextRecord[7] + "','" + nextRecord[8] + "'," + (nextRecord[9].trim().equals("") ? 0 : nextRecord[9]) + ",'" + gender + "'," + booth.getId() + ",'" + csvInfoList.get(0).getState() + "','" + sqlDate + "','" + sqlDate + "','sys','sys'),");

                                    if (tempcount % 1000 == 0) {
                                        sb.deleteCharAt(sb.length() - 1);
                                        sb.append(';');
                                        try {
                                            stmt.executeUpdate(sb.toString());
                                        } catch (Exception e) {
                                            String regex = "\\)\\,";
                                            String queries[] = sb.toString().split(regex);

                                            for (String query : queries) {
                                                if (query.contains("insert")) {
                                                    System.out.println("In Insert ");
                                                    try {
                                                        if (!query.contains(");")) {
                                                            stmt.executeUpdate(query + ");");
                                                        } else {
                                                            stmt.executeUpdate(query);
                                                        }
                                                    } catch (Exception exxx) {
                                                        successCount--;
                                                        writetoFile(errorLogFilePath, "    " + query + "    " + exxx.getMessage());
                                                    }
                                                } else {
                                                    try {
                                                        if (!query.contains(");")) {
                                                            stmt.executeUpdate("insert into citizen (assembly_no, ward_no, booth_no, srno, pincode, address, voter_id, first_name, family_name, age, gender,  booth_id, state, createddate, modifieddate, createdby, modifiedby) values " + query + ");");
                                                        } else {
                                                            stmt.executeUpdate("insert into citizen (assembly_no, ward_no, booth_no, srno, pincode, address, voter_id, first_name, family_name, age, gender,  booth_id, state, createddate, modifieddate, createdby, modifiedby) values " + query);
                                                        }
                                                    } catch (Exception ex) {
                                                        successCount--;
                                                        writetoFile(errorLogFilePath, "    " + query + "    " + ex.getMessage());
                                                    }

                                                }
                                            }
                                        }
                                        System.out.println(tempcount + " " + new Date());
                                        sb.setLength(0);
                                        sb.append("insert into citizen (assembly_no, ward_no, booth_no, srno, pincode, address, voter_id, first_name, family_name, age, gender,  booth_id, state, createddate, modifieddate, createdby, modifiedby) values ");
                                    }
                                } else {
                                    writetoFile(errorLogFilePath, " = booth not found : voter_id = " + nextRecord[6] + " assembly_no = " + nextRecord[0] + ", ward_no = " + nextRecord[1] + " & booth_no = " + nextRecord[2]);
                                }
                            } else {
                                citizenPresent.setAssemblyNo(nextRecord[0]);
                                citizenPresent.setWardNo(nextRecord[1]);
                                citizenPresent.setBoothNo(nextRecord[2]);
                                citizenPresent.setSrno(nextRecord[3]);
                                citizenPresent.setPincode(nextRecord[4]);
                                citizenPresent.setAddress(nextRecord[5]);
                                citizenPresent.setLatitude(null);
                                citizenPresent.setLongitude(null);
                                citizenRepository.save(citizenPresent);
                            }
                        }
                    }

                    try {
                        if (sb.length() != 0) {
                            stmt.executeUpdate(sb.toString());
                        } else {
                            System.out.println("end query");
                        }

                    } catch (Exception e) {
                        String regex = "\\)\\,";
                        String queries[] = sb.toString().split(regex);
                        for (String query : queries) {
                            if (query.contains("insert")) {
                                try {
                                    if (!query.contains(");")) {
                                        stmt.executeUpdate(query + ");");
                                    } else {
                                        stmt.executeUpdate(query);
                                    }
                                } catch (Exception eee) {
                                    writetoFile(errorLogFilePath, "    " + query + "    " + eee.getMessage());
                                }

                            } else {
                                try {
                                    if (!query.contains(");")) {
                                        stmt.executeUpdate("insert into citizen (assembly_no, ward_no, booth_no, srno, pincode, address, voter_id, first_name, family_name, age, gender,  booth_id, state, createddate, modifieddate, createdby, modifiedby) values " + query + ");");
                                    } else {
                                        stmt.executeUpdate("insert into citizen (assembly_no, ward_no, booth_no, srno, pincode, address, voter_id, first_name, family_name, age, gender,  booth_id, state, createddate, modifieddate, createdby, modifiedby) values " + query);
                                    }
                                } catch (Exception ex) {
                                    writetoFile(errorLogFilePath, "    " + query + "    " + ex.getMessage());
                                }
                            }
                        }
                    }

                    System.out.println("success end" + new Date());
                    stmt.close();
                    conn.close();
                } catch (SQLException se) {
                    //Handle errors for JDBC
                    se.printStackTrace();
                } catch (Exception e) {
                    //Handle errors for Class.forName
                    e.printStackTrace();
                } finally {
                    //finally block used to close resources
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    } catch (SQLException se2) {
                    }// nothing we can do
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }//end finally try
                }//end try

                csvInfoList.get(0).setStatus(tempcount == 0 ? "error" : "done");
                csvInfoList.get(0).setTotalCitizens((BigInteger.valueOf(count)));
                csvInfoList.get(0).setTotalSuccess((BigInteger.valueOf(successCount)));
                csvFileInfoRepository.save(csvInfoList.get(0));
            }
        }
    }

    private void writetoFile(String path, String value) throws IOException {
        // TODO Auto-generated method stub

        File f = new File(path);
        if (f.exists() && !f.isDirectory()) {
            BufferedWriter out = new BufferedWriter(new FileWriter(path, true));
            out.write(value + "\n");
            out.close();
        } else {
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(path));

            out.close();
        }
    }

    @Scheduled(fixedRate = 300000)
    public void setVotersLatitudeAndLongitude() {
        List<Object[]> citizens = citizenRepository.findAllCitizensWhereLatitudeAndLongitudeIsNull();
        System.out.println("Scheduled total citizens for latitude & longitude = " + citizens.size());
        citizens.forEach((citizenDto) -> {
            String lat = "-1";
            String lng = "-1";
            Citizen citizen = citizenRepository.findByVoterId(citizenDto[0] + "");
            if (Objects.isNull(citizen.getLatitude()) || Objects.isNull(citizen.getLongitude())) {
                try {
                    String address = citizenDto[1] + "".replaceAll("( )+", "%20");
                    URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&key=AIzaSyC6Nc3LTeqiLy2EEBrbs05xF6yXUK5XGwY");
                    URLConnection conn = url.openConnection();
                    conn.connect();
                    InputStreamReader isr = new InputStreamReader(conn.getInputStream());
                    StringBuilder sbLocation = new StringBuilder();
                    for (int i = 0; i != -1; i = isr.read()) {
                        sbLocation.append((char) i);
                    }

                    String getContent = sbLocation.toString().trim();
                    if (getContent.contains("results")) {
                        String temp = getContent.substring(getContent.indexOf("["));
                        JSONArray JSONArrayForAll = new JSONArray(temp);
                        if (Objects.nonNull(JSONArrayForAll) && JSONArrayForAll.length() > 1) {
                            if (Objects.nonNull(JSONArrayForAll.getJSONObject(0).getJSONObject("geometry"))) {
                                if (Objects.nonNull(JSONArrayForAll.getJSONObject(0).getJSONObject("geometry").getJSONObject("location"))) {
                                    lat = JSONArrayForAll.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lat").toString();
                                    lng = JSONArrayForAll.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lng").toString();
                                }
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    System.err.println("Exception 1 = " + citizen.getVoterId());
                } catch (IOException e) {
                    System.err.println("Exception 2 = " + citizen.getVoterId());
                }

                citizen.setLatitude(lat);
                citizen.setLongitude(lng);

                citizenRepository.save(citizen);
            }
        });
    }

    @Scheduled(cron = "0 0 0 */10 * ?")
    public void deleteUnwantedFiles() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -10);
        csvFileInfoRepository.findAllWithCreationDateTimeBefore(cal.getTime()).forEach(csvFile -> {
            File readableFile = new File(csvFile.getPath());
            String errorLogFilePath = csvFile.getPath().replace(".csv", "-errorLog.txt");
            File errorLogFile = new File(errorLogFilePath);
            if (readableFile.exists()) {
                readableFile.delete();
            }
            if (errorLogFile.exists()) {
                errorLogFile.delete();
            }
            csvFileInfoRepository.delete(csvFile);
        });
    }

}
