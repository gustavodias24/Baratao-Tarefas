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
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import benicio.solucoes.baratotarefas.adapter.AdapterArquivos;
import benicio.solucoes.baratotarefas.adapter.AdapterChecks;
import benicio.solucoes.baratotarefas.databinding.ActivityCadastroBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityCriacaoTarefaBinding;
import benicio.solucoes.baratotarefas.databinding.LayoutCriarCheckBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;
import benicio.solucoes.baratotarefas.model.CheckModel;
import benicio.solucoes.baratotarefas.model.FileModel;

public class CriacaoTarefaActivity extends AppCompatActivity {

    private List<FileModel> listaDeArquivosDoCheck = new ArrayList<>();
    private AdapterArquivos adapterFilesCheck;

    private static final int REQUEST_PICK_FILE = 1;
    private static final int REQUEST_PICK_FILE_IN_CHECK = 2;

    private String idTarefa;
    private StorageReference filesTarefa;
    private Dialog dialogCarregando, dialogCriarCheck;
    private ActivityCriacaoTarefaBinding mainBinding;

    private RecyclerView recyclerFiles;
    private AdapterArquivos adapterFiles;
    private List<FileModel> listaFilesTarefa = new ArrayList<>();

    private RecyclerView recyclerChecks;
    private AdapterChecks adapterChecks;
    private List<CheckModel> listaCheck = new ArrayList<>();

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

        mainBinding.enviarArquivo.setOnClickListener( view -> {
            openFilePicker(REQUEST_PICK_FILE);
        });

        mainBinding.adicionarCheck.setOnClickListener( view -> {
            dialogCriarCheck.show();
            listaDeArquivosDoCheck.clear();
            adapterFilesCheck.notifyDataSetChanged();
        });

        configurarRecyclerFiles();
        configurarRecyclerChecks();
        configurarDialogCriarCheck();
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
            String nomeReal = getFileName(fileUri);
            String nomeExibicao = truncateFileName(nomeReal, 20);
            String nomeDoBanco = fileNameForDb(nomeReal);

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
            String nomeReal = getFileName(fileUri);
            String nomeExibicao = truncateFileName(nomeReal, 20);
            String nomeDoBanco = fileNameForDb(nomeReal);

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

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(displayNameIndex);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
    private String fileNameForDb(String fileName){
        String extension = fileName.substring(fileName.lastIndexOf('.'));
        String nome = UUID.randomUUID().toString();
        return nome + extension;
    }
    private String truncateFileName(String fileName, int maxLength) {
        if (fileName.length() > maxLength) {
            String extension = fileName.substring(fileName.lastIndexOf('.'));
            String truncatedName = fileName.substring(0, maxLength - 3) + "..." + extension;
            return truncatedName;
        } else {
            return fileName;
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
        adapterChecks = new AdapterChecks(this, listaCheck, dialogCarregando);
        recyclerChecks.setAdapter(adapterChecks);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void configurarDialogCriarCheck(){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Adicionar um Check");
        LayoutCriarCheckBinding checkBinding = LayoutCriarCheckBinding.inflate(getLayoutInflater());

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

                CheckModel novoCheck = new CheckModel(
                        checkId, idTarefa, nomeCheck, false
                );

                novoCheck.getfilesDoCheck().addAll(listaDeArquivosDoCheck);

                listaCheck.add(novoCheck);
                adapterChecks.notifyDataSetChanged();

                Toast.makeText(this, "Check Adicionado", Toast.LENGTH_SHORT).show();

                checkBinding.nomeCheckField.getEditText().setText("");
                dialogCriarCheck.dismiss();

            }

        });
        b.setView(checkBinding.getRoot());

        dialogCriarCheck = b.create();
    }


}