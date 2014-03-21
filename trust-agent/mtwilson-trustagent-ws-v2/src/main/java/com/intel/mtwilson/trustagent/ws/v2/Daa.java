/*
 * Copyright (C) 2014 Intel Corporation
 * All rights reserved.
 */
package com.intel.mtwilson.trustagent.ws.v2;

import com.intel.mountwilson.common.TAException;
import com.intel.mountwilson.trustagent.commands.daa.ChallengeResponseDaaCmd;
import com.intel.mountwilson.trustagent.data.TADataContext;
import com.intel.mtwilson.My;
import com.intel.mtwilson.launcher.ws.ext.V2;
import com.intel.mtwilson.trustagent.TrustagentConfiguration;
import com.intel.mtwilson.trustagent.model.DaaChallenge;
import com.intel.mtwilson.trustagent.model.DaaResponse;
import java.io.IOException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 *
 * @author jbuhacoff
 */
@V2
@Path("/daa")
public class Daa {
    
    @POST
    @Path("/challenge")
    public DaaResponse daaChallenge(DaaChallenge daaChallenge) throws IOException, TAException {
        TrustagentConfiguration configuration = new TrustagentConfiguration(My.configuration().getConfiguration());
        if( !configuration.isDaaEnabled() ) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        }
        // TODO:  eliminate the TADataContext "middle man" and implement that daa command to accept DaaChallenge and return DaaResponse directly
        TADataContext context = new TADataContext();
        context.setDaaChallenge(daaChallenge.getChallenge());
        new ChallengeResponseDaaCmd(context).execute();
        DaaResponse daaResponse = new DaaResponse();
        daaResponse.setResponse(context.getDaaResponse());
        return daaResponse; 
    }
    
}
