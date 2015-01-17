package com.hm.heatmapping;

/**
 * Created by hmandala on 1/17/15.
 */
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import com.hm.heatmapping.GeocodeLookup;
import com.hm.heatmapping.HeatmapBuilder;

public class HeatmapTopologyBuilder {
    public static StormTopology build() {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("checkins", new Checkins(), 4);
        builder.setBolt("geocode-lookup", new GeocodeLookup(), 8)
                .setNumTasks(64)
                .shuffleGrouping("checkins");
        builder.setBolt("time-interval-extractor", new TimeIntervalExtractor(), 4)
                .shuffleGrouping("geocode-lookup");
        builder.setBolt("heatmap-builder", new HeatmapBuilder(), 4)
                .fieldsGrouping("time-interval-extractor",
                        new Fields("time-interval", "city"));
        builder.setBolt("persistor", new Persistor(), 1)
                .setNumTasks(4)
                .shuffleGrouping("heatmap-builder");
        return builder.createTopology();
    }
}