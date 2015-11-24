package com.chanbo.gq.mobilegis;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.chanbo.mapapi.R;
import android.widget.FrameLayout;

import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

/**
 * TODO: document your custom view class.
 */
public class ChanboMap extends FrameLayout {
    protected MapView mapView;
    private String mapURL = "http://gis.gqlife.cn/arcgis/rest/services/China/MapServer";
    private ArcGISTiledMapServiceLayer layer;
    private double center_x = 113.2746;
    private double center_y = 23.1277;
    private int lonlat_wkid = 4326;
    private int initial_level = 7;

    public ChanboMap(Context context) {
        super(context);
        init(null, 0);
    }

    public ChanboMap(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ChanboMap(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int var6 = super.getChildCount();

        for(int var7 = 0; var7 < var6; ++var7) {
            View var8 = this.getChildAt(var7);
            if(var8.getVisibility() != View.INVISIBLE) {
                int var9 = this.getPaddingLeft();
                int var10 = this.getPaddingTop();
                var8.layout(var9, var10, var9 + var8.getMeasuredWidth(), var10 + var8.getMeasuredHeight());
                //      var8.layout(var9, var10, var9 + this.getWidth(), var10 + this.getHeight());
            }
        }
    }

    private void initMap(Context context, AttributeSet attrs, int defStyle) {
        ArcGISRuntime.setClientId("uK0DxqYT0om1UXa9");
        mapView = new MapView(context);
        mapView.setEsriLogoVisible(false);
        //   mapView.setlay
        //    mDynamicServiceLayer = new ArcGISTiledMapServiceLayer(mapURL);
        layer = new ArcGISTiledMapServiceLayer(mapURL);
        mapView.addLayer(layer);
        this.addView(mapView);

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

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.ChanboMap, defStyle, 0);


        a.recycle();

        this.initMap(this.getContext(), attrs, defStyle);
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
}
