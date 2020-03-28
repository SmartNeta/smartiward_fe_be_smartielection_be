/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.mvc.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.mnt.sampark.modules.mdm.db.repository.*;
import com.mnt.sampark.modules.mdm.db.domain.AssemblyConstituency;
import com.mnt.sampark.modules.mdm.db.domain.Booth;
import com.mnt.sampark.modules.mdm.db.domain.ParliamentaryConstituency;
import com.mnt.sampark.modules.mdm.db.domain.Party;
import com.mnt.sampark.modules.mdm.db.domain.StateAssembly;
import com.mnt.sampark.modules.mdm.db.domain.Ward;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import javax.persistence.EntityManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *
 * @author govind
 */
@Service
public class ReportService {

    @Autowired
    StateAssemblyRepository stateAssemblyRepository;

    @Autowired
    BoothRepository boothRepository;

    @Autowired
    WardRepository wardRepository;

    @Autowired
    ParliamentaryConstituencyRepository parliamentaryConstituencyRepository;

    @Autowired
    AssemblyConstituencyRepository assemblyConstituencyRepository;

    @Autowired
    PartyRepository partyRepository;

    @Autowired
    CitizenRepository citizenRepository;

    @Autowired
    Configuration freemarkerMailConfiguration;

    @Autowired
    EntityManager em;

    @Autowired
    SegmentationsRepository segmentationsRepository;

    private NumberFormat numberFormatter = new DecimalFormat("#0.##");

    private DecimalFormat twoDForm = new DecimalFormat("#.##");

    public Map<String, Object> getParlimentoryReport(Long parlimentoryId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        Map<String, Object> resultMap = new HashMap<>();
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        Map<String, Object> pcReportData = new HashMap<>();
        List<String> assemblyArray = new ArrayList<>();
        List<String> assemblyIds = new ArrayList<>();
        List<Object[]> totalYears = em.createNativeQuery("SELECT CAST(p.year AS char) FROM previous_election p WHERE p.assembly_no IN (select a.no from assembly_constituency a where a.parliamentary_constituency_id = " + parlimentoryId + " ) GROUP BY p.year ORDER BY p.year ASC LIMIT 2;").getResultList();
        List<Object[]> totalPartiesCode = em.createNativeQuery("SELECT pa.code FROM previous_election p inner join party pa on pa.id = p.party_id where p.assembly_no IN (select a.no from assembly_constituency a where a.parliamentary_constituency_id = " + parlimentoryId + " ) group by pa.code order by pa.code asc;").getResultList();
        pcReportData.put("totalYears", totalYears);
        pcReportData.put("totalPartiesCode", totalPartiesCode);

        StringBuilder assemblyNumbers = new StringBuilder("(");
        ParliamentaryConstituency pcEntity = parliamentaryConstituencyRepository.findById(parlimentoryId).get();
        pcReportData.put("districtName", pcEntity.getDistrict().getName());
        List<Party> parties = partyRepository.partyPreferenceByParliamentary(pcEntity.getDistrict().getStateAssembly().getId(), pcEntity.getDistrict().getId());

        StringBuilder sb = new StringBuilder();
        List<String> allParties = new ArrayList<>();
        parties.forEach((action) -> {
            allParties.add(action.getCode());
            sb.append("COUNT(IF(c.responded_status = 'responded' AND c.party_preference = '" + action.getCode().trim() + "',1,null)) as '" + action.getCode().trim() + "_support',");
        });
        pcReportData.put("partyNames", allParties);

        BigInteger votersVisited = BigInteger.ZERO;
        BigInteger votersResponded = BigInteger.ZERO;
        BigInteger greenCount = BigInteger.ZERO;
        BigInteger yellowCount = BigInteger.ZERO;
        BigInteger orangeCount = BigInteger.ZERO;
        BigInteger redCount = BigInteger.ZERO;

        List<Object[]> overallResult = em.createNativeQuery("SELECT result.total_houses, result.total_voters, result.total_booths, CAST(NULL AS char) AS green,CAST(NULL AS char) AS yellow,CAST(NULL AS char) AS orange,CAST(NULL AS char) AS red, COUNT(*) voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,NULL)) AS voter_responded," + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed FROM citizen c INNER JOIN booth b ON b.id = c.booth_id INNER JOIN ward w ON w.id = b.ward_id  INNER JOIN assembly_constituency a ON a.id = w.assembly_constituency_id  INNER JOIN parliamentary_constituency P ON P.id = a.parliamentary_constituency_id , (SELECT COUNT(DISTINCT ci.address) total_houses, COUNT(*) total_voters, COUNT(DISTINCT ci.booth_id) total_booths  FROM citizen ci INNER JOIN booth b ON b.id = ci.booth_id  INNER JOIN ward w ON w.id = b.ward_id  INNER JOIN assembly_constituency a ON a.id = w.assembly_constituency_id WHERE a.parliamentary_constituency_id = " + parlimentoryId + " ) AS result WHERE c.responded_status IS not NULL and P.id = " + parlimentoryId).getResultList();
        List<Object[]> reportData = em.createNativeQuery("SELECT a.id , a.name, result.total_houses, result.total_voters, result.total_booths, CAST(NULL AS char) AS green, CAST(NULL AS char) AS yellow, CAST(NULL AS char) AS orange, CAST(NULL AS char) AS red, COUNT(*) as voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,NULL)) AS voters_responded," + sb.toString() + "  COUNT(IF(c.responded_status = 'responded' AND c.party_preference IS NULL,1,NULL)) AS NOT_disclosed from citizen c inner join booth b ON b.id = c.booth_id inner join ward w ON w.id = b.ward_id inner join assembly_constituency a ON a.id = w.assembly_constituency_id inner join parliamentary_constituency p ON p.id = a.parliamentary_constituency_id JOIN (SELECT COUNT(DISTINCT ci.address) total_houses, COUNT(*) total_voters, COUNT(DISTINCT ci.booth_id) total_booths, ci.assembly_no AS ano  FROM citizen ci INNER JOIN booth b ON b.id = ci.booth_id  INNER JOIN ward w ON w.id = b.ward_id  INNER JOIN assembly_constituency a ON a.id = w.assembly_constituency_id WHERE a.parliamentary_constituency_id = " + parlimentoryId + " GROUP BY ci.assembly_no) AS result ON (result.ano = c.assembly_no) WHERE c.responded_status IS NOT NULL and p.id = " + parlimentoryId + " GROUP BY c.assembly_no;").getResultList();

        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            for (Object[] reportObject : reportData) {
                assemblyIds.add(reportObject[0] + "");
                AssemblyConstituency assemblyConstituency = assemblyConstituencyRepository.findById(((BigInteger) reportObject[0]).longValue()).get();
                reportObject[0] = assemblyConstituency.getNo();
                assemblyArray.add(reportObject[0] + "");
                votersVisited = votersVisited.add(reportObject[9] != null ? (BigInteger) reportObject[9] : BigInteger.ZERO);
                votersResponded = votersResponded.add(reportObject[10] != null ? (BigInteger) reportObject[10] : BigInteger.ZERO);
                List<Object[]> boothStatus = em.createNativeQuery("select pp.booth_work,count(*) as total_booths from (select  case when p >= 0 and p <=  0.25 then 25 when p > 0.25 and p <= 0.50 then 50 when p > 0.50 and p <= 0.75 then 75 when p > 0.75 then 100 end as booth_work  from  (select count(*)/(b.voters_visited) p from citizen a,  (select citi.assembly_no,count(distinct(citi.booth_id)) total_booths, count(if(citi.responded_status is not null , 1,null)) as voters_visited from citizen citi group by citi.assembly_no) b where a.responded_status is not null and a.assembly_no = b.assembly_no and a.assembly_no = " + reportObject[0] + " group by booth_id) cc ) pp  group by pp.booth_work").getResultList();
                BigInteger green = BigInteger.ZERO;
                BigInteger yellow = BigInteger.ZERO;
                BigInteger orange = BigInteger.ZERO;
                for (Object[] obj : boothStatus) {
                    if (obj[0].equals(100)) {
                        green = green.add((BigInteger) obj[1]);
                        greenCount = greenCount.add((BigInteger) obj[1]);
                    } else if (obj[0].equals(75)) {
                        yellow = yellow.add((BigInteger) obj[1]);
                        yellowCount = yellowCount.add((BigInteger) obj[1]);
                    } else if (obj[0].equals(50)) {
                        orange = orange.add((BigInteger) obj[1]);
                        orangeCount = orangeCount.add((BigInteger) obj[1]);
                    } else if (obj[0].equals(25)) {
                        //redCount = redCount.add(((BigInteger) reportObject[4]).subtract(green.add(orange).add(yellow)));
                    }
                }
                reportObject[5] = green;
                reportObject[6] = yellow;
                reportObject[7] = orange;
                reportObject[8] = ((BigInteger) reportObject[4]).subtract(green.add(orange).add(yellow));
                redCount = redCount.add(((BigInteger) reportObject[8]));
            }
            assemblyNumbers.append(StringUtils.join(assemblyArray, ','));
        }
        if (assemblyArray.isEmpty()) {
            assemblyNumbers.append("'')");
        } else {
            assemblyNumbers.append(")");
        }
        List<Object[]> allAssemblyData = em.createNativeQuery("select a.id , a.name, COUNT(distinct c.address) as total_houses, COUNT(*) total_voters, COUNT(distinct c.booth_id) total_booths, 0 as green, 0 as yellow, 0 as orange, 0 as red, 0 as voter_visited, 0 as voters_responded," + sb.toString() + " 0 as not_disclosed from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id inner join assembly_constituency a on a.id = w.assembly_constituency_id where (c.assembly_no not in " + assemblyNumbers + " or c.assembly_no is null) and a.parliamentary_constituency_id = " + parlimentoryId + " group by c.assembly_no;").getResultList();
        reportData.addAll(allAssemblyData);
        allAssemblyData.forEach(action -> {
            AssemblyConstituency assemblyConstituency = assemblyConstituencyRepository.findById(((BigInteger) action[0]).longValue()).get();
            assemblyIds.add(action[0] + "");
            action[0] = assemblyConstituency.getNo();
            action[8] = 1;
        });
        int count = 1;
        Map<String, Object> mappp = new HashMap<>();
        Map<String, Object> overAllMap = new HashMap<>();
        BigInteger totalVoters = BigInteger.ZERO;
        BigInteger totalPolled = BigInteger.ZERO;
        for (Object yr : totalYears) {
            overAllMap.put("overAll_Voters_" + yr.toString(), totalVoters);
            overAllMap.put("overAll_Polled_" + yr.toString(), totalPolled);
            for (int i = 0; i < totalPartiesCode.size(); i++) {
                overAllMap.put("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i), 0l);
            }
        }

        for (String assemblyId : assemblyIds) {
            for (Object yr : totalYears) {
                String myQuery = "select sum(deo.total_voters), sum(deo.total_polled), concat(ROUND(sum(deo.total_polled) * 100 / sum(deo.total_voters), 0), '%') as per from (select p.assembly_no, p.ward_no, p.booth_no, p.total_voters, p.total_polled,p.total_party_voted FROM previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = " + yr.toString() + " and p.assembly_no = (select a.no from assembly_constituency a where a.id = " + assemblyId + " ) and p.parliamentary_id = " + parlimentoryId + " group by booth_no, p.ward_no) as deo;";
                List<Object[]> previousElection = em.createNativeQuery(myQuery).getResultList();
                if (previousElection.size() > 0) {
                    Object mObj[] = new Object[totalPartiesCode.size() + 3];

                    BigDecimal totalVoters1 = previousElection.get(0)[0] != null ? (BigDecimal) previousElection.get(0)[0] : BigDecimal.ZERO;
                    BigDecimal totalPolled1 = previousElection.get(0)[1] != null ? (BigDecimal) previousElection.get(0)[1] : BigDecimal.ZERO;
                    mObj[0] = previousElection.get(0)[0] != null ? previousElection.get(0)[0] : 0;
                    mObj[1] = previousElection.get(0)[1] != null ? previousElection.get(0)[1] : 0;
                    mObj[2] = previousElection.get(0)[2] != null ? previousElection.get(0)[2] : "0%";
                    int h = 3;
                    for (int i = 0; i < totalPartiesCode.size(); i++) {
                        List<Object[]> perc = em.createNativeQuery("select sum(deo.total_party_voted) as per ,sum(deo.total_party_voted) from(select p.total_party_voted, p.total_polled from previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = " + yr.toString() + " and p.assembly_no = (select a.no from assembly_constituency a where a.id = " + assemblyId + " ) and p.parliamentary_id = " + parlimentoryId + " and pa.code = '" + totalPartiesCode.get(i) + "') as deo;").getResultList();
                        mObj[h] = perc.isEmpty() || perc.get(0)[0] == null ? 0 : perc.get(0)[0];
                        Long partyTotal = perc.isEmpty() || perc.get(0)[1] == null ? 0 : Long.valueOf(perc.get(0)[1] + "");
                        overAllMap.put("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i), partyTotal + ((Long) overAllMap.get("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i))));
                        h++;
                    }
                    mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                    overAllMap.put("overAll_Voters_" + yr.toString(), totalVoters1.toBigInteger().add((BigInteger) overAllMap.get("overAll_Voters_" + yr.toString())));
                    overAllMap.put("overAll_Polled_" + yr.toString(), totalPolled1.toBigInteger().add((BigInteger) overAllMap.get("overAll_Polled_" + yr.toString())));
                } else {
                    Object mObj[] = new Object[totalPartiesCode.size() + 3];
                    mObj[0] = 0;
                    mObj[1] = 0;
                    mObj[2] = "0%";
                    int h = 3;
                    for (int i = 0; i < totalPartiesCode.size(); i++) {
                        mObj[h] = "0";
                        h++;
                    }
                    mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                }
            }
            count++;
        }

        pcReportData.put("reportData", reportData);
        pcReportData.put("mappp", mappp);
        pcReportData.put("overAllMap", overAllMap);
        if (Objects.nonNull(overallResult) && overallResult.size() > 0) {
            Object[] objectResult = overallResult.get(0);
            objectResult[3] = greenCount;
            objectResult[4] = yellowCount;
            objectResult[5] = orangeCount;
            objectResult[6] = redCount.longValue() + allAssemblyData.size();
            objectResult[7] = votersVisited;
            objectResult[8] = votersResponded;
            pcReportData.put("overallResult", objectResult);
        }

        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate("parlimentory_report.ftl", "UTF-8"), pcReportData);
        Document document = new Document(PageSize.A3.rotate(), 5, 5, 15, 15);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
        document.close();
        savePdfFile(os.toByteArray());
        saveExcelFile(generateParliamentoryExcelReport(pcReportData));
