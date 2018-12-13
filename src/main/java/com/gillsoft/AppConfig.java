package com.gillsoft;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Configuration
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
@EnableScheduling
@ComponentScans(value = {
		@ComponentScan("com.gillsoft")
	})
public class AppConfig {
	
	private static Logger LOGGER = LoggerFactory.getLogger(AppConfig.class.getName());

	private static final String SHOW_SQL = "hibernate.show_sql";
	private static final String FORMAT_SQL = "hibernate.format_sql";
	private static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";
	private static final String REVISION_ON_COLLECTION_CHANGE = "org.hibernate.envers.revision_on_collection_change";
	private static final String STORE_DATA_AT_DELETE = "org.hibernate.envers.store_data_at_delete";
	private static final String NON_CONTEXTUAL_CREATION = "hibernate.jdbc.lob.non_contextual_creation";
	private static final String DIALECT = "hibernate.dialect";
	private static final String POSTGRE_SQL9_DIALECT = "org.hibernate.dialect.PostgreSQL9Dialect";
	private static final String USE_JDBC_METADATA_DEFAULTS = "hibernate.temp.use_jdbc_metadata_defaults";
	
	private static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
	private static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
	private static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
	private static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
	private static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
	
	private static final String SSH_USE = "ssh.use";
	private static final String SSH_HOST = "ssh.host";
	private static final String SSH_PORT = "ssh.port";
	private static final String SSH_USER = "ssh.user";
	private static final String SSH_PASSWORD = "ssh.password";
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
		tunnel();
		
		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setDataSource(getDataSource());

		Properties props = new Properties();

		// Setting Hibernate properties
		props.put(SHOW_SQL, env.getProperty(SHOW_SQL));
		props.put(FORMAT_SQL, env.getProperty(FORMAT_SQL));
		props.put(HBM2DDL_AUTO, env.getProperty(HBM2DDL_AUTO));
		props.put(REVISION_ON_COLLECTION_CHANGE, false);
		props.put(STORE_DATA_AT_DELETE, true);
		props.put(NON_CONTEXTUAL_CREATION, true);
		props.put(DIALECT, POSTGRE_SQL9_DIALECT);
		props.put(USE_JDBC_METADATA_DEFAULTS, false);

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
	
	private static Session session;
	
	@Scheduled(initialDelay = 15000, fixedDelay = 5000)
	public void tunnel() {
		if (isUseSsh()) {
			if (session == null) {
				session = newSession();
			}
			if (!session.isConnected()) {
				session.disconnect();
				session = newSession();
			}
		}
	}
	
	private Session newSession() {
		JSch jsch = new JSch();
		try {
			InputStream key = AppConfig.class.getClassLoader().getResourceAsStream(env.getProperty(SSH_KEY));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IOUtils.copy(key, out);
			jsch.addIdentity("wpl", out.toByteArray(), null, getPassword());
			Session session = jsch.getSession(env.getProperty(SSH_USER),
					env.getProperty(SSH_HOST), Integer.valueOf(env.getProperty(SSH_PORT)));
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			session.setPortForwardingL(Integer.valueOf(env.getProperty(SSH_LOCAL_PORT)),
					env.getProperty(SSH_LOCAL_HOST), Integer.valueOf(env.getProperty(SSH_REMOTE_PORT)));
			return session;
		} catch (JSchException | IOException e) {
			LOGGER.info(e.getMessage(), e);
		}
		return null;
	}
	
	private byte[] getPassword() {
		String pass = env.getProperty(SSH_PASSWORD);
		if (pass == null
				|| pass.isEmpty()) {
			return null;
		} else {
			return pass.getBytes();
		}
	}

}
