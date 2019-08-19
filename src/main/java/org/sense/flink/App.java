package org.sense.flink;

import java.util.Scanner;

import org.apache.flink.runtime.client.JobExecutionException;
import org.sense.flink.examples.batch.MatrixMultiplication;
import org.sense.flink.examples.stream.edgent.AdaptiveFilterRangeMqttEdgent;
import org.sense.flink.examples.stream.edgent.MqttSensorDataCombinerByKeySkewedDAG;
import org.sense.flink.examples.stream.edgent.MqttSensorDataHLLKeyedProcessWindow;
import org.sense.flink.examples.stream.edgent.MqttSensorDataSkewedCombinerByKeySkewedDAG;
import org.sense.flink.examples.stream.edgent.MqttSensorDataSkewedJoinDAG;
import org.sense.flink.examples.stream.edgent.MqttSensorDataSkewedPartitionByKeyDAG;
import org.sense.flink.examples.stream.edgent.MqttSensorDataSkewedPartitionByKeySkewedDAG;
import org.sense.flink.examples.stream.edgent.MqttSensorDataSkewedRescaleByKeyDAG;
import org.sense.flink.examples.stream.edgent.MultiSensorMultiStationsJoinMqtt;
import org.sense.flink.examples.stream.edgent.MultiSensorMultiStationsReadingMqtt;
import org.sense.flink.examples.stream.edgent.MultiSensorMultiStationsReadingMqtt2;
import org.sense.flink.examples.stream.edgent.SensorsDynamicFilterMqttEdgentQEP;
import org.sense.flink.examples.stream.edgent.SensorsMultipleReadingMqttEdgentQEP;
import org.sense.flink.examples.stream.edgent.SensorsMultipleReadingMqttEdgentQEP2;
import org.sense.flink.examples.stream.edgent.SensorsReadingMqttEdgentQEP;
import org.sense.flink.examples.stream.edgent.SensorsReadingMqttJoinQEP;
import org.sense.flink.examples.stream.edgent.TemperatureAverageExample;
import org.sense.flink.examples.stream.edgent.WordCountMqttFilterQEP;
import org.sense.flink.examples.stream.edgent.WordCountSocketFilterQEP;
import org.sense.flink.examples.stream.table.MqttSensorDataAverageTableAPI;
import org.sense.flink.examples.stream.twitter.TwitterExample;
import org.sense.flink.examples.stream.valencia.ValenciaDataSkewedBloomFilterJoinExample;
import org.sense.flink.examples.stream.valencia.ValenciaDataSkewedBroadcastJoinExample;
import org.sense.flink.examples.stream.valencia.ValenciaDataSkewedCombinerExample;
import org.sense.flink.examples.stream.valencia.ValenciaDataSkewedJoinExample;
import org.sense.flink.examples.stream.valencia.ValenciaDataSkewedRepartitionJoinExample;

/**
 * 
 * @author Felipe Oliveira Gutierrez
 *
 */
