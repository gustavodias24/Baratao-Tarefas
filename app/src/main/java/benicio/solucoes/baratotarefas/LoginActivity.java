package benicio.solucoes.baratotarefas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import com.squareup.picasso.Picasso;

import benicio.solucoes.baratotarefas.databinding.ActivityLoginBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mainBinding;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Picasso.get().load(R.raw.logo).into(mainBinding.logo);

        mainBinding.cadastrarNovaConta.setOnClickListener(view -> startActivity(new Intent(this, CadastroActivity.class)));


    }
}