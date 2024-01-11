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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import benicio.solucoes.baratotarefas.adapter.AdapterArquivos;
import benicio.solucoes.baratotarefas.adapter.AdapterChecks;
import benicio.solucoes.baratotarefas.adapter.AdapterUsuarios;
import benicio.solucoes.baratotarefas.databinding.ActivityCriacaoTarefaBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityVisualizarTarefaBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutExibirUsersBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;
import benicio.solucoes.baratotarefas.model.FileModel;
import benicio.solucoes.baratotarefas.model.NotificacaoModel;
import benicio.solucoes.baratotarefas.model.TarefaModel;
import benicio.solucoes.baratotarefas.model.UserModel;
import benicio.solucoes.baratotarefas.service.FileNameUtils;

public class VisualizarTarefaActivity extends AppCompatActivity {

    private RecyclerView recyclerChecks;
    private AdapterChecks adapterChecks;

    private StorageReference filesTarefa;
    private static final int REQUEST_PICK_FILE = 1;
    private static final int REQUEST_PICK_FILE_IN_CHECK = 2;

    private RecyclerView recyclerFiles;
    private AdapterArquivos adapterFiles;

    private AdapterArquivos adapterFilesCheck;
    List<UserModel> listaDeUsuariosParaSelecionar = new ArrayList<>();
    List<UserModel> listaDeUsuariosParaSelecionarObservadores = new ArrayList<>();
    private Dialog dialogExibirSelecionarObservadores;
    private RecyclerView recyclerObservadoresSelecionados;
    private AdapterUsuarios adapterObeservadoresSelecionados;

    AdapterUsuarios adapterUsuariosSelecionaveis;
    AdapterUsuarios adapterUsuariosSelecionaveisObservadores;

    private AdapterUsuarios adapterUsuariosResponsaveisSelecionados;
    private RecyclerView recyclerUsuariosResponsavelSelecionados;
    private Dialog dialogExibirPessoalResponsavel;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference refNotificacoes = FirebaseDatabase.getInstance().getReference().child("notificacoes");
    private DatabaseReference refTarefas = FirebaseDatabase.getInstance().getReference().child("tarefas");
    private DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("usuarios");
    private ActivityVisualizarTarefaBinding mainBinding;
    private Bundle bundle;
    private String idTarefa;

    private Dialog dialogCarregando;

    private TarefaModel tarefaSelecionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityVisualizarTarefaBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        bundle = getIntent().getExtras();
        idTarefa = bundle.getString("idTarefa", "");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarDialogCarregando();

        dialogCarregando.show();

        refTarefas.child(idTarefa).get().addOnCompleteListener(task -> {
           if ( task.isSuccessful() ){
               dialogCarregando.dismiss();
               tarefaSelecionada = task.getResult().getValue(TarefaModel.class);
               getSupportActionBar().setTitle(tarefaSelecionada.getNomeTarefa());
               configurarNaoAdm();
               configurarDialogSelecionarResponsaveis();
               configurarDialogSelecionarObservadores();

           } else{
               finish();
               Toast.makeText(VisualizarTarefaActivity.this, "Problema de conexão", Toast.LENGTH_SHORT).show();
           }
        });

        mainBinding.procurarResponsavel.setOnClickListener( view -> {
            atualizarListaDeSelecaoDeUsuarios(dialogExibirPessoalResponsavel);
        });

        mainBinding.procurarObservador.setOnClickListener(view -> {
            atualizarListaDeSelecaoDeUsuarios(dialogExibirSelecionarObservadores);
        });


        mainBinding.btnProrrogacao.setOnClickListener(view -> {
            NotificacaoModel notificacaoResponsaveis = new NotificacaoModel(
            "Solicitação de Prorrogação", "Você foi solicitado para prorrogar a tarefa " + tarefaSelecionada.getNomeTarefa() + ", pois você é um dos responsáveis por ela.");

            for ( UserModel userObs : tarefaSelecionada.getUsuariosResponsaveis()){
                notificacaoResponsaveis.getListaToken().add(userObs.getToken());
            }

            refNotificacoes.child(UUID.randomUUID().toString()).setValue(notificacaoResponsaveis).addOnCompleteListener(taskNotifObservaores -> {
                Toast.makeText(this, "Solicitação enviada!", Toast.LENGTH_LONG).show();
                refNotificacoes.setValue(null);
            });
        });

