package com.example.catolica.findhospital;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginEmailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        Button btnCreateUser = (Button) findViewById(R.id.btnCreateUser);
        Button btnSignInEmailPassword = (Button) findViewById(R.id.btnSignInEmailAndPassword);

        btnCreateUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startIntent("create");
            }
        });

        btnSignInEmailPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startIntent("login");
            }
        });
    }

    private void startIntent(String acao) {
        EditText etxtEmail = (EditText) findViewById(R.id.etxtEmail);
        EditText etxtSenha = (EditText) findViewById(R.id.etxtSenha);

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("email", etxtEmail.getText().toString());
        intent.putExtra("senha", etxtSenha.getText().toString());
        intent.putExtra("acao", acao);

        startActivity(intent);
    }

}
