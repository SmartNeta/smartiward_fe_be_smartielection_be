package com.mnt.sampark.modules.mdm.tools;

import com.mnt.sampark.modules.mdm.db.domain.Citizen;
import com.mnt.sampark.modules.mdm.db.repository.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.*;
import org.springframework.data.domain.Page;

@Component
public class CitizenService {

    @Autowired
    CitizenRepository citizenRepository;

    @Autowired
    EntityManager em;

    public Citizen findByVoterId(String voterId) {
        return citizenRepository.findByVoterId(voterId);
    }

    public Optional<Citizen> findById(Long citizenId) {
        return citizenRepository.findById(citizenId);
    }

    public Citizen save(Citizen citizen) {
        return citizenRepository.save(citizen);
    }

    public List<HashMap<String, Object>> getDashboardData(Long boothId) {
        Query query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id = " + boothId + " group by  C.address order by cnt desc");
        List<Object[]> records = query.getResultList();
        List<HashMap<String, Object>> result = new ArrayList<>();
        for (Object[] record : records) {
            HashMap<String, Object> row = new HashMap<>();
            row.put("count", record[0]);
            row.put("address", record[1]);
            result.add(row);
        }
        return result;
    }

    public Map<String, Object> getDashboardData(String filterBy, Long id, int page, int size) {
        Query query = null;
        Query queryTotalRecords = null;
        Page pagable = null;
        if (filterBy.equalsIgnoreCase("state")) {
            queryTotalRecords = em.createNativeQuery("select count(distinct C.address) from citizen C where C.state = (select s.state from state_assembly s where s.id = " + id + " limit 1)");
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.state = (select s.state from state_assembly s where s.id = " + id + " limit 1) group by C.address order by cnt desc limit " + size + " offset " + page * size);
        } else if (filterBy.equalsIgnoreCase("PC")) {
            queryTotalRecords = em.createNativeQuery("select count(distinct C.address) from citizen C where C.booth_id in (select b.id from booth b where b.ward_id in(select w.id from ward w where w.assembly_constituency_id in(select a.id from assembly_constituency a where a.parliamentary_constituency_id =  " + id + ")))");
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id in (select b.id from booth b where b.ward_id in(select w.id from ward w where w.assembly_constituency_id in(select a.id from assembly_constituency a where a.parliamentary_constituency_id =  " + id + "))) group by C.address order by cnt desc limit " + size + " offset " + page * size);
        } else if (filterBy.equalsIgnoreCase("assembly")) {
            queryTotalRecords = em.createNativeQuery("select count(distinct C.address) from citizen C where C.booth_id in (select b.id from booth b where b.ward_id in(select w.id from ward w where w.assembly_constituency_id = " + id + " ))");
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id in (select b.id from booth b where b.ward_id in(select w.id from ward w where w.assembly_constituency_id = " + id + " )) group by C.address order by cnt desc limit " + size + " offset " + page * size);
        } else if (filterBy.equalsIgnoreCase("ward")) {
            queryTotalRecords = em.createNativeQuery("select count(distinct C.address) from citizen C where C.booth_id in (select b.id from booth b where b.ward_id = " + id + ")");
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id in (select b.id from booth b where b.ward_id = " + id + ") group by C.address order by cnt desc limit " + size + " offset " + page * size);
        } else {
            queryTotalRecords = em.createNativeQuery("select count(distinct C.address) from citizen C where C.booth_id = " + id + "");
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id = " + id + " group by C.address order by cnt desc limit " + size + " offset " + page * size);
        }
        List<Object[]> records123 = queryTotalRecords.getResultList();
        List<Object[]> records = query.getResultList();
        List<HashMap<String, Object>> result = new ArrayList<>();
        for (Object[] record : records) {
            HashMap<String, Object> row = new HashMap<>();
            row.put("count", record[0]);
            row.put("address", record[1]);
            result.add(row);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("content", result);
        map.put("totalElements", records123.get(0));
        return map;
    }

    public HashMap<String, Object> getDashboardVolunteerVotersByBoothId(Long boothId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", citizenRepository.getTotalVotersCountByBoothId(boothId));
        Map<String, Long> map = new HashMap<>();
        map.put("notAtHome", citizenRepository.getTotalByBoothIdAndStatus(boothId, "not at home").longValue());
        map.put("responded", citizenRepository.getTotalByBoothIdAndStatus(boothId, "responded").longValue());
        map.put("refused", citizenRepository.getTotalByBoothIdAndStatus(boothId, "refused").longValue());
        map.put("callBack", citizenRepository.getTotalByBoothIdAndStatus(boothId, "call back").longValue());
        result.put("status", map);
        return result;
    }

    public HashMap<String, Object> getDashboardVolunteerVotersByWardId(Long wardId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", citizenRepository.getTotalVotersCountByWardId(wardId));
        Map<String, Long> map = new HashMap<>();
        map.put("notAtHome", citizenRepository.getTotalByWardIdAndStatus(wardId, "not at home").longValue());
        map.put("responded", citizenRepository.getTotalByWardIdAndStatus(wardId, "responded").longValue());
        map.put("refused", citizenRepository.getTotalByWardIdAndStatus(wardId, "refused").longValue());
        map.put("callBack", citizenRepository.getTotalByWardIdAndStatus(wardId, "call back").longValue());
        result.put("status", map);
        return result;
    }

    public HashMap<String, Object> getDashboardVolunteerVotersByAssemblyConstituencyId(Long assemblyConstituencyId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("total", citizenRepository.getTotalVotersCountByAssemblyConstituencyId(assemblyConstituencyId));
        Map<String, Long> map = new HashMap<>();
        map.put("notAtHome", citizenRepository.getTotalByAssemblyConstituencyIdAndStatus(assemblyConstituencyId, "not at home").longValue());
        map.put("responded", citizenRepository.getTotalByAssemblyConstituencyIdAndStatus(assemblyConstituencyId, "responded").longValue());
        map.put("refused", citizenRepository.getTotalByAssemblyConstituencyIdAndStatus(assemblyConstituencyId, "refused").longValue());
        map.put("callBack", citizenRepository.getTotalByAssemblyConstituencyIdAndStatus(assemblyConstituencyId, "call back").longValue());
        result.put("status", map);
        return result;
    }

    public HashMap<String, Object> getDashboardVolunteerHousesByWardId(Long wardId) {
        HashMap<String, Object> result = new HashMap<>();
        List<Object[]> arr = citizenRepository.getHouseRespondedByWardId(wardId);
        result.put("total", citizenRepository.getTotalHousesCountByWardId(wardId));
        result.put("status", addCitizenStatus(arr));
        return result;
    }

    public HashMap<String, Object> getDashboardVolunteerHousesByBoothId(Long boothId) {
        HashMap<String, Object> result = new HashMap<>();
        List<Object[]> arr = citizenRepository.getHouseRespondedByBoothId(boothId);
        result.put("total", citizenRepository.getTotalHousesCountByBoothId(boothId));
        result.put("status", addCitizenStatus(arr));
        return result;
    }

    public HashMap<String, Object> getDashboardVolunteerHousesByAssemblyConstituencyId(Long assemblyConstituencyId) {
        HashMap<String, Object> result = new HashMap<>();
        List<Object[]> arr = citizenRepository.getHouseRespondedByAssemblyConstituencyId(assemblyConstituencyId);
        result.put("total", citizenRepository.getTotalHousesCountByAssemblyConstituencyId(assemblyConstituencyId));
        result.put("status", addCitizenStatus(arr));
        return result;
    }

    public BigInteger getCitizenCountWithMobile(HashMap<String, String> data) {
        String sql = "select count(*) from citizen C where (C.mobile != '' and C.mobile IS NOT NULL) ";
        sql = generatePieChartQuery(data, sql);
        Query query = em.createNativeQuery(sql);
        BigInteger withMobile = (BigInteger) query.getSingleResult();
        return withMobile;
    }

    public BigInteger getCitizenCountWithoutMobile(HashMap<String, String> data) {
        String sql = "select count(*) from citizen C where (C.mobile = '' or C.mobile IS NULL) ";
        sql = generatePieChartQuery(data, sql);
        Query query = em.createNativeQuery(sql);
        BigInteger withMobile = (BigInteger) query.getSingleResult();
        return withMobile;
    }

    public BigInteger getCitizenCountSegmentetionWise(HashMap<String, String> data, String first, String second) {
        String sql = "select count(*) from citizen C where  (C.voter_segmentation = '" + first + "' or C.voter_segmentation = '" + second + "') ";
        sql = generatePieChartQuery(data, sql);
        Query query = em.createNativeQuery(sql);
        BigInteger withMobile = (BigInteger) query.getSingleResult();
        return withMobile;
    }

    public BigInteger getCitizenCountMale(HashMap<String, String> data) {
        String sql = "select count(*) from citizen C where  C.gender = 'Male' ";
        sql = generatePieChartQuery(data, sql);
        Query query = em.createNativeQuery(sql);
        BigInteger withMobile = (BigInteger) query.getSingleResult();
        return withMobile;
    }

    public BigInteger getCitizenCountFemale(HashMap<String, String> data) {
        String sql = "select count(*) from citizen C where  C.gender = 'Female' ";
        sql = generatePieChartQuery(data, sql);
        Query query = em.createNativeQuery(sql);
        BigInteger withMobile = (BigInteger) query.getSingleResult();
        return withMobile;
    }

    public BigInteger getCitizenPartyPref(HashMap<String, String> data, String partyCode) {
        String sql = "select count(*) from citizen C where  C.party_preference = '" + partyCode + "' ";
        sql = generatePieChartQuery(data, sql);
        Query query = em.createNativeQuery(sql);
        BigInteger withMobile = (BigInteger) query.getSingleResult();
        return withMobile;
    }

    public BigInteger getCitizenAge(HashMap<String, String> data, int min, int max) {
        String sql = "select count(*) from citizen C where C.age >" + min + " and  C.age <= " + max + " ";
        sql = generatePieChartQuery(data, sql);
        Query query = em.createNativeQuery(sql);
        BigInteger withMobile = (BigInteger) query.getSingleResult();
        return withMobile;
    }

    private Map<String, Long> addCitizenStatus(List<Object[]> a) {
        Map<String, Long> map = new HashMap<>();
        Long responded = 0l;
        Long callBack = 0l;
        Long notAtHome = 0l;
        Long refused = 0l;
        for (int i = 0; i < a.size(); i++) {
            Object[] ob = a.get(i);
            if (Long.valueOf(ob[0] + "") > 0) {
                responded++;
            } else if (Long.valueOf(ob[1] + "") > 0) {
                callBack++;
            } else if (Long.valueOf(ob[2] + "") > 0) {
                notAtHome++;
            } else if (Long.valueOf(ob[3] + "") > 0) {
                refused++;
            }
        }
        map.put("responded", responded);
        map.put("callBack", callBack);
        map.put("notAtHome", notAtHome);
        map.put("refused", refused);
        return map;
    }

    private String generatePieChartQuery(HashMap<String, String> data, String sql) {
        String state = data.get("state");
        String assembly_no = data.get("assembly");
        String volunteerMobile = data.get("volunteerMobile");
        String ward_no = data.get("ward");
        String booth_id = data.get("booth");
        String gender = data.get("Gender");
        String withMobile1 = data.get("withMobile");
        String segmentation = data.get("segmentation");
        String ageGroup = data.get("ageGroup");
        String party = data.get("partyCode");
        String voted = data.get("voted");
        int max = 18;
        int min = 100;
        if (null != ageGroup) {
            switch (ageGroup) {
                case "18-35":
                    max = 35;
                    min = 18;
                    break;
                case "36-55":
                    max = 55;
                    min = 36;
                    break;
                case "Greater Than 55":
                    min = 56;
                    max = 100;
                    break;
                default:
                    break;
            }
        }
        if (state != null && !state.isEmpty()) {
            sql = sql + " and C.state ='" + state + "' ";
        }
        if (segmentation != null && !segmentation.isEmpty()) {
            sql = sql + " and C.voter_segmentation ='" + segmentation + "' ";
        }
        if (assembly_no != null && !assembly_no.isEmpty()) {
            sql = sql + " and C.assembly_no ='" + assembly_no + "' ";
        }
        if (ward_no != null && !ward_no.isEmpty()) {
            sql = sql + " and C.ward_no ='" + ward_no + "' ";
        }
        if (booth_id != null && !booth_id.isEmpty()) {
            sql = sql + " and C.booth_id ='" + booth_id + "' ";
        }
        if (gender != null && !gender.isEmpty()) {
            sql = sql + " and C.gender ='" + gender + "' ";
        }
        if (withMobile1.equals("true")) {
            sql = sql + " and (C.mobile != '' and C.mobile IS NOT NULL) ";
        }
        if (ageGroup != null && !ageGroup.isEmpty()) {
            sql = sql + " and C.age >= " + min + " and C.age <= " + max;
        }
        if (party != null && !party.isEmpty()) {
            sql = sql + " and C.party_preference = '" + party + "' ";
        }
        if (volunteerMobile != null && !volunteerMobile.isEmpty()) {
            sql = sql + " and C.volunteer_mobile like '%" + volunteerMobile + "%' ";
        }
        if (voted != null && !voted.isEmpty() && !voted.equalsIgnoreCase("All")) {
            if (voted.equalsIgnoreCase("Voted")) {
                sql = sql + " and C.voted = " + voted.equalsIgnoreCase("Voted");
            } else {
                sql = sql + " and (C.voted = false or C.voted is null) ";
            }
        }
        return sql;
    }

    public List<Map<String, Object>> getVotersGoogleMapData(HashMap<String, String> filterData) {
        String sql = "select C.first_name, C.responded_status, C.latitude, C.longitude, C.address, C.voter_id, C.family_name, C.gender, C.age, C.srno, C.booth_no from citizen C where C.latitude is not null and C.latitude != '-1' and C.longitude is not null and C.longitude != '-1'  and C.responded_status is not null ";
        sql = generatePieChartQuery(filterData, sql);
        sql = sql + " order by C.modifieddate DESC ";
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Object[]> result = em.createNativeQuery(sql).getResultList();
        result.stream().map((obj) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("voter_name", obj[0]);
            map.put("responded_status", obj[1]);
            map.put("lat", Double.valueOf(obj[2] + ""));
            map.put("lng", Double.valueOf(obj[3] + ""));
            map.put("address", obj[4]);
            map.put("voter_id", obj[5]);
            map.put("family_name", obj[6]);
            map.put("gender", obj[7]);
            map.put("age", obj[8]);
            map.put("sr_no", obj[9]);
            map.put("booth_number", obj[10]);
            return map;
        }).forEachOrdered((obj) -> {
            resultList.add(obj);
        });
        return resultList;
    }

