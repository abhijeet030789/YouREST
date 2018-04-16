package com.dwiveddi.restapi.variables;

import com.dwiveddi.restapi.templateengine.FreemarkerTemplateEngine;
import com.dwiveddi.restapi.utils.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;


/**
 * Created by dwiveddi on 4/14/2018.
 */
public class GlobalVariablesTest {


    @BeforeClass
    public void setUp() throws Exception {
        String generated = FreemarkerTemplateEngine.getInstance().generate(FileUtils.readFileAsString("src/test/resources/variables.json"), new HashMap<>());
        GlobalVariables.INSTANCE.putAll(new ObjectMapper().readValue(generated, Map.class));
    }

    @Test
    public void testEval() throws Exception {
        Assert.assertEquals(GlobalVariables.eval("${headers.uid}"), "muruganandham.kalimuthu@altisource.com");
        Assert.assertEquals(GlobalVariables.eval("${headers.arrayX[1].a}"), "a2");
        Assert.assertEquals(GlobalVariables.eval("${hostname}"), "10.0.60.219");
        Assert.assertEquals(GlobalVariables.eval("${headers.arrayY[0]}"), "s1");
        Assert.assertEquals(GlobalVariables.eval("${headers.jsonZ.x.y.z}"), "z1");
        //System.out.println(GlobalVariables.eval("${headers.jsonZ.x.y.z}"));

    }

    @Test
    public void testEvalObject() throws Exception {
        List<Map<String, Object>> oArr = (List<Map<String,Object>>)GlobalVariables.evalObject("${headers.arrayX}");
        for(Map<String, Object> json : oArr){
            json.put("uuu", "yyyy");
            System.out.println(json);
        }
        Object o = GlobalVariables.evalObject("${headers.jsonZ}");
        System.out.println(o);
        o = GlobalVariables.evalObject("${headers.jsonZ.x}");
        System.out.println(o);
        List<String> obj = (List<String>) GlobalVariables.evalObject("${headers.arrayY}");
        for (String s: obj){
            System.out.println(s+"hello");
        }
        System.out.println(o);
    }

    @Test
    public void evalObjectAsJson() throws Exception {
        System.out.println(GlobalVariables.evalJson("${headers.arrayX}"));
        System.out.println(GlobalVariables.evalJson("${headers.jsonZ}"));
        System.out.println(GlobalVariables.evalJson("${headers.jsonZ.x}"));
        //Object o = GlobalVariables.evalJson("${headers.arrayY}");
        //System.out.println(o.getClass().getSimpleName());
    }
}