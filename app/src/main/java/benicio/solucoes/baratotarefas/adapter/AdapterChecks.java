package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.model.CheckModel;
import benicio.solucoes.baratotarefas.model.FileModel;

public class AdapterChecks extends RecyclerView.Adapter<AdapterChecks.MyViewHolder> {

    Activity c;
    List<CheckModel> listaChecks;
    Dialog dialogCarregando;

    public AdapterChecks(Activity c, List<CheckModel> listaChecks, Dialog dialogCarregando) {
        this.c = c;
        this.listaChecks = listaChecks;
        this.dialogCarregando = dialogCarregando;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_check, parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CheckModel check = listaChecks.get(position);

        holder.itemView.getRootView().setClickable(false);

        holder.removeCheck.setOnClickListener(view -> {
            listaChecks.remove(position);
            this.notifyDataSetChanged();
        });

        holder.btnCheck.setOnClickListener(view -> {
            check.setChecked(
                    holder.btnCheck.isChecked()
            );
        });

        holder.nomeCheck.setText(
                check.getCheckNome()
        );


        if ( !check.getfilesDoCheck().isEmpty() ){
            holder.recyclerFiles.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
            holder.recyclerFiles.setHasFixedSize(true);
            holder.recyclerFiles.setLayoutManager(new LinearLayoutManager(c));
            AdapterArquivos adapterArquivos = new AdapterArquivos(check.getfilesDoCheck(), c, check.getIdTarefa(), dialogCarregando);
            holder.recyclerFiles.setAdapter(adapterArquivos);
        }

    }

    @Override
    public int getItemCount() {
        return listaChecks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RadioButton btnCheck;
        ImageButton removeCheck;
        TextView nomeCheck;

        RecyclerView recyclerFiles;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btnCheck = itemView.findViewById(R.id.radioCheck);
            removeCheck = itemView.findViewById(R.id.removeCheck);
            nomeCheck = itemView.findViewById(R.id.nomeCheckText);
            recyclerFiles = itemView.findViewById(R.id.recyclerCheckFilesExibicao);
        }
    }
}