//        resultMap.put("pdf", os.toByteArray());
//        resultMap.put("excel", generateParliamentoryExcelReport(pcReportData));
        return resultMap;
    }

    public Map<String, Object> getAssemblyReport(Long assemblyId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        Map<String, Object> assemblyReportData = new HashMap<>();
        AssemblyConstituency assemblyEntity = assemblyConstituencyRepository.findById(assemblyId).get();
        List<Long> wardIds = wardRepository.findAllByAssemblyConstituencyId(assemblyId).stream().map(mapper -> mapper.getId()).collect(Collectors.toList());
        assemblyReportData.put("assemblyNumber", assemblyEntity.getNo());
        List<Party> parties = partyRepository.partyPreferenceByAssembly(assemblyEntity.getParliamentaryConstituency().getDistrict().getStateAssembly().getId(), assemblyEntity.getParliamentaryConstituency().getDistrict().getId(), assemblyEntity.getId());
        return generateAssemblyOrWardReport(wardIds, assemblyReportData, parties, false, "", assemblyEntity.getNo(), assemblyId);
    }

    public Map<String, Object> getWardReport(Long wardId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        Map<String, Object> wardReportData = new HashMap<>();
        Ward wardEntity = wardRepository.findById(wardId).get();
        wardReportData.put("wardNumber", wardEntity.getNo());
        List<Long> wardIds = new ArrayList<>();
        wardIds.add(wardId);
        List<Party> parties = partyRepository.partyPreference(wardEntity.getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getStateAssembly().getId(), wardEntity.getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getId(), wardEntity.getAssemblyConstituency().getId(), wardEntity.getId());
        return generateAssemblyOrWardReport(wardIds, wardReportData, parties, true, wardEntity.getNo(), wardEntity.getAssemblyConstituency().getNo(), null);
    }

    public Map<String, Object> generateBoothReport(Long boothId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        Booth boothEntity = boothRepository.findById(boothId).get();
        String arr[] = {"A+", "A", "B", "C"};
        Long stateId = boothEntity.getWard().getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getStateAssembly().getId();
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (String action : arr) {
            Map<String, Object> mapSegmentation = new HashMap<>();
            String labelName = segmentationsRepository.findLabelBySegmentationAndStateAssemblyId(action, stateId + "");
            mapSegmentation.put("label", (labelName != null ? labelName : action) + "");
            mapSegmentation.put("segmentation", action + "");
            resultList.add(mapSegmentation);
        }
        Map<String, Object> boothReportData = new HashMap<>();
        boothReportData.put("segmentations", resultList);

        StringBuilder sb = new StringBuilder();
        Map<String, Object> resultMap = new HashMap<>();
        List<String> allParties = new ArrayList<>();
        boothReportData.put("boothNumber", "");
        if (Objects.nonNull(boothEntity)) {
            boothReportData.put("boothNumber", boothEntity.getNo());
            List<Party> parties = partyRepository.partyPreference(stateId, boothEntity.getWard().getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getId(), boothEntity.getWard().getAssemblyConstituency().getId(), boothEntity.getWard().getId());
            parties.forEach((action) -> {
                allParties.add(action.getCode());
                sb.append("COUNT(IF(c.responded_status = 'responded' AND c.party_preference = '" + action.getCode().trim() + "',1,null)) as '" + action.getCode().trim() + "_support',");
            });
        }

        List<Object[]> overallResult = em.createNativeQuery("select count(*) as voter_visited, COUNT(IF(c.responded_status = 'responded', 1 ,null)) as voter_responded, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'A+',1,null)) as A1, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'A',1,null)) as A, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'B',1,null)) as B, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'C',1,null)) as C, " + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed from citizen c where c.responded_status is not null and c.booth_id = " + boothId + ";").getResultList();
        List<Object[]> reportData = em.createNativeQuery("select c.booth_no, c.volunteer_mobile, count(*) as voter_visited, COUNT(IF(c.responded_status = 'responded', 1 ,null)) as voter_responded, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'A+',1,null)) as A1, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'A',1,null)) as A, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'B',1,null)) as B, COUNT(IF(c.responded_status = 'responded' AND c.voter_segmentation = 'C',1,null)) as C, " + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed from citizen c where c.responded_status is not null and c.booth_id = " + boothId + " GROUP BY c.volunteer_mobile order by voter_visited desc;").getResultList();

        if (Objects.nonNull(overallResult) && overallResult.size() > 0) {
            Object[] objectResult = overallResult.get(0);
            boothReportData.put("overallResult", objectResult);
        }
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            boothReportData.put("reportData", reportData);
        }
        boothReportData.put("partyNames", allParties);
        boothReportData.put("totalParties", allParties.size() + 1);
        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate("booth_report.ftl", "UTF-8"), boothReportData);
        Document document = new Document(PageSize.A4.rotate(), 5, 5, 15, 15);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
        document.close();
        savePdfFile(os.toByteArray());
        saveExcelFile(generateBoothExcelReport(boothReportData));
//        resultMap.put("pdf", os.toByteArray());
//        resultMap.put("excel", generateBoothExcelReport(boothReportData));
        return resultMap;
    }

    private Map<String, Object> generateAssemblyOrWardReport(List<Long> wardIds, Map<String, Object> reportDataMap, List<Party> parties, boolean isWardReport, String wardNo, String assemblyNo, Long assemblyId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        System.out.println("start " + new Date());
        Map<String, Object> resultMap = new HashMap<>();
        String reportFile = isWardReport ? "ward_report.ftl" : "assembly_report.ftl";
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        StringBuilder sb = new StringBuilder();
        StringBuilder wardIdsIn = new StringBuilder("(");
        wardIdsIn.append(StringUtils.join(wardIds, ','));
        if (wardIds.isEmpty()) {
            wardIdsIn.append("'')");
        } else {
            wardIdsIn.append(")");
        }
        List<Object[]> totalYears = em.createNativeQuery("SELECT CAST(p.year AS char) FROM previous_election p WHERE p.ward_no IN (select w.no from ward w where w.id in " + wardIdsIn + " ) GROUP BY p.year ORDER BY p.year ASC LIMIT 2;").getResultList();
        List<Object[]> totalPartiesCode = em.createNativeQuery("SELECT pa.code FROM previous_election p inner join party pa on pa.id = p.party_id where p.ward_no IN (select w.no from ward w where w.id in " + wardIdsIn + ") group by pa.code order by pa.code asc;").getResultList();
        reportDataMap.put("totalYears", totalYears);
        reportDataMap.put("totalPartiesCode", totalPartiesCode);
        List<String> boothArray = new ArrayList<>();
        StringBuilder boothIds = new StringBuilder("(");

        List<String> allParties = new ArrayList<>();
        parties.forEach((action) -> {
            allParties.add(action.getCode());
            sb.append("COUNT(IF(c.responded_status = 'responded' AND c.party_preference = '" + action.getCode().trim() + "',1,null)) as '" + action.getCode().trim() + "_support',");
        });
        List<Object[]> overallResult = new ArrayList<>();
        List<Object[]> overallResult1 = new ArrayList<>();
        if (isWardReport) {
            String query = "select CAST(null as char) as boothCount, CAST(null as char) as total_houses , CAST(null as char) as total_voters,  CAST(null as char) as empty, CAST(null as char) as voter_percentage , CAST(null as char) as empty2, COUNT(*) as voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,null)) as voter_responded, " + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where c.responded_status is not null and w.id in " + wardIdsIn + ";";
            overallResult = em.createNativeQuery(query).getResultList();
            overallResult1 = em.createNativeQuery("select count(distinct ci.booth_id) as booths, count(distinct ci.address) as address, count(*) as voters from citizen ci where ci.booth_id in (select b.id from booth b where b.ward_id in " + wardIdsIn + "  )").getResultList();
        } else {
            String query = "select CAST(null as char) as boothCount, CAST(null as char) as total_houses , CAST(null as char) as total_voters,  CAST(null as char) as empty, CAST(null as char) as voter_percentage , CAST(null as char) as empty2, COUNT(*) as voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,null)) as voter_responded, " + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where c.responded_status is not null and w.assembly_constituency_id = " + assemblyId + ";";
            overallResult = em.createNativeQuery(query).getResultList();
//            overallResult1 = em.createNativeQuery("select count(distinct ci.booth_id) as booths, count(distinct ci.address) as address, count(*) as voters from citizen ci where ci.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id =  " + assemblyId + " ))").getResultList();
            overallResult1 = citizenRepository.getOverallReport(assemblyId);
            System.out.println("after overallResult1 = " + new Date());
        }

        Double totalPercentage = 0.0;
        BigInteger voterVisited = BigInteger.ZERO;
        BigInteger votersResponded = BigInteger.ZERO;
        List<Object[]> reportData = new ArrayList<>();
        if (isWardReport) {
            reportData = em.createNativeQuery("select c.ward_no, c.booth_id, c.booth_no, 1 as total_booths, (select count(distinct ci.address) from citizen ci where ci.booth_id = c.booth_id) as total_houses, CAST(null as char) as empty, (select count(*) from citizen ci where ci.booth_id = c.booth_id) as total_voters, (COUNT(c.responded_status = 'responded') * 100) / (select count(*) from citizen ci where ci.booth_id = c.booth_id) as voter_percentage, CAST(null as char) as empty2, COUNT(*) as voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,null)) as voter_responded, " + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where c.responded_status is not null and w.id in " + wardIdsIn + " group by c.booth_id order by c.ward_no + 0, c.booth_no + 0 asc;").getResultList();
        } else {
            String query = "select c.ward_no, w.id, c.booth_no, (select count(distinct ci.booth_id) from citizen ci inner join booth bb on bb.id = ci.booth_id where bb.ward_id = w.id) as total_booths, (select count(distinct ci.address) from citizen ci inner join booth bb on bb.id = ci.booth_id where bb.ward_id = w.id) as total_houses, CAST(null as char) as empty, (select count(*) from citizen ci inner join booth bb on bb.id = ci.booth_id where bb.ward_id = w.id) as total_voters, (COUNT(c.responded_status = 'responded') * 100) / (select count(*) from citizen ci inner join booth bb on bb.id = ci.booth_id where bb.ward_id = w.id) as voter_percentage, CAST(null as char) as empty2, COUNT(*) as voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,null)) as voter_responded, " + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where c.responded_status is not null and w.assembly_constituency_id = " + assemblyId + " group by w.id order by c.ward_no + 0 asc;";
            reportData = em.createNativeQuery(query).getResultList();
            System.out.println("after reportData = " + new Date());
        }

        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            for (Object[] action : reportData) {
                boothArray.add(action[1] + "");
                BigDecimal temp = (BigDecimal) action[7];

                voterVisited = voterVisited.add(action[9] != null ? (BigInteger) action[9] : BigInteger.ZERO);
                votersResponded = votersResponded.add(action[10] != null ? (BigInteger) action[10] : BigInteger.ZERO);
                totalPercentage = totalPercentage + temp.doubleValue();
            }
            boothIds.append(StringUtils.join(boothArray, ','));
        }
        if (boothArray.isEmpty()) {
            boothIds.append("'')");
        } else {
            boothIds.append(")");
        }
        List<Object[]> reportDataAnoe = new ArrayList<>();
        System.out.println("before reportDataAnoe = " + new Date());
        if (isWardReport) {
            if (boothArray.isEmpty()) {
                reportDataAnoe = em.createNativeQuery("select c.ward_no, c.booth_id, c.booth_no,1 as totalBooth, COUNT(distinct c.address) as total_houses,CAST(null as char) as empty, COUNT(*) as total_voters, 0 as voter_percentage, CAST(null as char) as d, " + sb.toString() + "  0 as h,0 as i, 0 as j from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where c.booth_id in (select b.id from booth b where b.ward_id in " + wardIdsIn + ") group by c.booth_id order by c.booth_no + 0 asc;").getResultList();
            } else {
                reportDataAnoe = em.createNativeQuery("select c.ward_no, c.booth_id, c.booth_no,1 as totalBooth, COUNT(distinct c.address) as total_houses,CAST(null as char) as empty, COUNT(*) as total_voters, 0 as voter_percentage, CAST(null as char) as d, " + sb.toString() + "  0 as h,0 as i, 0 as j from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where c.booth_id in (select b.id from booth b where b.ward_id in " + wardIdsIn + ") and c.booth_id not in " + boothIds + " group by c.booth_id order by c.booth_no + 0 asc;").getResultList();
            }
        } else {
            if (boothArray.isEmpty()) {
                reportDataAnoe = em.createNativeQuery("select c.ward_no, w.id , CAST(null as char) as booth_no, (select count(distinct ci.booth_id) from citizen ci inner join booth bb on bb.id = ci.booth_id where bb.ward_id = w.id) as total_booths, COUNT(distinct c.address) as total_houses,CAST(null as char) as empty, COUNT(*) as total_voters , 0 as voter_percentage , CAST(null as char) as empty1, " + sb.toString() + "  0 as h, 0 as i, 0 as j  from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where w.assembly_constituency_id = " + assemblyId + " group by w.id order by c.ward_no + 0 asc;").getResultList();
            } else {
                reportDataAnoe = em.createNativeQuery("select c.ward_no, w.id , CAST(null as char) as booth_no, (select count(distinct ci.booth_id) from citizen ci inner join booth bb on bb.id = ci.booth_id where bb.ward_id = w.id) as total_booths, COUNT(distinct c.address) as total_houses,CAST(null as char) as empty, COUNT(*) as total_voters , 0 as voter_percentage , CAST(null as char) as empty1, " + sb.toString() + "  0 as h, 0 as i, 0 as j  from citizen c inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id where w.assembly_constituency_id = " + assemblyId + " and w.id not in " + boothIds + " group by w.id order by c.ward_no + 0 asc;").getResultList();
            }
        }
        System.out.println("after reportDataAnoe = " + new Date());

        reportDataAnoe.forEach(action -> {
            boothArray.add(action[1] + "");
        });

        int count = 1;
        Map<String, Object> mappp = new HashMap<>();
        Map<String, Object> overAllMap = new HashMap<>();
        BigInteger totalVoters = BigInteger.ZERO;
        BigInteger totalPolled = BigInteger.ZERO;
        for (Object yr : totalYears) {
            overAllMap.put("overAll_Voters_" + yr.toString(), totalVoters);
            overAllMap.put("overAll_Polled_" + yr.toString(), totalPolled);
            for (int i = 0; i < totalPartiesCode.size(); i++) {
                overAllMap.put("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i), 0l);
            }
        }
        if (isWardReport) {
            for (String boothId : boothArray) {
                for (Object yr : totalYears) {
                    String myQuery = "SELECT p.total_voters, p.total_polled, CONCAT(ROUND((p.total_polled * 100) / p.total_voters, 0), '%') as percentage FROM previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = '" + yr.toString() + "' and p.assembly_no = '" + assemblyNo + "' AND p.ward_no = '" + wardNo + "' AND p.booth_no = (select bt.no from booth bt where bt.id = " + boothId + " ) limit 1;";
                    List<Object[]> previousElection = em.createNativeQuery(myQuery).getResultList();
                    if (previousElection.size() > 0) {
                        Object mObj[] = new Object[totalPartiesCode.size() + 3];
                        totalVoters = previousElection.get(0)[0] != null ? (BigInteger) previousElection.get(0)[0] : BigInteger.ZERO;
                        totalPolled = previousElection.get(0)[1] != null ? (BigInteger) previousElection.get(0)[1] : BigInteger.ZERO;
                        mObj[0] = previousElection.get(0)[0] != null ? previousElection.get(0)[0] : 0;
                        mObj[1] = previousElection.get(0)[1] != null ? previousElection.get(0)[1] : 0;
                        mObj[2] = previousElection.get(0)[2] != null ? previousElection.get(0)[2] : "0%";
                        int h = 3;
                        for (int i = 0; i < totalPartiesCode.size(); i++) {
                            List<Object[]> perc = em.createNativeQuery("select p.total_party_voted as percentage, p.total_party_voted from previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = " + yr + " and p.assembly_no = " + assemblyNo + " AND p.ward_no = " + wardNo + " AND p.booth_no = (select bt.no from booth bt where bt.id = " + boothId + ") and pa.code = '" + totalPartiesCode.get(i) + "' ").getResultList();
                            mObj[h] = perc.isEmpty() || perc.get(0)[1] == null ? 0 : perc.get(0)[0];
                            Long partyTotal = perc.isEmpty() || perc.get(0)[1] == null ? 0 : Long.valueOf(perc.get(0)[1] + "");
                            overAllMap.put("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i), partyTotal + ((Long) overAllMap.get("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i))));
                            h++;
                        }
                        mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                        overAllMap.put("overAll_Voters_" + yr.toString(), totalVoters.add((BigInteger) overAllMap.get("overAll_Voters_" + yr.toString())));
                        overAllMap.put("overAll_Polled_" + yr.toString(), totalPolled.add((BigInteger) overAllMap.get("overAll_Polled_" + yr.toString())));
                    } else {
                        Object mObj[] = new Object[totalPartiesCode.size() + 3];
                        mObj[0] = 0;
                        mObj[1] = 0;
                        mObj[2] = "0%";
                        int h = 3;
                        for (int i = 0; i < totalPartiesCode.size(); i++) {
                            mObj[h] = "0";
                            h++;
                        }
                        mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                    }
                }
                count++;
            }
        } else {
            for (String wardId : boothArray) {
                for (Object yr : totalYears) {
                    String myQuery = "select sum(deo.total_voters), sum(deo.total_polled), concat(ROUND((sum(deo.total_polled) * 100 / sum(deo.total_voters)), 0), '%') as per from (select p.assembly_no, p.ward_no, p.booth_no, p.total_voters, p.total_polled,p.total_party_voted FROM previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = '" + yr.toString() + "' and p.assembly_no = '" + assemblyNo + "' AND p.ward_no = (select w.no from ward w where w.id = " + wardId + " ) group by p.booth_no) as deo;";
                    List<Object[]> previousElection = em.createNativeQuery(myQuery).getResultList();
                    if (previousElection.size() > 0) {
                        Object mObj[] = new Object[totalPartiesCode.size() + 3];
                        BigDecimal totalVoters1 = previousElection.get(0)[0] != null ? (BigDecimal) previousElection.get(0)[0] : BigDecimal.ZERO;
                        BigDecimal totalPolled1 = previousElection.get(0)[1] != null ? (BigDecimal) previousElection.get(0)[1] : BigDecimal.ZERO;
                        mObj[0] = previousElection.get(0)[0] != null ? previousElection.get(0)[0] : 0;
                        mObj[1] = previousElection.get(0)[1] != null ? previousElection.get(0)[1] : 0;
                        mObj[2] = previousElection.get(0)[2] != null ? previousElection.get(0)[2] : "0%";
                        int h = 3;
                        for (int i = 0; i < totalPartiesCode.size(); i++) {
                            List<Object[]> perc = em.createNativeQuery("select sum(deo.total_party_voted) as er ,sum(deo.total_party_voted) from(select p.total_party_voted, p.total_polled from previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = " + yr + " and p.assembly_no = " + assemblyNo + " AND p.ward_no = (select w.no from ward w where w.id = " + wardId + " ) and pa.code = '" + totalPartiesCode.get(i) + "') as deo; ").getResultList();
                            mObj[h] = perc.isEmpty() || perc.get(0)[0] == null ? 0 : perc.get(0)[0];
                            Long partyTotal = perc.isEmpty() || perc.get(0)[1] == null ? 0 : Long.valueOf(perc.get(0)[1] + "");
                            overAllMap.put("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i), partyTotal + ((Long) overAllMap.get("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i))));
                            h++;
                        }
                        mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                        overAllMap.put("overAll_Voters_" + yr.toString(), totalVoters1.toBigInteger().add((BigInteger) overAllMap.get("overAll_Voters_" + yr.toString())));
                        overAllMap.put("overAll_Polled_" + yr.toString(), totalPolled1.toBigInteger().add((BigInteger) overAllMap.get("overAll_Polled_" + yr.toString())));
                    } else {
                        Object mObj[] = new Object[totalPartiesCode.size() + 3];
                        mObj[0] = 0;
                        mObj[1] = 0;
                        mObj[2] = "0%";
                        int h = 3;
                        for (int i = 0; i < totalPartiesCode.size(); i++) {
                            mObj[h] = "0";
                            h++;
                        }
                        mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                    }
                }
                count++;
            }
        }
        reportData.addAll(reportDataAnoe);
        reportDataMap.put("reportData", reportData);
        reportDataMap.put("mappp", mappp);
        reportDataMap.put("overAllMap", overAllMap);
        if (Objects.nonNull(overallResult) && overallResult.size() > 0) {
            Object[] objectResult = overallResult.get(0);
            Object[] objectResult1 = overallResult1.get(0);
            if (reportData.size() > 0) {
                objectResult[4] = totalPercentage / reportData.size();
            } else {
                objectResult[4] = 0;
            }
            objectResult[0] = objectResult1[0];
            objectResult[1] = objectResult1[1];
            objectResult[2] = objectResult1[2];
            objectResult[6] = voterVisited;
            objectResult[7] = votersResponded;
            reportDataMap.put("overallResult", objectResult);
        }
        reportDataMap.put("partyNames", allParties);
        reportDataMap.put("totalParties", allParties.size() + 1);
        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate(reportFile, "UTF-8"), reportDataMap);
        Document document = new Document(PageSize.A3.rotate(), 5, 5, 15, 15);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
        document.close();
        savePdfFile(os.toByteArray());
