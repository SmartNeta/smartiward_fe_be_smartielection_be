/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;
import javax.persistence.*;

/**
 *
 * @author govind
 */
@Entity
@Table(name = "volunteer_notification")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VolunteerNotification extends AuditableLongBaseEntity {

    @Column(name = "header")
    private String header;

    @Column(name = "body")
    private String body;

    @Column(name = "web_link")
    private String webLink;

    @Column(name = "assembly_constituency_id")
    private Long assemblyConstituencyId;

    @Column(name = "parliamentary_constituency_id")
    private Long parliamentaryConstituencyId;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getAssemblyConstituencyId() {
        return assemblyConstituencyId;
    }

    public void setAssemblyConstituencyId(Long assemblyConstituencyId) {
        this.assemblyConstituencyId = assemblyConstituencyId;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public Long getParliamentaryConstituencyId() {
        return parliamentaryConstituencyId;
    }

    public void setParliamentaryConstituencyId(Long parliamentaryConstituencyId) {
        this.parliamentaryConstituencyId = parliamentaryConstituencyId;
    }

}
