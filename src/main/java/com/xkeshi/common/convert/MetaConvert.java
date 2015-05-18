package com.xkeshi.common.convert;


import com.xkeshi.common.globality.GlobalSource;
import com.xkeshi.pojo.meta.Meta;
import org.dozer.CustomConverter;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * meta对象和id之间的转换 <br>
 * @author David 
 */
public class MetaConvert implements CustomConverter {


	@SuppressWarnings("unchecked")
	@Override
	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class<?> destinationClass,
			Class<?> sourceClass) {
		if (null != sourceFieldValue) {
			if (sourceFieldValue instanceof Long) {
				List<Meta> metaList = null;

				Field[] fields = GlobalSource.class.getDeclaredFields();

				for (Field field : fields) {
					Class<?> fieldClazz = field.getType();
					if (fieldClazz.isAssignableFrom(List.class)) {
						Type type = field.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型
						if (type == null)
							continue;
						if (type instanceof ParameterizedType) // 判断泛型参数的类型
						{
							ParameterizedType pt = (ParameterizedType) type;
							Class<?> genericClazz = (Class<?>) pt
									.getActualTypeArguments()[0]; // 得到泛型里的class类型对象。
							if (genericClazz == destinationClass) {
								try {
									metaList = (List<Meta>) field.get(null);
									for (Meta meta : metaList) {
										if (meta.getId().equals(sourceFieldValue)) {
											return meta;
										}
									}
									break;
								} catch (IllegalArgumentException
										| IllegalAccessException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			} else if (sourceFieldValue instanceof Meta) {
				Meta meta = (Meta) sourceFieldValue;
				return meta.getId();
			}
		}
		return null;
	}

}