//        resultMap.put("pdf", os.toByteArray());
        saveExcelFile(isWardReport ? generateWardExcelReport(reportDataMap) : generateAssemblyExcelReport(reportDataMap));
//        resultMap.put("excel", isWardReport ? generateWardExcelReport(reportDataMap) : generateAssemblyExcelReport(reportDataMap));
        System.out.println("end = " + new Date());
        return resultMap;
    }

    public Map<String, Object> generateVotersMobileReport(String reportFor, Long id) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        Map<String, Object> resultMap = new HashMap<>();
        List<String[]> citizensData = new ArrayList<>();
        Map<String, Object> reportData = new HashMap<>();
        StringBuilder sqlQuery = new StringBuilder("SELECT c.assembly_no, c.ward_no, c.booth_no, CONCAT(CASE WHEN c.first_name IS NULL THEN '' ELSE c.first_name END ,' ', CASE WHEN c.family_name IS NULL THEN '' ELSE c.family_name END) AS name, c.voter_id, c.mobile , (select p.name from parliamentary_constituency p where p.id = (select a.parliamentary_constituency_id from assembly_constituency a where a.id =(select w.assembly_constituency_id from ward w where w.id = (select b.ward_id from booth b where b.id = c.booth_id)))) parliamentory FROM citizen c WHERE c.mobile IS NOT NULL AND c.booth_id");
        if (reportFor.equalsIgnoreCase("Booth")) {
            sqlQuery.append(" =").append(id).append(" ORDER BY parliamentory, c.assembly_no +0, c.ward_no +0, c.booth_no + 0, name ASC");
            reportData.put("reportFor", "Booth Number: " + boothRepository.findById(id).get().getNo());
        } else if (reportFor.equalsIgnoreCase("Ward")) {
            sqlQuery.append(" IN (select b.id FROM booth b WHERE b.ward_id = ").append(id).append(") ORDER BY parliamentory, c.assembly_no +0, c.ward_no +0, c.booth_no + 0, name ASC");
            reportData.put("reportFor", "Ward Number: " + wardRepository.findById(id).get().getNo());
        } else if (reportFor.equalsIgnoreCase("Assembly")) {
            sqlQuery.append(" IN (SELECT b.id FROM booth b WHERE b.ward_id in (SELECT w.id FROM ward w WHERE w.assembly_constituency_id = ").append(id).append(")) ORDER BY parliamentory, c.assembly_no +0, c.ward_no +0, c.booth_no + 0, name ASC");
            reportData.put("reportFor", "Assembly Constituency: " + assemblyConstituencyRepository.findById(id).get().getName());
        } else if (reportFor.equalsIgnoreCase("Parlimentory")) {
            sqlQuery.append(" IN (SELECT b.id FROM booth b WHERE b.ward_id IN (SELECT w.id FROM ward w WHERE w.assembly_constituency_id IN (SELECT a.id FROM assembly_constituency a WHERE a.parliamentary_constituency_id = ").append(id).append("))) ORDER BY parliamentory, c.assembly_no +0, c.ward_no +0, c.booth_no + 0, name ASC");
            reportData.put("reportFor", "Parlimentory Constituency: " + parliamentaryConstituencyRepository.findById(id).get().getName());
        } else {
            sqlQuery.setLength(0);
            reportData.put("isStateReport", "isStateReport");
            sqlQuery.append("SELECT c.assembly_no, c.ward_no, c.booth_no,CONCAT(CASE WHEN c.first_name IS NULL THEN '' ELSE c.first_name END ,' ', CASE WHEN c.family_name IS NULL THEN '' ELSE c.family_name END) AS name, c.voter_id, c.mobile, (select p.name from parliamentary_constituency p where p.id = (select a.parliamentary_constituency_id from assembly_constituency a where a.id =(select w.assembly_constituency_id from ward w where w.id = (select b.ward_id from booth b where b.id = c.booth_id)))) parliamentory FROM citizen c WHERE c.mobile IS NOT NULL AND c.state = (select s.state from state_assembly s where s.id = " + id + " ) ORDER BY parliamentory, c.assembly_no +0, c.ward_no +0, c.booth_no + 0, name ASC");
            reportData.put("reportFor", "State : " + stateAssemblyRepository.findById(id).get().getState());
        }
        //
        citizensData = em.createNativeQuery(sqlQuery.toString()).getResultList();
        if (citizensData.size() > 0) {
            reportData.put("reportData", citizensData);
        }
        Configuration configuration = prepareConfiguration();
        configuration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        Template template = configuration.getTemplate("voters_mobile_report.ftl");
        //freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");

        StringWriter stringWriter = new StringWriter();
        template.process(reportData, stringWriter);
//        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate("voters_mobile_report.ftl"), reportData);
        String attachmentBody = stringWriter.toString();
        Document document = new Document(PageSize.A4, 5, 5, 15, 15);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
        document.close();
        savePdfFile(os.toByteArray());
//        resultMap.put("pdf", os.toByteArray());
        saveExcelFile(generateVotersMobileReortExcel(reportData));
//        resultMap.put("excel", generateVotersMobileReortExcel(reportData));
        return resultMap;
    }

    private Configuration prepareConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        return configuration;
    }

    public Map<String, Object> getOverviewReportBy(Long id, Map<String, Object> reportData) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        List<Object[]> citizensData = new ArrayList<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        StateAssembly state = stateAssemblyRepository.findById((Long) reportData.get("stateId")).get();
        List<Party> parties = new ArrayList<>();
        List<String> allParties = new ArrayList<>();
        StringBuilder sb = new StringBuilder("");

        String sqlCommonQuery1 = " COUNT(*) as total_voters, COUNT(IF(c.voted = 1,1,null)) as voters_voted, CONCAT(ROUND(COUNT(IF(c.voted = 1,1,null)) / COUNT(*) * 100, 1), ' %') AS '% of total Voters' ";
//        String sqlCommonQuery1 = " COUNT(*) as total_voters, COUNT(IF(c.voted = 1,1,null)) as voters_voted, CONCAT((COUNT(IF(c.voted = 1,1,null)) / COUNT(*)) * 100, ' %') AS '% of total Voters', ";
//        String sqlCommonQuery2 = " COUNT(IF(c.voter_segmentation = 'A+',1,null)) AS 'A+', COUNT(IF(c.voter_segmentation = 'A',1,null)) AS A, COUNT(IF(c.voter_segmentation = 'B',1,null)) AS B, COUNT(IF(c.voter_segmentation = 'C',1,null)) AS C, COUNT(IF(c.voter_segmentation is null or (c.voter_segmentation not in('A+','A','B','C')) ,1,null)) AS Others from citizen c ";
        String sqlCommonQuery2 = "  from citizen c ";
        String sql = null;
        if (reportData.get("reportName").toString().equalsIgnoreCase("State Report")) {
            parties = partyRepository.findAllByStateAssembly(state);
            parties.forEach((action) -> {
                allParties.add(action.getCode());
                sb.append(", COUNT(IF(c.party_preference = '" + action.getCode().trim() + "',1,null)) as '" + action.getCode().trim() + "_support' ");
            });
            for (int i = 0; i < parties.size(); i++) {
                if (i == parties.size() - 1) {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' , COUNT(IF(c.voted = 1 and c.party_preference is null,1,null)) as not_disclosed ");
                } else {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' ");
                }
            }
            sql = "select p.name,        " + sqlCommonQuery1 + sb.toString() + sqlCommonQuery2 + " inner join booth b on b.id = c.booth_id inner join ward w on w.id = b.ward_id inner join assembly_constituency a on a.id = w.assembly_constituency_id inner join parliamentary_constituency p on p.id = a.parliamentary_constituency_id inner join district d on d.id = p.district_id inner join state_assembly s on s.id = d.state_assembly_id where s.id = " + reportData.get("stateId").toString() + " group by p.name order by c.assembly_no + 0 asc;";
        } else if (reportData.get("reportName").toString().equalsIgnoreCase("Parliamentary Constituency Report")) {
            parties = partyRepository.partyPreferenceByParliamentary(state.getId(), parliamentaryConstituencyRepository.findById(id).get().getDistrict().getId());
            parties.forEach((action) -> {
                allParties.add(action.getCode());
                sb.append(", COUNT(IF(c.party_preference = '" + action.getCode().trim() + "',1,null)) as '" + action.getCode().trim() + "_support' ");
            });

            for (int i = 0; i < parties.size(); i++) {
                if (i == parties.size() - 1) {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' , COUNT(IF(c.voted = 1 and c.party_preference is null,1,null)) as not_disclosed ");
                } else {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' ");
                }
            }
            sql = "select c.assembly_no, " + sqlCommonQuery1 + sb.toString() + sqlCommonQuery2 + reportData.get("sqlQueryForBoothIds").toString();

        } else if (reportData.get("reportName").toString().equalsIgnoreCase("Assembly Constituency Report")) {
            parties = partyRepository.partyPreferenceByAssembly(state.getId(), assemblyConstituencyRepository.findById(id).get().getParliamentaryConstituency().getDistrict().getId(), id);
            parties.forEach((action) -> {
                allParties.add(action.getCode());
                sb.append(", COUNT(IF(c.party_preference = '" + action.getCode().trim() + "',1,null)) as '" + action.getCode().trim() + "_support' ");
            });

            for (int i = 0; i < parties.size(); i++) {
                if (i == parties.size() - 1) {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' , COUNT(IF(c.voted = 1 and c.party_preference is null,1,null)) as not_disclosed ");
                } else {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' ");
                }
            }
            sql = "select c.ward_no,     " + sqlCommonQuery1 + sb.toString() + sqlCommonQuery2 + reportData.get("sqlQueryForBoothIds").toString();

        } else if (reportData.get("reportName").toString().equalsIgnoreCase("Ward Report")) {
            parties = partyRepository.partyPreferenceByAssembly(state.getId(), wardRepository.findById(id).get().getAssemblyConstituency().getParliamentaryConstituency().getDistrict().getId(), wardRepository.findById(id).get().getAssemblyConstituency().getId());
            parties.forEach((action) -> {
                allParties.add(action.getCode());
                sb.append(", COUNT(IF(c.party_preference = '" + action.getCode().trim() + "',1,null)) as '" + action.getCode().trim() + "_support' ");
            });
            for (int i = 0; i < parties.size(); i++) {
                if (i == parties.size() - 1) {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' , COUNT(IF(c.voted = 1 and c.party_preference is null,1,null)) as not_disclosed ");
                } else {
                    sb.append(", COUNT(IF(c.voted = 1 and c.party_preference = '" + parties.get(i).getCode().trim() + "',1,null)) as '" + parties.get(i).getCode().trim() + "_liveCount' ");
                }
            }
            sql = "select c.booth_no,    " + sqlCommonQuery1 + sb.toString() + sqlCommonQuery2 + reportData.get("sqlQueryForBoothIds").toString();

        }

        if (parties.size() > 0) {
            citizensData = em.createNativeQuery(sql).getResultList();
            reportData.put("reportData", citizensData);
        }
        reportData.put("partyNames", allParties);
        Map<String, Object> resultMap = new HashMap<>();
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate("last_day_counting.ftl", "UTF-8"), reportData);
        Document document = new Document(PageSize.A4.rotate(), 5, 5, 15, 15);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
        document.close();
        savePdfFile(os.toByteArray());