public class App {
	public static void main(String[] args) throws Exception {

		// BasicConfigurator.configure();

		try {
			int app = 0;
			do {
				// @formatter:off
				System.out.println("0  - exit");
				System.out.println("1  - World count (Sokect stream) with Filter and QEP");
				System.out.println("2  - World count (MQTT stream) with Filter and QEP");
				System.out.println("3  - Matrix multiplication using batch and QEP");
				System.out.println("4  - Two fake sensors (MQTT stream) and QEP");
				System.out.println("5  - Fake sensor from Apache Edgent (MQTT stream) and QEP");
				System.out.println("6  - Dynamic filter over fake data source and QEP");
				System.out.println("7  - Adaptive filter pushed down to Apache Edgent");
				System.out.println("8  - Temperature average example");
				System.out.println("9  - Consume MQTT from multiple temperature sensors");
				System.out.println("10 - Consume MQTT from multiple temperature sensors");
				System.out.println("11 - Consume MQTT from multiple sensors at train stations with ValueState");
				System.out.println("12 - Consume MQTT from multiple sensors at train stations with window");
				System.out.println("13 - Consume MQTT from multiple sensors at train stations and join within a window");
				System.out.println("14 - Consume MQTT from multiple sensors at train stations and join within a window");
				System.out.println("15 - Partition by Key and Reducing over a window");
				System.out.println("16 - Custom Partition by Key and Reducing over a window");
				System.out.println("17 - Random Partition by Key and Reducing over a window");
				System.out.println("18 - Combiner on the map phase just before shuffling and Reducing over a window");
				System.out.println("19 - Combiner on the map phase just before shuffling and Reducing over a window with RichFunction");
				System.out.println("20 - TwitterExample");
				System.out.println("21 - Estimate cardinality with HyperLogLog");
				System.out.println("22 - Estimate cardinality with HyperLogLogPlus");
				System.out.println("23 - Estimate cardinality with Bloom Filter");
				System.out.println("24 - Executin join over MQTT data using Flink Table API");
				System.out.println("25 - Reading values from Valencia Open-data Web Portal and processing a JOIN using Flink Data Stream");
				System.out.println("26 - Reading values from Valencia Open-data Web Portal and processing a COMBINER using Flink Data Stream");
				System.out.println("27 - Reading values from Valencia Open-data Web Portal and computing the Standard Repartition JOIN using Flink Data Stream");
				System.out.println("28 - Reading values from Valencia Open-data Web Portal and computing the Broadcast JOIN using Flink Data Stream");
				System.out.println("29 - Reading values from Valencia Open-data Web Portal and computing the Improved Repartition JOIN with Bloom Filter using Flink Data Stream");
				// @formatter:on

				String msg = "0";
				String ipAddressSource01 = "127.0.0.1";
				String ipAddressSink = "127.0.0.1";
				if (args != null && args.length > 0) {
					System.out.println("args 0: " + args[0]);
					System.out.println("args 1: " + args[1]);
					msg = args[0];
					if (msg.matches("-?\\d+")) {
						System.out.println("    Application choosed: " + msg);
					} else {
						msg = "999";
					}
					if (msg.equals("20")) { // testing twitter application
						System.out.println("testing Twitter application");
					} else if (args.length > 1) {
						ipAddressSource01 = args[1];
						if (!validIP(ipAddressSource01)) {
							ipAddressSource01 = "127.0.0.1";
							System.err.println("IP address invalid for Source. Using the default IP address: "
									+ ipAddressSource01);
						} else {
							System.out.println("Valid IP address for Source: " + ipAddressSource01);
						}
						if (args.length > 2) {
							ipAddressSink = args[2];
							if (!validIP(ipAddressSink)) {
								ipAddressSink = "127.0.0.1";
								System.err.println(
										"IP address invalid for Sink. Using the default IP address: " + ipAddressSink);
							} else {
								System.out.println("Valid IP address for SInk: " + ipAddressSink);
							}
						}
					}
				} else {
					System.out.print("    Please enter which application you want to run: ");
					msg = (new Scanner(System.in)).nextLine();
					System.out.print("    Please enter the IP address of the data source [127.0.0.1]: ");
					ipAddressSource01 = (new Scanner(System.in)).nextLine();
					if (!validIP(ipAddressSource01)) {
						ipAddressSource01 = "127.0.0.1";
					}
					System.out.println("Loaded IP address: " + ipAddressSource01);
					System.out.print("    Please enter the IP address of the data sink [127.0.0.1]: ");
					ipAddressSink = (new Scanner(System.in)).nextLine();
					if (!validIP(ipAddressSink)) {
						ipAddressSink = "127.0.0.1";
					}
					System.out.println("Loaded IP address: " + ipAddressSink);
				}

				app = Integer.valueOf(msg);
				switch (app) {
				case 0:
					System.out.println("bis später");
					break;
				case 1:
					System.out.println("App 1 selected");
					new WordCountSocketFilterQEP();
					app = 0;
					break;
				case 2:
					System.out.println("App 2 selected");
					new WordCountMqttFilterQEP();
					app = 0;
					break;
				case 3:
					System.out.println("App 3 selected");
					new MatrixMultiplication();
					app = 0;
					break;
				case 4:
					System.out.println("App 4 selected");
					new SensorsReadingMqttJoinQEP();
					app = 0;
					break;
				case 5:
					System.out.println("App 5 selected");
					new SensorsReadingMqttEdgentQEP();
					app = 0;
					break;
				case 6:
					System.out.println("App 6 selected");
					System.out.println("use 'mosquitto_pub -h 127.0.0.1 -t topic-parameter -m \"0.0,1000.0\"' ");
					System.out.println("on the terminal to change the parameters at runtime.");
					new SensorsDynamicFilterMqttEdgentQEP();
					app = 0;
					break;
				case 7:
					System.out.println("App 7 selected");
					System.out.println("The adaptive filter was pushed down to the data source");
					new AdaptiveFilterRangeMqttEdgent();
					app = 0;
					break;
				case 8:
					System.out.println("App 8 selected");
					System.out.println("Temperature average example");
					new TemperatureAverageExample();
					app = 0;
					break;
				case 9:
					// @formatter:off
					System.out.println("App 9 selected");
					System.out.println("Use [./bin/flink run examples/explore-flink.jar 9 -c] to run this program on the Flink standalone-cluster");
					System.out.println("Consuming values from 3 MQTT topics");
					// @formatter:on
					new SensorsMultipleReadingMqttEdgentQEP();
					app = 0;
					break;
				case 10:
					// @formatter:off
					System.out.println("App 10 selected");
					System.out.println("Use [./bin/flink run examples/explore-flink.jar 10 -c] to run this program on the Flink standalone-cluster");
					System.out.println("Consuming values from 3 MQTT topics");
					// @formatter:on
					new SensorsMultipleReadingMqttEdgentQEP2();
					app = 0;
					break;
				case 11:
					// @formatter:off
					System.out.println("App 11 selected (ValueState)");
					System.out.println("Use [./bin/flink run examples/explore-flink.jar 11 -c] to run this program on the Flink standalone-cluster");
					System.out.println("Consuming values from 2 MQTT topics");
					// @formatter:on
					new MultiSensorMultiStationsReadingMqtt();
					app = 0;
					break;
				case 12:
					// @formatter:off
					System.out.println("App 12 selected (window)");
					System.out.println("Use [./bin/flink run examples/explore-flink.jar 12 -c] to run this program on the Flink standalone-cluster");
					System.out.println("Consuming values from 2 MQTT topics");
					// @formatter:on
					new MultiSensorMultiStationsReadingMqtt2();
					app = 0;
					break;
				case 13:
					// @formatter:off
					System.out.println("App 13 selected (join within a window)");
					System.out.println("Use [./bin/flink run examples/explore-flink.jar 13 -c] to run this program on the Flink standalone-cluster");
					System.out.println("Consuming values from 2 MQTT topics");
					// @formatter:on
					new MultiSensorMultiStationsJoinMqtt();
					app = 0;
					break;
				case 14:
					new MqttSensorDataSkewedJoinDAG(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 15:
					new MqttSensorDataSkewedPartitionByKeyDAG(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 16:
					new MqttSensorDataSkewedRescaleByKeyDAG(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 17:
					new MqttSensorDataSkewedPartitionByKeySkewedDAG(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 18:
					new MqttSensorDataSkewedCombinerByKeySkewedDAG(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 19:
					new MqttSensorDataCombinerByKeySkewedDAG(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 20:
					System.out.println("application 20");
					new TwitterExample(args);
					app = 0;
					break;
				case 21:
					System.out.println("Estimate cardinality with HyperLogLog");
					new MqttSensorDataHLLKeyedProcessWindow(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 24:
					new MqttSensorDataAverageTableAPI(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 25:
					new ValenciaDataSkewedJoinExample();
					app = 0;
					break;
				case 26:
					new ValenciaDataSkewedCombinerExample(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 27:
					new ValenciaDataSkewedRepartitionJoinExample(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 28:
					new ValenciaDataSkewedBroadcastJoinExample(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				case 29:
					new ValenciaDataSkewedBloomFilterJoinExample(ipAddressSource01, ipAddressSink);
					app = 0;
					break;
				default:
					args = null;
					System.out.println("No application selected [" + app + "] ");
					break;
				}
			} while (app != 0);
		} catch (JobExecutionException ce) {
			System.err.println(ce.getMessage());
			ce.printStackTrace();
		}
	}

	public static boolean validIP(String ip) {
		try {
			if (ip == null || ip.isEmpty()) {
				return false;
			}

			String[] parts = ip.split("\\.");
			if (parts.length != 4) {
				return false;
			}

			for (String s : parts) {
				int i = Integer.parseInt(s);
				if ((i < 0) || (i > 255)) {
					return false;
				}
			}
			if (ip.endsWith(".")) {
				return false;
			}

			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
}
