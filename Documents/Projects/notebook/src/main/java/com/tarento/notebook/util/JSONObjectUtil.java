package com.tarento.notebook.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONObjectUtil {

	public static String getJsonString(Object object) throws JsonProcessingException {

	    ObjectMapper mapper = new ObjectMapper();

	    //initialize();
        if(mapper != null){
            return  mapper.writeValueAsString(object);
        }
        return null;
    }
}