//        resultMap.put("pdf", os.toByteArray());
//        resultMap.put("excel", generateOverviewReportExcel(reportData));
        saveExcelFile(generateOverviewReportExcel(reportData));
        return resultMap;
    }

    private byte[] generateBoothExcelReport(Map<String, Object> boothReportData) throws IOException {

        Object[] overallResult = (Object[]) boothReportData.get("overallResult");
        List<Object[]> reportData = (List<Object[]>) boothReportData.get("reportData");
        HSSFWorkbook workbook = new HSSFWorkbook();
        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet("Booth Report");
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));
        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

        sheet.addMergedRegion(new CellRangeAddress(6, 6, 6, 9));//Voter Gradation
        //CREATE EXCEL FORMAT WITH DATA

        Row tableHederLineOne = sheet.createRow(6);
        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        tableHederLineOne.createCell(6).setCellValue("Voter gradation");
        Row tableHederLineTwo = sheet.createRow(7);
        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue("Booth #");
        tableHederLineTwo.createCell(2).setCellValue("Volunteer Mobile #");
        tableHederLineTwo.createCell(4).setCellValue("Voters Visited");
        tableHederLineTwo.createCell(5).setCellValue("Voters Responded");
        List<Map<String, Object>> segmentationsList = (List<Map<String, Object>>) boothReportData.get("segmentations");
        tableHederLineTwo.createCell(6).setCellValue(segmentationsList.get(0).get("label") + "");
        tableHederLineTwo.createCell(7).setCellValue(segmentationsList.get(1).get("label") + "");
        tableHederLineTwo.createCell(8).setCellValue(segmentationsList.get(2).get("label") + "");
        tableHederLineTwo.createCell(9).setCellValue(segmentationsList.get(3).get("label") + "");
        List<String> allParties = (List<String>) boothReportData.get("partyNames");
        int index = 10;
        for (int i = 0; i < allParties.size(); i++) {
            tableHederLineTwo.createCell(index).setCellValue(allParties.get(i));
            sheet.setColumnWidth(index, 3200);
            index++;
        }

        //OverAll Result
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, index));//Blank Row
        Row overAllResultRow = sheet.createRow(9);
        overAllResultRow.setHeight((short) (500));
        short overAllResultColor = 44;
        sheet.addMergedRegion(new CellRangeAddress(9, 9, 0, 3));//Overall Result 
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        setBorder(cellTextCenter, "Black");
        overAllResultRow.createCell(0).setCellValue("Overall Result");
        overAllResultRow.createCell(4).setCellValue(overallResult[0] + "");
        overAllResultRow.createCell(5).setCellValue(overallResult[1] + "");
        overAllResultRow.createCell(6).setCellValue(overallResult[2] + "");
        overAllResultRow.createCell(7).setCellValue(overallResult[3] + "");
        overAllResultRow.createCell(8).setCellValue(overallResult[4] + "");
        overAllResultRow.createCell(9).setCellValue(overallResult[5] + "");

        int indexOverallResult = 10;
        for (int i = 6; i < (6 + allParties.size()); i++) {
            overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[i] + "");
            indexOverallResult++;
        }
        //parties
        overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[overallResult.length - 1] + "");
        setCellFontStyleBold(overAllResultRow, cellTextCenter);

        //list of data
        sheet.addMergedRegion(new CellRangeAddress(10, 10, 0, index));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 11;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(resultObj[0] + "");
                noRecords.createCell(2).setCellValue(resultObj[1] + "");
                noRecords.createCell(4).setCellValue(resultObj[2] + "");
                noRecords.createCell(5).setCellValue(resultObj[3] + "");
                noRecords.createCell(6).setCellValue(resultObj[4] + "");
                noRecords.createCell(7).setCellValue(resultObj[5] + "");
                noRecords.createCell(8).setCellValue(resultObj[6] + "");
                noRecords.createCell(9).setCellValue(resultObj[7] + "");
                int indexReportData = 10;
                for (int j = 8; j < (8 + allParties.size()); j++) {
                    noRecords.createCell(indexReportData).setCellValue(resultObj[j] + "");
                    indexReportData++;
                }
                noRecords.createCell(indexReportData).setCellValue(resultObj[resultObj.length - 1] + "");
                setCellFontStyleBold(noRecords, cellTextCenter);
                rowNumber++;
            }
        } else {
            Row noRecords = sheet.createRow(10);
            noRecords.createCell(0).setCellValue("No Records Found");
        }
        //
        if (allParties.size() > 0) {
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 10, index));//Voters Responded
        }
        tableHederLineOne.createCell(10).setCellValue("Voters Responded");
        tableHederLineTwo.createCell(index).setCellValue("Not disclosed");
        tableHederLineOne.setHeight((short) 400);
        tableHederLineTwo.setHeight((short) 450);
        setBorder(titleCellStyle, "white");
        setCellFontStyleBold(tableHederLineOne, titleCellStyle);
        setCellFontStyleBold(tableHederLineTwo, titleCellStyle);
        sheet.setColumnWidth(0, 2000);//Sr.No
        sheet.setColumnWidth(1, 3000);//Booth #
        sheet.setColumnWidth(2, 4500);//Volunteer Mobile #
        sheet.setColumnWidth(3, 1200);//
        sheet.setColumnWidth(4, 3600);//Voters Visited
        sheet.setColumnWidth(5, 4800);//Voters Responded

        for (int i = 6; i < 10; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 6; i < 10; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 200);
        }

        sheet.setColumnWidth(index, 4000);//Not disclosed

        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, index));//Booth Report
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, index));//Booth Number
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Surveyed Data \nBooth Report");
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        Row reportHeader = sheet.createRow(1);
        reportHeader.createCell(0).setCellValue("Booth# " + boothReportData.get("boothNumber"));
        setCellFontStyleBold(reportHeader, cellData);
        reportHeader.setHeight((short) (550));

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, index));//Last Updated
        Row reportDate = sheet.createRow(4);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);

        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes();
        return null;
    }

    private byte[] generateWardExcelReport(Map<String, Object> reportDataMap) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();

        Object[] overallResult = (Object[]) reportDataMap.get("overallResult");
        List<Object[]> reportData = (List<Object[]>) reportDataMap.get("reportData");
        Map<String, Object> overAllMap = (Map<String, Object>) reportDataMap.get("overAllMap");
        Map<String, Object> mappp = (Map<String, Object>) reportDataMap.get("mappp");
        List<Object[]> totalYears = (List<Object[]>) reportDataMap.get("totalYears");
        List<Object[]> totalPartiesCode = (List<Object[]>) reportDataMap.get("totalPartiesCode");

        //Status Description
        HSSFCellStyle csRed = workbook.createCellStyle();
        HSSFCellStyle csOrange = workbook.createCellStyle();
        HSSFCellStyle csYellow = workbook.createCellStyle();
        HSSFCellStyle csGreen = workbook.createCellStyle();

        csRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet("Ward Report");
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));

        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        Row tableHederLineOne = sheet.createRow(11);
        Row tableHederLineTwo = sheet.createRow(12);

        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue("Booth #");

        int rowNumberIndex = 3;
        int rowTwoIndx = 2;
        for (Object yr : totalYears) {
            sheet.addMergedRegion(new CellRangeAddress(11, 11, rowNumberIndex, rowNumberIndex + totalPartiesCode.size() + 2));//Blank Row
            tableHederLineOne.createCell(rowNumberIndex).setCellValue(yr.toString());
            rowNumberIndex += totalPartiesCode.size() + 4;
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Voters");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Polled");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("% Polled");
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                tableHederLineTwo.createCell(rowTwoIndx).setCellValue(party.toString());
                rowTwoIndx += 1;
            }
        }
        rowNumberIndex = rowTwoIndx + 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Booths");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Houses");

        rowNumberIndex += 2;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Voters");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("% Voters Visited");

        rowNumberIndex += 2;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Voters Visited");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Voters Responded");
        List<String> allParties = (List<String>) reportDataMap.get("partyNames");
        int index = rowNumberIndex + 1;
        for (int i = 0; i < allParties.size(); i++) {
            tableHederLineTwo.createCell(index).setCellValue(allParties.get(i));
            index++;
        }

        //OverAll Result
        sheet.addMergedRegion(new CellRangeAddress(13, 13, 0, index));//Blank Row
        Row overAllResultRow = sheet.createRow(14);
        overAllResultRow.setHeight((short) (500));
        short overAllResultColor = 44;
        sheet.addMergedRegion(new CellRangeAddress(14, 14, 0, 2));//Overall Result
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        setBorder(cellTextCenter, "Black");
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        overAllResultRow.createCell(0).setCellValue("Overall Result");

        rowTwoIndx = 2;
        for (Object yr : totalYears) {
            rowTwoIndx += 1;
            BigInteger overAll_Voters = (BigInteger) overAllMap.get("overAll_Voters_" + yr.toString());
            BigInteger overAll_Polled = (BigInteger) overAllMap.get("overAll_Polled_" + yr.toString());
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Voters.doubleValue() > 999 ? twoDForm.format(overAll_Voters.doubleValue() / 1000) + "K" : overAll_Voters + "");//
            rowTwoIndx += 1;
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Polled.doubleValue() > 999 ? twoDForm.format(overAll_Polled.doubleValue() / 1000) + "K" : overAll_Polled + "");//
            rowTwoIndx += 1;
            if (overAll_Polled.longValue() > 0 && overAll_Voters.longValue() > 0) {
                overAllResultRow.createCell(rowTwoIndx).setCellValue(Math.round(overAll_Polled.doubleValue() * 100 / overAll_Voters.doubleValue()) + "%");
            } else {
                overAllResultRow.createCell(rowTwoIndx).setCellValue("0%");
            }
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                String key = ("overAll_Party_" + yr.toString() + "_" + party.toString());
                Long partySupport = new Long(overAllMap.get(key) + "");
                overAllResultRow.createCell(rowTwoIndx).setCellValue(partySupport > 999 ? twoDForm.format(partySupport.doubleValue() / 1000) + "K" : partySupport + "");//
                rowTwoIndx += 1;
            }
        }

        rowNumberIndex = rowTwoIndx + 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[0] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[1] + "");
        rowNumberIndex += 2;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[2] + "");
        String percentageOverAll = overallResult[4].toString();
        rowNumberIndex += 1;
        int colorIndexPercentage = rowNumberIndex;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(numberFormatter.format(overallResult[4]) + "%");
        if ((Double.parseDouble(percentageOverAll)) > 75) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csGreen);
        } else if ((Double.parseDouble(percentageOverAll)) > 50) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csYellow);
        } else if ((Double.parseDouble(percentageOverAll)) > 25) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csOrange);
        } else {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csRed);
        }
        rowNumberIndex += 2;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[6] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[7] + "");

        int indexOverallResult = rowNumberIndex + 1;
        for (int i = 8; i < (8 + allParties.size()); i++) {
            overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[i] + "");
            indexOverallResult++;
        }
        //parties
        overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[overallResult.length - 1] + "");
        setCellBackgroundForAssemblyAndWard(overAllResultRow, cellTextCenter, colorIndexPercentage);

        //list of data
        sheet.addMergedRegion(new CellRangeAddress(15, 15, 0, index));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 16;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(resultObj[2] + "");
                rowTwoIndx = 2;
                for (Object yr : totalYears) {
                    String key = ("key_" + yr.toString() + "_" + (i + 1));
                    Object mObj[] = (Object[]) mappp.get(key);
                    rowTwoIndx += 1;
                    BigInteger displayValue = new BigInteger(mObj[0] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    displayValue = new BigInteger(mObj[1] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    noRecords.createCell(rowTwoIndx).setCellValue(mObj[2] + "");
                    rowTwoIndx += 1;
                    int arrIndex = 3;
                    for (Object party : totalPartiesCode) {
                        displayValue = new BigInteger(mObj[arrIndex] + "");
                        noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                        rowTwoIndx += 1;
                        arrIndex++;
                    }
                }
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[3] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[4] + "");

                rowTwoIndx += 2;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[6] + "");
                String percentage = resultObj[7].toString();
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(numberFormatter.format(resultObj[7]) + "%");
                if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(75))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csGreen);
                } else if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(50))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csYellow);
                } else if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(25))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csOrange);
                } else {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csRed);
                }

                rowTwoIndx += 2;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[9] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[10] + "");

                int indexReportData = rowTwoIndx + 1;
                for (int j = 11; j < (11 + allParties.size()); j++) {
                    noRecords.createCell(indexReportData).setCellValue(resultObj[j] + "");
                    indexReportData++;
                }
                noRecords.createCell(indexReportData).setCellValue(resultObj[resultObj.length - 1] + "");
                rowNumber++;
                setCellBackgroundForAssemblyAndWard(noRecords, cellTextCenter, colorIndexPercentage);
            }
        } else {
            Row noRecords = sheet.createRow(15);
            noRecords.createCell(0).setCellValue("No Records Found");
        }

        sheet.addMergedRegion(new CellRangeAddress(6, 6, index - 1, index));//Status Description
        sheet.addMergedRegion(new CellRangeAddress(7, 7, index - 1, index));//< 25% voters visited
        sheet.addMergedRegion(new CellRangeAddress(8, 8, index - 1, index));//26% - 50% voters visited
        sheet.addMergedRegion(new CellRangeAddress(9, 9, index - 1, index));//51% - 75% voters visited
        sheet.addMergedRegion(new CellRangeAddress(10, 10, index - 1, index));//76% - 100% voters visited
        Row statusDesc = sheet.createRow(6);
        Row first = sheet.createRow(7);
        Row second = sheet.createRow(8);
        Row third = sheet.createRow(9);
        Row fourth = sheet.createRow(10);

        statusDesc.createCell(index - 1).setCellValue("Status Description");
        csRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        first.createCell(index - 1).setCellValue("< 25% voters visited");
        first.createCell(index - 2).setCellValue("");
        first.getCell(index - 2).setCellStyle(csRed);

        csOrange.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        second.createCell(index - 1).setCellValue("26% - 50% voters visited");
        second.createCell(index - 2).setCellValue("");
        second.getCell(index - 2).setCellStyle(csOrange);

        csYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        third.createCell(index - 1).setCellValue("51% - 75% voters visited");
        third.createCell(index - 2).setCellValue("");
        third.getCell(index - 2).setCellStyle(csYellow);

        csGreen.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        fourth.createCell(index - 1).setCellValue("76% - 100% voters visited");
        fourth.createCell(index - 2).setCellValue("");
        fourth.getCell(index - 2).setCellStyle(csGreen);

        //
        tableHederLineTwo.createCell(index).setCellValue("Not disclosed");
        setBorder(titleCellStyle, "White");
        tableHederLineTwo.setHeight((short) 500);
        setCellFontStyleBold(tableHederLineTwo, titleCellStyle);
        setCellFontStyleBold(tableHederLineOne, titleCellStyle);

        for (int i = 0; i < index; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < index; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 200);//Not disclosed
        }
        sheet.setColumnWidth(index, 5000);
        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, index));//Booth Report
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, index));//Booth Number
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Surveyed Data \n Ward Report");
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        Row reportHeader = sheet.createRow(1);
        reportHeader.createCell(0).setCellValue("Ward# " + reportDataMap.get("wardNumber"));

        setCellFontStyleBold(reportHeader, cellData);
        reportHeader.setHeight((short) (550));

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, index));//Last Updated
        Row reportDate = sheet.createRow(4);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);
        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes(); 
        return null;
    }

    private byte[] generateAssemblyExcelReport(Map<String, Object> reportDataMap) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();

        Object[] overallResult = (Object[]) reportDataMap.get("overallResult");
        List<Object[]> reportData = (List<Object[]>) reportDataMap.get("reportData");
        Map<String, Object> overAllMap = (Map<String, Object>) reportDataMap.get("overAllMap");
        Map<String, Object> mappp = (Map<String, Object>) reportDataMap.get("mappp");
        List<Object[]> totalYears = (List<Object[]>) reportDataMap.get("totalYears");
        List<Object[]> totalPartiesCode = (List<Object[]>) reportDataMap.get("totalPartiesCode");

        //Status Description
        HSSFCellStyle csRed = workbook.createCellStyle();
        HSSFCellStyle csOrange = workbook.createCellStyle();
        HSSFCellStyle csYellow = workbook.createCellStyle();
        HSSFCellStyle csGreen = workbook.createCellStyle();

        csRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet("Assembly Constituency Report");
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));

        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        Row tableHederLineOne = sheet.createRow(11);
        Row tableHederLineTwo = sheet.createRow(12);
        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue("Ward #");

        int rowNumberIndex = 3;
        int rowTwoIndx = 2;
        for (Object yr : totalYears) {
            sheet.addMergedRegion(new CellRangeAddress(11, 11, rowNumberIndex, rowNumberIndex + totalPartiesCode.size() + 2));//Blank Row
            tableHederLineOne.createCell(rowNumberIndex).setCellValue(yr.toString());
            rowNumberIndex += totalPartiesCode.size() + 4;
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Voters");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Polled");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("% Polled");
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                tableHederLineTwo.createCell(rowTwoIndx).setCellValue(party.toString());
                rowTwoIndx += 1;
            }
        }
        rowNumberIndex = rowTwoIndx + 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Booths");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Houses");

        rowNumberIndex += 2;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Voters");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("% Voters Visited");

        rowNumberIndex += 2;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Voters Visited");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Voters Responded");
        List<String> allParties = (List<String>) reportDataMap.get("partyNames");
        int index = rowNumberIndex + 1;
        for (int i = 0; i < allParties.size(); i++) {
            tableHederLineTwo.createCell(index).setCellValue(allParties.get(i));
            index++;
        }

        //OverAll Result
        sheet.addMergedRegion(new CellRangeAddress(13, 13, 0, index));//Blank Row
        Row overAllResultRow = sheet.createRow(14);
        overAllResultRow.setHeight((short) (500));
        short overAllResultColor = 44;
        sheet.addMergedRegion(new CellRangeAddress(14, 14, 0, 2));//Overall Result 
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        setBorder(cellTextCenter, "Black");
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        overAllResultRow.createCell(0).setCellValue("Overall Result");

        rowTwoIndx = 2;
        for (Object yr : totalYears) {
            rowTwoIndx += 1;
            BigInteger overAll_Voters = (BigInteger) overAllMap.get("overAll_Voters_" + yr.toString());
            BigInteger overAll_Polled = (BigInteger) overAllMap.get("overAll_Polled_" + yr.toString());
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Voters.doubleValue() > 999 ? twoDForm.format(overAll_Voters.doubleValue() / 1000) + "K" : overAll_Voters + "");//
            rowTwoIndx += 1;
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Polled.doubleValue() > 999 ? twoDForm.format(overAll_Polled.doubleValue() / 1000) + "K" : overAll_Polled + "");//
            rowTwoIndx += 1;
            if (overAll_Polled.longValue() > 0 && overAll_Voters.longValue() > 0) {
                overAllResultRow.createCell(rowTwoIndx).setCellValue(Math.round(overAll_Polled.doubleValue() * 100 / overAll_Voters.doubleValue()) + "%");
            } else {
                overAllResultRow.createCell(rowTwoIndx).setCellValue("0%");
            }
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                String key = ("overAll_Party_" + yr.toString() + "_" + party.toString());
                Long partySupport = new Long(overAllMap.get(key) + "");
                overAllResultRow.createCell(rowTwoIndx).setCellValue(partySupport > 999 ? twoDForm.format(partySupport.doubleValue() / 1000) + "K" : partySupport + "");//
                rowTwoIndx += 1;
            }
        }

        rowNumberIndex = rowTwoIndx + 1;

        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[0] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[1] + "");

        rowNumberIndex += 2;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[2] + "");

        String percentageOverAll = overallResult[4].toString();
        rowNumberIndex += 1;
        int colorIndexPercentage = rowNumberIndex;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(numberFormatter.format(overallResult[4]) + "%");
        if ((Double.parseDouble(percentageOverAll)) > 75) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csGreen);
        } else if ((Double.parseDouble(percentageOverAll)) > 50) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csYellow);
        } else if ((Double.parseDouble(percentageOverAll)) > 25) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csOrange);
        } else {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csRed);
        }

        rowNumberIndex += 2;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[6] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[7] + "");

        int indexOverallResult = rowNumberIndex + 1;
        for (int i = 8; i < (8 + allParties.size()); i++) {
            overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[i] + "");
            indexOverallResult++;
        }
        //parties
        overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[overallResult.length - 1] + "");
        setCellBackgroundForAssemblyAndWard(overAllResultRow, cellTextCenter, colorIndexPercentage);

        //list of data
        sheet.addMergedRegion(new CellRangeAddress(15, 15, 0, index));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 16;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(resultObj[0] + "");

                rowTwoIndx = 2;
                for (Object yr : totalYears) {
                    String key = ("key_" + yr.toString() + "_" + (i + 1));
                    Object mObj[] = (Object[]) mappp.get(key);
                    rowTwoIndx += 1;
                    BigInteger displayValue = new BigInteger(mObj[0] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    displayValue = new BigInteger(mObj[1] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    noRecords.createCell(rowTwoIndx).setCellValue(mObj[2] + "");
                    rowTwoIndx += 1;
                    int arrIndex = 3;
                    for (Object party : totalPartiesCode) {
                        displayValue = new BigInteger(mObj[arrIndex] + "");
                        noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                        rowTwoIndx += 1;
                        arrIndex++;
                    }
                }
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[3] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[4] + "");

                rowTwoIndx += 2;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[6] + "");
                String percentage = resultObj[7].toString();
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(numberFormatter.format(resultObj[7]) + "%");
                if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(75))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csGreen);
                } else if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(50))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csYellow);
                } else if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(25))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csOrange);
                } else {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csRed);
                }

                rowTwoIndx += 2;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[9] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[10] + "");

                int indexReportData = rowTwoIndx + 1;
                for (int j = 11; j < (11 + allParties.size()); j++) {
                    noRecords.createCell(indexReportData).setCellValue(resultObj[j] + "");
                    indexReportData++;
                }
                noRecords.createCell(indexReportData).setCellValue(resultObj[resultObj.length - 1] + "");
                rowNumber++;
                setCellBackgroundForAssemblyAndWard(noRecords, cellTextCenter, colorIndexPercentage);
            }
        } else {
            Row noRecords = sheet.createRow(15);
            noRecords.createCell(0).setCellValue("No Records Found");
        }

        sheet.addMergedRegion(new CellRangeAddress(6, 6, index - 1, index));//Status Description
        sheet.addMergedRegion(new CellRangeAddress(7, 7, index - 1, index));//< 25% voters visited
        sheet.addMergedRegion(new CellRangeAddress(8, 8, index - 1, index));//26% - 50% voters visited
        sheet.addMergedRegion(new CellRangeAddress(9, 9, index - 1, index));//51% - 75% voters visited
        sheet.addMergedRegion(new CellRangeAddress(10, 10, index - 1, index));//76% - 100% voters visited
        Row statusDesc = sheet.createRow(6);
        Row first = sheet.createRow(7);
        Row second = sheet.createRow(8);
        Row third = sheet.createRow(9);
        Row fourth = sheet.createRow(10);

        statusDesc.createCell(index - 1).setCellValue("Status Description");
        csRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        first.createCell(index - 1).setCellValue("< 25% voters visited");
        first.createCell(index - 2).setCellValue("");
        first.getCell(index - 2).setCellStyle(csRed);

        csOrange.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        second.createCell(index - 1).setCellValue("26% - 50% voters visited");
        second.createCell(index - 2).setCellValue("");
        second.getCell(index - 2).setCellStyle(csOrange);

        csYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        third.createCell(index - 1).setCellValue("51% - 75% voters visited");
        third.createCell(index - 2).setCellValue("");
        third.getCell(index - 2).setCellStyle(csYellow);

        csGreen.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        fourth.createCell(index - 1).setCellValue("76% - 100% voters visited");
        fourth.createCell(index - 2).setCellValue("");
        fourth.getCell(index - 2).setCellStyle(csGreen);

        tableHederLineTwo.createCell(index).setCellValue("Not disclosed");
        setBorder(titleCellStyle, "White");
        tableHederLineTwo.setHeight((short) 500);
        setCellFontStyleBold(tableHederLineTwo, titleCellStyle);
        setCellFontStyleBold(tableHederLineOne, titleCellStyle);

        for (int i = 0; i < index; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < index; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 200);
        }
        sheet.setColumnWidth(index, 5000);//Not disclosed
        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, index));//Booth Report
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, index));//Booth Number
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Surveyed Data \nAssembly Constituency Report");
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        Row reportHeader = sheet.createRow(1);
        reportHeader.createCell(0).setCellValue("Assembly Constituency# " + reportDataMap.get("assemblyNumber"));
        setCellFontStyleBold(reportHeader, cellData);
        reportHeader.setHeight((short) (550));

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, index));//Last Updated
        Row reportDate = sheet.createRow(4);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);
        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes(); 
        return null;
    }

    private byte[] generateParliamentoryExcelReport(Map<String, Object> reportDataMap) throws IOException {

        HSSFWorkbook workbook = new HSSFWorkbook();

        Object[] overallResult = (Object[]) reportDataMap.get("overallResult");
        List<Object[]> reportData = (List<Object[]>) reportDataMap.get("reportData");
        Map<String, Object> overAllMap = (Map<String, Object>) reportDataMap.get("overAllMap");
        Map<String, Object> mappp = (Map<String, Object>) reportDataMap.get("mappp");
        List<Object[]> totalYears = (List<Object[]>) reportDataMap.get("totalYears");
        List<Object[]> totalPartiesCode = (List<Object[]>) reportDataMap.get("totalPartiesCode");
        //Status Description
        HSSFCellStyle csRed = workbook.createCellStyle();
        HSSFCellStyle csOrange = workbook.createCellStyle();
        HSSFCellStyle csYellow = workbook.createCellStyle();
        HSSFCellStyle csGreen = workbook.createCellStyle();

        csRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        csRed.setAlignment(HorizontalAlignment.CENTER);
        csOrange.setAlignment(HorizontalAlignment.CENTER);
        csYellow.setAlignment(HorizontalAlignment.CENTER);
        csGreen.setAlignment(HorizontalAlignment.CENTER);

        csRed.setVerticalAlignment(VerticalAlignment.CENTER);
        csOrange.setVerticalAlignment(VerticalAlignment.CENTER);
        csYellow.setVerticalAlignment(VerticalAlignment.CENTER);
        csGreen.setVerticalAlignment(VerticalAlignment.CENTER);

        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet("Parliamentary Constituency Report");
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));

        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        Row tableHederLineOne = sheet.createRow(11);
        Row tableHederLineTwo = sheet.createRow(12);

        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue("A.C. Number#");
        tableHederLineTwo.createCell(2).setCellValue("A.C. Name#");

        int rowNumberIndex = 4;
        int rowTwoIndx = 3;
        for (Object yr : totalYears) {
            sheet.addMergedRegion(new CellRangeAddress(11, 11, rowNumberIndex, rowNumberIndex + totalPartiesCode.size() + 2));//Blank Row
            tableHederLineOne.createCell(rowNumberIndex).setCellValue(yr.toString());
            rowNumberIndex += totalPartiesCode.size() + 4;
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Voters");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Polled");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("% Polled");
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                tableHederLineTwo.createCell(rowTwoIndx).setCellValue(party.toString());
                rowTwoIndx += 1;
            }
        }
        rowNumberIndex = rowTwoIndx + 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Houses");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Voters");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Booths");
        rowNumberIndex += 1;
        sheet.addMergedRegion(new CellRangeAddress(12, 12, rowNumberIndex, rowNumberIndex + 3));//Booth Status
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Booth Status");

        rowNumberIndex += 5;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Voters Visited");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Voters Responded");
        List<String> allParties = (List<String>) reportDataMap.get("partyNames");
        int index = rowNumberIndex + 1;
        for (int i = 0; i < allParties.size(); i++) {
            tableHederLineTwo.createCell(index).setCellValue(allParties.get(i));
            index++;
        }

        //OverAll Result
        sheet.addMergedRegion(new CellRangeAddress(13, 13, 0, index));//Blank Row
        Row overAllResultRow = sheet.createRow(14);
        overAllResultRow.setHeight((short) (500));
        short overAllResultColor = 44;
        sheet.addMergedRegion(new CellRangeAddress(14, 14, 0, 3));//Overall Result 
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        setBorder(cellTextCenter, "Black");
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        overAllResultRow.createCell(0).setCellValue("Overall Result");

        rowTwoIndx = 3;
        for (Object yr : totalYears) {
            rowTwoIndx += 1;
            BigInteger overAll_Voters = (BigInteger) overAllMap.get("overAll_Voters_" + yr.toString());
            BigInteger overAll_Polled = (BigInteger) overAllMap.get("overAll_Polled_" + yr.toString());
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Voters.doubleValue() > 999 ? twoDForm.format(overAll_Voters.doubleValue() / 1000) + "K" : overAll_Voters + "");//
            rowTwoIndx += 1;
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Polled.doubleValue() > 999 ? twoDForm.format(overAll_Polled.doubleValue() / 1000) + "K" : overAll_Polled + "");//
            rowTwoIndx += 1;
            if (overAll_Polled.longValue() > 0 && overAll_Voters.longValue() > 0) {
                overAllResultRow.createCell(rowTwoIndx).setCellValue(Math.round(overAll_Polled.doubleValue() * 100 / overAll_Voters.doubleValue()) + "%");
            } else {
                overAllResultRow.createCell(rowTwoIndx).setCellValue("0%");
            }
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                String key = ("overAll_Party_" + yr.toString() + "_" + party.toString());
                Long partySupport = new Long(overAllMap.get(key) + "");
                overAllResultRow.createCell(rowTwoIndx).setCellValue(partySupport > 999 ? twoDForm.format(partySupport.doubleValue() / 1000) + "K" : partySupport + "");//
                rowTwoIndx += 1;
            }
        }

        rowNumberIndex = rowTwoIndx + 1;

        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[0] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[1] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[2] + "");
        rowNumberIndex += 1;
        int startIndex = rowNumberIndex;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[3] + "");
        overAllResultRow.getCell(rowNumberIndex).setCellStyle(csGreen);
        rowNumberIndex += 1;

        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[4] + "");
        overAllResultRow.getCell(rowNumberIndex).setCellStyle(csYellow);
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[5] + "");
        overAllResultRow.getCell(rowNumberIndex).setCellStyle(csOrange);
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[6] + "");
        overAllResultRow.getCell(rowNumberIndex).setCellStyle(csRed);
        rowNumberIndex += 2;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[7] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[8] + "");
        int indexOverallResult = rowNumberIndex + 1;
        for (int i = 9; i < (9 + allParties.size()); i++) {
            overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[i] + "");
            indexOverallResult++;
        }
        //parties
        overAllResultRow.createCell(indexOverallResult).setCellValue(overallResult[overallResult.length - 1] + "");
        setCellBackgroundForParliamentory(overAllResultRow, cellTextCenter, startIndex);

        //list of data
        sheet.addMergedRegion(new CellRangeAddress(15, 15, 0, index));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 16;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(resultObj[0] + "");
                noRecords.createCell(2).setCellValue(resultObj[1] + "");

                rowTwoIndx = 3;
                for (Object yr : totalYears) {
                    String key = ("key_" + yr.toString() + "_" + (i + 1));
                    Object mObj[] = (Object[]) mappp.get(key);
                    rowTwoIndx += 1;
                    BigInteger displayValue = new BigInteger(mObj[0] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    displayValue = new BigInteger(mObj[1] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    noRecords.createCell(rowTwoIndx).setCellValue(mObj[2] + "");
                    rowTwoIndx += 1;
                    int arrIndex = 3;
                    for (Object party : totalPartiesCode) {
                        displayValue = new BigInteger(mObj[arrIndex] + "");
                        noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                        rowTwoIndx += 1;
                        arrIndex++;
                    }
                }
                rowTwoIndx += 1;

                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[2] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[3] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[4] + "");

                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[5] + "");
                noRecords.getCell(rowTwoIndx).setCellStyle(csGreen);
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[6] + "");
                noRecords.getCell(rowTwoIndx).setCellStyle(csYellow);
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[7] + "");
                noRecords.getCell(rowTwoIndx).setCellStyle(csOrange);
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[8] + "");
                noRecords.getCell(rowTwoIndx).setCellStyle(csRed);

                rowTwoIndx += 2;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[9] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[10] + "");

                int indexReportData = rowTwoIndx + 1;
                for (int j = 11; j < (11 + allParties.size()); j++) {
                    noRecords.createCell(indexReportData).setCellValue(resultObj[j] + "");
                    indexReportData++;
                }
                noRecords.createCell(indexReportData).setCellValue(resultObj[resultObj.length - 1] + "");
                rowNumber++;
                setCellBackgroundForParliamentory(noRecords, cellTextCenter, startIndex);
            }
        } else {
            Row noRecords = sheet.createRow(15);
            noRecords.createCell(0).setCellValue("No Records Found");
        }

        sheet.addMergedRegion(new CellRangeAddress(6, 6, index - 1, index));//Status Description
        sheet.addMergedRegion(new CellRangeAddress(7, 7, index - 1, index));//< 25% voters visited
        sheet.addMergedRegion(new CellRangeAddress(8, 8, index - 1, index));//26% - 50% voters visited
        sheet.addMergedRegion(new CellRangeAddress(9, 9, index - 1, index));//51% - 75% voters visited
        sheet.addMergedRegion(new CellRangeAddress(10, 10, index - 1, index));//76% - 100% voters visited
        Row statusDesc = sheet.createRow(6);
        Row first = sheet.createRow(7);
        Row second = sheet.createRow(8);
        Row third = sheet.createRow(9);
        Row fourth = sheet.createRow(10);

        statusDesc.createCell(index - 1).setCellValue("Status Description");
        csRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        first.createCell(index - 1).setCellValue("< 25% voters visited");
        first.createCell(index - 2).setCellValue("");
        first.getCell(index - 2).setCellStyle(csRed);

        csOrange.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        second.createCell(index - 1).setCellValue("26% - 50% voters visited");
        second.createCell(index - 2).setCellValue("");
        second.getCell(index - 2).setCellStyle(csOrange);

        csYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        third.createCell(index - 1).setCellValue("51% - 75% voters visited");
        third.createCell(index - 2).setCellValue("");
        third.getCell(index - 2).setCellStyle(csYellow);

        csGreen.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        fourth.createCell(index - 1).setCellValue("76% - 100% voters visited");
        fourth.createCell(index - 2).setCellValue("");
        fourth.getCell(index - 2).setCellStyle(csGreen);

        //
        tableHederLineTwo.createCell(index).setCellValue("Not disclosed");
        setBorder(titleCellStyle, "White");
        tableHederLineTwo.setHeight((short) 500);
        setCellFontStyleBold(tableHederLineTwo, titleCellStyle);
        setCellFontStyleBold(tableHederLineOne, titleCellStyle);

        for (int i = 0; i < index; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < index; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 200);
        }
        sheet.setColumnWidth(startIndex, 2500);//green
        sheet.setColumnWidth(startIndex + 1, 2500);//Yellow
        sheet.setColumnWidth(startIndex + 2, 2500);//orange
        sheet.setColumnWidth(startIndex + 3, 2500);//Red
        sheet.setColumnWidth(index, 5000);//Not disclosed
        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, index));//Booth Report
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, index));//Booth Number
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Surveyed Data \nParliamentary Constituency Report");
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        Row reportHeader = sheet.createRow(1);
        reportHeader.createCell(0).setCellValue("District# " + reportDataMap.get("districtName"));
        setCellFontStyleBold(reportHeader, cellData);
        reportHeader.setHeight((short) (550));

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, index));//Last Updated
        Row reportDate = sheet.createRow(4);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);
        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes();
        return null;
    }

    private byte[] generateVotersMobileReortExcel(Map<String, Object> reportMap) throws IOException {
        List<Object[]> reportData = (List<Object[]>) reportMap.get("reportData");
        HSSFWorkbook workbook = new HSSFWorkbook();
        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet("Voters Mobile Report");
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));

        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        Row tableHederLineTwo = sheet.createRow(7);
        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue("Parliamentory Name#");
        tableHederLineTwo.createCell(2).setCellValue("Assembly No#");
        tableHederLineTwo.createCell(3).setCellValue("Ward #");
        tableHederLineTwo.createCell(4).setCellValue("Booth #");
        tableHederLineTwo.createCell(6).setCellValue("Voter Name");
        tableHederLineTwo.createCell(7).setCellValue("Voter Id");
        tableHederLineTwo.createCell(8).setCellValue("Voter Mobile");
        short overAllResultColor = 44;
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        setBorder(cellTextCenter, "Black");
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 8));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 9;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(Objects.isNull(resultObj[6]) ? "N/A" : resultObj[6] + "");
                noRecords.createCell(2).setCellValue(Objects.isNull(resultObj[0]) ? "N/A" : resultObj[0] + "");
                noRecords.createCell(3).setCellValue(Objects.isNull(resultObj[1]) ? "N/A" : resultObj[1] + "");
                noRecords.createCell(4).setCellValue(Objects.isNull(resultObj[2]) ? "N/A" : resultObj[2] + "");
                noRecords.createCell(6).setCellValue(resultObj[3] + "");
                noRecords.createCell(7).setCellValue(resultObj[4] + "");
                noRecords.createCell(8).setCellValue(resultObj[5] + "");
                setCellFontStyleBold(noRecords, cellTextCenter);
                rowNumber++;
            }
        } else {
            Row noRecords = sheet.createRow(10);
            noRecords.createCell(0).setCellValue("No Records Found");
        }
        tableHederLineTwo.setHeight((short) 450);
        setBorder(titleCellStyle, "white");
        setCellFontStyleBold(tableHederLineTwo, titleCellStyle);

        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < 8; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 200);//Not disclosed
        }
        sheet.setColumnWidth(8, 8000);
        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));//Booth Report
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, 8));//Booth Number
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Voters Mobile Report");
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        Row reportHeader = sheet.createRow(1);
        reportHeader.createCell(0).setCellValue((String) reportMap.get("reportFor"));
        setCellFontStyleBold(reportHeader, cellData);
        reportHeader.setHeight((short) (550));

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 8));//Last Updated
        Row reportDate = sheet.createRow(4);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);
        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes();
        return null;
    }

    private void setCellFontStyleBold(Row row, HSSFCellStyle cellStyle) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null) {
                row.getCell(i).setCellStyle(cellStyle);
            }
        }
    }

    private void setCellBackgroundForAssemblyAndWard(Row row, HSSFCellStyle cellStyle, int colorIndex) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null) {
                // % House Visited column index 5
                if (i == colorIndex) {
                    CellStyle cellStyle1 = row.getCell(i).getCellStyle();
                    cellStyle1.setAlignment(HorizontalAlignment.CENTER);
                    cellStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
                    row.getCell(i).setCellStyle(cellStyle1);
                } else {
                    row.getCell(i).setCellStyle(cellStyle);
                }
            }
        }
    }

    private void setCellBackgroundForParliamentory(Row row, HSSFCellStyle cellStyle, int startIndex) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null) {
                //green orange yellow red color check
                if (!(i == startIndex || i == startIndex + 1 || i == startIndex + 2 || i == startIndex + 3)) {
                    row.getCell(i).setCellStyle(cellStyle);
                }
            }
        }
    }

    private void setBorder(HSSFCellStyle cellStyle, String color) {
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        if (color.equalsIgnoreCase("White")) {
            cellStyle.setTopBorderColor(IndexedColors.WHITE.getIndex());
            cellStyle.setBottomBorderColor(IndexedColors.WHITE.getIndex());
            cellStyle.setRightBorderColor(IndexedColors.WHITE.getIndex());
            cellStyle.setLeftBorderColor(IndexedColors.WHITE.getIndex());
        }
    }

    private byte[] generateOverviewReportExcel(Map<String, Object> reportMap) throws IOException {
        List<Object[]> reportData = (List<Object[]>) reportMap.get("reportData");
        HSSFWorkbook workbook = new HSSFWorkbook();
        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet(reportMap.get("reportName").toString());
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFCellStyle csGray = workbook.createCellStyle();
        csGray.setAlignment(HorizontalAlignment.CENTER);
        csGray.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        csGray.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csGray.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
        setBorder(csGray, "Black");

        HSSFCellStyle csBlack = workbook.createCellStyle();
        csBlack.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csBlack.setAlignment(HorizontalAlignment.CENTER);
        csBlack.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        csBlack.setFillForegroundColor(IndexedColors.BLACK.getIndex());

        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        csBlack.setFont(font);

        setBorder(csBlack, "White");

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));

        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        Row tableHederLineTwo = sheet.createRow(7);
        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue(reportMap.get("tableFirstColumnName").toString());
        tableHederLineTwo.getCell(1).setCellStyle(csGray);
        tableHederLineTwo.createCell(2).setCellValue("Total Voters");
        tableHederLineTwo.getCell(2).setCellStyle(csGray);
        tableHederLineTwo.createCell(3).setCellValue("Voters Voted");
        tableHederLineTwo.createCell(4).setCellValue("% of total Voters");
        tableHederLineTwo.getCell(4).setCellStyle(csGray);

        List<String> allParties = (List<String>) reportMap.get("partyNames");
        int index = 5;
        for (int i = 0; i < allParties.size(); i++) {
            tableHederLineTwo.createCell(index).setCellValue(allParties.get(i));
            tableHederLineTwo.getCell(index).setCellStyle(csGray);
            sheet.setColumnWidth(index, 3200);
            index++;
        }
        for (int i = 0; i < allParties.size(); i++) {
            tableHederLineTwo.createCell(index).setCellValue(allParties.get(i));
            sheet.setColumnWidth(index, 3200);
            index++;
        }
        tableHederLineTwo.createCell(index).setCellValue("Not Disclosed");
