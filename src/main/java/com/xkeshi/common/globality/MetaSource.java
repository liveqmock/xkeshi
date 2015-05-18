package com.xkeshi.common.globality;

import com.xkeshi.pojo.meta.Meta;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 全局Meta数据
 * 
 * @author David
 */
public class MetaSource {


	public static Long getIDByName(List<? extends Meta> metaList, String name) {
		for (Meta meta : metaList) {
			if (StringUtils.equalsIgnoreCase(meta.getName(), name)) {
				return meta.getId();
			}
		}
		return null;
	}

	public static String getNameByID(List<? extends Meta> metaList, Long id) {
		if (id == null) {
			return null;
		}
		for (Meta meta : metaList) {
			if (meta.getId().equals(id)) {
				return meta.getName();
			}
		}
		return null;
	}

}