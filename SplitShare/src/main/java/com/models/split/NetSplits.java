package com.models.split;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Harshit Rajvaidya
 */
public class NetSplits implements Serializable {

    public String totalGet;

    public String totalOwe;

    public Map<String, String> breakdown;

    public NetSplits(String totalGet, String totalOwe, Map<String, String> breakdown) {
        this.totalGet = totalGet;
        this.totalOwe = totalOwe;
        this.breakdown = breakdown;
    }


}
