package com.hm.heatmapping;

import backtype.storm.topology.base.*;
import backtype.storm.spout.*;
import backtype.storm.topology.*;
import backtype.storm.tuple.*;
import backtype.storm.task.*;

import java.io.IOException;
import java.util.Map;
import com.google.code.geocoder.*;
import com.google.code.geocoder.model.*;

/**
 * Created by hmandala on 1/17/15.
 */

public class GeocodeLookup extends BaseBasicBolt {
    private Geocoder geocoder;

    @Override
    public void declareOutputFields(OutputFieldsDeclarer fieldsDeclarer) {
        fieldsDeclarer.declare(new Fields("time", "geocode", "city"));
    }

    @Override
    public void prepare(Map config,
                        TopologyContext context) {
        geocoder = new Geocoder();
    }

    @Override
    public void execute(Tuple tuple,
                        BasicOutputCollector outputCollector) {
        String address = tuple.getStringByField("address");
        Long time = tuple.getLongByField("time");
        GeocoderRequest request = new GeocoderRequestBuilder()
                .setAddress(address)
                .setLanguage("en")
                .getGeocoderRequest();
        try {
            GeocodeResponse response = geocoder.geocode(request);
            GeocoderStatus status = response.getStatus();
            if (GeocoderStatus.OK.equals(status)) {
                GeocoderResult firstResult = response.getResults().get(0);
                LatLng latLng = firstResult.getGeometry().getLocation();
                String city = extractCity(firstResult);
                outputCollector.emit(new Values(time, latLng, city));
            }
        } catch(IOException e) {
            // Log
        }
    }

    private String extractCity(GeocoderResult result) {
        for (GeocoderAddressComponent component : result.getAddressComponents()) {
            if (component.getTypes().contains("locality")) return component.getLongName();
        }
        return "";
    }
}