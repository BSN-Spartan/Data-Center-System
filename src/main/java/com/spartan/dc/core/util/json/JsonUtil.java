package com.spartan.dc.core.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class JsonUtil<T>{
	public static Gson gons= new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	public static String toJson(Object src){
		return gons.toJson(src);
	}
	
	public static <T> T fromJson(String json,Type classd){
		return gons.fromJson(json, classd);
	}
	
}
