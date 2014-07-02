/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.mtwilson.as.rest.v2.resource;

import com.intel.mtwilson.as.rest.v2.model.HostTlsCertificate;
import com.intel.mtwilson.as.rest.v2.model.HostTlsCertificateCollection;
import com.intel.mtwilson.as.rest.v2.model.HostTlsCertificateFilterCriteria;
import com.intel.mtwilson.as.rest.v2.model.HostTlsCertificateLocator;
import com.intel.mtwilson.as.rest.v2.repository.HostTlsCertificateRepository;
import com.intel.mtwilson.jaxrs2.NoLinks;
import com.intel.mtwilson.jaxrs2.server.resource.AbstractCertificateJsonapiResource;
import com.intel.mtwilson.launcher.ws.ext.V2;
import javax.ws.rs.Path;

/**
 *
 * @author ssbangal
 */
@V2
@Path("/hosts/{host_id}/tls-policy/certificates")
public class HostTlsCertificates extends AbstractCertificateJsonapiResource<HostTlsCertificate, HostTlsCertificateCollection, HostTlsCertificateFilterCriteria, NoLinks<HostTlsCertificate>, HostTlsCertificateLocator>{

    private HostTlsCertificateRepository repository;

    public HostTlsCertificates() {
        this.repository = new HostTlsCertificateRepository();
    }

    
    @Override
    protected HostTlsCertificateCollection createEmptyCollection() {
        return new HostTlsCertificateCollection();
    }

    @Override
    protected HostTlsCertificateRepository getRepository() {
        return repository;
    }
    
}
