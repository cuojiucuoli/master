package com.theanswr.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.theanswr.commlib.CommLib;
import com.theanswr.interfaces.LogicalInterface;
import com.theanswr.task.Result;
import com.theanswr.task.TaskExecuteResponse;
import com.theanswr.task.TaskExecuteResponse.Builder;
import com.theanswr.task.TaskReadyForRunTaskResponse;
import com.theanswr.task.TaskRequest;
import com.theanswr.task.TypeChart;
import com.theanswr.task.TypeText;
import com.theanswr.util.HttpClientUtil;
import com.theanswr.utils.GrpcUtil;

import io.grpc.stub.StreamObserver;

@Component
public class Deeplook0001Impl implements LogicalInterface {
	private final static Logger log = LoggerFactory.getLogger(Deeplook0001Impl.class);
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void readyForRunTask(TaskRequest request, StreamObserver<TaskReadyForRunTaskResponse> responseObserver) {

		log.info("(readyForRun) Validate:" + request.getOption());

		try {
			String option = request.getOption();
			Map<String, Object> map = (Map<String, Object>) JSON.parse(option);
			String url = getURL(map);
			// 请求第三方获取返回json数据
			String deeLoopResult = HttpClientUtil.getDeepLookResult(url);

			// JSON数据转换MAP集合
			Map<String, Object> jsonMap = (Map<String, Object>) JSON.parse(deeLoopResult);
			// 获取该JSON中key为success的状态值
			Integer success = (Integer) jsonMap.get("success");

			if (success == null || success == 0) {
				log.warn("deeplook is processing");
				// 执行错误响应数据给对端
				errorStateResponse(request, responseObserver);

			} else {
				TaskReadyForRunTaskResponse response = TaskReadyForRunTaskResponse.newBuilder().setReadyForRun(true)
						.build();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		} catch (Exception e) {
			log.error("this process has encounter a server error",e);	
			errorStateResponse(request, responseObserver);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void runTask(TaskRequest request, StreamObserver<TaskExecuteResponse> responseObserver) {
		log.info("Execute:" + request.getOption());
		String noResultImg = "https://cdn.theanswr.com/productoutput/201804160954.png";
		// 获取对端传来的option
		String option = request.getOption();
		// 创建服务端起始时间
		long startTime = System.currentTimeMillis();
		// 获取对端初始化时间
		long initialAskTime = request.getInitialAskTime();
		ArrayList<Result> results = new ArrayList<Result>();
		String keyword = "";
		// json转换成map集合"{'DEEPLOOK_ALIAS_LOOKUP_INPUT','xxx'}"
		Map<String, Object> map = (Map<String, Object>) JSON.parse(option);

		try {
			// 获取DEEPLOOK_ALIAS_LOOKUP_INPUT对应的value 'xxx'
			keyword = (String) CommLib.getText(map, "DEEPLOOK_ALIAS_LOOKUP_INPUT", true);
		} catch (Exception e) {
			log.error("there are several error during the process acquiring value of 'DEEPLOOK_ALIAS_LOOKUP_INPUT'",e);
			responseResult(request, responseObserver, 1.0,Result.newBuilder().build() );	
		}

		String[] keywords = new String[] { keyword };
		// 当前时间是否大于对端初始化时间后的24小时.
		if (CommLib.isAlreadyAsked(initialAskTime)) {

			Result result1 = GrpcUtil.generateTextResultItem("DEEPLOOK_ALIAS_LOOKUP_NO_RESULT", keywords, 6, 12);
			Result result2 = GrpcUtil.generateMediaResultItem(noResultImg, 6, 12);

			results.add(result1);
			results.add(result2);
			responseResult(request, responseObserver, 0.0,results.toArray(new Result[0]));
			log.info("Done,TotalTime:" + (System.currentTimeMillis() - startTime));

		}

		String url = getURL(map);
		// 得到第三方响应的json字符串
		String deepLookResult = HttpClientUtil.getDeepLookResult(url);
		Map<String, Object> mapDeep = (Map<String, Object>) JSON.parse(deepLookResult);
		// 获取该json字符串下key为result的值

		JSONArray result = (JSONArray) mapDeep.get("result");

		if (mapDeep.get("result") == null || CommLib.checkAllWeightIsZero(result)) {
			if (CommLib.checkAllWeightIsZero(result)) {
				log.warn("result exist but the all values of weight are 0");
			} else {
				log.warn("empty result");
			}
			Result result1 = GrpcUtil.generateTextResultItem("DEEPLOOK_ALIAS_LOOKUP_NO_RESULT", keywords, 6, 12);
			Result result2 = GrpcUtil.generateMediaResultItem(noResultImg, 6, 12);
			results.add(result1);
			results.add(result2);
		} else {
			ArrayList<String> aliasResult = new ArrayList<String>();
			List weightList = CommLib.getJsonArrValueByKey(result, "weight");
			List nameList = CommLib.getJsonArrValueByKey(result, "name");
			for (int i = 0; i < weightList.size(); i++) {
				String weight = (String) weightList.get(i);
				if (result != null && weight != "0") {
					String value = "{name:" + nameList.get(i) + ",value:" + Math.pow(Double.parseDouble(weight), 1 / 3)
							+ "}";
					aliasResult.add(value);
				}
			}

			String[] variableArray = new String[] { keyword, aliasResult.size() + "" };
			Result result1 = GrpcUtil.generateTextResultItem("DEEPLOOK_ALIAS_LOOKUP_RESULT_TITLE", variableArray, 6,
					12);
			String aliasResultString = aliasResult.toString();
			Result result2 = GrpcUtil.generateChartResultItem(TypeChart.ChartType.TREEMAP_ALIAS, aliasResultString, 6,
					12, TypeText.newBuilder().build());
			results.add(result1);
			results.add(result2);
		}
		responseResult(request, responseObserver, 1.0, results.toArray(new Result[0]));
		
		log.info("Done,TotalExecTime" + (System.currentTimeMillis() - startTime));
	}
	
	
	
	
	
	/**
	 * success状态为0 或者异常执行errorStateResponse
	 * 
	 * @param request
	 * @param responseObserver
	 */
	private void errorStateResponse(TaskRequest request, StreamObserver<TaskReadyForRunTaskResponse> responseObserver) {
		log.info("deeplook is processing");
		long currentTime = System.currentTimeMillis();

		TaskReadyForRunTaskResponse response = TaskReadyForRunTaskResponse.newBuilder().setReadyForRun(false)
				.setEstimateFinishAt(currentTime + 3600).build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	/**
	 * 
	 * @param map
	 *            {KEY:DEEPLOOK_ALIAS_LOOKUP_INPUT,VALUE:XXX}
	 * @return url 根据具体条件拼接的字符串
	 */
	private String getURL(Map map) {
		String token = "vGt6aohBkH0n41JEH2WRDbEmpnwpcAxbsPLgV15J";
		String text = CommLib.getText(map, "DEEPLOOK_ALIAS_LOOKUP_INPUT", true);
		String url = "http://deeplook.ai/dlforum/api_ans.php?action=tags&searcg_group=1&search=" + text
				+ "&limit=10&access_token=" + token;
		log.info("the url you defined as" + url);
		return url;
	}
	/**
	 * 
	 * @param request
	 * @param responseObserver
	 * @param d :successRate
	 * @param results :返回结果
	 */
	private void responseResult(TaskRequest request, StreamObserver<TaskExecuteResponse> responseObserver,double d,Result... results) {
		Builder builder = TaskExecuteResponse.newBuilder().setSuccessRate(d);
		for (Result result : results) {
			builder.addResult(result);
		}
		TaskExecuteResponse response = builder.build();
		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

}
