package BottomNavigation.ParentNavigation;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jspm.R;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

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

    MapView mapView;
    IMapController mapController;
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
        setMap();
        // Inflate the layout for this fragment
        return view;
    }

    void initMap(){
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx,getActivity().getSharedPreferences("ParentMap",Context.MODE_PRIVATE));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
    }
    void setMap(){
        mapController = mapView.getController();
        startPoint = new GeoPoint(18.532398, 73.944065);
        mapController.setZoom(17.9);
        mapController.setCenter(startPoint);
    }
}