        mainBinding.enviarArquivo.setOnClickListener( view -> openFilePicker(REQUEST_PICK_FILE));
    }

    private void openFilePicker(int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Todos os tipos de arquivo
        startActivityForResult(intent, code);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            dialogCarregando.show();
            Uri fileUri = data.getData();
            String nomeReal = FileNameUtils.getFileName(fileUri, this);
            String nomeExibicao = FileNameUtils.truncateFileName(nomeReal, 20);
            String nomeDoBanco = FileNameUtils.fileNameForDb(nomeReal);

            UploadTask uploadTask = filesTarefa.child(nomeDoBanco).putFile(fileUri);

            uploadTask.addOnCompleteListener(uploadImageTask -> {
                if ( uploadImageTask.isSuccessful()){
                    filesTarefa.child(nomeDoBanco).getDownloadUrl().addOnCompleteListener( uri -> {
                        String linkImage = uri.getResult().toString();
                        Toast.makeText(this, "Arquivo Adicionado!", Toast.LENGTH_SHORT).show();
                        tarefaSelecionada.getArquivos().add(new FileModel(nomeReal, nomeDoBanco, nomeExibicao, linkImage, idTarefa));
                        adapterFiles.notifyDataSetChanged();
                        dialogCarregando.dismiss();
                    });
                }else{
                    dialogCarregando.dismiss();
                    Toast.makeText(this, "Erro ao subir arquivo.", Toast.LENGTH_SHORT).show();
                }
            });
        }else if (requestCode == REQUEST_PICK_FILE_IN_CHECK && resultCode == RESULT_OK){

            dialogCarregando.show();
            Uri fileUri = data.getData();
            String nomeReal = FileNameUtils.getFileName(fileUri, this);
            String nomeExibicao = FileNameUtils.truncateFileName(nomeReal, 20);
            String nomeDoBanco = FileNameUtils.fileNameForDb(nomeReal);

            UploadTask uploadTask = filesTarefa.child(nomeDoBanco).putFile(fileUri);

            uploadTask.addOnCompleteListener(uploadImageTask -> {
                if ( uploadImageTask.isSuccessful()){
                    filesTarefa.child(nomeDoBanco).getDownloadUrl().addOnCompleteListener( uri -> {
                        String linkImage = uri.getResult().toString();
                        Toast.makeText(this, "Arquivo Adicionado!", Toast.LENGTH_SHORT).show();

//                        tarefaSelecionada.getArquivos().add(new FileModel(
//                                nomeReal,
//                                nomeDoBanco,
//                                nomeExibicao,
//                                linkImage,
//                                idTarefa
//                        ));

                        adapterFilesCheck.notifyDataSetChanged();

                        dialogCarregando.dismiss();
                    });
                }else{
                    dialogCarregando.dismiss();
                    Toast.makeText(this, "Erro ao subir arquivo.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }




    private void configurarNaoAdm(){

        filesTarefa = FirebaseStorage.getInstance().getReference().getRoot().child("filesTarefa").child(idTarefa);

        mainBinding.nomeField.getEditText().setText(tarefaSelecionada.getNomeTarefa());
        mainBinding.descricaoField.getEditText().setText(tarefaSelecionada.getDescri());
        mainBinding.tempoField.setText(tarefaSelecionada.getHora());

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date data;
        try {
            data = sdf.parse(tarefaSelecionada.getData());

            // Crie um objeto Calendar e defina a data nele
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data);

            // Defina a data no CalendarView
            mainBinding.calendarView.setDate(calendar.getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        boolean isAdmin = false;
        for (UserModel userModel : tarefaSelecionada.getUsuariosResponsaveis()){
            if (userModel.getEmail().equals(user.getEmail())){
                isAdmin = true;
                break;
            }
        }
        if ( !isAdmin ){
            mainBinding.procurarResponsavel.setVisibility(View.GONE);
            mainBinding.adicionarCheck.setVisibility(View.GONE);
            mainBinding.enviarArquivo.setVisibility(View.GONE);
            mainBinding.procurarObservador.setVisibility(View.GONE);
            mainBinding.btnsAdmins.setVisibility(View.GONE);
        }else{
            mainBinding.btnProrrogacao.setVisibility(View.GONE);
        }

        configurarRecyclerResponsaveisSelecionados(isAdmin);
        configurarRecyclerObservadoresSelecionados(isAdmin);
        configurarRecyclerFiles(isAdmin);
        configurarRecyclerChecks(isAdmin);
    }

    private void configurarRecyclerChecks(Boolean isAdmin) {
        recyclerChecks = mainBinding.recyclerCheck;
        recyclerChecks.setLayoutManager(new LinearLayoutManager(this));
        recyclerChecks.setHasFixedSize(true);
        recyclerChecks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterChecks = new AdapterChecks(this, tarefaSelecionada.getChecks(), dialogCarregando, false, isAdmin);
        recyclerChecks.setAdapter(adapterChecks);
    }

    private void configurarRecyclerResponsaveisSelecionados(Boolean isAdmin) {
        recyclerUsuariosResponsavelSelecionados = mainBinding.recyclerResponsavel;
        recyclerUsuariosResponsavelSelecionados.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerUsuariosResponsavelSelecionados.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsuariosResponsavelSelecionados.setHasFixedSize(true);
        adapterUsuariosResponsaveisSelecionados = new AdapterUsuarios(tarefaSelecionada.getUsuariosResponsaveis(), this,isAdmin);
        recyclerUsuariosResponsavelSelecionados.setAdapter(adapterUsuariosResponsaveisSelecionados);
    }

    private void configurarRecyclerObservadoresSelecionados(Boolean isAdmi) {
        recyclerObservadoresSelecionados = mainBinding.recyclerObservadores;
        recyclerObservadoresSelecionados.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerObservadoresSelecionados.setLayoutManager(new LinearLayoutManager(this));
        recyclerObservadoresSelecionados.setHasFixedSize(true);
        adapterObeservadoresSelecionados = new AdapterUsuarios(tarefaSelecionada.getUsuariosObservadores(), this,isAdmi);
        recyclerObservadoresSelecionados.setAdapter(adapterObeservadoresSelecionados);
    }

    private void configurarRecyclerFiles(Boolean isAdmin){
        recyclerFiles = mainBinding.reyclerArquivos;
        recyclerFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiles.setHasFixedSize(true);
        recyclerFiles.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterFiles = new AdapterArquivos(tarefaSelecionada.getArquivos(), this, idTarefa, dialogCarregando, isAdmin);
        recyclerFiles.setAdapter(adapterFiles);
    }



    private void atualizarListaDeSelecaoDeUsuarios(Dialog dialogParaAbrir){
        dialogCarregando.show();

        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaDeUsuariosParaSelecionar.clear();
                listaDeUsuariosParaSelecionarObservadores.clear();

                if (snapshot.exists()){
                    for ( DataSnapshot dado: snapshot.getChildren()){
                        UserModel usuarioBdAdicioanr = dado.getValue(UserModel.class);
                        listaDeUsuariosParaSelecionar.add(usuarioBdAdicioanr);
                        listaDeUsuariosParaSelecionarObservadores.add(usuarioBdAdicioanr);
                    }
                    adapterUsuariosSelecionaveis.notifyDataSetChanged();
                    adapterUsuariosSelecionaveisObservadores.notifyDataSetChanged();
                }

                dialogParaAbrir.show();
                dialogCarregando.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialogCarregando.dismiss();
                Toast.makeText(VisualizarTarefaActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarDialogSelecionarResponsaveis(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutExibirUsersBinding usersBinding = LayoutExibirUsersBinding.inflate(getLayoutInflater());
        b.setTitle("Selecione os Responsáveis:");
        b.setPositiveButton("Fechar", (dialogInterface, i) -> {
            dialogExibirPessoalResponsavel.dismiss();
        });
        //INICIO configurar recyclerExibicao dos usuarios
        RecyclerView recyclerExibirUsuariosSelecionaveis = usersBinding.recyclerUsuarios;
        recyclerExibirUsuariosSelecionaveis.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerExibirUsuariosSelecionaveis.setLayoutManager(new LinearLayoutManager(this));
        recyclerExibirUsuariosSelecionaveis.setHasFixedSize(true);
        adapterUsuariosSelecionaveis = new AdapterUsuarios(listaDeUsuariosParaSelecionar, tarefaSelecionada.getUsuariosResponsaveis(), this, adapterUsuariosResponsaveisSelecionados, false);
        recyclerExibirUsuariosSelecionaveis.setAdapter(adapterUsuariosSelecionaveis);
        //FIM configurar recyclerExibicao dos usuarios
        b.setView(usersBinding.getRoot());
        dialogExibirPessoalResponsavel = b.create();
    }

    private void configurarDialogSelecionarObservadores(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        LayoutExibirUsersBinding usersBinding = LayoutExibirUsersBinding.inflate(getLayoutInflater());
        b.setTitle("Selecione os Observadores:");
        b.setPositiveButton("Fechar", (dialogInterface, i) -> {
            dialogExibirSelecionarObservadores.dismiss();
        });
        //INICIO configurar recyclerExibicao dos usuarios
        RecyclerView recyclerExibirUsuariosObservadores = usersBinding.recyclerUsuarios;
        recyclerExibirUsuariosObservadores.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerExibirUsuariosObservadores.setLayoutManager(new LinearLayoutManager(this));
        recyclerExibirUsuariosObservadores.setHasFixedSize(true);
        adapterUsuariosSelecionaveisObservadores = new AdapterUsuarios(listaDeUsuariosParaSelecionarObservadores, tarefaSelecionada.getUsuariosObservadores(), this, adapterObeservadoresSelecionados, false);
        recyclerExibirUsuariosObservadores.setAdapter(adapterUsuariosSelecionaveisObservadores);
        //FIM configurar recyclerExibicao dos usuarios
        b.setView(usersBinding.getRoot());
        dialogExibirSelecionarObservadores = b.create();
    }

}