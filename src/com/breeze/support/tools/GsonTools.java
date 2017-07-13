/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.breeze.support.tools;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 *
 * @author Administrator
 */
public class GsonTools {
	public static final int TYPE_BASE =0;
	public static final int TYPE_OBJECT =1;
	public static final int TYPE_ARRAY = 2;
	
    public static  Map<String,Object>parserJsonMapObj(String json){
        GsonBuilder gbuilder = new GsonBuilder();
        gbuilder.registerTypeAdapter(new com.google.gson.reflect.TypeToken<Map<String,Object>>() {}.getType(), new DesMap());
        Gson gson = gbuilder.create();
        return gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String,Object>>() {}.getType());
    }
    
    public static  Map<String,GsonTools.OS>parserJsonMapStrObj(String json){
        GsonBuilder gbuilder = new GsonBuilder();
        gbuilder.registerTypeAdapter(new com.google.gson.reflect.TypeToken<Map<String,GsonTools.OS>>() {}.getType(), new MapDes1L());
        Gson gson = gbuilder.create();
        return gson.fromJson(json, new com.google.gson.reflect.TypeToken<Map<String,GsonTools.OS>>() {}.getType());
    }
    
    public static void main(String[] args){
        String input = "{a:'abc',b:[1,2,3]}";
        Map<String,Object>result = parserJsonMapObj(input);
        System.out.println(result);
        System.out.println("-----------------------");
        Map<String,GsonTools.OS>r = parserJsonMapStrObj(input);
        System.out.println(r);
    }
    
    public static class OS{
    	String s;
    	int type;
    	OS(){
    		
    	}
    	public OS(String _s,int _type){
    		this.s = _s;
    		this.type = _type;
    	}
    	public String toString(){
    		return this.s;
    	}
    	public Object toObject(Class c){
    		Class tmpC = c.isArray()?c.getComponentType():c;
    		String tmpStr = tmpC.getName().replaceAll("\\.", Matcher.quoteReplacement("\\.")).replaceAll("\\$", Matcher.quoteReplacement("\\$"))+"\\.";
    		tmpStr = this.s.replaceAll(tmpStr, "");
    		
    		Gson gson = new Gson();
    		Object result = gson.fromJson(tmpStr, c);
    		return result;
    	}
    }
}


class DesMap implements JsonDeserializer<Map<String, Object>> {

    @Override
    public Map<String, Object> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        if (je.isJsonNull()){
            return null;
        }
        if (je.isJsonPrimitive()){
            return null;
        }
        if (je.isJsonArray()){
            return null;
        }
        return this.handleObj(je);
    }
    private Object deserialize(JsonElement je){
        if (je.isJsonNull()){
            return null;
        }
        if (je.isJsonPrimitive()){
            return this.handlePrimitive(je);
        }
        if (je.isJsonArray()){
            return this.handleArray(je);
        }
        return this.handleObj(je);
    }
    private Object handlePrimitive(JsonElement je){
        JsonPrimitive json = je.getAsJsonPrimitive();
        if (json.isBoolean()){
            return json.getAsBoolean();
        }
        if (json.isString()){
            return json.getAsString();
        }
        BigDecimal bigdec = json.getAsBigDecimal();
        try{
            bigdec.toBigIntegerExact();
            try{
                return bigdec.intValue();
            }catch(Exception ee){}
            return bigdec.longValue();
        }catch(Exception e){}
        return bigdec.doubleValue();
    }
    
    private Object[] handleArray(JsonElement je){
        JsonArray jarr = je.getAsJsonArray();
        Object[] result = new Object[jarr.size()];
        for (int i=0;i<jarr.size();i++){
            Object ele = this.deserialize(jarr.get(i));
            result[i] = ele;
        }
        return result;
    }
    private Map<String,Object> handleObj(JsonElement je){
        Map<String,Object>result = new HashMap<String,Object>();
        for (Map.Entry<String,JsonElement> entry:je.getAsJsonObject().entrySet()){
            JsonElement ele = (JsonElement)entry.getValue();
            Object value = this.deserialize(ele);
            result.put(entry.getKey(), value);
        }
        return result;
    }
}

class MapDes1L implements JsonDeserializer<Map<String, GsonTools.OS>> {

    @Override
    public Map<String, GsonTools.OS> deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        if (je.isJsonNull()){
            return null;
        }
        if (je.isJsonPrimitive()){
            return null;
        }
        if (je.isJsonArray()){
            return null;
        }
        return this.handleObj(je);
    }

    private Map<String,GsonTools.OS> handleObj(JsonElement je){
        Map<String,GsonTools.OS>result = new HashMap<String,GsonTools.OS>();
        for (Map.Entry<String,JsonElement> entry:je.getAsJsonObject().entrySet()){
            JsonElement ele = (JsonElement)entry.getValue();
            GsonTools.OS os = new GsonTools.OS();
            if (ele.isJsonArray()){
            	os.s = ele.toString();
            	os.type = GsonTools.TYPE_ARRAY;
            }else if(ele.isJsonObject()){
            	os.s = ele.toString();
            	os.type = GsonTools.TYPE_OBJECT;
            }else if(ele.isJsonPrimitive()){
            	os.s = ele.getAsString();
            	os.type = GsonTools.TYPE_BASE;
            }else{
            	continue;
            }
            result.put(entry.getKey(), os);
        }
        return result;
    }
}