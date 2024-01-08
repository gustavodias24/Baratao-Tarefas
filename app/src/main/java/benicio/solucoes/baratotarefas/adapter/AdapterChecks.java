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

    boolean isSubCheck;

    public AdapterChecks(Activity c, List<CheckModel> listaChecks, Dialog dialogCarregando, boolean isSubCheck) {
        this.c = c;
        this.listaChecks = listaChecks;
        this.dialogCarregando = dialogCarregando;
        this.isSubCheck = isSubCheck;
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


        if ( isSubCheck ){
            holder.editCheck.setVisibility(View.GONE);
            holder.arquivosText.setVisibility(View.GONE);
            holder.subChekcText.setVisibility(View.GONE);
        }

        holder.itemView.getRootView().setClickable(false);

        holder.removeCheck.setOnClickListener(view -> {
            listaChecks.remove(position);
            this.notifyDataSetChanged();
        });

        holder.btnCheck.setOnClickListener(view -> {

            if ( !check.getChecked() ){
                holder.btnCheck.setChecked(true);
                check.setChecked(true);
            }else{
                holder.btnCheck.setChecked(false);
                check.setChecked(false);
            }

        });

        holder.nomeCheck.setText(
                check.getCheckNome()
        );

        if ( !check.getSubChecks().isEmpty() ){
            holder.recyclerSubChecks.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
            holder.recyclerSubChecks.setHasFixedSize(true);
            holder.recyclerSubChecks.setLayoutManager(new LinearLayoutManager(c));
            AdapterChecks adapterSubChecks = new AdapterChecks(c, check.getSubChecks(), dialogCarregando, true);
            holder.recyclerSubChecks.setAdapter(adapterSubChecks);
        }

        if ( !check.getfilesDoCheck().isEmpty() ){
            holder.recyclerFiles.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
            holder.recyclerFiles.setHasFixedSize(true);
            holder.recyclerFiles.setLayoutManager(new LinearLayoutManager(c));
            AdapterArquivos adapterArquivos = new AdapterArquivos(check.getfilesDoCheck(), c, check.getIdTarefa(), dialogCarregando);
            holder.recyclerFiles.setAdapter(adapterArquivos);
        }

    }

    private Boolean veriificarTodosOsCheck(CheckModel check){
        Boolean todosChecados = true;

        if ( check.getSubChecks() != null && !check.getSubChecks().isEmpty()){
            for ( CheckModel subChek : check.getSubChecks()){
                if (!subChek.getChecked()){
                    todosChecados = false;
                    break;
                }
            }
        }
        return todosChecados;
    }

    @Override
    public int getItemCount() {
        return listaChecks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RadioButton btnCheck;
        ImageButton removeCheck;
        ImageButton editCheck;
        TextView nomeCheck, arquivosText, subChekcText;

        RecyclerView recyclerFiles, recyclerSubChecks;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btnCheck = itemView.findViewById(R.id.radioCheck);
            removeCheck = itemView.findViewById(R.id.removeCheck);
            editCheck = itemView.findViewById(R.id.editCheck);
            nomeCheck = itemView.findViewById(R.id.nomeCheckText);
            recyclerFiles = itemView.findViewById(R.id.recyclerCheckFilesExibicao);
            recyclerSubChecks = itemView.findViewById(R.id.recyclerCheckSubCheckExibicao);
            arquivosText = itemView.findViewById(R.id.textArquivosCheck);
            subChekcText = itemView.findViewById(R.id.textSubChecks);
        }
    }
}
