package com.dwiveddi.restapi.templateengine;
import freemarker.template.*;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dwiveddi on 4/2/2018.
 */
public class FreemarkerTemplateEngine {

    private static final Map<String, Object> GLOBAL_MAP =  new HashMap<>();
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void putToGlobalMap(String key, Object value){
        GLOBAL_MAP.put(key, value);
    }

    public Object getFromGlobalMap(String key){
        return GLOBAL_MAP.get(key);
    }

    public Map<String, Object> getGlobalMap(){
        //TOD: return a clone of the map
        return GLOBAL_MAP;
    }

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
            Template e = new Template("", templateSource, this.configuration);
            StringWriter generatedOutput = new StringWriter();
            e.process(data, generatedOutput);
            return generatedOutput.toString();
        } catch (TemplateException | IOException e) {
            throw new RuntimeException("Exception when replacing values " + e.getMessage(), e);
        }
    }

}
