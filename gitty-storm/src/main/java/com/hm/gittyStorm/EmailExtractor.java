package com.hm.gittyStorm;

/**
 * Created by hmandala on 1/16/15.
 */

import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.topology.*;
import backtype.storm.tuple.*;

public class EmailExtractor extends BaseBasicBolt {
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("email"));
    }
    @Override
    public void execute(Tuple tuple,
                        BasicOutputCollector outputCollector) {
        String commit = tuple.getStringByField("commit");
        String[] parts = commit.split(" ");
        outputCollector.emit(new Values(parts[1]));
    }
}
