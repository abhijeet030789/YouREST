package com.dwiveddi.restapi.templateengine;
import com.dwiveddi.restapi.variables.GlobalVariables;
import freemarker.template.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by dwiveddi on 4/2/2018.
 */
public class FreemarkerTemplateEngine {


    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final FreemarkerTemplateEngine INSTANCE = new FreemarkerTemplateEngine();

    public static FreemarkerTemplateEngine getInstance(){
        return INSTANCE;
    }

    private Configuration configuration;

    private FreemarkerTemplateEngine() {
        init();
    }

    private void init() {
        this.configuration = new Configuration(new Version(2, 3, 23));
        this.configuration.setDefaultEncoding("UTF-8");
        this.configuration.setLocale(Locale.US);
        this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public String generate(String templateSource, Map<String, Object> data) {
        try {
            data.put("uuid", (TemplateMethodModelEx)(list) -> UUID.randomUUID().toString().replaceAll("-", ""));
            data.put("randomString", new RandomString());
            data.put("eval", new Eval());
            data.put("evalObject", new EvalObject());
            data.put("evalJson", new EvalObjectAsJson());

            Template e = new Template("", templateSource, this.configuration);
            StringWriter generatedOutput = new StringWriter();
            e.process(data, generatedOutput);
            return generatedOutput.toString();
        } catch (TemplateException | IOException e) {
            throw new RuntimeException("Exception when replacing values " + e.getMessage(), e);
        }
    }

    private static class RandomString implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1 ){
                throw new IllegalArgumentException("Method 'randomString' requires exactly 1 integer input");
            }
            return randomString(((SimpleNumber)arguments.get(0)).getAsNumber().intValue());
        }
    }

    private static class Eval implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1 ){
                throw new IllegalArgumentException("Method 'eval' requires exactly 1 String input");
            }
            return GlobalVariables.eval(((SimpleScalar) arguments.get(0)).getAsString());
        }
    }

    private static class EvalObject implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1 ){
                throw new IllegalArgumentException("Method 'eval' requires exactly 1 String input");
            }
            return GlobalVariables.evalObject(((SimpleScalar) arguments.get(0)).getAsString());
        }
    }

    private static class EvalObjectAsJson implements TemplateMethodModelEx {
        @Override
        public Object exec(List arguments) throws TemplateModelException {
            if(arguments.size() != 1 ){
                throw new IllegalArgumentException("Method 'eval' requires exactly 1 String input");
            }
            return GlobalVariables.evalJson(((SimpleScalar) arguments.get(0)).getAsString());
        }
    }

    private static String randomString(int length) {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < length) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
}
