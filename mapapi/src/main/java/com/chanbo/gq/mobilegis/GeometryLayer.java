package com.chanbo.gq.mobilegis;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

import java.util.ArrayList;

/**
 * Created by su on 2015-11-11.
 */
public class GeometryLayer {

    private enum EditMode {
        NONE, POINT, POLYLINE, POLYGON, SAVING, RECT, LINE
    }
    private MapView mapView;
    private GraphicsLayer mGraphicsLayerEditing;
    EditMode mEditMode;
    ArrayList<Point> mPoints = new ArrayList<Point>();
    ArrayList<Point> mMidPoints = new ArrayList<Point>();
    boolean mMidPointSelected = false;
    boolean mVertexSelected = false;
    int mInsertingIndex;
    ArrayList<EditingStates> mEditingStates = new ArrayList<EditingStates>();

    SimpleMarkerSymbol mRedMarkerSymbol = new SimpleMarkerSymbol(Color.RED, 20, SimpleMarkerSymbol.STYLE.CIRCLE);

    SimpleMarkerSymbol mBlackMarkerSymbol = new SimpleMarkerSymbol(Color.BLACK, 20, SimpleMarkerSymbol.STYLE.CIRCLE);

    SimpleMarkerSymbol mGreenMarkerSymbol = new SimpleMarkerSymbol(Color.GREEN, 15, SimpleMarkerSymbol.STYLE.CIRCLE);

    public GeometryLayer(ChanboMap map) {
        mapView = map.mapView;
        mGraphicsLayerEditing = new GraphicsLayer();
        map.mapView.addLayer(mGraphicsLayerEditing);
        mEditMode = EditMode.NONE;

        try {
            mapView.setOnTouchListener(new MyTouchListener(map.mapView.getContext(), mapView));
        }
        catch (Exception e){
            Log.e("GeometryLayer", "msg:" + e.getMessage() + ";tracktrace:" + e.getStackTrace());
        }
    }

    public void drawLine(){
        mEditMode = EditMode.LINE;
    }

    public void drawRect(){
        mEditMode = EditMode.RECT;
    }

    public void drawPolygon(){
        mEditMode = EditMode.POLYGON;
    }

    public void drawPolyline(){
        mEditMode = EditMode.POLYLINE;
    }

    public void clear(){
        mEditMode = EditMode.NONE;
            // Clear feature editing data
            mPoints.clear();
            mMidPoints.clear();
            mEditingStates.clear();

            mMidPointSelected = false;
            mVertexSelected = false;
            mInsertingIndex = 0;

            if (mGraphicsLayerEditing != null) {
                mGraphicsLayerEditing.removeAll();
            }

            // Update action bar to reflect the new state
          /*  updateActionBar();
            int resId;
            switch (mEditMode) {
                case POINT:
                    resId = R.string.title_add_point;
                    break;
                case POLYGON:
                    resId = R.string.title_add_polygon;
                    break;
                case POLYLINE:
                    resId = R.string.title_add_polyline;
                    break;
                case NONE:
                default:
                    resId = R.string.app_name;
                    break;
            }
            getActionBar().setTitle(resId);*/
        mapView.setShowMagnifierOnLongPress(false);
    }

    public ArrayList<MapPoint> getPoints(){
        ArrayList<MapPoint> mapPoints = new ArrayList<MapPoint>();
        for(int i = 0; i < mPoints.size(); i++){
            Point point = (Point) GeometryEngine.project(mPoints.get(i),
                    mapView.getSpatialReference(),
                    SpatialReference.create(4326));

            MapPoint mP = new MapPoint();
            mP.setLon(point.getX());
            mP.setLat(point.getY());
            mapPoints.add(mP);
        }
        return mapPoints;
    }

