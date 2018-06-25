package com.theanswr.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.theanswr.interfaces.LogicalInterface;
import com.theanswr.task.TaskExecuteResponse;
import com.theanswr.task.TaskReadyForRunTaskResponse;
import com.theanswr.task.TaskRequest;
import com.theanswr.task.TaskServiceGrpc.TaskService;


import io.grpc.stub.StreamObserver;

public class TaskServiceImpl implements TaskService {
	
	private final static Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);
	
	private LogicalInterface logical;
	
	public TaskServiceImpl(LogicalInterface logical) {
		super();
		this.logical = logical;
	}

	@Override
	public void readyForRunTask(TaskRequest request, StreamObserver<TaskReadyForRunTaskResponse> responseObserver) {

		try {
			logical.readyForRunTask(request, responseObserver);
			
		} catch (Exception e) {
			log.error("the method 'readyForRunTask's'inner has encountered a server problem  ",e);
			throw new RuntimeException("the method 'readyForRunTask's'inner has encountered a server problem",e);
		}

	}

	@Override
	public void runTask(TaskRequest request, StreamObserver<TaskExecuteResponse> responseObserver) {
		
		try {
			logical.runTask(request, responseObserver);

		} catch (Exception e) {
			log.error("the internal of the method 'runTask' has encountered a server problem " + e);
			throw new RuntimeException("the internal of the method 'runTask' has encountered a server problem " + e);
		}
	}

}
