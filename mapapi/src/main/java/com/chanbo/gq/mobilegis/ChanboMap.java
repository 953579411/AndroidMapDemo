package com.chanbo.gq.mobilegis;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.esri.android.map.Layer;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.TiledServiceLayer;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnMapEventListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.io.UserCredentials;
import com.esri.core.portal.BaseMap;
import com.esri.core.portal.WebMap;

import java.util.List;

/**
 * Created by su on 2015-11-10.
 */
public class ChanboMap  {

    private String mapURL = "http://gis.gqlife.cn/arcgis/rest/services/China/MapServer";
    private double center_x = 113.2746;
    private double center_y = 23.1277;
    private int lonlat_wkid = 4326;
    private int initial_level = 7;

    protected MapView mapView;
    protected Context context;
    private ArcGISTiledMapServiceLayer layer;

    public ChanboMap(ViewGroup container) {
        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9");
        Context context = container.getContext();
        mapView = new MapView(context);
        this.context = context;
        mapView.setEsriLogoVisible(false);
    //    mDynamicServiceLayer = new ArcGISTiledMapServiceLayer(mapURL);
        layer = new ArcGISTiledMapServiceLayer(mapURL);
        mapView.addLayer(layer);
        container.addView(mapView);

        mapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object source, STATUS status) {

                if (status.equals(STATUS.INITIALIZED)) {
                  //  Point centerPoint = new Point(113.2746, 23.1277);
                  /*  Point mapPoint = (Point) GeometryEngine.project(
                            centerPoint, SpatialReference.create(4326),
                            mapView.getSpatialReference());
                    mapView.zoomTo(mapPoint, 10000);*/
                   // mapView.centerAndZoom(23.1277, 113.2746, 7);

                    mapView.centerAt((Point) GeometryEngine.project(new Point(center_x, center_y), SpatialReference.create(lonlat_wkid), mapView.getSpatialReference()), false);
                    mapView.setResolution(layer.getTileInfo().getResolutions()[initial_level]);
                    //callBack.onMapLoad();// 地图加载完成，回调到activity做相应处理
                }
            }
        });
    }

    public void zoomIn() {
        mapView.zoomin();
    }

    public void zoomOut() {
        mapView.zoomout();
    }

    public void centerAt(double lon, double lat) {
/*        Point p = new Point();
        p.setX(lon);
        p.setY(lat);
        Point mapPoint = (Point) GeometryEngine.project(
                p, SpatialReference.create(4326),
                mapView.getSpatialReference());
        Log.e("centerAt", "X:" + String.valueOf(mapPoint.getX()) + ";Y:" + String.valueOf(mapPoint.getY()));*/
        //mapView.zoomTo(mapPoint, 10000);
  //      mapView.centerAndZoom(lat, lon, 7);
        mapView.centerAt(lat, lon, true);
    }

  /*  public void setExtent(double xmin, double ymin, double xmax, double ymax) {
        Envelope mapExtent = new Envelope(xmin, ymin, xmax, ymax);
        Envelope mapExtent2 = (Envelope) GeometryEngine.project(
                mapExtent, SpatialReference.create(4326),
                mapView.getSpatialReference());
      //  mapExtent.
        mapView.setExtent(mapExtent2);
    }*/

    public void zoomAll(){
        mapView.centerAt((Point) GeometryEngine.project(new Point(center_x, center_y), SpatialReference.create(lonlat_wkid), mapView.getSpatialReference()), false);
        mapView.setResolution(layer.getTileInfo().getResolutions()[initial_level]);
    }

    /*public void show(){
      //  int r = mapView.addLayer(new ArcGISTiledMapServiceLayer(mapURL));
     //   Log.e("MAP2", mapURL);
      //  Log.e("Mapr", String.valueOf(r));
    }*/
}
