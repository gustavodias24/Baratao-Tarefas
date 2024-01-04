package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.model.FileModel;

public class AdapterArquivos extends RecyclerView.Adapter<AdapterArquivos.MyViewHolver> {

    List<FileModel> listaDeArquivos;
    Activity c;

    public AdapterArquivos(List<FileModel> listaDeArquivos, Activity c) {
        this.listaDeArquivos = listaDeArquivos;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolver onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolver(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_file, parent, false));
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolver holder, int position) {
        FileModel file = listaDeArquivos.get(position);

        holder.nomeFileText.setText(file.getNomeExibicao());

        if (file.getNomeReal().toLowerCase().endsWith(".png") || file.getNomeReal().toLowerCase().endsWith(".jpg")
                || file.getNomeReal().toLowerCase().endsWith(".jpeg") || file.getNomeReal().toLowerCase().endsWith(".gif")
                || file.getNomeReal().toLowerCase().endsWith(".bmp")) {
            Picasso.get().load(R.raw.filpepng).into(holder.logoFile);
        }else if ( file.getNomeReal().toLowerCase().endsWith(".mp4") || file.getNomeReal().toLowerCase().endsWith(".3gp")
                || file.getNomeReal().toLowerCase().endsWith(".avi") || file.getNomeReal().toLowerCase().endsWith(".mkv")){
            Picasso.get().load(R.raw.filevideo).into(holder.logoFile);
        }else{
            Picasso.get().load(R.raw.filedoc).into(holder.logoFile);
        }

    }

    @Override
    public int getItemCount() {
        return listaDeArquivos.size();
    }

    public class MyViewHolver extends  RecyclerView.ViewHolder {
        ImageView logoFile;
        TextView nomeFileText;
        public MyViewHolver(@NonNull View itemView) {
            super(itemView);
            logoFile = itemView.findViewById(R.id.logoFileView);
            nomeFileText = itemView.findViewById(R.id.nomeFileText);
        }
    }
}
