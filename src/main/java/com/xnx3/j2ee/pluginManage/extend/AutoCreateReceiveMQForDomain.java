package com.xnx3.j2ee.pluginManage.extend;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.xnx3.j2ee.util.ConsoleUtil;
import com.xnx3.j2ee.util.PluginUtil;
import com.xnx3.wangmarket.domain.mq.DomainMQ;

/**
 * 自动创建 domain 项目中，mq接收的监听
 * @author 管雷鸣
 *
 */
public class AutoCreateReceiveMQForDomain {
	

	/**
	 * 自动加载插件表的数据,执行
	 * @param c 扫描到继承 {@link PluginExtend#autoLoadPluginTableDate()}并实现了自动记载数据方法的 Class
	 * @param method 扫描到的{@link PluginExtend#autoLoadPluginTableDate()}
	 */
	public static void execute(Class c, Method method){
		String pluginId = PluginUtil.getPluginId(c.getName());
		if(pluginId == null || pluginId.length() == 0){
			return;
		}
		
		try {
			DomainMQ.receive(pluginId);
			ConsoleUtil.info("PluginExtend AutoCreateReceiveMQForDomain "+pluginId+" Finish");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
}