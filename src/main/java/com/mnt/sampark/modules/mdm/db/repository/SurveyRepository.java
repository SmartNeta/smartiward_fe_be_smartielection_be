package com.mnt.sampark.modules.mdm.db.repository;

import org.springframework.stereotype.Repository;

import com.mnt.sampark.modules.mdm.db.domain.Survey;
import com.mnt.sampark.modules.mdm.db.domain.SurveyQuestion;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository("SurveyRepository")
public interface SurveyRepository extends CrudJpaSpecRepository<Survey, Long> {

    public Survey findByCitizenIdAndSurveyQuestionId(Long citizenId, Long surveyQuestionId);

    @Query(value = "select count(*) from survey s where s.citizen_id in (select c.id from citizen c where c.state = (select s.state from state_assembly s where s.id = :stateId)) and s.survey_question_id = :questionId and s.answer != '' and s.answer is not null ", nativeQuery = true)
    public Long findAllIdsByStateId(@Param("stateId") Long stateId, @Param("questionId") Long questionId);

    @Query(value = "select count(*) from survey s where s.citizen_id in(select c.id from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id = :assemblyId))) and s.survey_question_id = :questionId and s.answer != '' and s.answer is not null", nativeQuery = true)
    public Long findAllIdsByAssemblyConstituencyId(@Param("assemblyId") Long assemblyId, @Param("questionId") Long questionId);

    @Query(value = "select count(*) from survey s where s.citizen_id in(select c.id from citizen c where c.booth_id in (select b.id from booth b where b.ward_id in (select w.id from ward w where w.assembly_constituency_id in :assemblyIds))) and s.survey_question_id = :questionId and s.answer != '' and s.answer is not null", nativeQuery = true)
    public Long findAllIdsByAssemblyConstituencyIdIn(@Param("assemblyIds") List<Long> assemblyId, @Param("questionId") Long questionId);

    @Query(value = "select count(*) from survey s where s.citizen_id in(select c.id from citizen c where c.booth_id in (select b.id from booth b where b.ward_id = :wardId)) and s.survey_question_id = :questionId and s.answer != '' and s.answer is not null", nativeQuery = true)
    public Long findAllIdsByWardId(@Param("wardId") Long wardId, @Param("questionId") Long questionId);

    @Query(value = "select count(*) from survey s where s.citizen_id in(select c.id from citizen c where c.booth_id = :boothId) and s.survey_question_id = :questionId and s.answer != '' and s.answer is not null", nativeQuery = true)
    public Long findAllIdsByBoothId(@Param("boothId") Long boothId, @Param("questionId") Long questionId);

}
