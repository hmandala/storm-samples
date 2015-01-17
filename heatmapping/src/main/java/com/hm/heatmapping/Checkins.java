package com.hm.heatmapping;

import java.io.IOException;
import java.util.*;
import backtype.storm.topology.base.*;
import backtype.storm.spout.*;
import backtype.storm.topology.*;
import backtype.storm.tuple.*;
import backtype.storm.task.*;
import org.apache.commons.io.IOUtils;
import java.nio.charset.Charset;
import java.nio.file.*;

/**
 * Created by hmandala on 1/17/15.
 */

public class Checkins extends BaseRichSpout {
    private List<String> checkins;
    private int nextEmitIndex;
    private SpoutOutputCollector outputCollector;
    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("time", "address"));
    }
    @Override
    public void open(Map config,
                     TopologyContext topologyContext,
                     SpoutOutputCollector spoutOutputCollector) {
        this.outputCollector = spoutOutputCollector;
        this.nextEmitIndex = 0;
        try {
            checkins =
                    IOUtils.readLines(ClassLoader.getSystemResourceAsStream("checkins.txt"),
                            Charset.defaultCharset().name());
            checkins = Files.readAllLines(Paths.get(
                            "/home/hmandala/projects/storm-samples/heatmapping/src/main/resources/checkins.txt"),
                    java.nio.charset.Charset.defaultCharset());
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nextTuple() {
        String checkin = checkins.get(nextEmitIndex);
        String[] parts = checkin.split(",");
        Long time = Long.valueOf(parts[0]);
        String address = parts[1];
        outputCollector.emit(new Values(time, address));
        nextEmitIndex = (nextEmitIndex + 1) % checkins.size();
    }
}
