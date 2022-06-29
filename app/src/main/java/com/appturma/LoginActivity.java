package com.appturma;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText edtUsuario,edtSenha;
    Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsuario = findViewById(R.id.editTextUsuario);
        edtSenha = findViewById(R.id.editTextSenha);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String usuario,senha;

                usuario = edtUsuario.getText().toString();
                senha = edtSenha.getText().toString();

                if (usuario.isEmpty() && senha.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this).setTitle("Erro").setMessage("Favor preencher os campos").setPositiveButton("OK",null);
                    builder.create().show();
                }

                else {
                    Intent base = new Intent(getApplicationContext(),BaseMenu.class);
                    startActivity(base);
                    finish();
                }
            }
        });



    }


}