//        tableHederLineTwo.getCell(index).setCellStyle(csGray);
//        List<Map<String, Object>> segmentationsList = (List<Map<String, Object>>) reportMap.get("segmentations");
//        tableHederLineTwo.createCell(index).setCellValue(segmentationsList.get(0).get("label") + " % of total voted");
//        tableHederLineTwo.getCell(index).setCellStyle(csBlack);
//        index += 1;
//        tableHederLineTwo.createCell(index).setCellValue(segmentationsList.get(1).get("label") + " % of total voted");
//        tableHederLineTwo.getCell(index).setCellStyle(csBlack);
//        index += 1;
//        tableHederLineTwo.createCell(index).setCellValue(segmentationsList.get(2).get("label") + " % of total voted");
//        tableHederLineTwo.getCell(index).setCellStyle(csBlack);
//        index += 1;
//        tableHederLineTwo.createCell(index).setCellValue(segmentationsList.get(3).get("label") + " % of total voted");
//        tableHederLineTwo.getCell(index).setCellStyle(csBlack);
//        index += 1;
//        tableHederLineTwo.createCell(index).setCellValue("Others % of total voted");
//        tableHederLineTwo.getCell(index).setCellStyle(csBlack);
        short overAllResultColor = 44;
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        setBorder(cellTextCenter, "Black");
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, index));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 9;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(resultObj[0] + "");
                noRecords.createCell(2).setCellValue(resultObj[1] + "");
                noRecords.createCell(3).setCellValue(resultObj[2] + "");
                noRecords.createCell(4).setCellValue(resultObj[3] + "");
                noRecords.getCell(1).setCellStyle(csGray);
                noRecords.getCell(2).setCellStyle(csGray);
                noRecords.getCell(4).setCellStyle(csGray);
                int indexReportData = 5;
                for (int j = 4; j < (4 + allParties.size()); j++) {
                    noRecords.createCell(indexReportData).setCellValue(resultObj[j] + "");
                    noRecords.getCell(indexReportData).setCellStyle(csGray);
                    indexReportData++;
                }
                for (int j = 4 + allParties.size(); j < (4 + allParties.size() + allParties.size()); j++) {
                    noRecords.createCell(indexReportData).setCellValue(resultObj[j] + "");
                    indexReportData++;
                }
                noRecords.createCell(indexReportData).setCellValue(resultObj[resultObj.length - 1] + "");
                setCellFontStyleBoldForOverViewReport(noRecords, cellTextCenter, 4 + allParties.size());
                rowNumber++;
            }
        } else {
            Row noRecords = sheet.createRow(8);
            noRecords.createCell(0).setCellValue("No Records Found");
        }
        tableHederLineTwo.setHeight((short) 450);
        setBorder(titleCellStyle, "white");
        setCellFontStyleBoldForOverViewReport(tableHederLineTwo, titleCellStyle, 4 + allParties.size());
        sheet.setColumnWidth(0, 2000);//Sr.No
        sheet.setColumnWidth(1, 6500);//Assembly #
        sheet.setColumnWidth(2, 3500);//Total Voters
        sheet.setColumnWidth(3, 3500);//Voters Voted
        sheet.setColumnWidth(4, 4500);//% of total Voters
        for (int i = 5; i < index; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 5; i < index; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 200);
        }
        sheet.setColumnWidth(index, 5600);//Others % of total voted
        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, index));//Booth Report
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, index));//Booth Number
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Last Day Counting \n" + reportMap.get("reportName").toString());
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        Row reportHeader = sheet.createRow(1);
        reportHeader.createCell(0).setCellValue((String) reportMap.get("reportFor"));
        setCellFontStyleBold(reportHeader, cellData);
        reportHeader.setHeight((short) (550));
        Row tableHederLineOneOn = sheet.createRow(6);
        if (allParties.size() > 1) {
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 5, allParties.size() + 4));//Survey Data
            sheet.addMergedRegion(new CellRangeAddress(6, 6, allParties.size() + 5, index));//Live Voting
            tableHederLineOneOn.createCell(5).setCellValue("Survey Data");
            tableHederLineOneOn.createCell(allParties.size() + 5).setCellValue("Live Voting");
        } else if (allParties.size() < 1) {
            tableHederLineOneOn.createCell(5).setCellValue("Live Voting");
        } else {
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 6, 7));//Live Voting
            tableHederLineOneOn.createCell(5).setCellValue("Survey Data");
            tableHederLineOneOn.createCell(6).setCellValue("Live Voting");
        }
        tableHederLineOneOn.getCell(5).setCellStyle(csGray);
        tableHederLineOneOn.getCell(allParties.size() + 5).setCellStyle(titleCellStyle);

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, index));//Last Updated
        Row reportDate = sheet.createRow(4);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);
        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes(); 
        return null;
    }

    private Object generateStateExcelReport(Map<String, Object> stateReportData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Map<String, Object> generateComplaintsSummaryReport(Long wardId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        Map<String, Object> resultMap = new HashMap<>();
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        Map<String, Object> reportDataMap = new HashMap<>();
        Ward wardEntity = wardRepository.findById(wardId).get();
        reportDataMap.put("wardNumber", wardEntity.getNo());
        List<Long> wardIds = new ArrayList<>();
        wardIds.add(wardId);
        StringBuilder wardIdsIn = new StringBuilder("(");
        wardIdsIn.append(StringUtils.join(wardIds, ','));
        if (wardIds.isEmpty()) {
            wardIdsIn.append("'')");
        } else {
            wardIdsIn.append(")");
        }
        List<Object[]> totalYears = em.createNativeQuery("SELECT CAST(p.year AS char) FROM previous_election p WHERE p.ward_no IN (select w.no from ward w where w.id in " + wardIdsIn + " ) GROUP BY p.year ORDER BY p.year ASC LIMIT 2;").getResultList();
        List<Object[]> totalPartiesCode = em.createNativeQuery("SELECT pa.code FROM previous_election p inner join party pa on pa.id = p.party_id where p.ward_no IN (select w.no from ward w where w.id in " + wardIdsIn + ") group by pa.code order by pa.code asc;").getResultList();
        reportDataMap.put("totalYears", totalYears);
        reportDataMap.put("totalPartiesCode", totalPartiesCode);
        List<String> boothArray = new ArrayList<>();
        StringBuilder boothIds = new StringBuilder("(");

        List<Object[]> overallResult = em.createNativeQuery("select b.id, b.`no` as boothNo, count(*) as total_complaints, (count(if(c.`status` = 'Resolved' , 1, null)) * 100 / count(*)) as per_resolved, count(if(c.`status` = 'Resolved' , 1, null)) as resolved, count(if(c.`status` not in ('Resolved','Under Review','Out Of Scope','Ignore','Inprogress') or c.`status` is null, 1, null)) as No_status, count(if(c.`status` = 'Inprogress' , 1, null)) as Inprogress, count(if(c.`status` = 'Under Review'  , 1, null)) as Under_Review, count(if(c.`status` = 'Out Of Scope' , 1, null)) as Out_Of_Scope, count(if(c.`status` = 'Ignore' , 1, null)) as Ignored from complaint c inner join citizen citi on citi.id = c.citizen_id or citi.voter_id = c.voter_id inner join booth b on b.id = citi.booth_id where b.ward_id in " + wardIdsIn).getResultList();
        List<Object[]> reportData = em.createNativeQuery("select b.id, b.`no` as boothNo, count(*) as total_complaints, (count(if(c.`status` = 'Resolved' , 1, null)) * 100 / count(*)) as per_resolved, count(if(c.`status` = 'Resolved' , 1, null)) as resolved, count(if(c.`status` not in ('Resolved','Under Review','Out Of Scope','Ignore','Inprogress') or c.`status` is null, 1, null)) as No_status, count(if(c.`status` = 'Inprogress' , 1, null)) as Inprogress, count(if(c.`status` = 'Under Review'  , 1, null)) as Under_Review, count(if(c.`status` = 'Out Of Scope' , 1, null)) as Out_Of_Scope, count(if(c.`status` = 'Ignore' , 1, null)) as Ignored from complaint c inner join citizen citi on citi.id = c.citizen_id or citi.voter_id = c.voter_id inner join booth b on b.id = citi.booth_id where b.ward_id in " + wardIdsIn + " group by b.no order by b.`no`+ 0").getResultList();

        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            for (Object[] action : reportData) {
                boothArray.add(action[0] + "");
            }
            boothIds.append(StringUtils.join(boothArray, ','));
            reportDataMap.put("reportData", reportData);
        }
        if (boothArray.isEmpty()) {
            boothIds.append("'')");
        } else {
            boothIds.append(")");
        }
//        List<Object[]> reportDataAnoe = new ArrayList<>();
//        reportDataAnoe = em.createNativeQuery("select b.id, b.`no` as boothNo, 0, '0.0000%', 0, 0, 0, 0, 0,0 from citizen c  inner join booth b on b.id = c.booth_id where b.ward_id in " + wardIdsIn + " and b.id not in " + boothIds + "  group by b.no order by b.`no`+ 0").getResultList();
//        reportDataAnoe.forEach(action -> {
//            boothArray.add(action[1] + "");
//        });
//        reportData.addAll(reportDataAnoe);

        int count = 1;
        Map<String, Object> mappp = new HashMap<>();
        Map<String, Object> overAllMap = new HashMap<>();
        BigInteger totalVoters = BigInteger.ZERO;
        BigInteger totalPolled = BigInteger.ZERO;
        for (Object yr : totalYears) {
            overAllMap.put("overAll_Voters_" + yr.toString(), totalVoters);
            overAllMap.put("overAll_Polled_" + yr.toString(), totalPolled);
            for (int i = 0; i < totalPartiesCode.size(); i++) {
                overAllMap.put("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i), 0l);
            }
        }

        for (String boothId : boothArray) {
            for (Object yr : totalYears) {
                String myQuery = "SELECT p.total_voters, p.total_polled, CONCAT(ROUND((p.total_polled * 100) / p.total_voters,0) , '%') as percentage FROM previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = '" + yr.toString() + "' and p.assembly_no = (select a.no from assembly_constituency a where a.id = (select w.assembly_constituency_id from ward w where w.id = (select b.ward_id from booth b where b.id = " + boothId + "))) AND p.ward_no = (select w.no from ward w where w.id = (select b.ward_id from booth b where b.id = " + boothId + ")) AND p.booth_no = (select bt.no from booth bt where bt.id = " + boothId + " ) limit 1;";
                List<Object[]> previousElection = em.createNativeQuery(myQuery).getResultList();
                if (previousElection.size() > 0) {
                    Object mObj[] = new Object[totalPartiesCode.size() + 3];
                    totalVoters = previousElection.get(0)[0] != null ? (BigInteger) previousElection.get(0)[0] : BigInteger.ZERO;
                    totalPolled = previousElection.get(0)[1] != null ? (BigInteger) previousElection.get(0)[1] : BigInteger.ZERO;
                    mObj[0] = previousElection.get(0)[0] != null ? previousElection.get(0)[0] : 0;
                    mObj[1] = previousElection.get(0)[1] != null ? previousElection.get(0)[1] : 0;
                    mObj[2] = previousElection.get(0)[2] != null ? previousElection.get(0)[2] : "0%";
                    int h = 3;
                    for (int i = 0; i < totalPartiesCode.size(); i++) {
                        List<Object[]> perc = em.createNativeQuery("select p.total_party_voted as percentage, p.total_party_voted from previous_election p INNER JOIN party pa ON pa.id = p.party_id WHERE p.year = " + yr + " and p.assembly_no = (select a.no from assembly_constituency a where a.id = (select w.assembly_constituency_id from ward w where w.id = (select b.ward_id from booth b where b.id = " + boothId + "))) AND p.ward_no = (select w.no from ward w where w.id = (select b.ward_id from booth b where b.id = " + boothId + ")) AND p.booth_no = (select bt.no from booth bt where bt.id = " + boothId + ") and pa.code = '" + totalPartiesCode.get(i) + "' ").getResultList();
                        mObj[h] = perc.isEmpty() || perc.get(0)[1] == null ? 0 : perc.get(0)[0];
                        Long partyTotal = perc.isEmpty() || perc.get(0)[1] == null ? 0 : Long.valueOf(perc.get(0)[1] + "");
                        overAllMap.put("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i), partyTotal + ((Long) overAllMap.get("overAll_Party_" + yr.toString() + "_" + totalPartiesCode.get(i))));
                        h++;
                    }
                    mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                    overAllMap.put("overAll_Voters_" + yr.toString(), totalVoters.add((BigInteger) overAllMap.get("overAll_Voters_" + yr.toString())));
                    overAllMap.put("overAll_Polled_" + yr.toString(), totalPolled.add((BigInteger) overAllMap.get("overAll_Polled_" + yr.toString())));
                } else {
                    Object mObj[] = new Object[totalPartiesCode.size() + 3];
                    mObj[0] = 0;
                    mObj[1] = 0;
                    mObj[2] = "0%";
                    int h = 3;
                    for (int i = 0; i < totalPartiesCode.size(); i++) {
                        mObj[h] = "0";
                        h++;
                    }
                    mappp.put("key_" + yr.toString() + "_" + count + "", mObj);
                }
            }
            count++;
        }

        reportDataMap.put("mappp", mappp);
        reportDataMap.put("overAllMap", overAllMap);
        if (Objects.nonNull(overallResult) && overallResult.size() > 0) {
            Object[] objectResult = overallResult.get(0);
            objectResult[3] = Objects.isNull(objectResult[3]) ? 0 : objectResult[3];
            reportDataMap.put("overallResult", objectResult);
        }
        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate("complaints_summary_report.ftl", "UTF-8"), reportDataMap);
        Document document = new Document(PageSize.A3.rotate(), 5, 5, 15, 15);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
        document.close();
        savePdfFile(os.toByteArray());
//        resultMap.put("pdf", os.toByteArray());
//        resultMap.put("excel", generateComplaintsSummaryExcelReport(reportDataMap));
        saveExcelFile(generateComplaintsSummaryExcelReport(reportDataMap));
        return resultMap;
    }

    private byte[] generateComplaintsSummaryExcelReport(Map<String, Object> reportDataMap) throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();

        Object[] overallResult = (Object[]) reportDataMap.get("overallResult");
        List<Object[]> reportData = (List<Object[]>) reportDataMap.get("reportData");
        Map<String, Object> overAllMap = (Map<String, Object>) reportDataMap.get("overAllMap");
        Map<String, Object> mappp = (Map<String, Object>) reportDataMap.get("mappp");
        List<Object[]> totalYears = (List<Object[]>) reportDataMap.get("totalYears");
        List<Object[]> totalPartiesCode = (List<Object[]>) reportDataMap.get("totalPartiesCode");

        //Status Description
        HSSFCellStyle csRed = workbook.createCellStyle();
        HSSFCellStyle csOrange = workbook.createCellStyle();
        HSSFCellStyle csYellow = workbook.createCellStyle();
        HSSFCellStyle csGreen = workbook.createCellStyle();

        csRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csYellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        csGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet("Complaints Summary Report");
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));

        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");

        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        Row tableHederLineOne = sheet.createRow(12);
        Row tableHederLineTwo = sheet.createRow(13);

        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue("Booth #");

        int rowNumberIndex = 3;
        int rowTwoIndx = 2;
        for (Object yr : totalYears) {
            sheet.addMergedRegion(new CellRangeAddress(12, 12, rowNumberIndex, rowNumberIndex + totalPartiesCode.size() + 2));//Blank Row
            tableHederLineOne.createCell(rowNumberIndex).setCellValue("Election Results " + yr.toString());
            rowNumberIndex += totalPartiesCode.size() + 4;
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Voters");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("Total Polled");
            rowTwoIndx += 1;
            tableHederLineTwo.createCell(rowTwoIndx).setCellValue("% Polled");
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                tableHederLineTwo.createCell(rowTwoIndx).setCellValue(party.toString());
                rowTwoIndx += 1;
            }
        }
        rowNumberIndex = rowTwoIndx + 1;

        sheet.addMergedRegion(new CellRangeAddress(12, 12, rowNumberIndex, rowNumberIndex + 7));//Blank Row
        tableHederLineOne.createCell(rowNumberIndex).setCellValue("Status of complaints received till date");

        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Total Complaints");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("% resolved");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Resolved");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("No Status");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("In progress");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Under Review");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Out Of Scope");
        rowNumberIndex += 1;
        tableHederLineTwo.createCell(rowNumberIndex).setCellValue("Ignore");

        //OverAll Result
        int index = rowNumberIndex;
        sheet.addMergedRegion(new CellRangeAddress(14, 14, 0, index));//Blank Row
        Row overAllResultRow = sheet.createRow(15);
        overAllResultRow.setHeight((short) (500));
        short overAllResultColor = 44;
        sheet.addMergedRegion(new CellRangeAddress(15, 15, 0, 2));//Overall Result
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        setBorder(cellTextCenter, "Black");
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        overAllResultRow.createCell(0).setCellValue("Overall Result");

        rowTwoIndx = 2;
        for (Object yr : totalYears) {
            rowTwoIndx += 1;
            BigInteger overAll_Voters = (BigInteger) overAllMap.get("overAll_Voters_" + yr.toString());
            BigInteger overAll_Polled = (BigInteger) overAllMap.get("overAll_Polled_" + yr.toString());
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Voters.doubleValue() > 999 ? twoDForm.format(overAll_Voters.doubleValue() / 1000) + "K" : overAll_Voters + "");//
            rowTwoIndx += 1;
            overAllResultRow.createCell(rowTwoIndx).setCellValue(overAll_Polled.doubleValue() > 999 ? twoDForm.format(overAll_Polled.doubleValue() / 1000) + "K" : overAll_Polled + "");//
            rowTwoIndx += 1;
            if (overAll_Polled.longValue() > 0 && overAll_Voters.longValue() > 0) {
                overAllResultRow.createCell(rowTwoIndx).setCellValue(Math.round(overAll_Polled.doubleValue() * 100 / overAll_Voters.doubleValue()) + "%");
            } else {
                overAllResultRow.createCell(rowTwoIndx).setCellValue("0%");
            }
            rowTwoIndx += 1;
            for (Object party : totalPartiesCode) {
                String key = ("overAll_Party_" + yr.toString() + "_" + party.toString());
                Long partySupport = new Long(overAllMap.get(key) + "");
                overAllResultRow.createCell(rowTwoIndx).setCellValue(partySupport > 999 ? twoDForm.format(partySupport.doubleValue() / 1000) + "K" : partySupport + "");//
                rowTwoIndx += 1;
            }
        }

        rowNumberIndex = rowTwoIndx + 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[2] + "");
        rowNumberIndex += 1;
        String percentageOverAll = overallResult[3].toString();
        int colorIndexPercentage = rowNumberIndex;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(numberFormatter.format(overallResult[3]) + "%");
        if ((Double.parseDouble(percentageOverAll)) > 75) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csGreen);
        } else if ((Double.parseDouble(percentageOverAll)) > 50) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csYellow);
        } else if ((Double.parseDouble(percentageOverAll)) > 25) {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csOrange);
        } else {
            overAllResultRow.getCell(rowNumberIndex).setCellStyle(csRed);
        }
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[4] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[5] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[6] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[7] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[8] + "");
        rowNumberIndex += 1;
        overAllResultRow.createCell(rowNumberIndex).setCellValue(overallResult[9] + "");

        setCellBackgroundForAssemblyAndWard(overAllResultRow, cellTextCenter, colorIndexPercentage);

        //list of data
        sheet.addMergedRegion(new CellRangeAddress(16, 16, 0, index));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 17;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(resultObj[1] + "");
                rowTwoIndx = 2;
                for (Object yr : totalYears) {
                    String key = ("key_" + yr.toString() + "_" + (i + 1));
                    Object mObj[] = (Object[]) mappp.get(key);
                    rowTwoIndx += 1;
                    BigInteger displayValue = new BigInteger(mObj[0] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    displayValue = new BigInteger(mObj[1] + "");
                    noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                    rowTwoIndx += 1;
                    noRecords.createCell(rowTwoIndx).setCellValue(mObj[2] + "");
                    rowTwoIndx += 1;
                    int arrIndex = 3;
                    for (Object party : totalPartiesCode) {
                        displayValue = new BigInteger(mObj[arrIndex] + "");
                        noRecords.createCell(rowTwoIndx).setCellValue(displayValue.longValue() > 999 ? twoDForm.format(displayValue.doubleValue() / 1000) + "K" : displayValue + "");//
                        rowTwoIndx += 1;
                        arrIndex++;
                    }
                }
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[2] + "");
                rowTwoIndx += 1;
                String percentage = resultObj[3].toString();
                noRecords.createCell(rowTwoIndx).setCellValue(numberFormatter.format(resultObj[3]) + "%");
                if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(75))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csGreen);
                } else if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(50))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csYellow);
                } else if (new BigDecimal(percentage).compareTo(new BigDecimal(String.valueOf(25))) > 0) {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csOrange);
                } else {
                    noRecords.getCell(rowTwoIndx).setCellStyle(csRed);
                }

                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[4] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[5] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[6] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[7] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[8] + "");
                rowTwoIndx += 1;
                noRecords.createCell(rowTwoIndx).setCellValue(resultObj[9] + "");

                rowNumber++;
                setCellBackgroundForAssemblyAndWard(noRecords, cellTextCenter, colorIndexPercentage);
            }
        } else {
            Row noRecords = sheet.createRow(16);
            noRecords.createCell(0).setCellValue("No Records Found");
        }

        sheet.addMergedRegion(new CellRangeAddress(6, 6, index - 1, index));//Status Description
        sheet.addMergedRegion(new CellRangeAddress(7, 7, index - 1, index));//< 25% total complaints resolved
        sheet.addMergedRegion(new CellRangeAddress(8, 8, index - 1, index));//26% - 50% total complaints resolved
        sheet.addMergedRegion(new CellRangeAddress(9, 9, index - 1, index));//51% - 75% total complaints resolved
        sheet.addMergedRegion(new CellRangeAddress(10, 10, index - 1, index));//76% - 100% total complaints resolved
        Row statusDesc = sheet.createRow(6);
        Row first = sheet.createRow(7);
        Row second = sheet.createRow(8);
        Row third = sheet.createRow(9);
        Row fourth = sheet.createRow(10);

        statusDesc.createCell(index - 1).setCellValue("Status Description");
        csRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        first.createCell(index - 1).setCellValue("< 25% total complaints resolved");
        first.createCell(index - 2).setCellValue("");
        first.getCell(index - 2).setCellStyle(csRed);

        csOrange.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        second.createCell(index - 1).setCellValue("26% - 50% total complaints resolved");
        second.createCell(index - 2).setCellValue("");
        second.getCell(index - 2).setCellStyle(csOrange);

        csYellow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        third.createCell(index - 1).setCellValue("51% - 75% total complaints resolved");
        third.createCell(index - 2).setCellValue("");
        third.getCell(index - 2).setCellStyle(csYellow);

        csGreen.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        fourth.createCell(index - 1).setCellValue("76% - 100% total complaints resolved");
        fourth.createCell(index - 2).setCellValue("");
        fourth.getCell(index - 2).setCellStyle(csGreen);

        //
        setBorder(titleCellStyle, "White");
        tableHederLineTwo.setHeight((short) 500);
        setCellFontStyleBold(tableHederLineTwo, titleCellStyle);
        setCellFontStyleBold(tableHederLineOne, titleCellStyle);

        for (int i = 0; i < index; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < index; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 200);//Not disclosed
        }
        sheet.setColumnWidth(index, 5000);
        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, index));//Booth Report
        sheet.addMergedRegion(new CellRangeAddress(1, 2, 0, index));//Booth Number
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Complaints Summary Report");
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        Row reportHeader = sheet.createRow(1);
        reportHeader.createCell(0).setCellValue("Ward# " + reportDataMap.get("wardNumber"));

        setCellFontStyleBold(reportHeader, cellData);
        reportHeader.setHeight((short) (550));

        sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, index));//Last Updated
        Row reportDate = sheet.createRow(4);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);
        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes();
        return null;
    }

    public Map<String, Object> getClientWiseReport() throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
        Map<String, Object> resultMap = new HashMap<>();
        List<Object[]> reportDataObject = new ArrayList<>();
        Map<String, Object> reportData = new HashMap<>();
        String sqlQuery = "SELECT s.state, (SELECT count(*) FROM volunteer v WHERE v.status != 'Deleted' and v.assembly_constituency_id in (SELECT a.id FROM assembly_constituency a WHERE a.parliamentary_constituency_id in (SELECT p.id from parliamentary_constituency p WHERE p.district_id in(SELECT d.id FROM district d WHERE d.state_assembly_id = s.id)))) AS  total_volunteer, (SELECT count(*) FROM user_detail ud WHERE ud.state_assembly_id = s.id) AS  total_users, (SELECT count(*) FROM citizen c where c.state = s.state and c.last_login is not null) AS total_citizens, (SELECT count(*) FROM citizen c where c.state = s.state) AS total_voters from state_assembly s ORDER BY s.state";
        reportDataObject = em.createNativeQuery(sqlQuery).getResultList();
        if (reportDataObject.size() > 0) {
            reportData.put("reportData", reportDataObject);
        }
        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate("client_wise_report.ftl", "UTF-8"), reportData);
        Document document = new Document(PageSize.A4, 5, 5, 15, 15);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, os);
        document.open();
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
        document.close();
        savePdfFile(os.toByteArray());
