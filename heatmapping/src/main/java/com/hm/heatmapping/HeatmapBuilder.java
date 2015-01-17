package com.hm.heatmapping;

/**
 * Created by hmandala on 1/17/15.
 */

import backtype.storm.topology.base.*;
import backtype.storm.topology.*;
import backtype.storm.tuple.*;
import backtype.storm.task.*;

import java.util.*;
import com.google.code.geocoder.model.*;
import backtype.storm.Constants;
import backtype.storm.Config;

public class HeatmapBuilder extends BaseBasicBolt {
    private Map<Long, List<LatLng>> heatmaps;

    @Override
    public void prepare(Map config,
                        TopologyContext context) {
        heatmaps = new HashMap<>();
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(new Fields("time-interval", "hotzones"));
    }

    @Override
    public void execute(Tuple tuple,
                        BasicOutputCollector outputCollector) {
        if (isTickTuple(tuple)) {
            emitHeatmap(outputCollector);
        } else {
            LatLng geocode = (LatLng) tuple.getValueByField("geocode");
            Long timeInterval = tuple.getLongByField("time-interval");
            List<LatLng> checkins = getCheckinsForInterval(timeInterval);
            checkins.add(geocode);
        }
    }

    private Long selectTimeInterval(Long time) {
        return time/(15*1000);
    }

    private List<LatLng> getCheckinsForInterval(Long timeInterval) {
        List<LatLng> hotzones = heatmaps.get(timeInterval);
        if (hotzones == null) {
            hotzones = new ArrayList<>();
            heatmaps.put(timeInterval, hotzones);
        }
        return hotzones;
    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Config conf = new Config();
        conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);
        return conf;
    }

    private boolean isTickTuple(Tuple tuple) {
        String sourceComponent = tuple.getSourceComponent();
        String sourceStreamId = tuple.getSourceStreamId();
        return sourceComponent.equals(Constants.SYSTEM_COMPONENT_ID)
                && sourceStreamId.equals(Constants.SYSTEM_TICK_STREAM_ID);
    }

    private void emitHeatmap(BasicOutputCollector outputCollector) {
        Long now = System.currentTimeMillis();
        Long emitUpToTimeInterval = selectTimeInterval(now);
        Set<Long> timeIntervalsAvailable = heatmaps.keySet();
        for (Long timeInterval : timeIntervalsAvailable) {
            if (timeInterval <= emitUpToTimeInterval) {
                List<LatLng> hotzones = heatmaps.remove(timeInterval);
                outputCollector.emit(new Values(timeInterval, hotzones));
            }
        }
    }
}