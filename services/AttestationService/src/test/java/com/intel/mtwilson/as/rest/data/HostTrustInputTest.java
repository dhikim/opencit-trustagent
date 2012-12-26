package com.intel.mtwilson.as.rest.data;

import com.intel.mtwilson.datatypes.OpenStackHostTrustLevelQuery;
import com.intel.mtwilson.datatypes.ErrorCode;
import com.intel.mountwilson.as.common.ValidationException;
import static com.jayway.restassured.path.json.JsonPath.with;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.intel.mtwilson.datatypes.Hostname;
import com.intel.mountwilson.as.common.ASException;
import java.util.Arrays;

/**
 * These tests verify that the data model serializes and de-serializes properly.
 *
 * @author jbuhacoff
 */
public class HostTrustInputTest {
    private static ObjectMapper mapper = new ObjectMapper();

    /**
     * Sample serialized object.
     * {"count":2,"pcrmask":"some pcr mask","hosts":["test-host-1","ESX host 2"]}
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void writeJSON() throws JsonGenerationException,
            JsonMappingException, IOException {
        OpenStackHostTrustLevelQuery test = new OpenStackHostTrustLevelQuery();
        test.count = 2;
        test.pcrMask = "some pcr mask";
        test.hosts = new Hostname[] { new Hostname("test-host-1"), new Hostname("ESX host 2") };
        
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        mapper.writeValue(stream, test);

        String json = stream.toString();
        System.out.println(json);

        assertEquals(2, with(json).getInt("count"));
        with(json).getString("pcrmask").equals("userName");
        Arrays.asList(with(json).getList("hosts")).containsAll(Arrays.asList(new String[] { "test-host-1", "ESX host 2" }));
    }
    
    /**
     * Sample serialized object.
     * {"count":2,"pcrmask":"some pcr mask","hosts":["test-host-1","ESX host 2"]}
     *
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Test
    public void readJSON() throws JsonGenerationException,
            JsonMappingException, IOException {
        
    	InputStream in = getClass().getResourceAsStream("HostTrustInputTest.sample.json");
    	try {
	        OpenStackHostTrustLevelQuery obj = mapper.readValue(in, OpenStackHostTrustLevelQuery.class);
	
	        assertEquals(2, obj.count);
	        assertEquals("some pcr mask",obj.pcrMask);
	        assertEquals("test-host-1",obj.hosts[0].toString());
	        assertEquals("ESX host 2",obj.hosts[1].toString());
    	}
    	finally {
    		if( in != null )
    			in.close();
    	}
    }

    
    @Test(expected=ValidationException.class)
    public void nullHostnameThrowsException() {  // datatype.Hostname
        Hostname h = new Hostname(null);
        System.err.println(h.toString());
    }

    @Test(expected=ValidationException.class)
    public void emptyHostnameThrowsException() {  // datatype.Hostname
    	Hostname h = new Hostname("");
        System.err.println(h.toString());
    }

    @Test(expected=ValidationException.class)
    public void invalidHostnameThrowsException() {  // datatype.Hostname
    	Hostname h = new Hostname("invalid, hostname has comma in it");
        System.err.println(h.toString());
    }

    @Test(expected=ASException.class)
    public void convertIllegalArgumentExceptionToASException() {
        try {
        	Hostname h = new Hostname("");            
            System.err.println(h.toString());
        }
        catch(IllegalArgumentException e) {
            throw new ASException(ErrorCode.AS_MISSING_INPUT, e.getMessage());
        }
    }
}
