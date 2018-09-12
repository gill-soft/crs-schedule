package com.gillsoft;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Configuration
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@ComponentScans(value = {
//		@ComponentScan("com.gillsoft.dao"),
		@ComponentScan("com.gillsoft")
	})
public class AppConfig {
	
	private static Logger LOGGER = Logger.getLogger(AppConfig.class.getName());

	private static final String SHOW_SQL = "hibernate.show_sql";
	private static final String FORMAT_SQL = "hibernate.format_sql";
	private static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
	private static final String REVISION_ON_COLLECTION_CHANGE = "org.hibernate.envers.revision_on_collection_change";
	private static final String STORE_DATA_AT_DELETE = "org.hibernate.envers.store_data_at_delete";
	
	private static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
	private static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
	private static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
	private static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
	private static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
	
	private static final String SSH_USE = "ssh.use";
	private static final String SSH_HOST = "ssh.host";
	private static final String SSH_PORT = "ssh.port";
	private static final String SSH_USER = "ssh.user";
	private static final String SSH_KEY = "ssh.private_key";
	private static final String SSH_LOCAL_HOST = "ssh.local.host";
	private static final String SSH_LOCAL_PORT = "ssh.local.port";
	private static final String SSH_REMOTE_PORT = "ssh.remote.port";
	
	private static final String POSTGRESQL_DRIVER = "postgresql.driver";
	private static final String POSTGRESQL_JDBCURL = "postgresql.jdbcUrl";
	private static final String POSTGRESQL_USERNAME = "postgresql.username";
	private static final String POSTGRESQL_PASSWORD = "postgresql.password";

	@Autowired
	private Environment env;

	@Bean
	public DataSource getDataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setDriverClassName(env.getProperty(POSTGRESQL_DRIVER));
		dataSource.setUrl(env.getProperty(POSTGRESQL_JDBCURL));
		dataSource.setUsername(env.getProperty(POSTGRESQL_USERNAME));
		dataSource.setPassword(env.getProperty(POSTGRESQL_PASSWORD));
		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean getSessionFactory() {
		if (isUseSsh()) {
			tunnel();
		}
		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setDataSource(getDataSource());

		Properties props = new Properties();

		// Setting Hibernate properties
		props.put(SHOW_SQL, env.getProperty(SHOW_SQL));
		props.put(FORMAT_SQL, env.getProperty(FORMAT_SQL));
		props.put(HBM2DDL_AUTO, env.getProperty(HBM2DDL_AUTO));
		props.put(REVISION_ON_COLLECTION_CHANGE, false);
		props.put(STORE_DATA_AT_DELETE, true);
		props.put("hibernate.jdbc.lob.non_contextual_creation", "true");
		props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL9Dialect");
		props.put("hibernate.temp.use_jdbc_metadata_defaults", "false");

		// Setting C3P0 properties
		props.put(C3P0_MIN_SIZE, env.getProperty(C3P0_MIN_SIZE));
		props.put(C3P0_MAX_SIZE, env.getProperty(C3P0_MAX_SIZE));
		props.put(C3P0_TIMEOUT, env.getProperty(C3P0_TIMEOUT));
		props.put(C3P0_ACQUIRE_INCREMENT, env.getProperty(C3P0_ACQUIRE_INCREMENT));
		props.put(C3P0_MAX_STATEMENTS, env.getProperty(C3P0_MAX_STATEMENTS));

		factoryBean.setHibernateProperties(props);
		factoryBean.setPackagesToScan("com.gillsoft.entity");

		return factoryBean;
	}

	@Bean
	public HibernateTransactionManager getTransactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(getSessionFactory().getObject());
		return transactionManager;
	}
	
	private boolean isUseSsh() {
		return Boolean.valueOf(env.getProperty(SSH_USE));
	}
	
	private void tunnel() {
		JSch jsch = new JSch();
		try {
			jsch.addIdentity(AppConfig.class.getClassLoader().getResource(env.getProperty(SSH_KEY)).getPath());
			Session session = jsch.getSession(env.getProperty(SSH_USER),
					env.getProperty(SSH_HOST), Integer.valueOf(env.getProperty(SSH_PORT)));
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			session.setPortForwardingL(Integer.valueOf(env.getProperty(SSH_LOCAL_PORT)),
					env.getProperty(SSH_LOCAL_HOST), Integer.valueOf(env.getProperty(SSH_REMOTE_PORT)));
		} catch (JSchException e) {
			LOGGER.log(Level.INFO, e.getMessage(), e);
		}
	}

}
