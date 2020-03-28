package com.mnt.sampark.modules.mdm.db.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

@Entity
@Table(name = "previous_election_uploads")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreviousElectionUploads extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private AssemblyConstituency assemblyConstituency;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public AssemblyConstituency getAssemblyConstituency() {
        return assemblyConstituency;
    }

    public void setAssemblyConstituency(AssemblyConstituency assemblyConstituency) {
        this.assemblyConstituency = assemblyConstituency;
    }

}