//        resultMap.put("pdf", os.toByteArray());
//        resultMap.put("excel", generateClientWiseReport(reportData));
        saveExcelFile(generateClientWiseReport(reportData));
        return resultMap;
    }

    private byte[] generateClientWiseReport(Map<String, Object> reportMap) throws IOException {
        List<Object[]> reportData = (List<Object[]>) reportMap.get("reportData");
        HSSFWorkbook workbook = new HSSFWorkbook();
        short colorIndex = 45;
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFSheet sheet = workbook.createSheet("Client Wise Report");
        HSSFFont hSSFFont = workbook.createFont();
        hSSFFont.setBold(true);
        hSSFFont.setFontHeightInPoints((short) (10));

        HSSFFont hSSFFontHead = workbook.createFont();
        hSSFFontHead.setBold(true);
        hSSFFontHead.setFontHeightInPoints((short) (16));

        HSSFFont titleTextFont = workbook.createFont();
        titleTextFont.setBold(true);
        titleTextFont.setFontHeightInPoints((short) (10));
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont(titleTextFont);
        titleCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(hSSFFont);

        HSSFCellStyle cellTextCenter = workbook.createCellStyle();
        cellTextCenter.setAlignment(HorizontalAlignment.CENTER);

        HSSFCellStyle cellData = workbook.createCellStyle();
        cellData.setFont(hSSFFontHead);
        cellData.setAlignment(HorizontalAlignment.CENTER);
        cellData.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        SimpleDateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        palette.setColorAtIndex(colorIndex, (byte) 255, (byte) 192, (byte) 0);
        titleCellStyle.setFillForegroundColor(colorIndex);

        Row tableHederLineTwo = sheet.createRow(4);
        tableHederLineTwo.createCell(0).setCellValue("Sr.No");
        tableHederLineTwo.createCell(1).setCellValue("State Name");
        tableHederLineTwo.createCell(3).setCellValue("Total Volunteer");
        tableHederLineTwo.createCell(4).setCellValue("Total Users");
        tableHederLineTwo.createCell(5).setCellValue("Total Citizens");
        tableHederLineTwo.createCell(6).setCellValue("Total Voters");
        short overAllResultColor = 44;
        palette.setColorAtIndex(overAllResultColor, (byte) 255, (byte) 242, (byte) 204);
        cellTextCenter.setFont(titleTextFont);
        cellTextCenter.setFillForegroundColor(overAllResultColor);
        cellTextCenter.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellTextCenter.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        setBorder(cellTextCenter, "Black");
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 6));//Blank Row
        if (Objects.nonNull(reportData) && reportData.size() > 0) {
            int rowNumber = 6;
            for (int i = 0; i < reportData.size(); i++) {
                Object[] resultObj = reportData.get(i);
                Row noRecords = sheet.createRow(rowNumber);
                noRecords.createCell(0).setCellValue((i + 1) + "");
                noRecords.createCell(1).setCellValue(Objects.isNull(resultObj[0]) ? "N/A" : resultObj[0] + "");
                noRecords.createCell(3).setCellValue(Objects.isNull(resultObj[1]) ? "N/A" : resultObj[1] + "");
                noRecords.createCell(4).setCellValue(Objects.isNull(resultObj[2]) ? "N/A" : resultObj[2] + "");
                noRecords.createCell(5).setCellValue(Objects.isNull(resultObj[3]) ? "N/A" : resultObj[3] + "");
                noRecords.createCell(6).setCellValue(resultObj[4] + "");
                setCellFontStyleBold(noRecords, cellTextCenter);
                rowNumber++;
            }
        } else {
            Row noRecords = sheet.createRow(7);
            noRecords.createCell(0).setCellValue("No Records Found");
        }
        tableHederLineTwo.setHeight((short) 450);
        setBorder(titleCellStyle, "white");
        setCellFontStyleBold(tableHederLineTwo, titleCellStyle);

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
        for (int i = 0; i < 6; i++) {
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1500);
        }
        sheet.setColumnWidth(6, 8000);
        //create header
        short headerColor = 43;
        palette.setColorAtIndex(headerColor, (byte) 255, (byte) 217, (byte) 102);
        cellData.setFillForegroundColor(headerColor);
        cellData.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));
        Row reportDetailsRow = sheet.createRow(0);
        reportDetailsRow.createCell(0).setCellValue("Client Wise Report");
        setCellFontStyleBold(reportDetailsRow, cellData);
        reportDetailsRow.setHeight((short) (700));

        sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 6));//Last Updated
        Row reportDate = sheet.createRow(2);
        reportDate.createCell(0).setCellValue("Last Updated: " + df.format(new Date()));
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        setCellFontStyleBold(reportDate, cellStyle);
        File excel = new File("ReportExcel.xlsx");
        try {
            FileOutputStream excelFos = new FileOutputStream(excel);
            workbook.write(excelFos);
            excelFos.flush();
            excelFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
        //return workbook.getBytes();
        return null;
    }

    public void savePdfFile(byte[] byteData) {
        File pdf = new File("ReportPDF.pdf");
        if (pdf.exists()) {
            pdf.delete();
        }
        pdf = new File("ReportPDF.pdf");
        try {
            FileOutputStream pdfFos = new FileOutputStream(pdf);
            pdfFos.write(byteData);
            pdfFos.close();
        } catch (Exception e) {
            throw new IllegalStateException("unable to upload file");
        }
    }

    public void saveExcelFile(byte[] byteData) {
//        File excel = new File("ReportExcel.xlsx");
//        try {
//            FileOutputStream excelFos = new FileOutputStream(excel);
//            excelFos.write(byteData);
//            excelFos.flush(); excelFos.close();
//        } catch (Exception e) {
//            throw new IllegalStateException("unable to upload file");
//        }
    }

    private void setCellFontStyleBoldForOverViewReport(Row row, HSSFCellStyle cellStyle, int index) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null && (i == 0 || i == 3)) {
                row.getCell(i).setCellStyle(cellStyle);
            } else if (row.getCell(i) != null && i != 1 && i != 2 && i != 4 && i > index) {
                row.getCell(i).setCellStyle(cellStyle);
            }
        }
    }

}

