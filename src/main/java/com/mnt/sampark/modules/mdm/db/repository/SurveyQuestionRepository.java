package com.mnt.sampark.modules.mdm.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.SurveyQuestion;

@Repository("SurveyQuestionRepository")
public interface SurveyQuestionRepository extends CrudJpaSpecRepository<SurveyQuestion, Long> {

//    @Query(value = "SELECT * FROM survey_question S where S.ward_id = :wardId and S.id NOT IN (select child_question_id from survey_question where child_question_id is not null)", nativeQuery = true)
//    List<SurveyQuestion> findAllByWardId(@Param("wardId") Long wardId);
//
    @Query(value = "SELECT * FROM survey_question S where S.state_assembly_id = :stateAssemblyId AND S.id NOT IN (select child_question_id from survey_question where child_question_id is not null)", nativeQuery = true)
    List<SurveyQuestion> findAllByStateAssemblyId(@Param("stateAssemblyId") Long stateAssemblyId);
//
    @Query(value = "SELECT * FROM survey_question S where ((S.state_assembly_id = :said AND S.ward_id IS NULL) oR S.ward_id = :wid) and S.id NOT IN (select child_question_id from survey_question where child_question_id is not null)", nativeQuery = true)
    List<SurveyQuestion> findAllByWardIdAndState(@Param("said") Long said, @Param("wid") Long wid);
//
//    @Query(value = "SELECT * FROM survey_question S where S.ward_id in (SELECT w.id FROM ward w WHERE w.assembly_constituency_id = :assemblyId ) and S.id NOT IN (select child_question_id from survey_question where child_question_id is not null)", nativeQuery = true)
//    List<SurveyQuestion> findAllByAssemblyConstituencyId(@Param("assemblyId") Long assemblyId);
//
//    @Query(value = "SELECT * FROM survey_question S where S.ward_id in (SELECT w.id FROM ward w WHERE w.assembly_constituency_id in :assemblyIds ) and S.id NOT IN (select child_question_id from survey_question where child_question_id is not null)", nativeQuery = true)
//    public List<SurveyQuestion> findAllByAssemblyConstituencyIdIn(@Param("assemblyIds") List<Long> assemblyIds);
//

    @Query(value = "select distinct sq.* from survey s inner join survey_question sq on sq.id = s.survey_question_id where s.citizen_id in (select c.id from citizen c where c.state = (select s.state from state_assembly s where s.id = :stateId))", nativeQuery = true)
    public List<SurveyQuestion> findAllQuestionByStateAssemblyId(@Param("stateId") Long stateId);

    @Query(value = "select distinct sq.* from survey s inner join survey_question sq on sq.id = s.survey_question_id where s.citizen_id in(select c.id from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in :assemblyIds)))", nativeQuery = true)
    public List<SurveyQuestion> findAllByAssemblyConstituencyIdIn(@Param("assemblyIds") List<Long> assemblyId);

    @Query(value = "select distinct sq.* from survey s inner join survey_question sq on sq.id = s.survey_question_id where s.citizen_id in(select c.id from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id = :assemblyId)))", nativeQuery = true)
    public List<SurveyQuestion> findAllByAssemblyConstituencyId(@Param("assemblyId") Long assemblyId);

    @Query(value = "select distinct sq.* from survey s inner join survey_question sq on sq.id = s.survey_question_id where s.citizen_id in(select c.id from citizen c where c.booth_id in (select b.id from booth b where b.ward_id = :wardId))", nativeQuery = true)
    public List<SurveyQuestion> findAllByWardId(@Param("wardId") Long wardId);

    @Query(value = "select distinct sq.* from survey s inner join survey_question sq on sq.id = s.survey_question_id where s.citizen_id in(select c.id from citizen c where c.booth_id = :boothId)", nativeQuery = true)
    public List<SurveyQuestion> findAllByBoothId(@Param("boothId") Long boothId);

}
