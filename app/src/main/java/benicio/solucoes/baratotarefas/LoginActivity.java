package benicio.solucoes.baratotarefas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.squareup.picasso.Picasso;

import benicio.solucoes.baratotarefas.databinding.ActivityLoginBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityMainBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mainBinding;
    private Dialog dialogCarregando;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        configurarDialogCarregando();
        Picasso.get().load(R.raw.logo).into(mainBinding.logo);

        mainBinding.cadastrarNovaConta.setOnClickListener(view -> startActivity(new Intent(this, CadastroActivity.class)));

        mainBinding.entrar.setOnClickListener( view -> {
            String email = mainBinding.emailField.getEditText().getText().toString();
            String senha = mainBinding.senhaField.getEditText().getText().toString();

            if ( !email.isEmpty() && !senha.isEmpty() ){
                dialogCarregando.show();
                mainBinding.textInfo.setVisibility(View.GONE);

                auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener( loginTask -> {
                   if ( loginTask.isSuccessful() ){
                       finish();
                       startActivity(new Intent(this, TarefasActivity.class));
                       Toast.makeText(this, "Bem-vindo de volta!", Toast.LENGTH_SHORT).show();
                   }else{
                       try {
                           throw loginTask.getException();
                       }
                       catch (FirebaseNetworkException e){
                           exibirError("Problema de conexão da internet!");
                       }
                       catch (FirebaseAuthInvalidUserException e){
                           exibirError("Usuário não cadastrado!");
                       }
                       catch (FirebaseAuthInvalidCredentialsException e){
                           exibirError("Credenciais inválidas!");
                       }
                       catch (Exception e){
                           exibirError(e.getMessage());
                       }
                   }
                });
            }
        });

    }


    public void exibirError(String erro){
        dialogCarregando.dismiss();
        mainBinding.textInfo.setVisibility(View.VISIBLE);
        mainBinding.textInfo.setText(erro);
        Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }
}