    public void undo() {
        if (mEditingStates.size() == 0)
            return;
        mEditingStates.remove(mEditingStates.size() - 1);
        mPoints.clear();
        if (mEditingStates.size() == 0) {
            mMidPointSelected = false;
            mVertexSelected = false;
            mInsertingIndex = 0;
        } else {
            EditingStates state = mEditingStates.get(mEditingStates.size() - 1);
            mPoints.addAll(state.points);
//            Log.d(TAG, "# of points = " + mPoints.size());
            mMidPointSelected = state.midPointSelected;
            mVertexSelected = state.vertexSelected;
            mInsertingIndex = state.insertingIndex;
        }
        refresh();
    }

    public void deleteSelectedVertex(){
        if (!mVertexSelected) {
            mPoints.remove(mPoints.size() - 1); // remove last vertex
        } else {
            mPoints.remove(mInsertingIndex); // remove currently selected vertex
        }
        mMidPointSelected = false;
        mVertexSelected = false;
        mEditingStates.add(new EditingStates(mPoints, mMidPointSelected, mVertexSelected, mInsertingIndex));
        refresh();
    }

    private class EditingStates {
        ArrayList<Point> points = new ArrayList<Point>();

        boolean midPointSelected = false;

        boolean vertexSelected = false;

        int insertingIndex;

        public EditingStates(ArrayList<Point> points, boolean midpointselected, boolean vertexselected, int insertingindex) {
            this.points.addAll(points);
            this.midPointSelected = midpointselected;
            this.vertexSelected = vertexselected;
            this.insertingIndex = insertingindex;
        }
    }

