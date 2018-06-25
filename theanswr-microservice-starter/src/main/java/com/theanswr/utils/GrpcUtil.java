package com.theanswr.utils;

import java.util.Arrays;
import java.util.List;

import com.theanswr.task.Result;
import com.theanswr.task.Result.Type;
import com.theanswr.task.TypeChart;
import com.theanswr.task.TypeChart.ChartType;
import com.theanswr.task.TypeMedia;
import com.theanswr.task.TypeText;
import com.theanswr.task.TypeText.Builder;

public class GrpcUtil {

	public static TypeText generateTextResult(String resultName, String[] array) {

		Builder build = TypeText.newBuilder().setCode(resultName);
		List<String> asList = Arrays.asList(array);
		build.addVariable(asList.toString());

		TypeText typeText = build.build();
		return typeText;

	}

	public static Result generateTextResultItem(String resultName, String[] array, int width, int height) {
		TypeText textResult = generateTextResult(resultName, array);
		Result result = Result.newBuilder().setType(Type.TEXT).setText(textResult).setWidth(width).setHeight(height)
				.build();
		return result;

	}

	public static Result generateChartResultItem(ChartType chartType, String chartData, int width, int height,
			TypeText reference) {
		TypeChart chartResult = TypeChart.newBuilder().setChartType(chartType).setData(chartData)
				.setReference(reference).build();
		Result result = Result.newBuilder().setType(Result.Type.CHART).setChart(chartResult).setWidth(width)
				.setHeight(height).build();

		return result;

	}

	public static Result generateMediaResultItem(String url, int width, int height) {
		TypeMedia media = TypeMedia.newBuilder().setLink(url).build();
		Result result = Result.newBuilder().setType(Type.MEDIA).setMedia(media).setWidth(width).setHeight(height)
				.build();
		return result;
	}
}
