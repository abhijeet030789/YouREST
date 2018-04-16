package com.dwiveddi.restapi.variables;

import com.dwiveddi.restapi.templateengine.FreemarkerTemplateEngine;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;

/**
 * Created by dwiveddi on 4/10/2018.
 */
public final class GlobalVariables extends HashMap<String, Object>{

    public static final GlobalVariables INSTANCE = new GlobalVariables();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private GlobalVariables(){
    }

    public static String eval(String template){
        return FreemarkerTemplateEngine.getInstance().generate(template, INSTANCE);
    }

    public static Object evalObject(String expression){
        validateExpression(expression);
        String arr[] = expression.trim().replace("$", "").replace("{", "").replace("}", "").split("\\.");
        Map<String, Object> map = INSTANCE;
        for(int i = 0; i < arr.length; i++){
            if(i == arr.length -1){//last element
                return map.get(arr[i]);
            }else{
                map = (Map<String, Object>)map.get(arr[i]);
            }
        }
        return null;
    }

    public static String evalJson(String expression){
        try {
            return OBJECT_MAPPER.writeValueAsString(evalObject(expression));
        } catch (IOException e) {
            throw new RuntimeException(format("Exception while evalAsString(%s))",expression), e);
        }
    }

    private static void validateExpression(String expression){
        //validate if it is in format ${[a-z.A-Z.0-9]}
    }
}
