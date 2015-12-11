package com.example.chanbo.demoapplication;

import com.chanbo.gq.mobilegis.*;
import com.esri.android.map.event.OnPinchListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnZoomListener;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    //MapView mMapView;
    private ChanboMap chanboMap;
    private ImageLayer imageLayer;
    private GeometryLayer geometryLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chanboMap = (ChanboMap)findViewById(R.id.map);
    /*    chanboMap.mapView.setOnPinchListener(new OnPinchListener() {
            @Override
            public void prePointersMove(float v, float v1, float v2, float v3, double v4) {
                Log.d("ANDROIDMAPDEMO", "prePointersMove");
            }

            @Override
            public void postPointersMove(float v, float v1, float v2, float v3, double v4) {
                Log.d("ANDROIDMAPDEMO", "postPointersMove");
            }

            @Override
            public void prePointersDown(float v, float v1, float v2, float v3, double v4) {
                Log.d("ANDROIDMAPDEMO", "prePointersDown");
            }

            @Override
            public void postPointersDown(float v, float v1, float v2, float v3, double v4) {
                Log.d("ANDROIDMAPDEMO", "postPointersDown");
            }

            @Override
            public void prePointersUp(float v, float v1, float v2, float v3, double v4) {
                Log.d("ANDROIDMAPDEMO", "prePointersUp");
            }

            @Override
            public void postPointersUp(float v, float v1, float v2, float v3, double v4) {
                Log.d("ANDROIDMAPDEMO", "postPointersUp");
            }
        });

        chanboMap.mapView.setOnSingleTapListener(new OnSingleTapListener() {

            @Override
            public void onSingleTap(float x, float y) {
                Log.d("ANDROIDMAPDEMO", "ChanboMap:onSingleTap:x:" + String.valueOf(x) + ";y:" + String.valueOf(y));
            }
        });

        chanboMap.setOnZoomListener(new OnZoomListener() {
            @Override
            public void preAction(float v, float v1, double v2) {
                Log.d("ANDROIDMAPDEMO", "preAction:v:" + String.valueOf(v) + ";v1:" + String.valueOf(v1) + ";v2:" + String.valueOf(v2));
            }

            @Override
            public void postAction(float v, float v1, double v2) {
                Log.d("ANDROIDMAPDEMO", "postAction:v:" + String.valueOf(v) + ";v1:" + String.valueOf(v1) + ";v2:" + String.valueOf(v2));
            }
        });*/
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

   //     FrameLayout container = (FrameLayout) findViewById(R.id.container);
        //chanboMap =  new ChanboMap(container);
        imageLayer = new ImageLayer(chanboMap);
        imageLayer.setOnTapGraphicListener(new OnTapGraphicListener() {
            @Override
            public void onSingleTap(String s) {
                Log.d("ANDROIDMAPDEMO", "ImageLayer:Targetid" + s);
            }
        });
        geometryLayer = new GeometryLayer(chanboMap);


      /*  FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
             //chanboMap.show();
             return true;
        }
        else if (id == R.id.action_centerAt) {
            chanboMap.centerAt(113.2746, 23.1277);
            return true;
        }
        else if (id == R.id.action_zoomIn) {
            chanboMap.zoomIn();
            return true;
        }
        else if (id == R.id.action_zoomOut) {
            chanboMap.zoomOut();
            return true;
        }
        else if (id == R.id.action_zoomAll) {
            chanboMap.zoomAll();
            return true;
        }
        else if (id == R.id.action_drawTarget) {
            Drawable image = this.getBaseContext().getResources()
                    .getDrawable(R.drawable.chanbo_map_logo);
            imageLayer.add("ABC", 113.2746, 23.1277, image);
            return true;
        }
        else if (id == R.id.action_removeTarget) {
            imageLayer.remove("ABC");
            return true;
        }
        else if (id == R.id.action_clearTarget) {
            imageLayer.clear();
            return true;
        }
        else if (id == R.id.action_drawPolygon) {
            geometryLayer.drawPolygon();
            return true;
        }
        else if (id == R.id.action_unDoGeometry) {
            geometryLayer.undo();
            return true;
        }
        else if (id == R.id.action_deleteSelectedVertex) {
            geometryLayer.deleteSelectedVertex();
            return true;
        }
        else if (id == R.id.action_clearGeometry) {
            geometryLayer.clear();
            return true;
        }
        else if (id == R.id.action_getPointsGeometry) {
            ArrayList<MapPoint> mps = geometryLayer.getPoints();
            Log.d("ANDROIDMAPDEMO", "getPoints.length:" + String.valueOf(mps.size()));
            for(int i = 0; i < mps.size(); i ++){
                Log.d("ANDROIDMAPDEMO", "getPoints.x:" + String.valueOf(i + 1) + ":" + String.valueOf(mps.get(i).getLon()));
                Log.d("ANDROIDMAPDEMO", "getPoints.y:" + String.valueOf(i + 1) + ":" + String.valueOf(mps.get(i).getLat()));
            }
            return true;
        }
        else if (id == R.id.action_drawPolyline) {
            geometryLayer.drawPolyline();
            return true;
        }
        else if (id == R.id.action_drawRect) {
            geometryLayer.drawRect();
            return true;
        }
        else if (id == R.id.action_drawLine) {
            geometryLayer.drawLine();
            return true;
        }
        else if (id == R.id.action_gpsToMap) {
         //   geometryLayer.drawLine();
            MapPoint p = MapUtils.GpsToMap(113.43781932, 22.53157433);
            Log.d("ANDROIDMAPDEMO", "gpsToMap:lon:" + String.valueOf(p.getLon()) + ";lat:" + String.valueOf(p.getLat()));
            return true;
        }
        else if (id == R.id.action_findPoint) {
            MapPointLayer mapPointLayer = new MapPointLayer(chanboMap);
            mapPointLayer.setOnFindPointListener(new OnFindPointListener() {
                @Override
                public void onFindPoint(List<FindPointResult> list) {
                    for (int i = 0; i < list.size(); i++) {
                        double x = list.get(i).x;
                        double y = list.get(i).y;
                        String name = list.get(i).name;
                        Log.d("ANDROIDMAPDEMO", "X:" + String.valueOf(x) + ";Y:" + String.valueOf(y) + ";NAME:" + name);
                    }
                }
            });
            mapPointLayer.findPointInView("麦当劳");
            return true;
        }
        else if (id == R.id.action_findRoad) {
            MapRoadLayer mapRoadLayer = new MapRoadLayer(chanboMap);
            mapRoadLayer.setOnFindRoadListener(new OnFindRoadListener() {
                @Override
                public void onFindRoad(List<FindRoadResult> list) {
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject shape = list.get(i).shape;
                        String name = list.get(i).name;
                        Log.d("ANDROIDMAPDEMO", "NAME:" + name + ";shape:" + shape.toString());
                    }
                }
            });
            mapRoadLayer.findRoadInView("番中");
            return true;
        }
        else if (id == R.id.action_findRoute) {
            RouteLayer routeLayer = new RouteLayer(chanboMap);
            routeLayer.setOnFindRouteListener(new OnFindRouteListener() {
                @Override
                public void onFindRoute(FindRouteResult findRouteResult) {
                    Log.d("ANDROIDMAPDEMO", "总长度:" + String.valueOf(findRouteResult.getTotalLength()));
                    List<NodePoint> list = findRouteResult.getData();
                    for (int i = 0; i < list.size(); i++) {
                        double lon = list.get(i).getLon();
                        double lat = list.get(i).getLat();
                      //  double dis = list.get(i).getDistanceFromStart();
                        Log.d("ANDROIDMAPDEMO", "lon:" + String.valueOf(lon) + ";lat:" + String.valueOf(lat));
                    }
                }
            });
            routeLayer.findFastestRoute(113.443, 22.528, 111.698, 21.954);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
