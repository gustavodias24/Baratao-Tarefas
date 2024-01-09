package benicio.solucoes.baratotarefas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import benicio.solucoes.baratotarefas.databinding.ActivityLoginBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityTarefasBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutFiltroBinding;

public class TarefasActivity extends AppCompatActivity {

    private ActivityTarefasBinding mainBinding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private Dialog dialogFiltro;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityTarefasBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.semtarefas).into(mainBinding.semtarefaimage);
        getSupportActionBar().setTitle("TAREFAS");

        mainBinding.fab.setOnClickListener( view -> {
            startActivity(new Intent(this, CriacaoTarefaActivity.class));
        });

        mainBinding.filtroBtn.setOnClickListener(view -> dialogFiltro.show());

        configurarDialogFiltro();
    }

    private void configurarDialogFiltro(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Filtrar Tarefas");
        LayoutFiltroBinding filtroBinding = LayoutFiltroBinding.inflate(getLayoutInflater());
        b.setView(filtroBinding.getRoot());
        dialogFiltro = b.create();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if ( item.getItemId() == R.id.sair){
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}