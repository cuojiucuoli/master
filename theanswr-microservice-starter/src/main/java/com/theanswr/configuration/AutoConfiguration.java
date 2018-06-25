package com.theanswr.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.theanswr.interfaces.LogicalInterface;
import com.theanswr.service.TaskServiceImpl;
import com.theanswr.task.TaskServiceGrpc;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

@Component
@EnableConfigurationProperties
public class AutoConfiguration {

	@Autowired
	LogicalInterface logical;

	@Autowired
	PropertiesConfiguration propertiesConfig;

	private final static Logger log = LoggerFactory.getLogger(AutoConfiguration.class);

	

	@Scheduled(fixedDelay = 1000)
	public synchronized void executor() {
		 
		if (Thread.activeCount()<7) {
			log.info("Current Active Count is :"+Thread.activeCount());
			Runnable runnable = new Runnable() {

				@Override
				public void run() {
					if (logical == null) {
						log.info("There is no object implementing the given interface");
						
					} else {
						Server server = NettyServerBuilder.forPort(propertiesConfig.getPort())
								.addService(TaskServiceGrpc.bindService(new TaskServiceImpl(logical))).build();
						
						try {
							server.start();
							log.info("Open Server,The Port is" + propertiesConfig.getPort());
							server.awaitTermination();
						} catch (Exception e) {
							log.error("The process of openning server has encountered a server problem", e);
						}

					}
					
				};
			};
			Thread thread = new Thread(runnable);
			thread.start();
			log.info("Open Thread :"+thread.getName());
		}
	}

}
