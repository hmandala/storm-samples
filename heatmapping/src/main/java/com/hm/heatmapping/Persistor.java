package com.hm.heatmapping;

/**
 * Created by hmandala on 1/17/15.
 */

import backtype.storm.topology.base.*;
import backtype.storm.topology.*;
import backtype.storm.task.*;
import backtype.storm.tuple.Tuple;

import java.util.*;
import com.google.code.geocoder.model.*;
import org.slf4j.*;
import redis.clients.jedis.*;
import org.codehaus.jackson.map.ObjectMapper;

public class Persistor extends BaseBasicBolt {
    private final Logger logger = LoggerFactory.getLogger(Persistor.class);
    private Jedis jedis;
    private ObjectMapper objectMapper;

    @Override
    public void prepare(Map stormConf,
                        TopologyContext context) {
        jedis = new Jedis("localhost");
        objectMapper = new ObjectMapper();
    }

    @Override
    public void execute(Tuple tuple,
                        BasicOutputCollector outputCollector) {
        Long timeInterval = tuple.getLongByField("time-interval");
        List<LatLng> hz = (List<LatLng>) tuple.getValueByField("hotzones");
        List<String> hotzones = asListOfStrings(hz);
        try {
            String key = "checkins-" + timeInterval;
            String value = objectMapper.writeValueAsString(hotzones);
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("Error persisting for time: " + timeInterval, e);
        }
    }

    private List<String> asListOfStrings(List<LatLng> hotzones) {
        List<String> hotzonesStandard = new ArrayList<>(hotzones.size());
        for (LatLng geoCoordinate : hotzones) {
            hotzonesStandard.add(geoCoordinate.toUrlValue());
        }
        return hotzonesStandard;
    }

    @Override
    public void cleanup() {
        if (jedis.isConnected()) {
            jedis.quit();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // No output fields to be declared #H
    }
}
