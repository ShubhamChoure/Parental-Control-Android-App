package BottomNavigation.ParentNavigation;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jspm.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

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
    GeoPoint startPoint;
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
        initMap();
        getChildName();
        // Inflate the layout for this fragment
        return view;
    }

    void initMap(){
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx,getActivity().getSharedPreferences("ParentMap",Context.MODE_PRIVATE));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        location = new Location("");
        startMarker = new Marker(mapView);
        firebaseDatabase = FirebaseDatabase.getInstance("https://parent-control-eb1f8-default-rtdb.asia-southeast1.firebasedatabase.app/");
    }
    void setMap(Location location){
        mapController = mapView.getController();
        startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        mapController.setZoom(17.9);
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
    }