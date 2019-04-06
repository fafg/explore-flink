package org.sense.flink.examples.stream.udfs;

import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.sense.flink.mqtt.CompositeKeyStationPlatform;
import org.sense.flink.mqtt.MqttSensor;

public class StationPlatformKeySelector
		implements KeySelector<Tuple2<CompositeKeyStationPlatform, MqttSensor>, CompositeKeyStationPlatform> {
	private static final long serialVersionUID = 4041776391429253262L;

	@Override
	public CompositeKeyStationPlatform getKey(Tuple2<CompositeKeyStationPlatform, MqttSensor> value) throws Exception {
		return value.f0;
	}
}