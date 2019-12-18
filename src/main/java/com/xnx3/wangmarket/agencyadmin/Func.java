package com.xnx3.wangmarket.agencyadmin;

import com.xnx3.j2ee.func.SessionUtil;
import com.xnx3.j2ee.shiro.UserBean;
import com.xnx3.wangmarket.agencyadmin.entity.Agency;
import com.xnx3.wangmarket.agencyadmin.entity.AgencyData;

/**
 * 常用的一些函数
 * @author 管雷鸣
 */
public class Func {
	
	/**
	 * 获取当前登陆用户的上级用户代理信息。如果当前用户的上级有，且是代理的话
	 * @return {@link Agency} 或 null
	 */
	public static Agency getParentAgency(){
		UserBean agencyBean = SessionUtil.getUserBeanForSession();
		if(agencyBean == null){
			return null;
		}else{
			return agencyBean.getParentAgency();
		}
	}
	
	/**
	 * 获取当前登陆用户的上级用户代理信息的变长表 (agency_data) 数据。如果当前用户的上级有，且是代理的话
	 * @return {@link AgencyData} 或 null
	 */
	public static AgencyData getParentAgencyData(){
		UserBean agencyBean = SessionUtil.getUserBeanForSession();
		if(agencyBean == null){
			return null;
		}else{
			return agencyBean.getParentAgencyData();
		}
	}
	
	
	/**
	 * 获取当前登陆用户的代理信息。如果当前用户是代理的话
	 * @return {@link Agency} 或 null
	 */
	public static Agency getMyAgency(){
		UserBean agencyBean = SessionUtil.getUserBeanForSession();
		if(agencyBean == null){
			return null;
		}else{
			return agencyBean.getMyAgency();
		}
	}
	

	/**
	 * 获取当前代理信息的变长表 (agency_data) 数据。如果当前用户的上级有，且是代理的话
	 * @return {@link AgencyData} 或 null
	 */
	public static AgencyData getMyAgencyData(){
		UserBean agencyBean = SessionUtil.getUserBeanForSession();
		if(agencyBean == null){
			return null;
		}else{
			return agencyBean.getMyAgencyData();
		}
	}
}
