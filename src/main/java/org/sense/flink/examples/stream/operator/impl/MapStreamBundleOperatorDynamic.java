package org.sense.flink.examples.stream.operator.impl;

import org.apache.flink.api.java.functions.KeySelector;
import org.sense.flink.examples.stream.operator.AbstractMapStreamBundleOperatorDynamic;
import org.sense.flink.examples.stream.trigger.BundleTriggerDynamic;
import org.sense.flink.examples.stream.udf.MapBundleFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapStreamBundleOperatorDynamic<K, V, IN, OUT>
		extends AbstractMapStreamBundleOperatorDynamic<K, V, IN, OUT> {
	private static final long serialVersionUID = 8268545000501962135L;
	private static final Logger logger = LoggerFactory.getLogger(MapStreamBundleOperatorDynamic.class);

	/** KeySelector is used to extract key for bundle map. */
	private final KeySelector<IN, K> keySelector;

	public MapStreamBundleOperatorDynamic(MapBundleFunction<K, V, IN, OUT> function,
			BundleTriggerDynamic<K, IN> bundleTrigger, KeySelector<IN, K> keySelector) {
		super(function, bundleTrigger);
		this.keySelector = keySelector;
	}

	@Override
	protected K getKey(IN input) throws Exception {
		return this.keySelector.getKey(input);
	}
}
