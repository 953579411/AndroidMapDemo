package com.chanbo.gq.mobilegis;

/**
 * Created by su on 2015-11-13.
 */
public class MapUtils {
    public static MapPoint GpsToMap(double lon, double lat){
        LocatePoint p = new LocatePoint();
        Transformgcjwgs.wgs2gcj(lon, lat, p);
        MapPoint mp = new MapPoint();
        mp.setLon(p.Lon);
        mp.setLat(p.Lat);
        return mp;
    }
}
