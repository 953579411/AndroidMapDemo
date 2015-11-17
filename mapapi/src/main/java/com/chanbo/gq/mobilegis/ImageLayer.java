package com.chanbo.gq.mobilegis;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.ViewGroup;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;

import java.util.Hashtable;

/**
 * Created by su on 2015-11-11.
 */
public class ImageLayer {

    private GraphicsLayer graphicsLayer;
    private Hashtable hashData = new Hashtable();
    private MapView mapView;

    public ImageLayer(ChanboMap map) {
        mapView = map.mapView;
        graphicsLayer = new GraphicsLayer();
        map.mapView.addLayer(graphicsLayer);
    }

    public void add(String targetId, double lon, double lat, Drawable img){
        ImageLayerObject imageLayerObject = new ImageLayerObject();
        imageLayerObject.targetId = targetId;
        imageLayerObject.lon = lon;
        imageLayerObject.lat = lat;
        imageLayerObject.img = img;
        if (hashData.containsKey(targetId)){
            int tmpid = ((ImageLayerObject)hashData.get(targetId)).graphicid;
            graphicsLayer.removeGraphic(tmpid);
        }
        hashData.put(targetId, imageLayerObject);

        PictureMarkerSymbol symbol = new PictureMarkerSymbol(img);
        Point point1 = new Point(lon, lat);
        // 投影
        Point point = (Point) GeometryEngine.project(point1,
                SpatialReference.create(4326),
                mapView.getSpatialReference());
        Graphic g = new Graphic(point, symbol);
        int id = graphicsLayer.addGraphic(g);
        imageLayerObject.graphicid = id;
    }

    public void remove(String targetId){
        if (hashData.containsKey(targetId)){
            int tmpid = ((ImageLayerObject)hashData.get(targetId)).graphicid;
            graphicsLayer.removeGraphic(tmpid);
            hashData.remove(targetId);
        }
    }

    public void clear(){
        hashData.clear();
        graphicsLayer.removeAll();
    }

}