//    public Map<String, Object> getStateReport(Long stateId) throws MalformedTemplateNameException, ParseException, IOException, DocumentException, TemplateException {
//        Map<String, Object> resultMap = new HashMap<>();
//        freemarkerMailConfiguration.setClassForTemplateLoading(this.getClass(), "/templates/reports/");
//        Map<String, Object> stateReportData = new HashMap<>();
//        List<String> districtArray = new ArrayList<>();
//        StringBuilder districtIds = new StringBuilder("(");
//        StateAssembly stateEntity = stateAssemblyRepository.findById(stateId).get();
//        stateReportData.put("stateName", stateEntity.getState());
//        List<Party> parties = partyRepository.findAllByStateAssembly(stateEntity);
//
//        StringBuilder sb = new StringBuilder();
//        StringBuilder sball = new StringBuilder("(");
//        List<String> allParties = new ArrayList<>();
//        parties.forEach((action) -> {
//            sb.append("COUNT(IF(c.responded_status = 'responded' AND c.party_preference = '" + action.getCode().trim() + "',1,null)) as " + action.getCode().trim() + "_support,");
//        });
//        for (int i = 0; i < parties.size(); i++) {
//            allParties.add(parties.get(i).getCode());
//            if (i == parties.size() - 1) {
//                sball.append("'" + parties.get(i).getCode().trim() + "')");
//            } else {
//                sball.append("'" + parties.get(i).getCode().trim() + "',");
//            }
//        }
//        if (allParties.size() > 0) {
//            sb.append(" COUNT(IF(c.responded_status = 'responded' AND c.party_preference not in " + sball.toString() + ",1,null)) as other_support,");
//        } else {
//            sb.append(" COUNT(IF(c.responded_status = 'responded' AND c.party_preference is not null,1,null)) as other_support,");
//        }
//        allParties.add("Others");
//        stateReportData.put("partyNames", allParties);
//
//        BigInteger voterVisited = BigInteger.ZERO;
//        BigInteger votersResponded = BigInteger.ZERO;
//        BigInteger greenCount = BigInteger.ZERO;
//        BigInteger yellowCount = BigInteger.ZERO;
//        BigInteger orangeCount = BigInteger.ZERO;
//        BigInteger redCount = BigInteger.ZERO;
//
//        List<Object[]> overallResult = em.createNativeQuery("SELECT result.totalHouses, result.total_voters, result.totalBooths, CAST(NULL AS char) AS green,CAST(NULL AS char) AS yellow,CAST(NULL AS char) AS orange,CAST(NULL AS char) AS red, COUNT(*) voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,null)) AS voter_responded, " + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed FROM citizen c INNER JOIN state_assembly s ON s.state = c.state INNER JOIN district d ON d.state_assembly_id = s.id, (SELECT COUNT(DISTINCT ci.address) AS totalHouses, COUNT(DISTINCT ci.booth_id) AS totalBooths, count(*) AS total_voters FROM citizen ci WHERE ci.state = '" + stateEntity.getState() + "' ) AS result WHERE c.responded_status IS NOT NULL AND c.state = '" + stateEntity.getState() + "'").getResultList();
//        List<Object[]> reportData = em.createNativeQuery("SELECT d.id, d.name, result.houses, result.voters, result.booths, CAST(null AS char) AS green, CAST(null AS char) AS yellow, CAST(null AS char) AS orange, CAST(null AS char) AS red, COUNT(*) as voter_visited1, COUNT(IF(c.responded_status = 'responded', 1 ,null)) AS voter_responded," + sb.toString() + " COUNT(IF(c.responded_status = 'responded' and c.party_preference is null,1,null)) as not_disclosed from citizen c INNER JOIN booth b on b.id = c.booth_id\n"
//                + "INNER JOIN ward w on w.id = b.ward_id INNER JOIN assembly_constituency a on a.id = w.assembly_constituency_id INNER JOIN parliamentary_constituency p on p.id = a.parliamentary_constituency_id INNER JOIN district d on d.id = p.district_id JOIN ((SELECT COUNT(DISTINCT c1.booth_id) booths, COUNT(DISTINCT c1.address) houses, COUNT(*) voters, p1.district_id as did from citizen c1 INNER JOIN booth b1 on b1.id = c1.booth_id INNER JOIN ward w1 on w1.id = b1.ward_id INNER JOIN assembly_constituency a1 on a1.id = w1.assembly_constituency_id INNER JOIN parliamentary_constituency p1 on p1.id = a1.parliamentary_constituency_id group by p1.district_id)) result on (result.did = d.id) where c.responded_status is not null and d.state_assembly_id = " + stateId + " group by d.id;").getResultList();
//
//        if (Objects.nonNull(reportData) && reportData.size() > 0) {
//            for (Object[] reportObject : reportData) {
//                districtArray.add(reportObject[0] + "");
//                voterVisited = voterVisited.add(reportObject[9] != null ? (BigInteger) reportObject[9] : BigInteger.ZERO);
//                votersResponded = votersResponded.add(reportObject[10] != null ? (BigInteger) reportObject[10] : BigInteger.ZERO);
//                List<Object[]> boothStatus = em.createNativeQuery("select pp.booth_work, count(*) as total_booths from ( select case when p >= 0 and p <=  0.25 then 25 when p > 0.25 and p <= 0.50 then 50 when p > 0.50 and p <= 0.75 then 75 when p > 0.75 then 100 end as booth_work from (select count(*)/(b.total_booths) p from citizen a , (select d.id as districtId, count(distinct(citi.booth_id)) as total_booths from citizen citi inner join state_assembly s on s.state = citi.state  inner join district d on d.state_assembly_id = s.id) b where a.responded_status is not null and b.districtId = " + reportObject[0] + " group by booth_id) cc) pp group by pp.booth_work;").getResultList();
//                BigInteger green = BigInteger.ZERO;
//                BigInteger yellow = BigInteger.ZERO;
//                BigInteger orange = BigInteger.ZERO;
//                for (Object[] obj : boothStatus) {
//                    if (obj[0].equals(100)) {
//                        green = green.add((BigInteger) obj[1]);
//                        greenCount = greenCount.add((BigInteger) obj[1]);
//                    } else if (obj[0].equals(75)) {
//                        yellow = yellow.add((BigInteger) obj[1]);
//                        yellowCount = yellowCount.add((BigInteger) obj[1]);
//                    } else if (obj[0].equals(50)) {
//                        orange = orange.add((BigInteger) obj[1]);
//                        orangeCount = orangeCount.add((BigInteger) obj[1]);
//                    }
//                }
//                reportObject[5] = green;
//                reportObject[6] = yellow;
//                reportObject[7] = orange;
//                reportObject[8] = ((BigInteger) reportObject[4]).subtract(green.add(orange).add(yellow));
//                redCount = redCount.add(((BigInteger) reportObject[8]));
//            }
//            districtIds.append(StringUtils.join(districtArray, ','));
//        }
//        if (districtArray.isEmpty()) {
//            districtIds.append("'')");
//        } else {
//            districtIds.append(")");
//        }
//        List<Object[]> allAssemblyData = em.createNativeQuery("SELECT d.id, d.name, result.houses, result.voters, result.booths, 0 AS green, 0 AS yellow, 0 AS orange, 0 AS red, 0 AS voter_visited, 0 AS voters_responded, " + sb.toString() + "  0 AS not_disclosed from citizen c INNER JOIN booth b on b.id = c.booth_id INNER JOIN ward w on w.id = b.ward_id INNER JOIN assembly_constituency a on a.id = w.assembly_constituency_id INNER JOIN parliamentary_constituency p on p.id = a.parliamentary_constituency_id INNER JOIN district d on d.id = p.district_id JOIN ((SELECT COUNT(DISTINCT c1.booth_id) booths, COUNT(DISTINCT c1.address) houses, COUNT(*) voters, p1.district_id as did from citizen c1 INNER JOIN booth b1 on b1.id = c1.booth_id INNER JOIN ward w1 on w1.id = b1.ward_id INNER JOIN assembly_constituency a1 on a1.id = w1.assembly_constituency_id INNER JOIN parliamentary_constituency p1 on p1.id = a1.parliamentary_constituency_id group by p1.district_id)) result on (result.did = d.id) where c.responded_status is not null and d.state_assembly_id = " + stateId + " and d.id not in " + districtIds + " group by d.id;").getResultList();
//        reportData.addAll(allAssemblyData);
//
//        stateReportData.put("reportData", reportData);
//
//        if (Objects.nonNull(overallResult) && overallResult.size() > 0) {
//            Object[] objectResult = overallResult.get(0);
//            objectResult[3] = greenCount;
//            objectResult[4] = yellowCount;
//            objectResult[5] = orangeCount;
//            objectResult[6] = redCount;
//            objectResult[7] = voterVisited;
//            objectResult[8] = votersResponded;
//            stateReportData.put("overallResult", objectResult);
//        }
//
//        String attachmentBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerMailConfiguration.getTemplate("state_report.ftl", "UTF-8"), stateReportData);
//        Document document = new Document(PageSize.A4.rotate(), 5, 5, 15, 15);
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        PdfWriter writer = PdfWriter.getInstance(document, os);
//        document.open();
//        XMLWorkerHelper.getInstance().parseXHtml(writer, document, new ByteArrayInputStream(attachmentBody.getBytes()));
//        document.close();
//        savePdfFile(os.toByteArray());
//        resultMap.put("pdf", os.toByteArray());
//        saveExcelFile(generateStateExcelReport(stateReportData));
//        //resultMap.put("excel", generateStateExcelReport(stateReportData));
//        return resultMap;
//    }
