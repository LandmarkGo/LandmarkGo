package com.OPSC.OPSC7312;
import android.Manifest.permission;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import org.apache.commons.io.IOUtils;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    //google map object
    private GoogleMap mMap;
    //current and destination location objects
    Location myLocation = null;
    Location destinationLocation = null;
    protected LatLng start = null;
    protected LatLng FavStart = null;
    protected LatLng end = null;
    final static String TAG = "MapsActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String distanceString = "";
    private String durationString = "";
    private Bitmap photoBitmap = null;
    Autocomplete autocomplete;
    double ratingValue;
    String favID;
    private BottomSheetDialog bottomSheetDialog;
    private FloatingActionButton fab;
    TextView MeasurementText;
    private String placeID;
    private View bottomSheetView;
    private String PlaceName = "";
    private String MarkerAddress = "";
    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 23;
    boolean locationPermission = false;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;
    private String AddressEnd;
    float ratingBarValue;
    String uid;
    RatingBar ratingBar;
    boolean isFABOpen = false;
    private String Units = "metric";
    private String preferred = "";
    //polyline object
    private List<Polyline> polylines = null;
    DatabaseReference dbRef, dbRefSettings, dbRefClubs, dbRefRest, dbRefLiq, dbRefKFC;
    FirebaseUser user;
    private FirebaseDatabase rootNode;
    ArrayList<String> ID = new ArrayList<>();
    public MapsActivity() throws IOException {}
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //request location permission.
        requestPermision();

        //init google map fragment to show map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        dbRef = FirebaseDatabase.getInstance().getReference().child("Favorite");
        rootNode = FirebaseDatabase.getInstance();

        dbRefSettings = FirebaseDatabase.getInstance().getReference().child("Settings");
        rootNode = FirebaseDatabase.getInstance();

        dbRefClubs = FirebaseDatabase.getInstance().getReference().child("Locations").child("Clubs");
        rootNode = FirebaseDatabase.getInstance();

        dbRefKFC = FirebaseDatabase.getInstance().getReference().child("Locations").child("KFC");
        rootNode = FirebaseDatabase.getInstance();

        dbRefLiq = FirebaseDatabase.getInstance().getReference().child("Locations").child("liquorstores");
        rootNode = FirebaseDatabase.getInstance();

        dbRefRest = FirebaseDatabase.getInstance().getReference().child("Locations").child("Restaurants");
        rootNode = FirebaseDatabase.getInstance();


        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();



        //Gets Favorite Places from Firebase
        FavoritesToArrayList();

        SearchBar();



        fab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        fab1 = (FloatingActionButton) findViewById(R.id.floating_action_button1);
        fab2 = (FloatingActionButton) findViewById(R.id.floating_action_button2);
        fab3 = (FloatingActionButton) findViewById(R.id.floating_action_button3);
        fab4 = (FloatingActionButton) findViewById(R.id.floating_action_button4);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (!isFABOpen)
                {
                    showFABMenu();
                    //Set fab to new background image
                    fab.setImageResource(R.drawable.ic_close);
                    fab1.setImageResource(R.drawable.travel);
                    fab1.setTooltipText("Clear Route");
                    fab2.setImageResource(R.drawable.favoritered);
                    fab2.setTooltipText("Favourites");
                    fab3.setTooltipText("Measurement");
                    fab4.setTooltipText("Landmarks");
                }
                else
                {
                    closeFABMenu();
                    fab.setImageResource(R.drawable.ic_plus_24);
                }
            }
        });

        fab1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mMap.clear();
                //CustomMarkersJacks();
                //CustomMarkersKFC();
                //CustomMarkersHardwareStores();
                //CustomMarkersRestaurants();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Go to activity_favorites
                Intent intent = new Intent(MapsActivity.this, Favorites.class);
                startActivity(intent);
            }
        });

        //Pull measurement by uid from firebase
        dbRefSettings.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    {
                        SettingsClass cc = ds.getValue(SettingsClass.class);
                        if (cc.getUid().equals(uid))
                        {
                            Units = cc.getMeasurement();
                            if (Units.equals("metric"))
                            {
                                fab3.setImageResource(R.drawable.km);
                            }
                            else
                            {
                                fab3.setImageResource(R.drawable.miles);
                            }
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        //Pull measurement by uid from firebase
        dbRefSettings.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    {
                        SettingsClass cc = ds.getValue(SettingsClass.class);
                        if (cc.getUid().equals(uid))
                        {
                            Log.d(TAG,"Testing if getting UID " + cc.getUid() + "UID Method" + uid);

                            preferred = cc.getPreferredLandmark();
                            Log.d(TAG, "Preferred Landmark: " + cc.getPreferredLandmark());
                            if (preferred.equals("KFC"))
                            {
                                fab4.setImageResource(R.drawable.kfc);
                                mMap.clear();
                                CustomMarkersKFC();
                            }
                            else if (preferred.equals("HardwareStores"))
                            {
                                fab4.setImageResource(R.drawable.bottle);
                                mMap.clear();
                                CustomMarkersHardwareStores();
                            }
                            else if (preferred.equals("Restaurant"))
                            {
                                fab4.setImageResource(R.drawable.rest);
                                mMap.clear();
                                CustomMarkersRestaurants();
                            }
                            else if (preferred.equals("Jacks"))
                            {
                                fab4.setImageResource(R.drawable.clubs);
                                mMap.clear();
                                CustomMarkersJacks();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        fab3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (Units.equals("metric"))
                {
                    fab3.setImageResource(R.drawable.miles);
                    Units = "imperial";
                    SettingsClass cc = new SettingsClass(uid, Units, preferred);
                    dbRefSettings.child(cc.getUid() + "").setValue(cc);
                }
                else
                {
                    fab3.setImageResource(R.drawable.km);
                    Units = "metric";
                    SettingsClass cc = new SettingsClass(uid, Units, preferred);
                    dbRefSettings.child(cc.getUid() + "").setValue(cc);
                }
            }

        });
        showPreferredMarkers();

    }


    private void requestPermision()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
        }
        else
        {
            locationPermission = true;
        }
    }


    private void showFABMenu()
    {
        isFABOpen = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_62));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_125));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_185));
        fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_230));
    }

    private void closeFABMenu()
    {
        isFABOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab4.animate().translationY(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case LOCATION_REQUEST_CODE:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //if permission granted.
                    locationPermission = true;
                    getMyLocation();
                }
                else
                {
                    //if permission denied.
                    locationPermission = false;
                }
                return;
            }
        }
    }

    boolean myLocationFound = false;

    //to get user location
    private void getMyLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        //Get my current location

        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
        {
            @Override
            public void onMyLocationChange(Location location)
            {
                myLocation = location;
                //Convert location to LatLong
                start = new LatLng(location.getLatitude(), location.getLongitude());

                Log.d(TAG, "MyLocation: TEST2.0" + start);

                LatLng ltlng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltlng, 16f);

                if (myLocationFound == false)
                {
                    mMap.animateCamera(cameraUpdate);
                    myLocationFound = true;

                    showPreferredMarkers();
                    try
                    {
                        checkIntent();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });


        //longPress();

        //get destination location when user click on map
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng latLng)
            {

                end = latLng;
                //Separate latLng value to separate coordinates in double
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;

                mMap.clear();
                //Show preferred markers

                mMap.addMarker(new MarkerOptions().title("Destination").position(end));

                getMyLocation();

                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                Log.d(TAG, "MyLocation setOnMapClicklistener: " + start);

                bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.BottomSheetDialogTheme);
                bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));

                try
                {
                    Json(start, end);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                FavouriteIcon();

                Button btn = bottomSheetView.findViewById(R.id.buttonDirections);
                bottomSheetView.findViewById(R.id.buttonDirections).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Findroutes(start, end);
                    }
                });
            }
        });

    }

    private void showPreferredMarkers()
    {
        //Pull measurement by uid from firebase
        dbRefSettings.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    {
                        SettingsClass cc = ds.getValue(SettingsClass.class);
                        if (cc.getUid().equals(uid)) {
                            preferred = cc.getPreferredLandmark();

                            if (preferred.equals("KFC"))
                            {
                                fab4.setImageResource(R.drawable.kfc);
                                mMap.clear();
                                CustomMarkersKFC();
                            }
                            else if (preferred.equals("HardwareStores"))
                            {
                                fab4.setImageResource(R.drawable.tools);
                                mMap.clear();
                                CustomMarkersHardwareStores();
                            }
                            else if (preferred.equals("Restaurant"))
                            {
                                fab4.setImageResource(R.drawable.rest);
                                mMap.clear();
                                CustomMarkersRestaurants();
                            }
                            else if (preferred.equals("Jacks"))
                            {
                                fab4.setImageResource(R.drawable.bagel);
                                mMap.clear();
                                CustomMarkersJacks();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (preferred.equals("KFC"))
                {
                    fab4.setImageResource(R.drawable.bottle);
                    preferred = "HardwareStores";
                    SettingsClass cc = new SettingsClass(uid, Units, preferred);
                    dbRefSettings.child(cc.getUid() + "").setValue(cc);
                }
                else if (preferred.equals("HardwareStores"))
                {
                    fab4.setImageResource(R.drawable.rest);
                    preferred = "Restaurant";
                    SettingsClass cc = new SettingsClass(uid, Units, preferred);
                    dbRefSettings.child(cc.getUid() + "").setValue(cc);
                }
                else if (preferred.equals("Restaurant"))
                {
                    fab4.setImageResource(R.drawable.clubs);
                    preferred = "Jacks";
                    SettingsClass cc = new SettingsClass(uid, Units, preferred);
                    dbRefSettings.child(cc.getUid() + "").setValue(cc);
                }
                else if (preferred.equals("Jacks"))
                {
                    fab4.setImageResource(R.drawable.kfc);
                    preferred = "KFC";
                    SettingsClass cc = new SettingsClass(uid, Units, preferred);
                    dbRefSettings.child(cc.getUid() + "").setValue(cc);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        getMyLocation();
    }

    public void longPress()
    {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                end = latLng;

                mMap.clear();

                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                Findroutes(start, end);

                bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.BottomSheetDialogTheme);
                bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));
                bottomSheetView.findViewById(R.id.buttonDirections).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });
    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(MapsActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {
            Routing routing = new Routing.Builder().travelMode(AbstractRouting.TravelMode.DRIVING).withListener(this).alternativeRoutes(true).waypoints(Start, End).key("AIzaSyANNxd0f6zOf_VvWwI-B3bY0rOblhUW410").build();
            bottomSheetDialog.dismiss();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e)
    {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart()
    {
        Log.d(TAG, "Routing Started");
        mMap.clear();
        showPreferredMarkers();
    }

    //If Route finding success..

    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;

        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++)
        {
            if (i == shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);
            }
            else {}
        }
        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        mMap.addMarker(endMarker);
        //Animate to destination
        CameraUpdate center1 = CameraUpdateFactory.newLatLng(polylineEndLatLng);
        CameraUpdate zoom1 = CameraUpdateFactory.zoomTo(16);
        mMap.moveCamera(center1);
    }

    @Override
    public void onRoutingCancelled()
    {
        Findroutes(start, end);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Findroutes(start, end);
    }

    public void SearchBar()
    {
        // Initialize Places.
        Places.initialize(getApplicationContext(), "AIzaSyANNxd0f6zOf_VvWwI-B3bY0rOblhUW410");
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setCountry("ZA");
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER));
        Context context = getApplicationContext();

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            @Override
            public void onPlaceSelected(Place place)
            {
                bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.BottomSheetDialogTheme);
                bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));
                //Set the textViews to the place name and address
                String ID = place.getId();
                TextView placeName = bottomSheetView.findViewById(R.id.NameOfPlace1);

                TextView placePhone = bottomSheetView.findViewById(R.id.PhoneOfPlace);
                ImageView photoBitmap = bottomSheetView.findViewById(R.id.Picture);

                placeName.setText(place.getAddress());

                placePhone.setText(place.getPhoneNumber());
                Log.d("Place", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getPhoneNumber());

                LatLng coordinates = place.getLatLng();

                end = coordinates;

                mMap.clear();

                start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

                Log.d("End", coordinates.toString());
                Log.d("Start", start.toString());

                // Define a Place ID.
                final String placeId = ID;

                final List<Place.Field> fields = Collections.singletonList(Place.Field.PHOTO_METADATAS);

                final FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, fields);

                placesClient.fetchPlace(placeRequest).addOnSuccessListener((response) ->
                {
                    final Place place2 = response.getPlace();

                    // Get the photo metadata.
                    final List<PhotoMetadata> metadata = place2.getPhotoMetadatas();
                    if (metadata == null || metadata.isEmpty())
                    {
                        Log.w(TAG, "No photo metadata.");
                        return;
                    }
                    final PhotoMetadata photoMetadata = metadata.get(0);

                    // Get the attribution text.
                    final String attributions = photoMetadata.getAttributions();

                    // Create a FetchPhotoRequest.
                    final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata).build();
                    placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) ->
                            {
                                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                                photoBitmap.setImageBitmap(bitmap);
                            }
                    ).addOnFailureListener((exception) ->
                    {
                        if (exception instanceof ApiException)
                        {
                            final ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + exception.getMessage());
                            final int statusCode = apiException.getStatusCode();
                            // TODO: Handle error with given status code.
                        }
                    });
                });
                try
                {
                    Json(start, end);
                    Log.d("Json", "Json");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                bottomSheetView.findViewById(R.id.buttonDirections).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // TODO: Get info about the selected place.
                        MarkerOptions endMarker = new MarkerOptions();
                        endMarker.position(place.getLatLng());
                        start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        end = place.getLatLng();
                        Findroutes(start, place.getLatLng());
                        bottomSheetDialog.dismiss();
                    }
                });

                FavouriteIcon();

                placePhone.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + place.getPhoneNumber()));
                        startActivity(intent);
                    }
                });

            }
            @Override
            public void onError(Status status)
            {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    public void Json(LatLng start, LatLng end) throws IOException
    {
        Log.d(TAG, "Json: " + start.toString() + end.toString());

        //Convert variable start from LatLng to a string
        String startString = start.toString();
        //Remove the brackets from the string
        startString = startString.replace("(", "");
        //Remove lat/lng from the string
        startString = startString.replace("lat/lng: ", "");
        //Remove the bracket and the end of the string
        startString = startString.replace(")", "");
        Log.d(TAG, "Json: " + startString);

        //Convert variable end from LatLng to a string
        String endString = end.toString();
        //Remove the brackets from the string
        endString = endString.replace("(", "");
        //Remove lat/lng from the string
        endString = endString.replace("lat/lng: ", "");
        //Remove the bracket and the end of the string
        endString = endString.replace(")", "");
        //Split up the two strings into two separate strings
        String[] startArray = startString.split(",");
        String[] endArray = endString.split(",");

        HttpUrl mySearchUrl = new HttpUrl.Builder().scheme("https").host("maps.googleapis.com").addPathSegment("maps").addPathSegment("api").addPathSegment("distancematrix").addPathSegment("json").addQueryParameter("origins", startArray[0] + "," + startArray[1] + "&destinations=" + endString + "&units=" + Units + "&key=AIzaSyANNxd0f6zOf_VvWwI-B3bY0rOblhUW410").build();
        String afterDecode = URLDecoder.decode(mySearchUrl.toString(), "UTF-8");

        Log.d(TAG, "Json: " + mySearchUrl.toString());

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url(afterDecode).method("GET", null).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                Log.d(TAG, "onResponse: " + response.body().string());
                Thread thread = new Thread()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Response response = client.newCall(request).execute();
                            JSONObject jsonObject = new JSONObject(response.body().string());// parse response into json object
                            //Pull origin_addresses from JSON
                            JSONArray destinationAddresses = jsonObject.getJSONArray("destination_addresses");
                            //Pull our duration and distance from the json object
                            JSONArray rows = jsonObject.getJSONArray("rows");
                            JSONObject elements = rows.getJSONObject(0);
                            JSONArray elementsArray = elements.getJSONArray("elements");
                            JSONObject distance = elementsArray.getJSONObject(0);
                            JSONObject duration = elementsArray.getJSONObject(0);
                            JSONObject distanceObject = distance.getJSONObject("distance");
                            JSONObject durationObject = duration.getJSONObject("duration");
                            distanceString = distanceObject.getString("text");
                            durationString = durationObject.getString("text");
                            Log.d(TAG, "Distance: " + distanceString + " Duration" + durationString);
                            //Update UI using method
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Log.d(TAG, "run: " + distanceString + durationString);

                                    //Format origin_addresses
                                    String destinationAddress = destinationAddresses.toString();
                                    destinationAddress = destinationAddress.replace("[", "");
                                    destinationAddress = destinationAddress.replace("]", "");
                                    destinationAddress = destinationAddress.replace("\"", "");

                                    TextView Distance = bottomSheetView.findViewById(R.id.DistanceOfPlace);
                                    TextView Time = bottomSheetView.findViewById(R.id.TimeOfArrival);
                                    TextView Address = bottomSheetView.findViewById(R.id.NameOfPlace1);
                                    Address.setText(destinationAddress);
                                    Distance.setText(distanceString);
                                    Time.setText(durationString);

                                    AddressEnd = destinationAddress;
                                    Log.d(TAG, "run: " + end.latitude + ": " + end.longitude);

                                }
                            });
                        }
                        catch (JSONException | IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                thread.setPriority(Thread.MAX_PRIORITY);
                thread.start();

            }
        });
    }

    boolean Favorite = false;

    public void checkIfFav()
    {
        Intent intent = getIntent();
        Double Lat = intent.getDoubleExtra("Lat", 0);
        Double Long = intent.getDoubleExtra("Long", 0);
    }

    public void FavouriteIcon()
    {
        String RandomID = UUID.randomUUID().toString();

        ImageButton btnFave = bottomSheetView.findViewById(R.id.ic_favorite);
        ratingBar = bottomSheetView.findViewById(R.id.ratingBar);


        for (int i = 0; i < alFavorites.size(); i++)
        {
            if (alFavorites.get(i).getLatitude() == end.latitude && alFavorites.get(i).getLongitude() == end.longitude)
            {
                btnFave.setImageResource(R.drawable.favoritered);
                favID = alFavorites.get(i).getFavID();
                ratingBar.setRating((float) alFavorites.get(i).getRating());
                ratingBar.setIsIndicator(false);
                Favorite = true;
            }
        }




        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener()
        {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser)
            {
                if (fromUser)
                {
                    Log.d(TAG, "onRatingChanged: " + rating);
                    ratingBarValue = rating;
                    ratingValue = ratingBarValue;

                    FavoriteClass cc = new FavoriteClass(uid, end.latitude, end.longitude, ratingValue, AddressEnd, favID);
                    dbRef.child(favID + "").setValue(cc);


                }
            }
        });





        btnFave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (Favorite == false)
                {
                    FavoriteClass cc = new FavoriteClass(uid, end.latitude, end.longitude, ratingValue, AddressEnd, RandomID);
                    dbRef.child(RandomID + "").setValue(cc);
                    btnFave.setImageResource(R.drawable.favoritered);
                    Favorite = true;
                    ratingBar.setIsIndicator(false);
                    favID = RandomID;
                }
                else
                {
                    for (int i = 0; i < alFavorites.size(); i++)
                    {
                        if (alFavorites.get(i).getLatitude() == end.latitude && alFavorites.get(i).getLongitude() == end.longitude)
                        {
                            dbRef.child(alFavorites.get(i).getFavID()).removeValue();
                        }
                    }
                    btnFave.setImageResource(R.drawable.favorite);
                    ratingBar.setIsIndicator(true);
                    ratingBar.setRating(0);
                    ratingBarValue = 0;
                    Favorite = false;
                }
            }
        });







    }

    ArrayList<FavoriteClass> alFavorites = new ArrayList<FavoriteClass>();

    private void FavoritesToArrayList()
    {
        DatabaseReference refFavorites = FirebaseDatabase.getInstance().getReference().child("Favorite");
        refFavorites.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                alFavorites.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    FavoriteClass cc = ds.getValue(FavoriteClass.class);
                    if (cc.getUid().equals(uid))
                    {
                        alFavorites.add(cc);
                        Log.d(TAG, "Favorite Found! Added to alFavorites");
                        Log.d(TAG, "favID: " + cc.getFavID());
                    } else {
                        Log.d(TAG, "UID Mismatch: " + cc.getUid() + " | " + uid);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public void checkIntent() throws IOException
    {
        //Check intents from Favorites class
        Intent intent = getIntent();
        Double Lat = intent.getDoubleExtra("Lat", 0);
        Double Long = intent.getDoubleExtra("Long", 0);
        Double rating = intent.getDoubleExtra("Rating", 0);
        Log.d(TAG, "Lat Intent: " + Lat + " Long Intent: " + Long);




        //Convert to LatLng
        //My current location in LatLng

        //start = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        end = new LatLng(Lat, Long);
        Log.d(TAG, "Lat Line 925: " + Lat + " Long Intent: " + Long);

        if (Lat != 0 && Long != 0)
        {
            LatLng latLng = new LatLng(Lat, Long);

            Log.d(TAG, "End: " + latLng);

            mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));

            //mMap.addMarker(new MarkerOptions().position(latLng).title("Destination"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

            Log.d(TAG, "Start Test 1234: " + start);
            Log.d(TAG, "End Test 1234: " + latLng);

            bottomSheetDialog = new BottomSheetDialog(MapsActivity.this, R.style.BottomSheetDialogTheme);
            bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_bottom_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));

            ratingBar = bottomSheetView.findViewById(R.id.ratingBar);
            ratingBar.setRating(rating.floatValue());

            FavouriteIcon();

            Json(start, latLng);

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            bottomSheetDialog.findViewById(R.id.buttonDirections).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mMap.clear();
                    Findroutes(start, latLng);
                    bottomSheetDialog.dismiss();
                    Log.d(TAG, "Testing123: " + start);
                }
            });
        }
    }
    public Bitmap resizeBitmap(String drawableName, int width, int height)
    {
        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier(drawableName, "drawable", getPackageName()));
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false);
    }

    ArrayList<Double> latitudeClub = new ArrayList<Double>();
    ArrayList<Double> longitudeClub = new ArrayList<Double>();

    ArrayList<Double> latitudeKFC = new ArrayList<Double>();
    ArrayList<Double> longitudeKFC = new ArrayList<Double>();

    ArrayList<Double> latitudeRest = new ArrayList<Double>();
    ArrayList<Double> longitudeRest = new ArrayList<Double>();

    ArrayList<Double> latitudeliquor = new ArrayList<Double>();
    ArrayList<Double> longitudeliquor = new ArrayList<Double>();

    public void CustomMarkersJacks() {
        //Pull custom markers from Firebase
        DatabaseReference refMarkers = FirebaseDatabase.getInstance().getReference().child("Locations").child("Jacks");
        refMarkers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LocationClass cc = ds.getValue(LocationClass.class);

                    latitudeClub.add(cc.getLatitude());
                    longitudeClub.add(cc.getLongitude());

                    //Get latitude and longitude list and save it to a LatLng list
                    for (int i = 0; i < latitudeClub.size(); i++)
                    {
                        LatLng latLng = new LatLng(latitudeClub.get(i), longitudeClub.get(i));
                        mMap.addMarker(new MarkerOptions()
                                .title("").position(latLng).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("bagel", 90, 100))));
                    }


                    Log.d(TAG, "I hope to god it works: " + cc.getLatitude() + " " + cc.getLongitude() + " | " + uid);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    public void CustomMarkersKFC()
    {
        //Pull custom markers from Firebase
        DatabaseReference refMarkers = FirebaseDatabase.getInstance().getReference().child("Locations").child("KFC");
        refMarkers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LocationClass cc = ds.getValue(LocationClass.class);

                    latitudeKFC.add(cc.getLatitude());
                    longitudeKFC.add(cc.getLongitude());

                    //Get latitude and longitude list and save it to a LatLng list
                    for (int i = 0; i < latitudeKFC.size(); i++) {
                        LatLng latLng = new LatLng(latitudeKFC.get(i), longitudeKFC.get(i));
                        mMap.addMarker(new MarkerOptions()
                                .title("").position(latLng).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("kfc", 90, 100))));
                    }


                    Log.d(TAG, "I hope to god it works: " + cc.getLatitude() + " " + cc.getLongitude() + " | " + uid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void CustomMarkersRestaurants() {
        //Pull custom markers from Firebase
        DatabaseReference refMarkers = FirebaseDatabase.getInstance().getReference().child("Locations").child("Restaurants");
        refMarkers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LocationClass cc = ds.getValue(LocationClass.class);

                    latitudeRest.add(cc.getLatitude());
                    longitudeRest.add(cc.getLongitude());

                    //Get latitude and longitude list and save it to a LatLng list
                    for (int i = 0; i < latitudeRest.size(); i++) {
                        LatLng latLng = new LatLng(latitudeRest.get(i), longitudeRest.get(i));
                        mMap.addMarker(new MarkerOptions()
                                .title("").position(latLng).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("rest", 90, 100))));

                    }
                    //Set onclicklisteners for map markers by using the LatLng list
                    for (int i = 0; i < latitudeRest.size(); i++) {
                        LatLng latLng = new LatLng(latitudeRest.get(i), longitudeRest.get(i));
                        mMap.setOnMarkerClickListener(marker -> {
                            if (marker.getPosition().equals(latLng)) {
                                Log.d(TAG, "Marker Clicked: " + marker.getPosition());

                            }
                            return false;
                        });
                    }
                    Log.d(TAG, "I hope to god it works: " + cc.getLatitude() + " " + cc.getLongitude() + " | " + uid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    public void CustomMarkersHardwareStores()
    {
        //Pull custom markers from Firebase
        DatabaseReference refMarkers = FirebaseDatabase.getInstance().getReference().child("Locations").child("HardwareStores");
        refMarkers.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    LocationClass cc = ds.getValue(LocationClass.class);

                    latitudeliquor.add(cc.getLatitude());
                    longitudeliquor.add(cc.getLongitude());

                    //Get latitude and longitude list and save it to a LatLng list
                    for (int i = 0; i < latitudeliquor.size(); i++) {
                        LatLng latLng = new LatLng(latitudeliquor.get(i), longitudeliquor.get(i));
                        mMap.addMarker(new MarkerOptions()
                                .title("").position(latLng).icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap("tools", 90, 100))));
                    }
                    Log.d(TAG, "I hope to god it works: " + cc.getLatitude() + " " + cc.getLongitude() + " | " + uid);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}