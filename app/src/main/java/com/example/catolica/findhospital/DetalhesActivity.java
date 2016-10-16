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

        //obtem os dados de latitude e longitude do marcador selecionado
        final String latitude = extras.getString("latitude");
        final String longitude = extras.getString("longitude");

        //obtendo os TextViews e Buttons da tela
        TextView txtNomeValor = (TextView) findViewById(R.id.txtNomeValor);
        TextView txtEnderecoValor = (TextView) findViewById(R.id.txtEnderecoValor);
        TextView txtTelefoneValor = (TextView) findViewById(R.id.txtTelefoneValor);
        TextView txtLatitudeValor = (TextView) findViewById(R.id.txtLatitudeValor);
        TextView txtLongitudeValor = (TextView) findViewById(R.id.txtLongitudeValor);
        Button btnTracarRota = (Button) findViewById(R.id.btnTracarRota);

        //Verifica se o marcardor é do local atual do usuário, neste caso, serão ocultas algumas
        // TextViews e Buttons que não se fazem necessários
        if(Objects.equals(extras.getString("nome"), "Você está aqui!")) {
            TextView txtNome = (TextView) findViewById(R.id.txtNome);
            TextView txtEndereco = (TextView) findViewById(R.id.txtEndereco);
            TextView txtTelefone = (TextView) findViewById(R.id.txtTelefone);

            txtNome.setVisibility(View.GONE);
            txtEndereco.setVisibility(View.GONE);
            txtTelefone.setVisibility(View.GONE);
            txtNomeValor.setVisibility(View.GONE);
            txtEnderecoValor.setVisibility(View.GONE);
            txtTelefoneValor.setVisibility(View.GONE);
            btnTracarRota.setVisibility(View.GONE);
        } else {
            //caso negativo, carrega todos os campos de tela
            txtNomeValor.setText(extras.getString("nome"));
            txtEnderecoValor.setText(extras.getString("endereco"));
            txtTelefoneValor.setText(extras.getString("telefone"));

            //setando o evento de clique no botão de Traçar Rota
            btnTracarRota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Chamando o app do google maps e passando as coordenadas para que o mesmo faça
                    //o processo de traçar a rota para o local
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + ", " + longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });
        }

        //setando a latitude e longitude nas TextViews da tela
        txtLatitudeValor.setText(latitude);
        txtLongitudeValor.setText(longitude);
    }
}