    public List<Object[]> getVotersCSV(Long id, String filterBy) {
        List<Object[]> allVoters = new ArrayList<>();
        if (filterBy.equalsIgnoreCase("ward")) {
            allVoters.addAll(citizenRepository.getByWard(id));
        } else if (filterBy.equalsIgnoreCase("assembly")) {
            allVoters.addAll(citizenRepository.getByAssembly(id));
        } else if (filterBy.equalsIgnoreCase("pc")) {
            allVoters.addAll(citizenRepository.getByPC(id));
        } else if (filterBy.equalsIgnoreCase("state")) {
            allVoters.addAll(citizenRepository.getByState(id));
        }
//        if (filterBy.equalsIgnoreCase("ward")) {
//            allVoters.addAll(em.createNativeQuery("select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender  from citizen c where c.booth_id in (select b.id from booth b where b.ward_id = " + id + ");").getResultList());
//        } else if (filterBy.equalsIgnoreCase("assembly")) {
//            allVoters.addAll(em.createNativeQuery("select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender  from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id = " + id + "));").getResultList());
//        } else if (filterBy.equalsIgnoreCase("pc")) {
//            allVoters.addAll(em.createNativeQuery("select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender  from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in (select a.id from assembly_constituency a where a.parliamentary_constituency_id = " + id + ")));").getResultList());
//        } else if (filterBy.equalsIgnoreCase("state")) {
//            allVoters.addAll(em.createNativeQuery("select c.assembly_no, c.ward_no, c.booth_no, c.srno, c.pincode, c.address, c.voter_id, c.first_name, c.family_name, concat(c.age,'') as age, c.gender  from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in (select a.id from assembly_constituency a where a.parliamentary_constituency_id in (select P.id from parliamentary_constituency P where P.district_id IN (select D.id from district D where D.state_assembly_id = " + id + ")))));").getResultList());
//        }
        return allVoters;
    }

