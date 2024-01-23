package benicio.solucoes.baratotarefas;


import static com.google.firebase.messaging.Constants.MessagePayloadKeys.SENDER_ID;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import benicio.solucoes.baratotarefas.adapter.AdapterTarefas;
import benicio.solucoes.baratotarefas.databinding.ActivityTarefasBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutFiltroBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;
import benicio.solucoes.baratotarefas.model.TarefaModel;
import benicio.solucoes.baratotarefas.model.UserModel;

public class TarefasActivity extends AppCompatActivity {
    private SimpleDateFormat sdf;
    private boolean filtroCompleto = false;
    private String dataInicialFiltro = "", dataFinalFiltro = "", nomeFiltro = "";
    private String idCriador = "";
    private RecyclerView recyclerTarefas;
    private AdapterTarefas adapterTarefas;
    private List<TarefaModel> tarefas = new ArrayList<>();
    private List<Integer> filtros = new ArrayList<>();
    private DatabaseReference refTarefas = FirebaseDatabase.getInstance().getReference().child("tarefas");
    private DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("usuarios");
    private ActivityTarefasBinding mainBinding;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseUser user;
    private String emailLogado ;
    private Dialog dialogFiltro,dialogCarregando;

    @SuppressLint({"ResourceType", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityTarefasBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        sdf  = new SimpleDateFormat("dd/MM/yyyy");

        Picasso.get().load(R.raw.semtarefas).into(mainBinding.semtarefaimage);
        getSupportActionBar().setTitle("TAREFAS");

        askNotificationPermission();

        user = auth.getCurrentUser();
        emailLogado = user.getEmail();

        mainBinding.fab.setOnClickListener( view -> {
            startActivity(new Intent(this, CriacaoTarefaActivity.class));
        });

        mainBinding.filtroBtn.setOnClickListener(view -> {
            filtroCompleto = false;
            dialogFiltro.show();
        });

        configurarDialogCarregando();
        pegarIdCriador();
        configurarDialogFiltro();
        configurarRecyclerTarefas();
        guardarToken();
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

        filtroBinding.filtrar.setOnClickListener( view -> {
            dataInicialFiltro = filtroBinding.dataInicialField.getText().toString();
            dataFinalFiltro = filtroBinding.dataFinalField.getText().toString();
            nomeFiltro = filtroBinding.nomeField.getText().toString();
            filtroCompleto = true;
            listarTarefas();
            dialogFiltro.dismiss();
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
                try {
                    dialogCarregando.dismiss();
                }catch (Exception e){}

                if ( snapshot.exists() ){
                    tarefas.clear();
                    for (DataSnapshot dado : snapshot.getChildren()){

                        TarefaModel tarefa = dado.getValue(TarefaModel.class);


                        boolean addTarefa = false;

                        if ( tarefa.getIdCriador().equals(idCriador) ) {
                            addTarefa = true;
                        }

                        if ( !addTarefa ){
                            if ( tarefa.getUsuariosObservadores() != null){
                                for( UserModel usuarioModel : tarefa.getUsuariosObservadores()){
                                    if( usuarioModel.getEmail().equals(emailLogado) ){
                                        addTarefa = true;
                                        break;
                                    }
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
                                addTarefa = true;
                            }else{
                                if ( filtros.contains(tarefa.getStatus())){
                                    addTarefa = false;
                                }
                            }

                            if ( filtroCompleto ){
                                if ( !dataFinalFiltro.isEmpty() && !dataInicialFiltro.isEmpty()){
                                    try{
                                        Date dataDoAgendamento = sdf.parse(tarefa.getData());
                                        Date dataInicial = sdf.parse(dataInicialFiltro);
                                        Date dataFinal = sdf.parse(dataFinalFiltro);

                                        Log.d("mayara", "onDataChange: dataDoAgendamento " + dataDoAgendamento);
                                        Log.d("mayara", "onDataChange: dataInicial " + dataInicial);
                                        Log.d("mayara", "onDataChange: dataFinal "  + dataFinal);
                                        if ( dataDoAgendamento.after(dataInicial) && dataDoAgendamento.before(dataFinal) ){
                                            addTarefa = true;
                                        }else{
                                            addTarefa = false;
                                        }
                                    }catch (Exception ignored){}
                                }

                                if ( !nomeFiltro.isEmpty() && addTarefa){
                                    for ( UserModel responsaveis : tarefa.getUsuariosResponsaveis()){
                                        if ( responsaveis.getNome().trim().toLowerCase().contains(nomeFiltro.trim().toLowerCase())){
                                            addTarefa = true;
                                            break;
                                        }
                                    }

                                    if ( !addTarefa ){
                                        for ( UserModel observadores : tarefa.getUsuariosObservadores()){
                                            if ( observadores.getNome().trim().toLowerCase().contains(nomeFiltro.trim().toLowerCase())){
                                                addTarefa = true;
                                                break;
                                            }
                                        }
                                    }

                                }
                            }// fim filtro completo

                            if ( addTarefa ){
                                tarefas.add(tarefa);
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

    private void pegarIdCriador(){

        dialogCarregando.show();
        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dialogCarregando.dismiss();
                for ( DataSnapshot dado : snapshot.getChildren()){
                    UserModel userDado = dado.getValue(UserModel.class);
                    assert userDado != null;
                    if ( userDado.getEmail().equals(user.getEmail())){
                        idCriador = userDado.getId();
                        listarTarefas();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
                Toast.makeText(TarefasActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private  void guardarToken(){
        dialogCarregando.show();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            dialogCarregando.dismiss();
            if ( task.isSuccessful() ){
                dialogCarregando.show();

                String token =  task.getResult();

                refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dialogCarregando.dismiss();
                        if  ( snapshot.exists() ){
                            UserModel user = null;
                            for (DataSnapshot dado : snapshot.getChildren()){
                                user  = dado.getValue(UserModel.class);

                                if ( user.getEmail().equals(emailLogado) ){
                                    break;
                                }
                            }
                            if ( user != null){
                                user.setToken(token);
                                dialogCarregando.show();
                                refUsers.child(user.getId()).setValue(
                                        user
                                ).addOnCompleteListener(task2 -> {
                                    dialogCarregando.dismiss();
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }) ;
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                } else {
                    Toast.makeText(this, "Aplicativo Bloqueado para Notificações", Toast.LENGTH_SHORT).show();
                }
            });

    private void askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS ) ==
                    PackageManager.PERMISSION_GRANTED) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

}