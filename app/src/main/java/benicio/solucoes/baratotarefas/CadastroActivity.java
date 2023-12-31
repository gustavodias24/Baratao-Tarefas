package benicio.solucoes.baratotarefas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Base64;
import java.util.UUID;

import benicio.solucoes.baratotarefas.databinding.ActivityCadastroBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityLoginBinding;
import benicio.solucoes.baratotarefas.model.UserModel;

public class CadastroActivity extends AppCompatActivity {
    private static final String TAG = "mayara";
    private ActivityCadastroBinding mainBinding;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        getSupportActionBar().setTitle("CADASTRO");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainBinding.entrar.setOnClickListener( view -> {
            mainBinding.textInfo.setVisibility(View.GONE);
            UserModel novoUsuario = new UserModel();

            String id = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            Log.d(TAG, "onCreate: "+ id);
            novoUsuario.setId(id);
            novoUsuario.setEmail(mainBinding.emailField.getEditText().getText().toString().trim());
            novoUsuario.setLogin(mainBinding.loginField.getEditText().getText().toString().trim());
            novoUsuario.setNome(mainBinding.nomeField.getEditText().getText().toString().trim());
            novoUsuario.setSenha(mainBinding.senhaField.getEditText().getText().toString().trim());

            if ( !novoUsuario.getEmail().isEmpty() && !novoUsuario.getSenha().isEmpty() ){
                auth.createUserWithEmailAndPassword(novoUsuario.getEmail(), novoUsuario.getSenha()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        refUsuarios.child(novoUsuario.getId()).setValue(novoUsuario).addOnCompleteListener( taskUsuario -> {
                            if ( taskUsuario.isSuccessful() ){
                                auth.signInWithEmailAndPassword(novoUsuario.getEmail(), novoUsuario.getSenha()).addOnCompleteListener( logarTask -> {
                                    if ( task.isSuccessful()){
                                        Toast.makeText(CadastroActivity.this, "Usuário criado com sucesso.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(this, TarefasActivity.class));
                                    }else{
                                        exibirError("Conta criada, volte e faça login!");
                                    }
                                });
                            }else{
                                exibirError("Erro de conexão, tente novamente.");
                            }
                        });
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            exibirError("Senha fraca!");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            exibirError("Email inválido!");
                        } catch (FirebaseAuthUserCollisionException e) {
                            exibirError("Já existe um usuário com o mesmo email!");
                        } catch (Exception e) {
                            exibirError(e.getMessage());
                        }
                    }
                });
            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }

    public void exibirError(String erro){
        mainBinding.textInfo.setVisibility(View.VISIBLE);
        mainBinding.textInfo.setText(erro);
        Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
    }
}