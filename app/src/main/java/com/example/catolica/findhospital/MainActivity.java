package com.example.catolica.findhospital;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "mapa";
    private GoogleMap mMap;
    private List<Local> locais = new ArrayList<>();
    private Marker userMarker;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog("Você precisa dar permissão de acesso a sua localização", new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }

        setContentView(R.layout.activity_main);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.i(TAG, "onAuthStateChanged:signed_in:user:" + user.getUid());
                } else {
                    Log.i(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            mAuth.addAuthStateListener(mAuthListener);
        } catch (Exception e) {
            Log.e(TAG, "onStart: " + e.toString());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //inicia os locais no mapa
        initLocais();

        //busca a posição do usuário e insere um marcador no mapa
        minhaPosicao();

        //cria uma popup personalizada para ser exibida ao clicar em um marcador no mapa
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                //cria uma view personalizada na caixa de detalhes do marcador do mapa
                Context context = getApplicationContext();

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        //muda a posição do marcador da posição do usuário toda vez que houver alteração de localização
        Log.i(TAG, "onLocationChanged");
        userMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //setando um evento de clique ao clicar na popup do marcador
        Log.i(TAG, "setOnInfoWindowClickListener");
        Local local = getLocal(marker.getTitle());

        Intent intent = new Intent(getApplicationContext(), DetalhesActivity.class);
        if (local != null) {
            intent.putExtra("nome", local.nome);
            intent.putExtra("endereco", local.endereco);
            intent.putExtra("imagem", local.imagem);
            intent.putExtra("latitude", String.valueOf(local.latitude));
            intent.putExtra("longitude", String.valueOf(local.longitude));
        } else {
            intent.putExtra("nome", marker.getTitle());
            intent.putExtra("latitude", String.valueOf(marker.getPosition().latitude));
            intent.putExtra("longitude", String.valueOf(marker.getPosition().longitude));
        }
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    //adiciona um marcador no mapa baseado nos dados do local
    public void addMarker(Local local) {
        LatLng point = new LatLng(local.latitude, local.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(local.nome);
        markerOptions.snippet(local.endereco);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_hospital_marker));

        mMap.addMarker(markerOptions);
    }

    //adiciona o marcador da posição atual do usuário
    public void addUserMarker(Local local) {
        LatLng point = new LatLng(local.latitude, local.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(local.nome);
        markerOptions.snippet(local.endereco);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_pin));

        userMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        Log.i(TAG, "addUserMarker");
    }

    //inicia os locais no mapa
    public void initLocais() {
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-10.2596918,-48.4746192&language=pt-BR&radius=50000&types=hospital|doctor&keyword=hospital&key=" + getString(R.string.google_maps_key);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("status").equals("OK")) {
                        JSONArray resultArray = response.getJSONArray("results");
                        for (int i = 0; i < resultArray.length(); i++) {
                            JSONObject item = resultArray.getJSONObject(i);
                            JSONObject location = item.getJSONObject("geometry").getJSONObject("location");
                            Local local = new Local(item.getString("name"), item.getString("vicinity"), item.getString("icon"), location.getDouble("lat"), location.getDouble("lng"));
                            locais.add(local);
                            addMarker(local);
                        }
                    } else {
                        Log.e(TAG, "onResponse: erro na string de pesquisa");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: " + error.toString());
            }
        });

        queue.add(request);
    }

    //chama a caixa de dialogo para confirmação de permissoes
    private void callDialog(String message, final String[] permissions) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões necessárias");
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "A aplicação não funcionará corretamente sem as permissões solicitadas.", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    //busca a posição a localização atual do usuario e adiciona um marcador ao mapa
    private void minhaPosicao() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Location ultimaLocalizacao;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog("Você precisa dar permissão de acesso a sua localização", new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }

        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
            ultimaLocalizacao = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            addUserMarker(new Local("Você está aqui!", ultimaLocalizacao.getLatitude(), ultimaLocalizacao.getLongitude()));

            Log.i(TAG, "GPS");
        } else if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
            ultimaLocalizacao = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            addUserMarker(new Local("Você está aqui!", ultimaLocalizacao.getLatitude(), ultimaLocalizacao.getLongitude()));

            Log.i(TAG, "Network");
        } else {
            Toast.makeText(MainActivity.this, "Seu GPS e sua rede estão desativos, favor ativá-los.", Toast.LENGTH_SHORT).show();
        }
    }

    //percorre o array em busca de um local especifico pelo nome
    private Local getLocal(String nome) {
        for (Local local : locais) {
            if (nome.equals(local.nome))
                return local;
        }
        return null;
    }
}
