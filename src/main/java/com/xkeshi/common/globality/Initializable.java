package com.xkeshi.common.globality;

/**
 * @author David
 *	在Spring容器初启动后执行初始化
 */
public interface Initializable {
	/**
	 * 初始化方法
	 */
	public void init();
}
