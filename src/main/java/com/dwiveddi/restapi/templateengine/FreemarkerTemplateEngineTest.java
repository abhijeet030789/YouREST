package com.dwiveddi.restapi.templateengine;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dwiveddi on 4/2/2018.
 */
public class FreemarkerTemplateEngineTest {

    @Test
    public void testGenerate() throws Exception {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> innerMap = new HashMap<>();
        innerMap.put("sd", "bhaasa");
        map.put("x", innerMap);
        map.put("y", "Maiyaa");
        System.out.println(FreemarkerTemplateEngine.getInstance().generate("abcdefg${x.sd}----${uuid()} ", map));

    }
}