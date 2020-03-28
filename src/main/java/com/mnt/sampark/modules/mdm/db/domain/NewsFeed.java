package com.mnt.sampark.modules.mdm.db.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mnt.sampark.core.db.model.AuditableLongBaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "news_feed")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsFeed extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "header", length = 1000)
    @NotNull(message = "header can not be empty")
    private String header;

    @Column(name = "details", length = 5000)
    @NotNull(message = "details can not be empty")
    private String details;

    @Column(name = "web_link")
    private String webLink;

    @Column(name = "image", length = 100)
    private String image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "state_assembly_id")
    @NotNull(message = "State assembly can not be empty")
    private StateAssembly stateAssembly;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public StateAssembly getStateAssembly() {
        return stateAssembly;
    }

    public void setStateAssembly(StateAssembly stateAssembly) {
        this.stateAssembly = stateAssembly;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

}
