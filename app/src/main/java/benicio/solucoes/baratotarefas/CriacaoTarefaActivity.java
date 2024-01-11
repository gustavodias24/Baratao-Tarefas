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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CalendarView;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import benicio.solucoes.baratotarefas.adapter.AdapterArquivos;
import benicio.solucoes.baratotarefas.adapter.AdapterChecks;
import benicio.solucoes.baratotarefas.adapter.AdapterUsuarios;
import benicio.solucoes.baratotarefas.databinding.ActivityCadastroBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityCriacaoTarefaBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutCriarCheckBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutCriarSubCheckBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutExibirUsersBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;
import benicio.solucoes.baratotarefas.model.CheckModel;
import benicio.solucoes.baratotarefas.model.FileModel;
import benicio.solucoes.baratotarefas.model.NotificacaoModel;
import benicio.solucoes.baratotarefas.model.TarefaModel;
import benicio.solucoes.baratotarefas.model.UserModel;
import benicio.solucoes.baratotarefas.service.FileNameUtils;

public class CriacaoTarefaActivity extends AppCompatActivity {

    private DatabaseReference refNotificacoes = FirebaseDatabase.getInstance().getReference().child("notificacoes");

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference refUsers = FirebaseDatabase.getInstance().getReference().child("usuarios");
    private DatabaseReference refTarefas = FirebaseDatabase.getInstance().getReference().child("tarefas");
    List<UserModel> listaDeUsuariosParaSelecionar = new ArrayList<>();
    List<UserModel> listaDeUsuariosParaSelecionarObservadores = new ArrayList<>();
    AdapterUsuarios adapterUsuariosSelecionaveis;
    AdapterUsuarios adapterUsuariosSelecionaveisObservadores;

    
    private List<UserModel> listaDeUsuariosObservadoresSelecionados = new ArrayList<>();

    private AdapterUsuarios adapterObeservadoresSelecionados;
    private RecyclerView recyclerObservadoresSelecionados;


    private List<UserModel> listaDeUsuariosReponsaveisSelecionados = new ArrayList<>();
    private AdapterUsuarios adapterUsuariosResponsaveisSelecionados;
    private RecyclerView recyclerUsuariosResponsavelSelecionados;
    
    




    private List<FileModel> listaDeArquivosDoCheck = new ArrayList<>();
    private List<CheckModel> listaDeSubChecks = new ArrayList<>();
    private AdapterArquivos adapterFilesCheck;
    private AdapterChecks adapterSubCheck;

    private static final int REQUEST_PICK_FILE = 1;
    private static final int REQUEST_PICK_FILE_IN_CHECK = 2;

    private String idTarefa;
    private StorageReference filesTarefa;
    private Dialog dialogCarregando, dialogCriarCheck, dialogExibirPessoalResponsavel,
            dialogExibirSelecionarObservadores, dialogSubCheck;
    private ActivityCriacaoTarefaBinding mainBinding;

    private RecyclerView recyclerFiles;
    private AdapterArquivos adapterFiles;
    private List<FileModel> listaFilesTarefa = new ArrayList<>();

    private RecyclerView recyclerChecks;
    private AdapterChecks adapterChecks;
    private List<CheckModel> listaCheck = new ArrayList<>();

    private String dataPrazo = "";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCriacaoTarefaBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        idTarefa = UUID.randomUUID().toString();
        filesTarefa = FirebaseStorage.getInstance().getReference().getRoot().child("filesTarefa").child(idTarefa);

