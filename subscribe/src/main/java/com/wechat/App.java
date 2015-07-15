package com.wechat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wechat.common.config.WechatConfig;

/**
 * 
 * @Description 
 * @author mHuang
 * @date 2015年7月13日 下午12:51:41 
 * @version V1.0.0
 */
public class App 
{
	public static Logger logger =  LoggerFactory.getLogger(App.class);
	
	public volatile static ApplicationContext applicationContext = null;
    
	public static void main( String[] args )
    {
    	logger.info("===begin start====");
    	applicationContext= new ClassPathXmlApplicationContext("spring.xml");
    	//启动spring 加载配置
    	WechatConfig wechatConfig =  (WechatConfig)applicationContext.getBean("wechatCofig");
    	wechatConfig.init();
    	logger.info("===end start====");
    }
}
