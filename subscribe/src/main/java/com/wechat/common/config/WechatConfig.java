package com.wechat.common.config;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.wechat.common.service.WechatConfgService;

/**
 * 
 * @Description 微信配置文件
 * @author mHuang
 * @date 2015年7月13日 下午2:24:28 
 * @version V1.0.0
 */
@Component("wechatCofig")
public class WechatConfig {
	
	@Autowired
	private WechatConfgService wechatConfigService;
	
	@Value("#{wehchatConfig.WECHAT_SINGLE_GETSUBLIST_URL}")
	private String wechat_single_getsublist_url;
	
	@Value("#{wehchatConfig.WECHAT_MODIFY_GETSUBLIST_URL}")
	private String wechat_modify_getsublist_url;

	@Value("#{wehchatConfig.WECHAT_SERVER_TYPE}")
	private String wechat_server_type;
	
	@Value("#{wehchatConfig.WECHAT_TOKEN_REFRESHTIME}")
	private String wechat_token_refreshTime;
	
	@Value("#{wehchatConfig.WECHAT_ACCESS_TOKEN_URL}")
	private String wechat_access_token_url;
	
	@Value("#{wehchatConfig.WECHAT_GETUSER_URL}")
	private String wechat_getuser_url;
	
	public static  Long CONFIG_ID;
	public static  String APPSECRET;
	public static  String APPID;
	public static  String TOKEN;
	
	public void init(){
		Map<String, Object> configMap = wechatConfigService.queryConfig();
		APPSECRET = (String)configMap.get("appSecret");
		APPID = (String)configMap.get("appId");
		TOKEN = (String)configMap.get("token");
		CONFIG_ID = (Long)configMap.get("id");
		wechat_access_token_url = wechat_access_token_url.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		wechat_getuser_url = wechat_getuser_url.replace("APPID", APPID);
	}
	
	public static enum Config{
		serverToken("serverToken","tokenRefreshTime"),
		webTicket("webTicket","ticketRefreshTime");
		
		private String tokenType;
		private String refreshTime;

		public static String getValue(String name){
			for(Config config :Config.values()){
				if(StringUtils.equals(config.getTokenType(),name)){
					return config.getRefreshTime();
				}
			}
			return null;
		}
		
		Config(String tokenType,String refreshTime){
			this.tokenType = tokenType;
			this.refreshTime = refreshTime;
		}

		public String getTokenType() {
			return tokenType;
		}

		public void setTokenType(String tokenType) {
			this.tokenType = tokenType;
		}

		public String getRefreshTime() {
			return refreshTime;
		}

		public void setRefreshTime(String refreshTime) {
			this.refreshTime = refreshTime;
		}
	}
	
	public String getWechat_single_getsublist_url() {
		return wechat_single_getsublist_url;
	}

	public String getWechat_modify_getsublist_url() {
		return wechat_modify_getsublist_url;
	}

	public void setWechat_single_getsublist_url(String wechat_single_getsublist_url) {
		this.wechat_single_getsublist_url = wechat_single_getsublist_url;
	}

	public void setWechat_modify_getsublist_url(String wechat_modify_getsublist_url) {
		this.wechat_modify_getsublist_url = wechat_modify_getsublist_url;
	}

	public String getWechat_server_type() {
		return wechat_server_type;
	}

	public void setWechat_server_type(String wechat_server_type) {
		this.wechat_server_type = wechat_server_type;
	}

	public String getWechat_token_refreshTime() {
		return wechat_token_refreshTime;
	}

	public void setWechat_token_refreshTime(String wechat_token_refreshTime) {
		this.wechat_token_refreshTime = wechat_token_refreshTime;
	}

	public String getWechat_access_token_url() {
		return wechat_access_token_url;
	}

	public void setWechat_access_token_url(String wechat_access_token_url) {
		this.wechat_access_token_url = wechat_access_token_url;
	}

	public String getWechat_getuser_url() {
		return wechat_getuser_url;
	}

	public void setWechat_getuser_url(String wechat_getuser_url) {
		this.wechat_getuser_url = wechat_getuser_url;
	}
}
