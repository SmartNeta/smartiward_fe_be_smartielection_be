package com.mnt.sampark.modules.mdm.db.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;
import java.math.BigInteger;

@Entity
@Table(name = "csv_file_info")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CSVFileInfo extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "path", length = 250)
    private String path;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "total_success")
    private BigInteger totalSuccess;

    @Column(name = "total_citizens")
    private BigInteger totalCitizens;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigInteger getTotalSuccess() {
        return totalSuccess;
    }

    public void setTotalSuccess(BigInteger totalSuccess) {
        this.totalSuccess = totalSuccess;
    }

    public BigInteger getTotalCitizens() {
        return totalCitizens;
    }

    public void setTotalCitizens(BigInteger totalCitizens) {
        this.totalCitizens = totalCitizens;
    }

}