        getSupportActionBar().setTitle("NOVA TAREFA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarDialogCarregando();

        mainBinding.enviarArquivo.setOnClickListener( view -> openFilePicker(REQUEST_PICK_FILE));

        mainBinding.adicionarCheck.setOnClickListener( view -> {
            dialogCriarCheck.show();
            listaDeArquivosDoCheck.clear();
            listaDeSubChecks.clear();
            adapterFilesCheck.notifyDataSetChanged();
        });

        Date date = new Date();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dataPrazo = dateFormat.format(date.getTime());
        mainBinding.procurarResponsavel.setOnClickListener( view -> {
            atualizarListaDeSelecaoDeUsuarios(dialogExibirPessoalResponsavel);
        });

        mainBinding.procurarObservador.setOnClickListener(view -> {
            atualizarListaDeSelecaoDeUsuarios(dialogExibirSelecionarObservadores);
        });

        mainBinding.calendarView.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            String formattedMonth = String.format(Locale.getDefault(), "%02d", i1 + 1);
            String formattedDay = String.format(Locale.getDefault(), "%02d", i2);
            dataPrazo = formattedDay + "/" + formattedMonth  + "/"  + i;
        });

        mainBinding.concluir.setOnClickListener( view -> {
            String hora = mainBinding.tempoField.getText().toString();
            String descri = mainBinding.descricaoField.getEditText().getText().toString();
            String tituloTarefa = mainBinding.nomeField.getEditText().getText().toString();

            if ( hora.isEmpty() ){ hora = "00:00";}
            if ( tituloTarefa.isEmpty()){ tituloTarefa = "Sem Título";}
            if ( descri.isEmpty()){ descri = "Sem Descrição";}

            TarefaModel novaTarefa = new TarefaModel(
                    tituloTarefa,
                    idTarefa,
                    listaDeUsuariosReponsaveisSelecionados,
                    listaDeUsuariosObservadoresSelecionados,
                    listaCheck,
                    descri,
                    listaFilesTarefa,
                    dataPrazo,
                    hora,
                    0
            );

            dialogCarregando.show();
            refTarefas.child(idTarefa).setValue(novaTarefa).addOnCompleteListener( task -> {
                dialogCarregando.dismiss();
               if ( task.isSuccessful() ){
                   NotificacaoModel notificacaoResponsaveis = new NotificacaoModel("Nova Tarefa", "Você foi envolvido como responsável em uma nova tarefa!");
                   NotificacaoModel notificacaoObservadores = new NotificacaoModel("Nova Tarefa", "Você foi envolvido como observador em uma nova tarefa!");

                   for ( UserModel userObs : listaDeUsuariosReponsaveisSelecionados){
                       notificacaoResponsaveis.getListaToken().add(userObs.getToken());
                   }

                   for ( UserModel userObs : listaDeUsuariosObservadoresSelecionados){
                       notificacaoObservadores.getListaToken().add(userObs.getToken());
                   }

                   refNotificacoes.child(UUID.randomUUID().toString()).setValue(notificacaoResponsaveis).addOnCompleteListener( taskNotResponsaveis -> {
                       refNotificacoes.child(UUID.randomUUID().toString()).setValue(notificacaoObservadores).addOnCompleteListener( taskNotifObservaores -> {
                           finish();
                           Toast.makeText(this, "Tarefa criada com sucesso!", Toast.LENGTH_LONG).show();
                           refNotificacoes.setValue(null);
                       });
                   });


               }else{
                   Toast.makeText(this, "Erro de conexão", Toast.LENGTH_LONG).show();
               }
            });
        });

        configurarRecyclerFiles();
        configurarRecyclerResponsaveisSelecionados();
        configurarRecyclerObservadoresSelecionados();
        configurarRecyclerChecks();
        configurarDialogCriarCheck();
        configurarDialogSelecionarResponsaveis();
