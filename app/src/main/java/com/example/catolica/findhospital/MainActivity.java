package com.example.catolica.findhospital;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "mapa";
    private GoogleMap mMap;
    private List<Local> locais = new ArrayList<>();
    private Marker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                callDialog("Você precisa dar permissão de acesso a sua localização", new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locais.add(new Local("Unidade de Pronto Atendimento Norte - UPA Norte", "Q. 203 Norte Alameda Central - Plano Diretor Norte", -10.1749058, -48.3402127));
        locais.add(new Local("Hospital e Maternidade Pública Dona Regina Siqueira Campos", "Q. 104 Norte Rua NE 5, Lote 21/41 - Centro, 77006-020", -10.1816082, -48.3257026));
        locais.add(new Local("Hospital Geral de Palmas Dr. Francisco Ayres - HGP", "201 Sul - Av. Ns1, Conjunto 02, Lote 02, s/n - Plano Diretor Sul, 77015-202", -10.1962363, -48.3358432));
        locais.add(new Local("Hospital Oswaldo Cruz", "OC ACSU SO 40 - Centro, 77000-000", -10.208447, -48.3366885));
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

        for (Local local : locais) {
            addMarker(local);
        }

        minhaPosicao();

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

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

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(TAG, "setOnInfoWindowClickListener");
                Intent intent = new Intent(getApplicationContext(), DetalhesActivity.class);
                intent.putExtra("nome", marker.getTitle());
                intent.putExtra("endereco", marker.getSnippet());
                intent.putExtra("latitude", String.valueOf(marker.getPosition().latitude));
                intent.putExtra("longitude", String.valueOf(marker.getPosition().longitude));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        userMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    public void addMarker(Local local) {
        LatLng point = new LatLng(local.latitude, local.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(local.nome);
        markerOptions.snippet(local.endereco);

        mMap.addMarker(markerOptions);
    }

    public void addUserMarker(Local local) {
        LatLng point = new LatLng(local.latitude, local.longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(local.nome);
        markerOptions.snippet(local.endereco);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_person_pin));

        userMarker = mMap.addMarker(markerOptions);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.i(TAG, "setOnInfoWindowClickListener");
                Intent intent = new Intent(getApplicationContext(), DetalhesActivity.class);
                intent.putExtra("nome", marker.getTitle());
                intent.putExtra("endereco", marker.getSnippet());
                intent.putExtra("latitude", String.valueOf(marker.getPosition().latitude));
                intent.putExtra("longitude", String.valueOf(marker.getPosition().longitude));
                intent.putExtra("hideTracarRota", "true");
                startActivity(intent);
            }
        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
        Log.i(TAG, "addUserMarker");
    }

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
            locationManager.removeUpdates(this);

            addUserMarker(new Local("Você está aqui!", "", ultimaLocalizacao.getLatitude(), ultimaLocalizacao.getLongitude()));

            Log.i(TAG, "GPS");
        } else if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, this);
            ultimaLocalizacao = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.removeUpdates(this);

            addUserMarker(new Local("Você está aqui!", "", ultimaLocalizacao.getLatitude(), ultimaLocalizacao.getLongitude()));

            Log.i(TAG, "Network");
        } else {
            Toast.makeText(MainActivity.this, "Seu GPS e sua rede estão desativos, favor ativá-los.", Toast.LENGTH_SHORT).show();
        }
    }
}
