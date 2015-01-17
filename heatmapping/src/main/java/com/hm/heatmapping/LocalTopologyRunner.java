package com.hm.heatmapping;

/**
 * Created by hmandala on 1/17/15.
 */
import backtype.storm.Config;
import backtype.storm.generated.StormTopology;
import backtype.storm.LocalCluster;
import com.hm.heatmapping.HeatmapTopologyBuilder;

public class LocalTopologyRunner {
    public static void main(String[] args) {
        Config config = new Config();
        StormTopology topology = HeatmapTopologyBuilder.build();
        LocalCluster localCluster = new LocalCluster();
        localCluster.submitTopology("local-heatmap", config, topology);
    }
}