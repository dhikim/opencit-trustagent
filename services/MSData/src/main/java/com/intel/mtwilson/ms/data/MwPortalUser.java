/*
 * Copyright (C) 2012 Intel Corporation
 * All rights reserved.
 */
package com.intel.mtwilson.ms.data;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author jbuhacoff
 */
@Entity
@Table(name = "mw_portal_user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MwPortalUser.findByUsername", query = "SELECT a FROM MwPortalUser a WHERE a.username = :username"),
    @NamedQuery(name = "MwPortalUser.findByUsernameEnabled", query = "SELECT a FROM MwPortalUser a WHERE a.username = :username AND a.enabled = :enabled"),
    @NamedQuery(name = "MwPortalUser.findAll", query = "SELECT a FROM MwPortalUser a"),
    @NamedQuery(name = "MwPortalUser.findById", query = "SELECT a FROM MwPortalUser a WHERE a.id = :id"),
    @NamedQuery(name = "MwPortalUser.findByUsernameLike", query = "SELECT a FROM MwPortalUser a WHERE a.username LIKE :username"), // it's the caller's responsibility to add "%" before and/or after the name value
    @NamedQuery(name = "MwPortalUser.findByCommentLike", query = "SELECT a FROM MwPortalUser a WHERE a.comment LIKE :comment"), // it's the caller's responsibility to add "%" before and/or after the name value
    @NamedQuery(name = "MwPortalUser.findByEnabled", query = "SELECT a FROM MwPortalUser a WHERE a.enabled = :enabled"),
    @NamedQuery(name = "MwPortalUser.findByStatus", query = "SELECT a FROM MwPortalUser a WHERE a.status = :status"),
    @NamedQuery(name = "MwPortalUser.findByEnabledStatus", query = "SELECT a FROM MwPortalUser a WHERE a.enabled = :enabled AND a.status = :status")})
public class MwPortalUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "username")
    private String username;
    @Basic(optional = false)
    @Lob
    @Column(name = "keystore")
    private byte[] keystore;
    @Basic(optional = false)
    @Column(name = "enabled")
    private boolean enabled;
    @Basic(optional = false)
    @Column(name = "status")
    private String status;
    @Basic(optional = true)
    @Column(name = "comment")
    private String comment;

    public MwPortalUser() {
    }

    public MwPortalUser(Integer id) {
        this.id = id;
    }

    public MwPortalUser(Integer id, String username, byte[] keystore, boolean enabled, String status) {
        this.id = id;
        this.username = username;
        this.keystore = keystore;
        this.enabled = enabled;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getKeystore() {
        return keystore;
    }

    public void setKeystore(byte[] keystore) {
        this.keystore = keystore;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MwPortalUser)) {
            return false;
        }
        MwPortalUser other = (MwPortalUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.intel.mountwilson.ms.data.MwPortalUser[ id=" + id + " ]";
    }
        
}
