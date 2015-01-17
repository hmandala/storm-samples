package com.hm.gittyStorm;

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.*;
import backtype.storm.tuple.*;
import java.util.*;
import backtype.storm.task.TopologyContext;

/**
 * Created by hmandala on 1/16/15.
 */


public class EmailCounter extends BaseBasicBolt {
    private Map<String, Integer> counts;
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // This bolt does not emit anything and therefore does
    } // not declare any output fields.

    @Override
    public void prepare(Map config,
                        TopologyContext context) {
        counts = new HashMap<String, Integer>();
    }

    @Override
    public void execute(Tuple tuple,
                        BasicOutputCollector outputCollector) {
        String email = tuple.getStringByField("email");
        counts.put(email, countFor(email) + 1);
        printCounts();
    }

    private Integer countFor(String email) {
        Integer count = counts.get(email);
        return count == null ? 0 : count;
    }

    private void printCounts() {
        for (String email : counts.keySet()) {
            System.out.println(String.format("%s has count of %s", email, counts.get(email)));
        }
    }
}