package BottomNavigation.ParentNavigation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.Objects;

import Locks.SetPatternActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParentMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParentMap extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    MapView mapView;
    String childName;
    String childTemp;
    Location location;
    double latitude,longitude;
    Marker startMarker;
    IMapController mapController;
    FirebaseDatabase firebaseDatabase;
    GeoPoint startPoint,geofencePoint;
    Polygon polygon;
    Toolbar mapToolbar;
    MapEventsOverlay mapEventsOverlay;
    MapEventsReceiver mapEventsReceiver;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String GeoFence_Shared_Pref_Id ="GEOFENCE_DATA";
    SearchView  searchView;
    public ParentMap() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParentMap.
     */
    // TODO: Rename and change types and number of parameters
    public static ParentMap newInstance(String param1, String param2) {
        ParentMap fragment = new ParentMap();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parent_map, container, false);
        mapView = view.findViewById(R.id.parentMapView);
        mapToolbar = view.findViewById(R.id.mapToolbar);
        setMapToolbar();
        initMap();
        setGeofenceMarker();
        getChildName();


        // Inflate the layout for this fragment
        return view;
    }

    void initMap(){
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx,getActivity().getSharedPreferences("ParentMap",Context.MODE_PRIVATE));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        sharedPreferences = getContext().getSharedPreferences(GeoFence_Shared_Pref_Id,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        location = new Location("");
        startMarker = new Marker(mapView);
        mapController = mapView.getController();
        firebaseDatabase = FirebaseDatabase.getInstance("https://parent-control-eb1f8-default-rtdb.asia-southeast1.firebasedatabase.app/");
    }
    void setMap(Location location){
        startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setZoom(13.0);
        mapController.setCenter(startPoint);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(startMarker);
    }

    void getChildName() {
        db.collection("Relation").whereEqualTo("Mail", mAuth.getCurrentUser().getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
              if(task.isSuccessful()) {
                  for (QueryDocumentSnapshot document : task.getResult()) {
                      childTemp = document.getString("LinkChild");
                  }

                  int dotIndex = childTemp.indexOf('.');
                  childName = childTemp.substring(0, dotIndex);
                  Log.e("6969", "Child Name = " + childName);
                  updateMap();
                  updateMapOnChange();
              }else{
                  Log.e("6969", "Fetching Child Name Was Unsuccessful");
              }
            }
        });
    }
    private void updateMap() {

        firebaseDatabase.getReference(childName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    DataSnapshot dataSnapshot = task.getResult();
                    String locationStr = (String) dataSnapshot.getValue();
                    Log.e("6969","Location is fetched from database : "+locationStr);
                    String latStr = locationStr.substring(0,locationStr.indexOf('a')).trim();
                    String lonStr = locationStr.substring(locationStr.indexOf('d')+1).trim();
                    latitude = Double.parseDouble(latStr);
                    longitude = Double.parseDouble(lonStr);
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);
                    setMap(location);
                }
            });
        }
        void updateMapOnChange(){
        firebaseDatabase.getReference(childName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String locationStr = (String) snapshot.getValue();
                Log.e("6969","Location is fetched from database : "+locationStr);
                String latStr = locationStr.substring(0,locationStr.indexOf('a')).trim();
                String lonStr = locationStr.substring(locationStr.indexOf('d')+1).trim();
                latitude = Double.parseDouble(latStr);
                longitude = Double.parseDouble(lonStr);
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                setMap(location);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.setGeofenceHomeOption){
            if(geofencePoint==null){
                Toast.makeText(getContext(), "Please Tap On Map To Select Location ", Toast.LENGTH_SHORT).show();
            }else{
                setGeofenceHomeSquare();
            }
        }else if (item.getItemId()==R.id.setGeofenceSchoolOption) {
            if(geofencePoint==null){
                Toast.makeText(getContext(), "Please Tap On Map To Select Location ", Toast.LENGTH_SHORT).show();
            }else{
                setGeofenceSchoolSquare();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_parent_map,menu);
        super.onCreateOptionsMenu(menu, inflater);

    }
    void setMapToolbar(){
        setHasOptionsMenu(true);
        setMenuVisibility(true);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mapToolbar);
    }

    void setGeofenceMarker(){
        mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                geofencePoint = new GeoPoint(p.getLatitude(),p.getLongitude());
                Marker marker = new Marker(mapView);
                marker.setPosition(p);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                marker.setIcon(getResources().getDrawable(R.drawable.baseline_location_on_24));
                mapView.getOverlays().add(marker);
                mapView.invalidate();
                Log.e("6969","Tapped On Map");
                return true;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };
        mapEventsOverlay = new MapEventsOverlay(mapEventsReceiver);
        mapView.getOverlays().add(mapEventsOverlay);
    }

    void setGeofenceHomeSquare(){
        //creating square of 500 meter
        ArrayList<GeoPoint> squarePoints = new ArrayList<>();
        polygon = new Polygon();
        GeoPoint centerCo = new GeoPoint(geofencePoint.getLatitude(),geofencePoint.getLongitude());
        GeoPoint temp = centerCo.destinationPoint(250,360);
        GeoPoint b = temp.destinationPoint(250,90);
        editor.putFloat("HomeBLat",((float) b.getLatitude())).commit();
        editor.putFloat("HomeBLong",((float) b.getLongitude())).commit();
        squarePoints.add(b);
        GeoPoint d = b.destinationPoint(500,180);
        editor.putFloat("HomeDLat",((float) d.getLatitude())).commit();
        editor.putFloat("HomeDLong",((float) d.getLongitude())).commit();
        squarePoints.add(d);
        GeoPoint c = d.destinationPoint(500,270);
        editor.putFloat("HomeCLat",((float) c.getLatitude())).commit();
        editor.putFloat("HomeCLong",((float) c.getLongitude())).commit();
        squarePoints.add(c);
        GeoPoint a = c.destinationPoint(500,360);
        editor.putFloat("HomeALat",((float) a.getLatitude())).commit();
        editor.putFloat("HomeALong",((float) a.getLongitude())).commit();
        squarePoints.add(a);
        polygon.setPoints(squarePoints);
        mapView.getOverlays().add(polygon);
    }
    void setGeofenceSchoolSquare(){
        //creating square of 500 meter
        ArrayList<GeoPoint> squarePoints = new ArrayList<>();
        polygon = new Polygon();
        GeoPoint centerCo = new GeoPoint(geofencePoint.getLatitude(),geofencePoint.getLongitude());
        GeoPoint temp = centerCo.destinationPoint(250,360);
        GeoPoint b = temp.destinationPoint(250,90);
        editor.putFloat("SchoolBLat",((float) b.getLatitude())).commit();
        editor.putFloat("SchoolBLong",((float) b.getLongitude())).commit();
        squarePoints.add(b);
        GeoPoint d = b.destinationPoint(500,180);
        editor.putFloat("SchoolDLat",((float) d.getLatitude())).commit();
        editor.putFloat("SchoolDLong",((float) d.getLongitude())).commit();
        squarePoints.add(d);
        GeoPoint c = d.destinationPoint(500,270);
        editor.putFloat("SchoolCLat",((float) c.getLatitude())).commit();
        editor.putFloat("SchoolCLong",((float) c.getLongitude())).commit();
        squarePoints.add(c);
        GeoPoint a = c.destinationPoint(500,360);
        editor.putFloat("SchoolALat",((float) a.getLatitude())).commit();
        editor.putFloat("SchoolALong",((float) a.getLongitude())).commit();
        squarePoints.add(a);
        polygon.setPoints(squarePoints);
        mapView.getOverlays().add(polygon);
    }
}