    public Object[] getReportCsv(Long qid, String op1, String op2, String op3, String op4, String op5, String op6, String query, String pc, String ac, String ward, String booth) {
        List<Object[]> allVoters = em.createNativeQuery("SELECT (select St.state from state_assembly St where St.id = SQ.state_assembly_id) as State,CAST(null as char) as pc,  CAST(null as char) as ac,  CAST(null as char) as wrd,  CAST(null as char) as boothn, SQ.question, count(*) as TotalResponce, COUNT(CASE WHEN S.answer = '" + op1 + "' THEN 1 END) as Option1 , COUNT(CASE WHEN S.answer = '" + op2 + "' THEN 1 END ) as Option2,"
                + " COUNT(CASE WHEN S.answer = '" + op3 + "' THEN 1 END) as Option3, COUNT(CASE WHEN S.answer = '" + op4 + "' THEN 1 END) as Option4, "
                + " COUNT(CASE WHEN S.answer = '" + op5 + "' THEN 1 END) as Option5 ,COUNT(CASE WHEN S.answer = '" + op6 + "' THEN 1 END) as Option6 "
                + "from survey S, survey_question SQ, citizen c "
                + "WHERE S.survey_question_id = " + qid + " and SQ.id = S.survey_question_id and c.id = S.citizen_id " + query).getResultList();
        allVoters.forEach((action) -> {
            action[1] = pc;
            action[2] = ac;
            action[3] = ward;
            action[4] = booth;
            action[7] = (String) (!"".equals(op1) ? op1 + " : " + action[7] : "");
            action[8] = (String) (!"".equals(op2) ? op2 + " : " + action[8] : "");
            action[9] = (String) (!"".equals(op3) ? op3 + " : " + action[9] : "");
            action[10] = (String) (!"".equals(op4) ? op4 + " : " + action[10] : "");
            action[11] = (String) (!"".equals(op5) ? op5 + " : " + action[11] : "");
            action[12] = (String) (!"".equals(op6) ? op6 + " : " + action[12] : "");
        });

        return allVoters.toArray();
    }

