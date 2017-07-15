package com.example.shwetashahane.assignment5;

/**
 * Created by shwetashahane on 4/14/17.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;


public class MainShowMapPanel extends Fragment {

    private GoogleMap gMap;
    private com.google.android.gms.maps.MapView mapView;
    static LatLng markerLatLong;
    public static double markerLat;
    public static double markerLong;

    public MainShowMapPanel() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("inside map fragment");
        View view = inflater.inflate(R.layout.fragment_showmap_main_panel, container, false);
        mapView = (com.google.android.gms.maps.MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                gMap = googleMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        markerLatLong = latLng;
                        markerLat = markerLatLong.latitude;
                        markerLong = markerLatLong.longitude;
                        gMap.addMarker(new MarkerOptions().position(markerLatLong).draggable(true).title("Current Location"));
                    }
                });

                gMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                    public void onMarkerDrag(Marker marker) {
                        System.out.println("Marker1 " + marker.getId() + " Drag@" + marker.getPosition());
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        LatLng dragPosition = marker.getPosition();
                        double dragLat = dragPosition.latitude;
                        double dragLong = dragPosition.longitude;
                        Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);
                    }

                    @Override
                    public void onMarkerDragStart(Marker marker) {
                        System.out.println("Marker3 " + marker.getPosition() + " DragStart");

                    }
                });
                gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        System.out.println("shweta");
                    }
                });

            }

        });

        Button doneButton = (Button) view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Show Map Fragment");
                System.out.println("Marker lat n long" + markerLat + "-" + markerLong);
                DecimalFormat df = new DecimalFormat("#.##");
                TextView latText = (TextView) getActivity().findViewById(R.id.latText);
                latText.setText("[" + df.format(markerLat) + "," + df.format(markerLong) + "]");
                getFragmentManager().popBackStack();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


}