//        configurarDialogSelecionarUsuario();
        configurarDialogSelecionarObservadores();
        configurarDialogSubCheck();
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
//                        if( user.getEmail() != null && !user.getEmail().isEmpty()){
//                            if ( user.getEmail().equals(usuarioBdAdicioanr.getEmail())){
//                                usuarioBdAdicioanr.setNome("Você");
//                            }
//                        }
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
                Toast.makeText(CriacaoTarefaActivity.this, "Problema de conexão!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void configurarRecyclerResponsaveisSelecionados() {
        recyclerUsuariosResponsavelSelecionados = mainBinding.recyclerResponsavel;
        recyclerUsuariosResponsavelSelecionados.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerUsuariosResponsavelSelecionados.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsuariosResponsavelSelecionados.setHasFixedSize(true);
        adapterUsuariosResponsaveisSelecionados = new AdapterUsuarios(listaDeUsuariosReponsaveisSelecionados, this,true);
        recyclerUsuariosResponsavelSelecionados.setAdapter(adapterUsuariosResponsaveisSelecionados);
    }

    private void configurarRecyclerObservadoresSelecionados() {
        recyclerObservadoresSelecionados = mainBinding.recyclerObservadores;
        recyclerObservadoresSelecionados.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerObservadoresSelecionados.setLayoutManager(new LinearLayoutManager(this));
        recyclerObservadoresSelecionados.setHasFixedSize(true);
        adapterObeservadoresSelecionados = new AdapterUsuarios(listaDeUsuariosObservadoresSelecionados, this,true);
        recyclerObservadoresSelecionados.setAdapter(adapterObeservadoresSelecionados);
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
        adapterUsuariosSelecionaveis = new AdapterUsuarios(listaDeUsuariosParaSelecionar, listaDeUsuariosReponsaveisSelecionados, this, adapterUsuariosResponsaveisSelecionados, false);
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
        adapterUsuariosSelecionaveisObservadores = new AdapterUsuarios(listaDeUsuariosParaSelecionarObservadores, listaDeUsuariosObservadoresSelecionados, this, adapterObeservadoresSelecionados, false);
        recyclerExibirUsuariosObservadores.setAdapter(adapterUsuariosSelecionaveisObservadores);
        //FIM configurar recyclerExibicao dos usuarios
        b.setView(usersBinding.getRoot());
        dialogExibirSelecionarObservadores = b.create();
    }
    
    
    
    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
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
                        listaFilesTarefa.add(new FileModel(nomeReal, nomeDoBanco, nomeExibicao, linkImage, idTarefa));
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

                        listaDeArquivosDoCheck.add(new FileModel(
                                nomeReal,
                                nomeDoBanco,
                                nomeExibicao,
                                linkImage,
                                idTarefa
                        ));

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

    private void configurarRecyclerFiles(){
        recyclerFiles = mainBinding.reyclerArquivos;
        recyclerFiles.setLayoutManager(new LinearLayoutManager(this));
        recyclerFiles.setHasFixedSize(true);
        recyclerFiles.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterFiles = new AdapterArquivos(listaFilesTarefa, this, idTarefa, dialogCarregando);
        recyclerFiles.setAdapter(adapterFiles);
    }
    private void configurarRecyclerChecks() {
        recyclerChecks = mainBinding.recyclerCheck;
        recyclerChecks.setLayoutManager(new LinearLayoutManager(this));
        recyclerChecks.setHasFixedSize(true);
        recyclerChecks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterChecks = new AdapterChecks(this, listaCheck, dialogCarregando, false);
        recyclerChecks.setAdapter(adapterChecks);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void configurarDialogSubCheck(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Adicionar um Sub Check");
        LayoutCriarSubCheckBinding subCheckBinding = LayoutCriarSubCheckBinding.inflate(getLayoutInflater());
        subCheckBinding.cadastrarCheckBtn.setOnClickListener(view -> {
            String nomeSubCheck = subCheckBinding.nomeCheckField.getEditText().getText().toString();
            if ( nomeSubCheck.isEmpty() ) {
                Toast.makeText(this, "Escreva Algo no Sub Check!", Toast.LENGTH_SHORT).show();
            }else{
                String checkId = UUID.randomUUID().toString();

                listaDeSubChecks.add(new CheckModel(
                        checkId, idTarefa, nomeSubCheck,"", false
                ));

                adapterSubCheck.notifyDataSetChanged();
                subCheckBinding.nomeCheckField.getEditText().setText("");
                dialogSubCheck.dismiss();
            }
        });
        b.setView(subCheckBinding.getRoot());
        dialogSubCheck = b.create();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void configurarDialogCriarCheck(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Adicionar um Check");
        LayoutCriarCheckBinding checkBinding = LayoutCriarCheckBinding.inflate(getLayoutInflater());


        checkBinding.recyclerSubChecks.setHasFixedSize(true);
        checkBinding.recyclerSubChecks.setLayoutManager(new LinearLayoutManager(this));
        checkBinding.recyclerSubChecks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterSubCheck = new AdapterChecks(this, listaDeSubChecks, dialogCarregando, true);
        checkBinding.recyclerSubChecks.setAdapter(adapterSubCheck);

        checkBinding.criarSubCheck.setOnClickListener( view -> dialogSubCheck.show());

        checkBinding.recyclerFileInCheck.setHasFixedSize(true);
        checkBinding.recyclerFileInCheck.setLayoutManager(new LinearLayoutManager(this));
        checkBinding.recyclerFileInCheck.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapterFilesCheck = new AdapterArquivos(listaDeArquivosDoCheck, this, idTarefa, dialogCarregando);
        checkBinding.recyclerFileInCheck.setAdapter(adapterFilesCheck);

        checkBinding.enviarArquivoInCheck.setOnClickListener( view -> {
            openFilePicker(REQUEST_PICK_FILE_IN_CHECK);
        });

        checkBinding.cadastrarCheckBtn.setOnClickListener( view -> {
            String nomeCheck = checkBinding.nomeCheckField.getEditText().getText().toString();

            if ( nomeCheck.isEmpty() ){
                Toast.makeText(this, "Escreva Algo no Check!", Toast.LENGTH_SHORT).show();
            }else{

                String checkId = UUID.randomUUID().toString();
                String comentarioCheck = checkBinding.comentarioCheckField.getEditText().toString();

                if ( comentarioCheck.isEmpty()) { comentarioCheck = "Sem comentários."; }

                CheckModel novoCheck = new CheckModel(
                        checkId, idTarefa, nomeCheck, comentarioCheck, false
                );

                novoCheck.getfilesDoCheck().addAll(listaDeArquivosDoCheck);

                novoCheck.getSubChecks().addAll(listaDeSubChecks);

                listaCheck.add(novoCheck);
                adapterChecks.notifyDataSetChanged();

                Toast.makeText(this, "Check Adicionado", Toast.LENGTH_SHORT).show();

                checkBinding.nomeCheckField.getEditText().setText("");
                checkBinding.comentarioCheckField.getEditText().setText("");
                dialogCriarCheck.dismiss();

            }

        });
        b.setView(checkBinding.getRoot());

        dialogCriarCheck = b.create();
    }


}