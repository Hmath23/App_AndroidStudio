package com.appturma;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.appturma.TrocaSenha;

import org.json.JSONArray;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    // private String apiPath = "http://10.0.2.2/future-web/usuarios/listar/";
    private String apiPath = "http://10.0.2.2:8080/siteturma88/usuarios/listar/";
    private JSONArray restulJsonArray;
    private int logado = 0;
    private String mensagem = "", strusuario = "", stremail = "", newSenha ="";
    EditText edtUsuario,edtSenha;
    Button btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsuario = findViewById(R.id.editTextUsuario);
        edtSenha = findViewById(R.id.editTextSenha);
        btnLogin = findViewById(R.id.btnLogin);

        AndroidNetworking.initialize(getApplicationContext());

        btnLogin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String usuario,senha;

                usuario = edtUsuario.getText().toString();
                senha = edtSenha.getText().toString();

                if (usuario.isEmpty() || senha.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Erro")
                            .setMessage("Favor preencher os campos")
                            .setPositiveButton("OK",null);
                    builder.create().show();
                }

                else {
                    sendApi();
                }
            }
        });
    }



    protected void sendApi(){
        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);
        AndroidNetworking.post(apiPath)
                .addBodyParameter("HTTP_ACCEPT","application/json")
                .addBodyParameter("txtNomeUsuario",edtUsuario.getText().toString())
                .addBodyParameter("txtSenhaUsuario",edtSenha.getText().toString())
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // Se o objeto for diferente de nulo
                            if (jsonObject != null){
                                restulJsonArray = jsonObject.getJSONArray("RetornoDados");
                                JSONObject jsonObj = null;
                                for (int i=0; i < restulJsonArray.length();i++){
                                    jsonObj = restulJsonArray.getJSONObject(i);
                                    logado = jsonObj.getInt("plogado");
                                    strusuario = jsonObj.getString("pnomecompleto");
                                    stremail = jsonObj.getString("pemail");
                                }
                                switch (logado){
                                    case 1:
                                        mensagem = "Bem vindo ao Sistema";
                                        break;
                                    case 2:
                                        mensagem = "Usuário já está conectado";
                                        break;
                                    case 3:
                                        mensagem = "Primeiro acesso, portanto, altere sua senha";
                                        break;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("Aviso")
                                        .setMessage(mensagem)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (logado == 1){
                                                    Intent base = new Intent(getApplicationContext(),BaseMenu.class);
                                                    base.putExtra("usuario", strusuario.toString());
                                                    base.putExtra("email", stremail.toString());
                                                    startActivity(base);
                                                    finish();
                                                }
                                                else if (logado == 3 ){
                                                    Intent troca = new Intent(getApplicationContext(),TrocaSenha.class);
                                                    troca.putExtra("usuario", edtUsuario.getText().toString());
                                                    troca.putExtra("email", stremail.toString());
                                                    startActivity(troca);
                                                    finish();

                                                }
                                            }
                                        });
                                builder.create().show();
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        try {
                            if (anError.getErrorCode() == 0){
                                mensagem = "Problemas com a conexão!! \nTente Novamente.";
                            }
                            else {
                                JSONObject jsonObject = new JSONObject(anError.getErrorBody());
                                if (jsonObject.getJSONObject("RetornoDados").getInt("sucesso") == 0){
                                    mensagem = "Usuário ou senha inválidos";
                                }
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Aviso")
                                    .setMessage(mensagem)
                                    .setPositiveButton("OK",null);
                            builder.create().show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.d("BridgeUpdateService","error" + anError.getErrorCode() +anError.getErrorDetail());
                    }
                });
    }



}
