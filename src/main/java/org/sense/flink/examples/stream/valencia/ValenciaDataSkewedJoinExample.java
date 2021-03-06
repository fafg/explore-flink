package org.sense.flink.examples.stream.valencia;

import static org.sense.flink.util.MetricLabels.METRIC_VALENCIA_DISTRICT_MAP;
import static org.sense.flink.util.MetricLabels.METRIC_VALENCIA_FILTER;
import static org.sense.flink.util.MetricLabels.METRIC_VALENCIA_SINK;
import static org.sense.flink.util.MetricLabels.METRIC_VALENCIA_SOURCE;
import static org.sense.flink.util.MetricLabels.METRIC_VALENCIA_SYNTHETIC_FLATMAP;

import java.util.ArrayList;
import java.util.List;

import org.apache.flink.api.java.tuple.Tuple4;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.sense.flink.examples.stream.udf.impl.TrafficPollutionByDistrictJoinFunction;
import org.sense.flink.examples.stream.udf.impl.ValenciaItemDistrictMap;
import org.sense.flink.examples.stream.udf.impl.ValenciaItemDistrictSelector;
import org.sense.flink.examples.stream.udf.impl.ValenciaItemFilter;
import org.sense.flink.examples.stream.udf.impl.ValenciaItemSyntheticData;
import org.sense.flink.pojo.Point;
import org.sense.flink.pojo.ValenciaItem;
import org.sense.flink.source.ValenciaItemConsumer;
import org.sense.flink.util.CRSCoordinateTransformer;
import org.sense.flink.util.ValenciaItemType;

/**
 * 
 * @author Felipe Oliveira Gutierrez
 *
 */
public class ValenciaDataSkewedJoinExample {

	public static void main(String[] args) throws Exception {
		new ValenciaDataSkewedJoinExample("127.0.0.1", "127.0.0.1");
	}

	public ValenciaDataSkewedJoinExample(String ipAddressSource, String ipAddressSink) throws Exception {
		List<Tuple4<Point, Long, Long, String>> coordinates = syntheticCoordinates();
		boolean offlineData = true;
		boolean collectWithTimestamp = true;
		boolean skewedDataInjection = true;
		long frequencyMilliSeconds1 = Time.seconds(20).toMilliseconds();
		long frequencyMilliSeconds2 = Time.seconds(60).toMilliseconds();

		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.IngestionTime);

		// @formatter:off
		// Sources -> add synthetic data -> filter
		DataStream<ValenciaItem> streamTrafficJam = env
				.addSource(new ValenciaItemConsumer(ValenciaItemType.TRAFFIC_JAM, frequencyMilliSeconds1, collectWithTimestamp, !offlineData, skewedDataInjection, Long.MAX_VALUE, false)).name(METRIC_VALENCIA_SOURCE + "-" + ValenciaItemType.TRAFFIC_JAM)
				.map(new ValenciaItemDistrictMap()).name(METRIC_VALENCIA_DISTRICT_MAP)
				.flatMap(new ValenciaItemSyntheticData(ValenciaItemType.TRAFFIC_JAM, coordinates)).name(METRIC_VALENCIA_SYNTHETIC_FLATMAP)
				.filter(new ValenciaItemFilter(ValenciaItemType.TRAFFIC_JAM)).name(METRIC_VALENCIA_FILTER)
				;

		DataStream<ValenciaItem> streamAirPollution = env
				.addSource(new ValenciaItemConsumer(ValenciaItemType.AIR_POLLUTION, frequencyMilliSeconds2, collectWithTimestamp, !offlineData, skewedDataInjection, Long.MAX_VALUE, false)).name(METRIC_VALENCIA_SOURCE + "-" + ValenciaItemType.AIR_POLLUTION)
				.map(new ValenciaItemDistrictMap()).name(METRIC_VALENCIA_DISTRICT_MAP)
				.flatMap(new ValenciaItemSyntheticData(ValenciaItemType.AIR_POLLUTION, coordinates)).name(METRIC_VALENCIA_SYNTHETIC_FLATMAP)
				.filter(new ValenciaItemFilter(ValenciaItemType.AIR_POLLUTION)).name(METRIC_VALENCIA_FILTER)
				;

		// Join -> Print
		streamTrafficJam.join(streamAirPollution)
				.where(new ValenciaItemDistrictSelector())
 				.equalTo(new ValenciaItemDistrictSelector())
		 		.window(TumblingEventTimeWindows.of(Time.seconds(20)))
		 		.apply(new TrafficPollutionByDistrictJoinFunction())
		 		.print().name(METRIC_VALENCIA_SINK)
		  		;
		// streamTrafficJam.print();
		// streamAirPollution.print();

		disclaimer(env.getExecutionPlan(), ipAddressSource);
		env.execute(ValenciaDataSkewedJoinExample.class.getSimpleName());
		// @formatter:on
	}

	private List<Tuple4<Point, Long, Long, String>> syntheticCoordinates() {
		// Static coordinate to create synthetic data
		List<Tuple4<Point, Long, Long, String>> coordinates = new ArrayList<Tuple4<Point, Long, Long, String>>();
		coordinates.add(Tuple4.of(new Point(725140.37, 4371855.492, CRSCoordinateTransformer.DEFAULT_CRS_EPSG_25830),
				3L, 9L, "Extramurs"));
		coordinates.add(Tuple4.of(new Point(726777.707, 4369824.436, CRSCoordinateTransformer.DEFAULT_CRS_EPSG_25830),
				10L, 9L, "Quatre Carreres"));
		return coordinates;
	}

	private void disclaimer(String logicalPlan, String ipAddressSource) {
		// @formatter:off
		System.out.println("This is the application [" + ValenciaDataSkewedJoinExample.class.getSimpleName() + "].");
		System.out.println("It aims to show a repartition-join operation for stream data which reduces items on the shuffle phase.");
		System.out.println();
		//System.out.println("Changing frequency >>>");
		//System.out.println("It is possible to publish a 'multiply factor' to each item from the source by issuing the commands below.");
		//System.out.println("Each item will be duplicated by 'multiply factor' times.");
		//System.out.println("where: 1 <= 'multiply factor' <=1000");
		//System.out.println("mosquitto_pub -h " + ipAddressSource + " -t " + topicParamFrequencyPull + " -m \"AIR_POLLUTION 500\"");
		//System.out.println("mosquitto_pub -h " + ipAddressSource + " -t " + topicParamFrequencyPull + " -m \"TRAFFIC_JAM 1000\"");
		//System.out.println("mosquitto_pub -h " + ipAddressSource + " -t " + topicParamFrequencyPull + " -m \"NOISE 600\"");
		//System.out.println();
		System.out.println("Use the 'Flink Plan Visualizer' [https://flink.apache.org/visualizer/] in order to see the logical plan of this application.");
		System.out.println("Logical plan >>>");
		System.out.println(logicalPlan);
		System.out.println();
		// @formatter:on
	}
}
