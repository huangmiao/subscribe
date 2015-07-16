修改执行时间
修改 SubscribeServiceImpl 下的doWork方法里的spring定时器注解时间配法即可
修改数据库 
databse.propertics 里修改数据库配置
修改微信   
wechat.propertics 修改执行的类型 （测试或者正式）
打包 
通过maven执行install打包App.jar copy target包下的App.jar 打包到服务端。
执行 copy start包下的系统版本到服务器端执行。


注：修改配置后请重新通过maven install重新打包。
