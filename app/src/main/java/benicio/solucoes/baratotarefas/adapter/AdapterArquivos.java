package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.databinding.LayoutExibirFileBinding;
import benicio.solucoes.baratotarefas.model.FileModel;

public class AdapterArquivos extends RecyclerView.Adapter<AdapterArquivos.MyViewHolver> {

    List<FileModel> listaDeArquivos;
    Activity c;
    Dialog dialogExibir;

    StorageReference filesTarefa;

    public AdapterArquivos(List<FileModel> listaDeArquivos, Activity c, String idTarefa) {
        this.listaDeArquivos = listaDeArquivos;
        this.c = c;
        this.filesTarefa = FirebaseStorage.getInstance().getReference().getRoot().child("filesTarefa").child(idTarefa);
    }

    @NonNull
    @Override
    public MyViewHolver onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolver(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_file, parent, false));
    }

    @SuppressLint({"ResourceType", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolver holder, int position) {
        FileModel file = listaDeArquivos.get(position);

        holder.nomeFileText.setText(file.getNomeExibicao());

        holder.itemView.getRootView().setClickable(false);

        holder.removeFile.setOnClickListener( view -> {
            filesTarefa.child(file.getNomeBancoDeDados()).delete();
            try{
                listaDeArquivos.remove(position);
                this.notifyItemRemoved(position);
            }catch (Exception e){
                listaDeArquivos.remove(0);
                this.notifyDataSetChanged();
            }

        });

        holder.viewFile.setOnClickListener( view -> {
            Log.d("mayara", "onBindViewHolder: " + file.getNomeReal());
            configurarDialog(file.getfileLink());
            dialogExibir.show();
        });

        verificarArquivo(file, holder);

    }

    @SuppressLint("ResourceType")
    private void verificarArquivo(FileModel file, MyViewHolver holder){
        if (file.getNomeReal().toLowerCase().endsWith(".png") || file.getNomeReal().toLowerCase().endsWith(".jpg")
                || file.getNomeReal().toLowerCase().endsWith(".jpeg") || file.getNomeReal().toLowerCase().endsWith(".gif")
                || file.getNomeReal().toLowerCase().endsWith(".bmp")) {
            Picasso.get().load(R.raw.filpepng).into(holder.logoFile);
            configurarDialog(file.getfileLink());
            holder.viewFile.setVisibility(View.VISIBLE);
        }else if (file.getNomeReal().toLowerCase().endsWith(".mp4") || file.getNomeReal().toLowerCase().endsWith(".3gp")
                || file.getNomeReal().toLowerCase().endsWith(".avi") || file.getNomeReal().toLowerCase().endsWith(".mkv")){
            Picasso.get().load(R.raw.filevideo).into(holder.logoFile);
            holder.viewFile.setVisibility(View.INVISIBLE);
        }else{
            Picasso.get().load(R.raw.filedoc).into(holder.logoFile);
        }
    }

    private  void configurarDialog(String link){
        AlertDialog.Builder b = new AlertDialog.Builder(c);
        b.setTitle("Visualização do Arquivo:");
        LayoutExibirFileBinding exibirFileBinding = LayoutExibirFileBinding.inflate(c.getLayoutInflater());
        Picasso.get().load(link).into(exibirFileBinding.imageView);
        exibirFileBinding.fechaBtn.setOnClickListener( fecharView -> dialogExibir.dismiss());
        b.setView(exibirFileBinding.getRoot());
        dialogExibir = b.create();
    }
    @Override
    public int getItemCount() {
        return listaDeArquivos.size();
    }

    public class MyViewHolver extends  RecyclerView.ViewHolder {

        ImageButton removeFile, viewFile, downloadFile;
        ImageView logoFile;
        TextView nomeFileText;
        public MyViewHolver(@NonNull View itemView) {
            super(itemView);
            logoFile = itemView.findViewById(R.id.logoFileView);
            nomeFileText = itemView.findViewById(R.id.nomeFileText);
            removeFile = itemView.findViewById(R.id.removeFile);
            viewFile = itemView.findViewById(R.id.viewFile);
            downloadFile = itemView.findViewById(R.id.downloadFile);
        }
    }
}