    /**
     * Draws polyline or polygon (dependent on current mEditMode) between the vertices in mPoints.
     */
    private void drawPolylineOrPolygon() {
        Graphic graphic;
        MultiPath multipath;

        // Create and add graphics layer if it doesn't already exist
        if (mGraphicsLayerEditing == null) {
            mGraphicsLayerEditing = new GraphicsLayer();
            mapView.addLayer(mGraphicsLayerEditing);
        }

        if (mPoints.size() > 1) {

            // Build a MultiPath containing the vertices
            if ((mEditMode == EditMode.POLYLINE) || (mEditMode == EditMode.RECT) || (mEditMode == EditMode.LINE)) {
                multipath = new Polyline();
            } else {
                multipath = new Polygon();
            }
            if (mEditMode == EditMode.RECT){
                if (mPoints.size() > 1) {
                    double xmin, ymin, xmax, ymax;
                    xmin = Math.min(mPoints.get(0).getX(), mPoints.get(1).getX());
                    ymin = Math.min(mPoints.get(0).getY(), mPoints.get(1).getY());
                    xmax = Math.max(mPoints.get(0).getX(), mPoints.get(1).getX());
                    ymax = Math.max(mPoints.get(0).getY(), mPoints.get(1).getY());
                    multipath.startPath(xmin, ymin);
                    multipath.lineTo(xmax, ymin);
                    multipath.lineTo(xmax, ymax);
                    multipath.lineTo(xmin, ymax);
                    multipath.lineTo(xmin, ymin);
                }
            } else {
                multipath.startPath(mPoints.get(0));
                for (int i = 1; i < mPoints.size(); i++) {
                    multipath.lineTo(mPoints.get(i));
                }
            }

            // Draw it using a line or fill symbol
            if ((mEditMode == EditMode.POLYLINE) || (mEditMode == EditMode.RECT) || (mEditMode == EditMode.LINE)) {
                graphic = new Graphic(multipath, new SimpleLineSymbol(Color.BLACK, 4));
            } else {
                SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.YELLOW);
                simpleFillSymbol.setAlpha(100);
                simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.BLACK, 4));
                graphic = new Graphic(multipath, (simpleFillSymbol));
            }
            mGraphicsLayerEditing.addGraphic(graphic);
        }
    }

    /**
     * Draws mid-point half way between each pair of vertices in mPoints.
     */
    private void drawMidPoints() {
        int index;
        Graphic graphic;

        mMidPoints.clear();
        if (mPoints.size() > 1) {

            // Build new list of mid-points
            for (int i = 1; i < mPoints.size(); i++) {
                Point p1 = mPoints.get(i - 1);
                Point p2 = mPoints.get(i);
                mMidPoints.add(new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2));
            }
            if (mEditMode == EditMode.POLYGON && mPoints.size() > 2) {
                // Complete the circle
                Point p1 = mPoints.get(0);
                Point p2 = mPoints.get(mPoints.size() - 1);
                mMidPoints.add(new Point((p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2));
            }

            // Draw the mid-points
            index = 0;
            for (Point pt : mMidPoints) {
                if (mMidPointSelected && mInsertingIndex == index) {
                    graphic = new Graphic(pt, mRedMarkerSymbol);
                } else {
                    graphic = new Graphic(pt, mGreenMarkerSymbol);
                }
                mGraphicsLayerEditing.addGraphic(graphic);
                index++;
            }
        }
    }

    /**
     * Draws point for each vertex in mPoints.
     */
    private void drawVertices() {
        int index = 0;
        SimpleMarkerSymbol symbol;

        for (Point pt : mPoints) {
            if (mVertexSelected && index == mInsertingIndex) {
                // This vertex is currently selected so make it red
                symbol = mRedMarkerSymbol;
            } else if (index == mPoints.size() - 1 && !mMidPointSelected && !mVertexSelected) {
                // Last vertex and none currently selected so make it red
                symbol = mRedMarkerSymbol;
            } else {
                // Otherwise make it black
                symbol = mBlackMarkerSymbol;
            }
            Graphic graphic = new Graphic(pt, symbol);
            mGraphicsLayerEditing.addGraphic(graphic);
            index++;
        }
    }

    /**
     * Updates action bar to show actions appropriate for current state of the app.
     */
    private void updateActionBar() {
       /* if (mEditMode == EditMode.NONE || mEditMode == EditMode.SAVING) {
            // We are not editing
            if (mEditMode == EditMode.NONE) {
                showAction(R.id.action_add, true);
            } else {
                showAction(R.id.action_add, false);
            }
            showAction(R.id.action_discard, false);
            showAction(R.id.action_save, false);
            showAction(R.id.action_delete, false);
            showAction(R.id.action_undo, false);
        } else {
            // We are editing
            showAction(R.id.action_add, false);
            showAction(R.id.action_discard, true);
            if (isSaveValid()) {
                showAction(R.id.action_save, true);
            } else {
                showAction(R.id.action_save, false);
            }
            if (mEditMode != EditMode.POINT && mPoints.size() > 0 && !mMidPointSelected) {
                showAction(R.id.action_delete, true);
            } else {
                showAction(R.id.action_delete, false);
            }
            if (mEditingStates.size() > 0) {
                showAction(R.id.action_undo, true);
            } else {
                showAction(R.id.action_undo, false);
            }
        }*/
    }

    /**
     * Redraws everything on the mGraphicsLayerEditing layer following an edit and updates the items shown on the action
     * bar.
     */
    private void refresh() {
        if (mGraphicsLayerEditing != null) {
            mGraphicsLayerEditing.removeAll();
        }
        drawPolylineOrPolygon();
        if ((mEditMode != EditMode.RECT) && (mEditMode != EditMode.LINE)) {
            drawMidPoints();
        }
        drawVertices();

        updateActionBar();
    }

    private class MyTouchListener extends MapOnTouchListener {
        MapView mapView;

        public MyTouchListener(Context context, MapView view) {
            super(context, view);
            mapView = view;
        }

        @Override
        public boolean onLongPressUp(MotionEvent point) {
            handleTap(point);
            super.onLongPressUp(point);
            return true;
        }

        @Override
        public boolean onSingleTap(final MotionEvent e) {
            handleTap(e);
            return true;
        }

        /***
         * Handle a tap on the map (or the end of a magnifier long-press event).
         *
         * @param e The point that was tapped.
         */
        private void handleTap(final MotionEvent e) {

            // Ignore the tap if we're not creating a feature just now
            if (mEditMode == EditMode.NONE || mEditMode == EditMode.SAVING) {
                return;
            }

            Point point = mapView.toMapPoint(new Point(e.getX(), e.getY()));

            // If we're creating a point, clear any existing point
            if (mEditMode == EditMode.POINT) {
                mPoints.clear();
            }

            // If a point is currently selected, move that point to tap point
            // If tap coincides with a mid-point, select that mid-point
            int idx1 = -1;
            if ((mEditMode != EditMode.RECT)  && (mEditMode != EditMode.LINE)) {
                idx1 = getSelectedIndex(e.getX(), e.getY(), mMidPoints, mapView);
            }
            if ((idx1 != -1) && (mEditMode != EditMode.RECT) && (mEditMode != EditMode.LINE)) {
                mMidPointSelected = true;
                mInsertingIndex = idx1;
            } else {
                // If tap coincides with a vertex, select that vertex
                int idx2 = getSelectedIndex(e.getX(), e.getY(), mPoints, mapView);
                if (idx2 != -1) {
                    mVertexSelected = true;
                    mInsertingIndex = idx2;
                } else  if (mMidPointSelected || mVertexSelected) {
                    movePoint(point);
                } else {
                    // No matching point above, add new vertex at tap point
                    if (((mEditMode == EditMode.RECT) || (mEditMode == EditMode.LINE)) && (mPoints.size() > 1)){
                        mPoints.remove(mPoints.size() - 1);
                    }
                    mPoints.add(point);
                    mEditingStates.add(new EditingStates(mPoints, mMidPointSelected, mVertexSelected, mInsertingIndex));
                }
            }


            // Redraw the graphics layer
            refresh();
        }

        /**
         * Checks if a given location coincides (within a tolerance) with a point in a given array.
         *
         * @param x Screen coordinate of location to check.
         * @param y Screen coordinate of location to check.
         * @param points Array of points to check.
         * @param map MapView containing the points.
         * @return Index within points of matching point, or -1 if none.
         */
        private int getSelectedIndex(double x, double y, ArrayList<Point> points, MapView map) {
            final int TOLERANCE = 40; // Tolerance in pixels

            if (points == null || points.size() == 0) {
                return -1;
            }

            // Find closest point
            int index = -1;
            double distSQ_Small = Double.MAX_VALUE;
            for (int i = 0; i < points.size(); i++) {
                Point p = map.toScreenPoint(points.get(i));
                double diffx = p.getX() - x;
                double diffy = p.getY() - y;
                double distSQ = diffx * diffx + diffy * diffy;
                if (distSQ < distSQ_Small) {
                    index = i;
                    distSQ_Small = distSQ;
                }
            }

            // Check if it's close enough
            if (distSQ_Small < (TOLERANCE * TOLERANCE)) {
                return index;
            }
            return -1;
        }

        /**
         * Moves the currently selected point to a given location.
         *
         * @param point Location to move the point to.
         */
        private void movePoint(Point point) {
            if (mMidPointSelected) {
                // Move mid-point to the new location and make it a vertex
                mPoints.add(mInsertingIndex + 1, point);
            } else {
                // Must be a vertex: move it to the new location
                ArrayList<Point> temp = new ArrayList<Point>();
                for (int i = 0; i < mPoints.size(); i++) {
                    if (i == mInsertingIndex) {
                        temp.add(point);
                    } else {
                        temp.add(mPoints.get(i));
                    }
                }
                mPoints.clear();
                mPoints.addAll(temp);
            }
            // Go back to the normal drawing mode and save the new editing state
            if ((mEditMode != EditMode.RECT) && (mEditMode != EditMode.LINE)) {
                mMidPointSelected = false;
                mVertexSelected = false;
            }
            mEditingStates.add(new EditingStates(mPoints, mMidPointSelected, mVertexSelected, mInsertingIndex));
        }

    }
}
