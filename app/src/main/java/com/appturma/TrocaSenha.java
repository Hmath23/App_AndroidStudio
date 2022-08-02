package com.appturma;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.appturma.LoginActivity;
import com.appturma.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrocaSenha extends AppCompatActivity {

    private String apiTres = "http://10.0.2.2:8080/siteturma88/usuarios/trocar/";
    private JSONArray restulJsonArray;
    private int logado = 0;
    private String mensagem = "", strusuario = "", stremail = "", newSenha ="";
    private TextView txtnome, txtemail, txtnomeuser;
    EditText edtNovaSenha;
    public String novaSenha;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_troca_senha);

        txtnome = findViewById(R.id.textViewNomeCompleto);
        txtnomeuser = findViewById(R.id.textViewNomeUsuario);
        txtemail = findViewById(R.id.textViewEmailUsuario);

        edtNovaSenha = findViewById(R.id.editTextNovaSenha);

        Intent login = getIntent();
        txtnome.setText(String.valueOf(login.getStringExtra("nomecompleto")));
        txtnomeuser.setText(String.valueOf(login.getStringExtra("usuario")));
        txtemail.setText(String.valueOf(login.getStringExtra("email")));

        novaSenha = edtNovaSenha.getText().toString();
    }

    protected void apiNovaSenha(){
        AndroidNetworking.post(apiTres)
                .addBodyParameter("HTTP_ACCEPT","application/json")
                .addBodyParameter("txtNomeUsuario",txtnome.getText().toString())
                .addBodyParameter("txtEmailUsuario",txtemail.getText().toString())
                .addBodyParameter("txtNovaSenha", edtNovaSenha.getText().toString())
                .setTag("test")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                restulJsonArray = jsonObject.getJSONArray("RetornoDados");
                                JSONObject jsonObj = null;
                                jsonObj = restulJsonArray.getJSONObject(0);
                                logado = jsonObj.getInt("sucesso");
                                if (logado == 1) {
                                    Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(login);
                                    finish();
                                }
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError){
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(TrocaSenha.this)
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