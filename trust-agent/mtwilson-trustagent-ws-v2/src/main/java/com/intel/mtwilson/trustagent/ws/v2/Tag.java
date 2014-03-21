/*
 * Copyright (C) 2014 Intel Corporation
 * All rights reserved.
 */
package com.intel.mtwilson.trustagent.ws.v2;

import com.intel.mountwilson.common.TAException;
import com.intel.mountwilson.trustagent.commands.SetAssetTag;
import com.intel.mountwilson.trustagent.data.TADataContext;
import com.intel.mtwilson.launcher.ws.ext.V2;
import com.intel.mtwilson.trustagent.model.TagWriteRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author jbuhacoff
 */
@V2
@Path("/tag")
public class Tag {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void writeTag(TagWriteRequest tagInfo, @Context HttpServletResponse response) throws TAException {
        TADataContext context = new TADataContext();
        context.setAssetTagHash(Hex.encodeHexString(tagInfo.getTag()));
        new SetAssetTag(context).execute();
        response.setStatus(Response.Status.NO_CONTENT.getStatusCode());
    }
    
}
