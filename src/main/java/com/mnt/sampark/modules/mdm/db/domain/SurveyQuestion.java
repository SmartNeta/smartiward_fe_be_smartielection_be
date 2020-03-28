package com.mnt.sampark.modules.mdm.db.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

@Entity
@Table(name = "survey_question")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyQuestion extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "question", length = 500, nullable = false)
    private String question;

    @Column(name = "type", length = 500, nullable = false)
    private String type;

    @Column(name = "options", length = 500)
    private String options;

    @Column(name = "mandetory")
    private Boolean mandatory = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stateAssembly_id")
    private StateAssembly stateAssembly;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_question_id")
    private SurveyQuestion childQuestion;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public StateAssembly getStateAssembly() {
        return stateAssembly;
    }

    public void setStateAssembly(StateAssembly stateAssembly) {
        this.stateAssembly = stateAssembly;
    }

    public SurveyQuestion getChildQuestion() {
        return childQuestion;
    }

    public void setChildQuestion(SurveyQuestion childQuestion) {
        this.childQuestion = childQuestion;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
}