    public List<String[]> getQuestionAnswers(Long qid, String filterQuery) {
        return (List<String[]>) em.createNativeQuery("select CAST(null as char) as state,CAST(null as char) as pc,  CAST(null as char) as ac,  CAST(null as char) as wrd,  CAST(null as char) as boothn, CAST(null as char) as question, s.answer , count(*) as total_count from survey s, citizen c where s.survey_question_id = " + qid + " and trim(s.answer) != '' and s.answer is not null and c.id = s.citizen_id " + filterQuery + " group by s.answer order by total_count desc").getResultList();
    }

    public List<String[]> getQuestionAnswersDumpVoterWise(StringBuilder query) {
        return (List<String[]>) em.createNativeQuery(query.toString()).getResultList();
    }

    public List<Map<String, Object>> findNearestVoters(HashMap<String, Object> map) {
        List<Map<String, Object>> voters = new ArrayList<>();
        String query = "SELECT c.voter_id, c.first_name, c.family_name,c.gender, c.age,  concat(COALESCE(c.address, ''), ',' ,COALESCE(c.state , ''),',',COALESCE(c.pincode, '')) as address, c.srno, c.booth_no, c.responded_status, c.id, SQRT(POW(69.1 * ( c.latitude - " + map.get("latitude") + "), 2) + POW(69.1 * (" + map.get("longitude") + " - c.longitude) * COS(c.latitude / 57.3), 2)) AS distance FROM citizen c where c.state = '" + map.get("stateName") + "' ";
        String voterId = Objects.nonNull(map.get("voterId")) ? map.get("voterId") + "" : null;
        String wardNo = Objects.nonNull(map.get("wardNo")) && map.get("wardNo") != "All" ? map.get("wardNo") + "" : null;
        String boothId = Objects.nonNull(map.get("boothId")) && !(map.get("boothId") + "").equalsIgnoreCase("-1") && !(map.get("boothId") + "").equalsIgnoreCase("None") ? map.get("boothId") + "" : null;
        String wardId = Objects.nonNull(map.get("wardId")) && !(map.get("wardId") + "").equalsIgnoreCase("-1") ? map.get("wardId") + "" : null;
        String firstName = Objects.nonNull(map.get("firstName")) ? map.get("firstName") + "" : null;
        String familyName = Objects.nonNull(map.get("familyName")) ? map.get("familyName") + "" : null;
        String address = Objects.nonNull(map.get("address")) ? map.get("address") + "" : null;
        String respondedStatus = Objects.nonNull(map.get("respondedStatus")) ? map.get("respondedStatus") + "" : null;
        String srNo = Objects.nonNull(map.get("srNo")) ? map.get("srNo") + "" : null;
        String assemblyId = Objects.nonNull(map.get("assemblyId")) ? map.get("assemblyId") + "" : null;
        String searchingBoothId = Objects.nonNull(map.get("searchingBoothId")) && map.get("searchingBoothId") != "undefined" ? map.get("searchingBoothId") + "" : null;

        if (voterId != null && !"".equals(voterId) && !voterId.isEmpty()) {
            query = query + " and c.voter_id like '%" + voterId + "%' ";
        }
        if (wardNo != null && !"".equals(wardNo) && !wardNo.isEmpty()) {
            query = query + " and c.ward_no = '" + wardNo + "' ";
        }

        if (boothId != null && !"".equals(boothId) && !boothId.isEmpty()) {
            query = query + " and c.booth_no = '" + boothId + "' ";
            if (searchingBoothId != null && !"".equals(searchingBoothId) && !searchingBoothId.isEmpty()) {
                query = query + " and c.booth_id = '" + searchingBoothId + "' ";
            }
        } else if (wardId != null && !"".equals(wardId) && !wardId.isEmpty()) {
            query = query + " and c.booth_id in (select b.id from booth b where b.ward_id = " + wardId + ") ";
        } else if (assemblyId != null && !"".equals(assemblyId) && !assemblyId.isEmpty()) {
            query = query + " and c.booth_id in (select b.id from booth b where b.ward_id IN (select w.id from ward w where w.assembly_constituency_id = " + assemblyId + "))  ";
        }

        if (firstName != null && !"".equals(firstName) && !firstName.isEmpty()) {
            query = query + " and c.first_name like '%" + firstName + "%' ";
        }
        if (familyName != null && !"".equals(familyName) && !familyName.isEmpty()) {
            query = query + " and c.family_name like '%" + familyName + "%' ";
        }
        if (address != null && !"".equals(address) && !address.isEmpty()) {
            query = query + " and c.address like'%" + address + "%' ";
        }
        if (respondedStatus != null && !"".equals(respondedStatus) && !respondedStatus.isEmpty()) {
            query = query + " and c.responded_status = '" + respondedStatus + "' ";
        }
        if (srNo != null && !"".equals(srNo) && !srNo.isEmpty()) {
            query = query + " and c.srno like '%" + srNo + "%' ";
        }
        query = query + " HAVING distance < " + Double.parseDouble(map.get("distance") + "") * 0.621;
        if (firstName != null && !"".equals(firstName) && !firstName.isEmpty()) {
            query = query + " order by c.first_name asc ";
        }
        System.out.println("query  " + query);
        List<Object[]> allVoters = em.createNativeQuery(query).getResultList();
        allVoters.stream().map((voter) -> {
            Map<String, Object> voterMap = new HashMap<>();
            voterMap.put("voter_id", voter[0]);
            voterMap.put("first_name", voter[1]);
            voterMap.put("family_name", voter[2]);
            voterMap.put("gender", voter[3]);
            voterMap.put("age", voter[4]);
            voterMap.put("address", voter[5]);
            voterMap.put("srno", voter[6]);
            voterMap.put("booth_no", voter[7]);
            voterMap.put("boothName", voter[7]);
            voterMap.put("responded_status", voter[8]);
            voterMap.put("id", voter[9]);
            return voterMap;
        }).forEachOrdered((voterMap) -> {
            voters.add(voterMap);
        });
        return voters;
    }

