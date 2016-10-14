package com.example.catolica.findhospital;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class DetalhesActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        Bundle extras = getIntent().getExtras();

        final String latitude = extras.getString("latitude");
        final String longitude = extras.getString("longitude");

        TextView txtNomeValor = (TextView) findViewById(R.id.txtNomeValor);
        TextView txtEnderecoValor = (TextView) findViewById(R.id.txtEnderecoValor);
        TextView txtLatitudeValor = (TextView) findViewById(R.id.txtLatitudeValor);
        TextView txtLongitudeValor = (TextView) findViewById(R.id.txtLongitudeValor);
        Button btnTracarRota = (Button) findViewById(R.id.btnTracarRota);

        if(Objects.equals(extras.getString("nome"), "Você está aqui!")) {
            TextView txtNome = (TextView) findViewById(R.id.txtNome);
            TextView txtEndereco = (TextView) findViewById(R.id.txtEndereco);

            txtNome.setVisibility(View.GONE);
            txtEndereco.setVisibility(View.GONE);
            txtNomeValor.setVisibility(View.GONE);
            txtEnderecoValor.setVisibility(View.GONE);
            btnTracarRota.setVisibility(View.GONE);
        } else {
            txtNomeValor.setText(extras.getString("nome"));
            txtEnderecoValor.setText(extras.getString("endereco"));

            btnTracarRota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + ", " + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }

        txtLatitudeValor.setText(latitude);
        txtLongitudeValor.setText(longitude);
    }
}
