package com.xkeshi.shiro;

import java.io.IOException;

import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.xkeshi.shiro.freemarker.ShiroTags;

import freemarker.template.TemplateException;
/**
 *  
 * 添加FreeMarker支持shiro
 *
 */
public class ShiroTagFreeMarkerConfigurer extends FreeMarkerConfigurer {
	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		super.afterPropertiesSet();
		this.getConfiguration().setSharedVariable("shiro", new ShiroTags());
	}
}
