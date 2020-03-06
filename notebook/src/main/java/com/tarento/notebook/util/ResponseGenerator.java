
package com.tarento.notebook.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tarento.notebook.constants.JsonKey;
import com.tarento.notebook.models.ResponseContainer;

public class ResponseGenerator {
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String failureResponse(ResponseContainer responseContainer) throws JsonProcessingException {

        ObjectNode response = objectMapper.createObjectNode();
        response.put(JsonKey.STATUS_CODE.getMessage(), responseContainer.getStatusCode());
        response.put(JsonKey.STATUS_MESSAGE.getMessage(), responseContainer.getStatusMessage());
        response.put(JsonKey.ERROR_MESSAGE.getMessage(), responseContainer.getMessage());

        return JSONObjectUtil.getJsonString(response);
    }

    /**
     * this method will crate success response and send to controller.
     *
     * @param obj Object
     * @return ObjectNode object.
     */
    public static String successResponse(ResponseContainer responseContainer, Object obj) throws JsonProcessingException {

        ObjectNode response = objectMapper.createObjectNode();
        response.put(JsonKey.STATUS_CODE.getMessage(), responseContainer.getStatusCode());
        response.put(JsonKey.STATUS_MESSAGE.getMessage(), responseContainer.getStatusMessage());
        if (obj == null) {
            response.putPOJO(JsonKey.RESPONSE_DATA.getMessage(), objectMapper.createObjectNode());
        } else {
            response.putPOJO(JsonKey.RESPONSE_DATA.getMessage(), obj);
        }

        return JSONObjectUtil.getJsonString(response);
    }

    /**
     * this method will crate success response and send to controller.
     *
     * @return ObjectNode object.
     */
    public static String successResponse(ResponseContainer responseContainer) throws JsonProcessingException {

        ObjectNode response = objectMapper.createObjectNode();
        response.put(JsonKey.STATUS_CODE.getMessage(), responseContainer.getStatusCode());
        response.put(JsonKey.STATUS_MESSAGE.getMessage(), responseContainer.getStatusMessage());
        response.putPOJO(JsonKey.RESPONSE_DATA.getMessage(), objectMapper.createObjectNode());

        return JSONObjectUtil.getJsonString(response);
    }
}

