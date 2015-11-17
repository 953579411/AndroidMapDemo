package com.example.su.myapplication;

import com.chanbo.gq.mobilegis.*;

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



import java.util.ArrayList;


public class MainActivity extends Activity {
    //MapView mMapView;
    private ChanboMap chanboMap;
    private ImageLayer imageLayer;
    private GeometryLayer geometryLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        FrameLayout container = (FrameLayout) findViewById(R.id.container);
        chanboMap =  new ChanboMap(container);
        imageLayer = new ImageLayer(chanboMap);
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
            Log.e("getPoints.length:", String.valueOf(mps.size()));
            for(int i = 0; i < mps.size(); i ++){
                Log.e("getPoints.x:", String.valueOf(i + 1) + ":" + String.valueOf(mps.get(i).getLon()));
                Log.e("getPoints.y:", String.valueOf(i + 1) + ":" + String.valueOf(mps.get(i).getLat()));
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
            Log.e("gpsToMap", "lon:" + String.valueOf(p.getLon()) + ";lat:" + String.valueOf(p.getLat()));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