    public Map<String, Object> getNumberOfVotersPerHouseCSV(String filterBy, Long id) {
        Query query = null;
        if (filterBy.equalsIgnoreCase("state")) {
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.state = (select s.state from state_assembly s where s.id = " + id + " limit 1) group by C.address order by cnt desc");
        } else if (filterBy.equalsIgnoreCase("PC")) {
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id in (select b.id from booth b where b.ward_id in(select w.id from ward w where w.assembly_constituency_id in(select a.id from assembly_constituency a where a.parliamentary_constituency_id =  " + id + "))) group by C.address order by cnt desc");
        } else if (filterBy.equalsIgnoreCase("assembly")) {
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id in (select b.id from booth b where b.ward_id in(select w.id from ward w where w.assembly_constituency_id = " + id + " )) group by C.address order by cnt desc");
        } else if (filterBy.equalsIgnoreCase("ward")) {
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id in (select b.id from booth b where b.ward_id = " + id + ") group by C.address order by cnt desc");
        } else {
            query = em.createNativeQuery("select count(*) as cnt, C.address from citizen C where C.booth_id = " + id + " group by C.address order by cnt desc");
        }
        List<Object[]> records = query.getResultList();
        List<HashMap<String, Object>> result = new ArrayList<>();
        records.stream().map((record) -> {
            HashMap<String, Object> row = new HashMap<>();
            row.put("Address", record[1]);
            row.put("Total_Persons_Count", record[0]);
            return row;
        }).forEachOrdered((row) -> {
            result.add(row);
        });
        Map<String, Object> map = new HashMap<>();
        map.put("content", result);
        return map;
    }

    public Object[] getCitizenAge(HashMap<String, String> filterData, String sql) {
        sql = generatePieChartQuery(filterData, sql);
        Query query = em.createNativeQuery(sql);
        List<Object[]> result = query.getResultList();
        return result.size() > 0 ? result.get(0) : null;
    }

}
