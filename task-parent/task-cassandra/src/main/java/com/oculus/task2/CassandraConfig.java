package com.oculus.task2;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.ClusterBuilderConfigurer;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.CodecRegistry;
import com.datastax.driver.core.TypeCodec;

@Configuration
@EnableCassandraRepositories
public class CassandraConfig extends AbstractCassandraConfiguration {

	@Value("${cassandra.contactpoints}")
	private String contactPoints;

	@Value("${cassandra.port}")
	private int port;

	@Value("${cassandra.keyspace}")
	private String keySpace;

	@Value("${cassandra.basePackages}")
	private String basePackages;

	@Override
	protected String getKeyspaceName() {
		return keySpace;
	}

	@Override
	protected String getContactPoints() {
		
		return contactPoints;
	}

	@Override
	protected int getPort() {
		return port;
	}

	@Override
	public SchemaAction getSchemaAction() {
		return SchemaAction.CREATE_IF_NOT_EXISTS;
	}

	@Override
	public String[] getEntityBasePackages() {
		return new String[] {basePackages};
	}
	
	
	@Override
	protected boolean getMetricsEnabled() { 
		return false; 
	}
	
	@Override
	protected ClusterBuilderConfigurer getClusterBuilderConfigurer() {
		//registre codec
        CodecRegistry codecRegistry = new CodecRegistry();
        codecRegistry.register(new DateCodec(TypeCodec.date(), Date.class));
        ;
        ClusterBuilderConfigurer conf = new ClusterBuilderConfigurer() {
			
			@Override
			public Builder configure(Builder clusterBuilder) {
				return clusterBuilder.withCodecRegistry(codecRegistry);
			}
		};
        return conf;
	}
	
	@Override
	protected List<String> getStartupScripts() {
		final String script =
		        "CREATE KEYSPACE IF NOT EXISTS "
		            + keySpace
		            + " WITH durable_writes = true"
		            + " AND replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};";
		    return Arrays.asList(script);
	}
}