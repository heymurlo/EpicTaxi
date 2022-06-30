package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.InputListener;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends Activity implements DrivingSession.DrivingRouteListener {

    private MapView mapview;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession = null;
    private PlacemarkMapObject nowPoint;
    private DrivingRoute lastRoute;
    private PolylineMapObject lastRoutePolyline;
    private PlacemarkMapObject userGeo;
    private Point mainPoint = new Point(54.7348, 55.957);
    private boolean setStartPosition = false;
    private PlacemarkMapObject currentPoint;


    private InputListener inputListener = new InputListener() {
        public void onMapTap(@NonNull Map map, @NonNull Point point) {
            if (setStartPosition) {
                mainPoint = point;
                userGeo.setGeometry(point);
                mapview.getMap().move(new CameraPosition(mainPoint, 14.0f, 0.0f, 0.0f));
                setStartPosition = false;
            } else {
                if (nowPoint == null) {
                    nowPoint = mapObjects.addPlacemark(point);
                } else {
                    nowPoint.setGeometry(point);
                }
                submitRequest(point);
            }
        }

        @Override
        public void onMapLongTap(@NonNull Map map, @NonNull Point point) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        MapKitFactory.setApiKey("c873c3d7-4da8-433b-94ee-664d84fa8aed");
        MapKitFactory.initialize(this);

        setCurrLocation();
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        mapview = (MapView) findViewById(R.id.mapview);
        mapview.getMap().move(
                new CameraPosition(new Point(mainPoint.getLatitude(), mainPoint.getLongitude()), 14.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null);
        mapview.getMap().getMapObjects().addPlacemark(new Point(mainPoint.getLatitude(), mainPoint.getLongitude()), ImageProvider.fromResource(this, R.drawable.arrow));
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter();
        mapObjects = mapview.getMap().getMapObjects().addCollection();
        userGeo = mapObjects.addPlacemark(mainPoint);
        mapview.getMap().addInputListener(inputListener);
    }


    @Override
    protected void onStop() {
        mapview.onStop();
        MapKitFactory.getInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapview.onStart();
    }

    public void setCurrLocation() {
        MyLocationListener.SetUpLocationListener(this);
        mainPoint = new Point(MyLocationListener.userLoc.getLatitude(),
                MyLocationListener.userLoc.getLongitude());

    }



    public void onClickCalculate (View view) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            double sum = ((lastRoute.getMetadata().getWeight().getDistance().getValue() * 99)
                    / 2000) + 100;
            dialog.setTitle("Стоимость поездки  " + String.format("%.0f", sum) + " рублей")
                    .create();
            dialog.setPositiveButton("OK", null);
            dialog.show();
    }

    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
        if(lastRoutePolyline != null){
            mapObjects.remove(lastRoutePolyline);
        }
        lastRoutePolyline = mapObjects.addPolyline(routes.get(0).getGeometry());
        lastRoute = routes.get(0);
    }


    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String errorMessage = getString(R.string.unknown_error_message);
        if (error instanceof RemoteError) {
            errorMessage = getString(R.string.remote_error_message);
        } else if (error instanceof NetworkError) {
            errorMessage = getString(R.string.network_error_message);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    private void submitRequest(Point routeEndPoint) {
        DrivingOptions drivingOptions = new DrivingOptions();
        VehicleOptions vehicleOptions = new VehicleOptions();
        ArrayList<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(
                mainPoint,
                RequestPointType.WAYPOINT,
                null));
        requestPoints.add(new RequestPoint(
                routeEndPoint,
                RequestPointType.WAYPOINT,
                null));
        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
    }


}