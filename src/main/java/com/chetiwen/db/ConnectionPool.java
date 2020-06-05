package com.chetiwen.db;


import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.chetiwen.util.PropertyUtil;

import java.beans.PropertyVetoException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
 <!-- 基本属性 url、user、password -->
 <property name="driverClass" value="${jdbc.driverClassName}" />
 <property name="jdbcUrl" value="${jdbc.url}" />
 <property name="user" value="${jdbc.username}" />
 <property name="password" value="${jdbc.password}" />
 <!-- 连接池中保留的最小连接数。-->
 <property name="minPoolSize" value="3" />
 <!-- 连接池中保留的最大连接数。-->
 <property name="maxPoolSize" value="15" />
 <!-- 初始化时获取的连接数，取值应在minPoolSize与maxPoolSize之间 -->
 <property name="initialPoolSize" value="3" />
 <!-- 当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。-->
 <property name="acquireIncrement" value="3" />
 <!-- 连接在池中保持空闲而不被空闲连接回收器线程(如果有)回收的最小时间值，单位秒。若为0则永不丢弃。-->
 <property name="maxIdleTime" value="120" />
 <!-- 在空闲连接回收器线程运行期间休眠的时间值,以秒为单位 -->
 <property name="idleConnectionTestPeriod" value="60" />
 <!-- 用来检测连接是否有效的sql -->
 <property name="preferredTestQuery" value="select 1" />
 <!-- 定义在从数据库获取新连接失败后重复尝试的次数。-->
 <property name="acquireRetryAttempts" value="10" />
 <!-- 因性能消耗大请只在需要的时候使用它。如果设为true那么在每个connection提交的时候都将校验其有效性。
 建议使用idleConnectionTestPeriod方法来提升连接测试的性能。-->
 <property name="testConnectionOnCheckout" value="false" />
 <!-- c3p0是异步操作的，缓慢的JDBC操作通过帮助进程完成。扩展这些操作可以有效的提升性能，通过多线程实现多个操作同时被执行 -->
 <property name="numHelperThreads" value="32" />
 </bean>
 */
public class ConnectionPool {
    public static String dbDriver = PropertyUtil.readValue("database.driver");
    public static String jdbcUrl = PropertyUtil.readValue("database.jdbc.url");
    public static String userName = PropertyUtil.readValue("database.user");
    public static String password = PropertyUtil.readValue("database.password");
    public static boolean isProduction = false;
    public static String hostName;

    private static ComboPooledDataSource comboPooledDataSource;

    static {
        try {
            hostName = InetAddress.getLocalHost().getHostName();
            if (PropertyUtil.readValue("production.hostname").equals(hostName)) {
                isProduction = true;
            }

            if (isProduction) {
                jdbcUrl = PropertyUtil.readValue("database.jdbc.url.production");
                userName = PropertyUtil.readValue("database.user.production");
                password = PropertyUtil.readValue("database.password.production");
            }

            try {
                Class.forName(dbDriver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            comboPooledDataSource = new ComboPooledDataSource();
            comboPooledDataSource.setDriverClass(dbDriver);
            comboPooledDataSource.setJdbcUrl(jdbcUrl);
            comboPooledDataSource.setUser(userName);
            comboPooledDataSource.setPassword(password);

            comboPooledDataSource.setMaxPoolSize(20);
            comboPooledDataSource.setMinPoolSize(5);
            comboPooledDataSource.setInitialPoolSize(5);
            comboPooledDataSource.setPreferredTestQuery("select 1");
            comboPooledDataSource.setIdleConnectionTestPeriod(60);
            comboPooledDataSource.setMaxIdleTime(120);
            comboPooledDataSource.setTestConnectionOnCheckin(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public synchronized static Connection getConnection() {
        Connection connection = null;
        try {
            connection = comboPooledDataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return connection;
        }
    }

}
