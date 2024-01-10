package benicio.solucoes.baratotarefas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import benicio.solucoes.baratotarefas.adapter.AdapterTarefas;
import benicio.solucoes.baratotarefas.databinding.ActivityLoginBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityTarefasBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutFiltroBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;
import benicio.solucoes.baratotarefas.model.TarefaModel;
import benicio.solucoes.baratotarefas.model.UserModel;

public class TarefasActivity extends AppCompatActivity {

    private RecyclerView recyclerTarefas;
    private AdapterTarefas adapterTarefas;
    private List<TarefaModel> tarefas = new ArrayList<>();
    private List<Integer> filtros = new ArrayList<>();
    private DatabaseReference refTarefas = FirebaseDatabase.getInstance().getReference().child("tarefas");
    private ActivityTarefasBinding mainBinding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String emailLogado ;
    private Dialog dialogFiltro,dialogCarregando;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityTarefasBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.semtarefas).into(mainBinding.semtarefaimage);
        getSupportActionBar().setTitle("TAREFAS");

        user = auth.getCurrentUser();
        emailLogado = user.getEmail();

        mainBinding.fab.setOnClickListener( view -> {
            startActivity(new Intent(this, CriacaoTarefaActivity.class));
        });

        mainBinding.filtroBtn.setOnClickListener(view -> dialogFiltro.show());

        configurarDialogCarregando();
        configurarDialogFiltro();
        configurarRecyclerTarefas();
    }

    @Override
    protected void onStart() {
        super.onStart();
        listarTarefas();
    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    private void configurarRecyclerTarefas() {
        recyclerTarefas = mainBinding.recyclerTarefas;
        recyclerTarefas.setLayoutManager(new LinearLayoutManager(this));
        recyclerTarefas.setHasFixedSize(true);
        recyclerTarefas.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterTarefas = new AdapterTarefas(tarefas, this);
        recyclerTarefas.setAdapter(adapterTarefas);
    }

    private void configurarDialogFiltro(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Filtrar Tarefas");
        LayoutFiltroBinding filtroBinding = LayoutFiltroBinding.inflate(getLayoutInflater());

        filtroBinding.checkBoxPendentes.setOnClickListener(view -> {
            if (filtroBinding.checkBoxPendentes.isChecked() ){
                filtros.add(0);
            }else{
                Integer i = Integer.valueOf(0);
                filtros.remove(i);
            }
            listarTarefas();
        });



        filtroBinding.checkBoxConcluidos.setOnClickListener(view -> {
            if (filtroBinding.checkBoxConcluidos.isChecked() ){
                filtros.add(2);
            }else{
                Integer i = Integer.valueOf(2);
                filtros.remove(i);
            }
            listarTarefas();
        });



        filtroBinding.checkBoxVencidos.setOnClickListener(view -> {
            if (filtroBinding.checkBoxVencidos.isChecked() ){
                filtros.add(1);
            }else{
                Integer i = Integer.valueOf(1);
                filtros.remove(i);
            }

            listarTarefas();
        });


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

    private void listarTarefas(){
        dialogCarregando.show();
        refTarefas.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogCarregando.dismiss();
                if ( snapshot.exists() ){
                    tarefas.clear();
                    for (DataSnapshot dado : snapshot.getChildren()){
                        TarefaModel tarefa = dado.getValue(TarefaModel.class);

                        boolean addTarefa = false;

                        if ( tarefa.getUsuariosObservadores() != null){
                            for( UserModel usuarioModel : tarefa.getUsuariosObservadores()){
                                if( usuarioModel.getEmail().equals(emailLogado)){
                                    addTarefa = true;
                                    break;
                                }
                            }
                        }
                        if ( !addTarefa ){
                            if( tarefa.getUsuariosResponsaveis() != null){
                                for ( UserModel usuarioModel : tarefa.getUsuariosResponsaveis()){
                                    if( usuarioModel.getEmail().equals(emailLogado)){
                                        addTarefa = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if ( addTarefa ){
                            if ( filtros.isEmpty() || filtros.size() == 3 ){
                                tarefas.add(tarefa);
                            }else{
                                if ( filtros.contains(tarefa.getStatus())){
                                    tarefas.add(tarefa);
                                }
                            }
                        }
                    }
                    adapterTarefas.notifyDataSetChanged();
                    if ( tarefas.isEmpty() ){
                        mainBinding.recyclerTarefas.setVisibility(View.GONE);
                        mainBinding.semtarefaimage.setVisibility(View.VISIBLE);
                    }else{
                        mainBinding.recyclerTarefas.setVisibility(View.VISIBLE);
                        mainBinding.semtarefaimage.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
            }
        });
    }

}