package benicio.solucoes.baratotarefas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import com.squareup.picasso.Picasso;

import benicio.solucoes.baratotarefas.databinding.ActivityLoginBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityTarefasBinding;

public class TarefasActivity extends AppCompatActivity {

    private ActivityTarefasBinding mainBinding;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityTarefasBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.semtarefas).into(mainBinding.semtarefaimage);
        getSupportActionBar().setTitle("TAREFAS");
    }
}