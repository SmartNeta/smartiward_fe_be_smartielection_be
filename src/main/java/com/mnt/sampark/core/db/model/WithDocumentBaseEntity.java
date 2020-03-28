/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mnt.sampark.core.db.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * 
 */
@MappedSuperclass
public abstract class WithDocumentBaseEntity extends AuditableLongBaseEntity {

	@Column(name="document_id", length=36, unique=true)
	private String documentId;
	
	@Column(name="fk_parent_id")
	private Long parentId;
	
	

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	
	
}
