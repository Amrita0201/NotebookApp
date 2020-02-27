package com.tarento.notebook.util;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public class JSONObjectUtil {
	
	@Autowired
    public ObjectMapper mapper;
    @Autowired
    public Gson gson;

	public String getJsonString(Object object) throws JsonProcessingException {
        //initialize();
        if(mapper != null){
            return  mapper.writeValueAsString(object);
        }
        if(gson != null){
            return gson.toJson(object);
        }
        return null;
    }
}
