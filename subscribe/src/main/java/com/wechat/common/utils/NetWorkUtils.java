package com.wechat.common.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetWorkUtils {
	private static Logger logger = LoggerFactory.getLogger(NetWorkUtils.class);
	public static String sync(String url) {
        HttpURLConnection connection = null;
        try {
            URL address_url = new URL(url);
            connection = (HttpURLConnection) address_url.openConnection();
           
            connection.setDoOutput(true);  
            connection.setDoInput(true);
            connection.setUseCaches(false); 
            connection.setRequestMethod("GET"); 
            
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("accept-language", "zh-CN");
            //设置访问超时时间及读取网页流的超市时间,毫秒值
            System.setProperty("sun.net.client.defaultConnectTimeout","30000");
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");

            //得到访问页面的返回值
            int response_code = connection.getResponseCode();
            if (response_code == HttpURLConnection.HTTP_OK) {
            	String result = IOUtils.toString(connection.getInputStream());
            	logger.info("获取的数据是：",result);
            	return result;
            }
        } catch (MalformedURLException e) {
            logger.error("mal：",e);
        } catch (IOException e) {
        	logger.error("io异常：",e);
        } finally {
            if(connection !=null){
                connection.disconnect();
            }
        }
        return null;
    }

}
