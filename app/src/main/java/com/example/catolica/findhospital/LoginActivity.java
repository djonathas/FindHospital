package com.example.catolica.findhospital;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_GOOGLE_SING_IN = 1;
    private static final String TAG = "login";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        init();
    }

    //inicia a autenticação
    public void init() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("538991968987-orddc58dgqfajo6ehcbac5uvvd2auebr.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Erro ao efetuar o login com o Google Plus", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        SignInButton btnSignInButton = (SignInButton) findViewById(R.id.sing_in_button_google);
        Button btnSignInAnonymously = (Button) findViewById(R.id.btnSignInAnonymously);
        Button btnSignInEmail = (Button) findViewById(R.id.btnSignInEmail);

        btnSignInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        btnSignInAnonymously.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firebaseAuthAnonymous();
            }
        });

        btnSignInEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginEmailActivity.class));
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String email = extras.getString("email");
            String senha = extras.getString("senha");

            if (extras.getString("acao").equals("create")) {
                firebaseCreateUser(email, senha);
            } else if (extras.getString("acao").equals("login")) {
                firebaseAuthEmailAndPassword(email, senha);
            }
        }
    }

    //logando com a conta do google
    private void signIn() {
        Toast.makeText(LoginActivity.this, "Logando com Google Plus...", Toast.LENGTH_SHORT).show();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_SING_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //evento disparado apos a chamada da tela de login do google
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_GOOGLE_SING_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }
        }
    }

    //metodo chamado quando o usuario seleciona uma conta google para login
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Falha na autenticação", Toast.LENGTH_SHORT).show();
                } else {
                    finish();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //metodo para logar como anonimo
    private void firebaseAuthAnonymous() {
        Toast.makeText(LoginActivity.this, "Logando como Anônimo", Toast.LENGTH_SHORT).show();

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i(TAG, "signInAnonymously:onComplete");
                if (!task.isSuccessful()) {
                    Log.e(TAG, "signInAnonymously", task.getException());
                    Toast.makeText(LoginActivity.this, "Falha na autenticação", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    //metodo para criar um usuario com email e senha
    private void firebaseCreateUser(final String email, final String senha) {
        Toast.makeText(LoginActivity.this, "Criando usuário...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i(TAG, "createUserWithEmailAndPassword:onComplete");
                if (!task.isSuccessful()) {
                    Log.e(TAG, "createUserWithEmailAndPassword", task.getException());
                    Toast.makeText(LoginActivity.this, "Falha na criação do usuário", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //metodo para logar usando email e senha anteriormente cadastrados
    private void firebaseAuthEmailAndPassword(String email, String senha) {
        Toast.makeText(LoginActivity.this, "Logando com e-mail...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.i(TAG, "signInWithEmailAndPassword:onComplete");
                if (!task.isSuccessful()) {
                    Log.e(TAG, "signInWithEmailAndPassword", task.getException());
                    Toast.makeText(LoginActivity.this, "Falha na autenticação", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
