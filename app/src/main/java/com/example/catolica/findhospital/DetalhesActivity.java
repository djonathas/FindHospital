package com.example.catolica.findhospital;

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

        TextView txtLatitudeValor = (TextView) findViewById(R.id.txtLatitudeValor);
        TextView txtLongitudeValor = (TextView) findViewById(R.id.txtLongitudeValor);

        txtLatitudeValor.setText(extras.getString("latitude"));
        txtLongitudeValor.setText(extras.getString("longitude"));

        if(Objects.equals(extras.getString("hideTracarRota"), "true")) {
            TextView txtNomeValor = (TextView) findViewById(R.id.txtNomeValor);
            TextView txtEnderecoValor = (TextView) findViewById(R.id.txtEnderecoValor);
            Button btnTracarRota = (Button) findViewById(R.id.btnTracarRota);

            txtNomeValor.setVisibility(View.INVISIBLE);
            txtEnderecoValor.setVisibility(View.INVISIBLE);
            btnTracarRota.setVisibility(View.INVISIBLE);
        }
    }
}
