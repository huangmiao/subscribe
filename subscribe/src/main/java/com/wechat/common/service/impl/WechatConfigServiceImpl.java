package com.wechat.common.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.wechat.common.config.WechatConfig;
import com.wechat.common.service.WechatConfgService;

/**
 * 
 * @Description 微信配置service
 * @author mHuang
 * @date 2015年7月13日 上午11:36:52 
 * @version V1.0.0
 */
@Service("wechatConfigService")
public class WechatConfigServiceImpl implements WechatConfgService{

	@Autowired
	private WechatConfig wechatConfig;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<String, Object> queryConfig() {
		String queryConfig = "SELECT id,appId,appSecret,token,payKey,payMchId from t_wechat_config where isValid = 1 and serverType = ?";
		return jdbcTemplate.queryForMap(queryConfig,new Object[]{wechatConfig.getWechat_server_type()});
	}
}
