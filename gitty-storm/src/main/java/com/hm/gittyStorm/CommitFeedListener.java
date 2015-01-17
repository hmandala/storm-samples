package com.hm.gittyStorm;

import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.spout.SpoutOutputCollector;

import java.io.File;
import java.util.*;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.task.TopologyContext;
import org.apache.commons.io.IOUtils;
import java.nio.charset.Charset;
import java.io.IOException;
import java.nio.file.*;
import backtype.storm.tuple.Values;

public class CommitFeedListener extends BaseRichSpout {

    private SpoutOutputCollector outputCollector;
    private List<String> commits;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("commit"));
    }

    @Override
    public void open(Map configMap,
                     TopologyContext context,
                     SpoutOutputCollector outputCollector) {
        this.outputCollector = outputCollector;
        try {
            //commits = IOUtils.readLines(ClassLoader.getSystemResourceAsStream("/home/hmandala/projects/gitty-storm/src/main/resources/changelog.txt"),
            //        Charset.defaultCharset().name());
            //commits = IOUtils.readLines("/home/hmandala/projects/gitty-storm/src/main/resources/changelog.txt");
            commits = Files.readAllLines(Paths.get(
                    "/home/hmandala/projects/gitty-storm/src/main/resources/changelog.txt"),
                    java.nio.charset.Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nextTuple() {
        for (String commit : commits) {
            outputCollector.emit(new Values(commit));
        }
    }
}