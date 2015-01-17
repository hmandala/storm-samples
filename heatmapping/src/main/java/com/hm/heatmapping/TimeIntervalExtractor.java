package com.hm.heatmapping;

/**
 * Created by hmandala on 1/17/15.
 */

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.*;
import backtype.storm.tuple.*;
import com.google.code.geocoder.model.*;

public class TimeIntervalExtractor extends BaseBasicBolt {
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("time-interval", "geocode", "city"));
    }
    @Override
    public void execute(Tuple tuple,
                        BasicOutputCollector outputCollector) {
        Long time = tuple.getLongByField("time");
        LatLng geocode = (LatLng) tuple.getValueByField("geocode");
        String city = tuple.getStringByField("city");
        Long timeInterval = time / (15 * 1000);
        outputCollector.emit(new Values(timeInterval, geocode, city));
    }
}