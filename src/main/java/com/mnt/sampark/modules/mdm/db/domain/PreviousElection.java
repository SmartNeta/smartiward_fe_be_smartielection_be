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
@Table(name = "previous_election")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PreviousElection extends AuditableLongBaseEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "year")
    private Long year;

    @Column(name = "total_voters")
    private Long totalVoters;

    @Column(name = "total_polled")
    private Long totalPolled;

    @Column(name = "total_party_voted")
    private Long totalPartyVoted;

    @Column(name = "assembly_no")
    private String assemblyNumber;

    @Column(name = "ward_no")
    private String wardNumber;

    @Column(name = "booth_no")
    private String boothNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    @NotNull
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parliamentary_id")
    private ParliamentaryConstituency parliamentaryConstituency;

    public Long getYear() {
        return year;
    }

    public void setYear(Long year) {
        this.year = year;
    }

    public Long getTotalVoters() {
        return totalVoters;
    }

    public void setTotalVoters(Long totalVoters) {
        this.totalVoters = totalVoters;
    }

    public Long getTotalPolled() {
        return totalPolled;
    }

    public void setTotalPolled(Long totalPolled) {
        this.totalPolled = totalPolled;
    }

    public Long getTotalPartyVoted() {
        return totalPartyVoted;
    }

    public void setTotalPartyVoted(Long totalPartyVoted) {
        this.totalPartyVoted = totalPartyVoted;
    }

    public Party getParty() {
        return party;
    }

    public void setParty(Party party) {
        this.party = party;
    }

    public String getAssemblyNumber() {
        return assemblyNumber;
    }

    public void setAssemblyNumber(String assemblyNumber) {
        this.assemblyNumber = assemblyNumber;
    }

    public String getWardNumber() {
        return wardNumber;
    }

    public void setWardNumber(String wardNumber) {
        this.wardNumber = wardNumber;
    }

    public String getBoothNumber() {
        return boothNumber;
    }

    public void setBoothNumber(String boothNumber) {
        this.boothNumber = boothNumber;
    }

    public ParliamentaryConstituency getParliamentaryConstituency() {
        return parliamentaryConstituency;
    }

    public void setParliamentaryConstituency(ParliamentaryConstituency parliamentaryConstituency) {
        this.parliamentaryConstituency = parliamentaryConstituency;
    }

}
