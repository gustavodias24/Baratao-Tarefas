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
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import benicio.solucoes.baratotarefas.adapter.AdapterArquivos;
import benicio.solucoes.baratotarefas.databinding.ActivityCadastroBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityCriacaoTarefaBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;
import benicio.solucoes.baratotarefas.model.FileModel;

public class CriacaoTarefaActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_FILE = 1;
    private StorageReference filesTarefa = FirebaseStorage.getInstance().getReference().getRoot().child("filesTarefa");
    private Dialog dialogCarregando;
    private ActivityCriacaoTarefaBinding mainBinding;

    private RecyclerView recyclerFiles;
    private AdapterArquivos adapterFiles;
    private List<FileModel> listaFilesTarefa = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCriacaoTarefaBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("NOVA TAREFA");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configurarDialogCarregando();

        mainBinding.enviarArquivo.setOnClickListener( view -> {
            openFilePicker();
        });

        configurarRecyclerFiles();
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

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // Todos os tipos de arquivo
        startActivityForResult(intent, REQUEST_PICK_FILE);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PICK_FILE && resultCode == RESULT_OK) {
            Uri fileUri = data.getData();
            String nomeReal = getFileName(fileUri);
            String nomeExibicao = truncateFileName(nomeReal, 20);

            Toast.makeText(this, "Arquivo Adicionado!", Toast.LENGTH_SHORT).show();
            listaFilesTarefa.add(new FileModel(nomeReal, fileNameForDb(nomeReal), nomeExibicao, fileUri));
            adapterFiles.notifyDataSetChanged();

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
        adapterFiles = new AdapterArquivos(listaFilesTarefa, this);
        recyclerFiles.setAdapter(adapterFiles);
    }


}