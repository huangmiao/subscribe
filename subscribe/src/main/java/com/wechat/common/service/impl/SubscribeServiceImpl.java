package com.wechat.common.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wechat.common.config.WechatConfig;
import com.wechat.common.service.SubscribeService;
import com.wechat.common.utils.NetWorkUtils;

/**
 * 
 * @Description 关注service实现
 * @author mHuang
 * @date 2015年7月13日 上午9:28:38 
 * @version V1.0.0
 */
@Component
public class SubscribeServiceImpl implements SubscribeService{

	public Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private WechatConfig config;
	
	/**
	 * 
	 * @Description 获取token
	 * @author mHuang
	 * @return
	 */
	public String getToken(){
		return getToken(WechatConfig.Config.serverToken.name(), WechatConfig.CONFIG_ID);
	}
	
	/**
	 * 
	 * @Description 存放数据
	 * @author mHuang
	 * @param jsonObj
	 */
	public void data(JSONObject jsonObj){
		if(jsonObj.containsKey("data")){
			List openList =  jsonObj.getJSONObject("data").getJSONArray("openid");
			for(Object openId:openList){
				if(querySubscribe(openId) == 0){
					String usr = NetWorkUtils.sync(
							config.getWechat_getuser_url().
							replace("ACCESS_TOKEN", getToken()).
							replace("OPENID", (CharSequence) openId));
					String nickname = (String) JSON.parseObject(usr).get("nickname");
					updateSubscribe(openId, nickname);
				}
			}
		}
	}
	
	/**
	 * 
	 * @Description 判断数据
	 * @author mHuang
	 * @param jsonObj
	 */
	public void next(JSONObject jsonObj){
		if(jsonObj.containsKey("next_openid") && 
				StringUtils.isNotEmpty(jsonObj.getString("next_openid"))){
			String nextOpen = NetWorkUtils.sync(
				config.getWechat_modify_getsublist_url().
					replace("ACCESS_TOKEN", getToken()).
					replace("NEXT_OPENID1", jsonObj.getString("next_openid"))
			);
			JSONObject nextJson = JSON.parseObject(nextOpen);
			if(nextJson.containsKey("next_openid") && 
					StringUtils.isNotEmpty(nextJson.getString("next_openid"))){
				next(nextJson);
			}
		}
		data(jsonObj);
	}
	/**
	 * 
	 * Description  每天2点获取1次
	 * @see com.wechat.common.service.SubscribeService#doWork()
	 */
	@Scheduled(cron="* * 2 * * ?")
	public void doWork() {
		if(StringUtils.isNotEmpty(getToken())){
			String res = NetWorkUtils.sync(config.getWechat_single_getsublist_url().replace("ACCESS_TOKEN", getToken()));
			final JSONObject jsonObj = JSON.parseObject(res);
			logger.info("====正在组装数据中====");
			next(jsonObj);
			logger.info("====数据组装完成====");
		}
	}
	public String getToken(String type,Long id){
		String token = (String)this.queryToken(type, id);
		if(StringUtils.isEmpty(token)){
			String tmp;
			if(StringUtils.equals(type, WechatConfig.Config.serverToken.name())){
				tmp = NetWorkUtils.sync(config.getWechat_access_token_url());
			}else if(StringUtils.equals(type, WechatConfig.Config.webTicket.name())){
				tmp = NetWorkUtils.sync(config.getWechat_access_token_url().replace("ACCESS_TOKEN", getToken(WechatConfig.Config.serverToken.name(),WechatConfig.CONFIG_ID)));
			}else{
				return null;
			}
			JSONObject json = JSON.parseObject(tmp);
			if(!CollectionUtils.isEmpty(json)){
				if(StringUtils.equals(type, WechatConfig.Config.serverToken.name())){
					if(json.containsKey("access_token")){
						token = json.getString("access_token");
						updateToken(type,token,WechatConfig.CONFIG_ID);
					}else{
						logger.info("微信返回access_token错误:"+json);
					}
				}else{
					if(json.containsKey("ticket")){
						token = json.getString("ticket");
						updateToken(type,token,WechatConfig.CONFIG_ID);
					}else{
						logger.info("微信返回jsticket参数错误:"+json);
					}
				}
			}else{
				logger.info("微信返回access_token错误:"+tmp);
			}
		}
		return token;
	}
	
	public int updateToken(String type,String value,Long id){
		String updateTokenSql = null;
		
		if(StringUtils.equals(type, WechatConfig.Config.serverToken.name())){
			updateTokenSql = "update t_wechat_config set serverToken=? , tokenRefreshTime = now() where id = ?";
		}else if(StringUtils.equals(type, WechatConfig.Config.webTicket.name())){
			updateTokenSql = "update t_wechat_config set webTicket=? , ticketRefreshTime = now() where id = ?";
		}else{
			return -1;
		}
		return jdbcTemplate.update(updateTokenSql,new Object[]{value,id});
	}
	
	/**
	 * 
	 * @Description 获取access_token
	 * @author mHuang
	 * @param type
	 * @param id
	 * @return
	 */
	public String queryToken(final String type,Long id){
		String queryTokenSql = null;
		
		if(StringUtils.equals(type, WechatConfig.Config.serverToken.name())){
			queryTokenSql = " SELECT serverToken from t_wechat_config where id = ? and unix_timestamp(now()) - unix_timestamp(tokenRefreshTime) < ? ";
		}else if(StringUtils.equals(type, WechatConfig.Config.webTicket.name())){
			queryTokenSql = " SELECT webTicket from t_wechat_config where id = ? and unix_timestamp(now()) - unix_timestamp(ticketRefreshTime) < ? ";
		}else{
			return null;
		}
		try{
			//jdbc查询数据为null的时候会出现的异常。与目标不符合
			return jdbcTemplate.queryForObject(queryTokenSql,new Object[]{id,config.getWechat_token_refreshTime()}, String.class);
		}catch(EmptyResultDataAccessException e){
			logger.error("查询数据列不对的异常（INFO）：",e);
			return null;
		}
	}
	
	public int updateSubscribe(Object openId,String nickName){
		String sql = "insert into t_usr_subscribe(openId,nickName) values(?,?) ON DUPLICATE KEY UPDATE nickname=?";
		return jdbcTemplate.update(sql, new Object[]{openId,nickName,nickName});
	}
	
	public Long querySubscribe(Object openId){
		String sql = "select count(*) from t_usr_subscribe where openId = ? and  nickname <> ''";
		return jdbcTemplate.queryForObject(sql,new Object[]{openId},Long.class);
	}
}
