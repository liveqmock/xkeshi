package com.xpos.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringMatcher {
	
	public static String getSpiltString (String context,String fir, String sec) {
		Matcher m=Pattern.compile(fir+"(.*?)"+sec).matcher(context);
        while (!m.hitEnd() && m.find()) {
        	return m.group(1);
        } 
        return null;
        
	}
}
