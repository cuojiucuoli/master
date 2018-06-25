package com.theanswr.interfaces;

import com.theanswr.task.TaskExecuteResponse;
import com.theanswr.task.TaskReadyForRunTaskResponse;
import com.theanswr.task.TaskRequest;

import io.grpc.stub.StreamObserver;
/**
 * 实现该接口可自动开启服务
 * @author yao
 * 
 */
public interface LogicalInterface{

	public void readyForRunTask(TaskRequest request, StreamObserver<TaskReadyForRunTaskResponse> responseObserver) throws Exception ;

	public void runTask(TaskRequest request, StreamObserver<TaskExecuteResponse> responseObserver)throws Exception ;
}
