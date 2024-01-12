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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.model.CheckModel;
import benicio.solucoes.baratotarefas.model.FileModel;
import benicio.solucoes.baratotarefas.model.TarefaModel;

public class AdapterChecks extends RecyclerView.Adapter<AdapterChecks.MyViewHolder> {

    private DatabaseReference refTarefas = FirebaseDatabase.getInstance().getReference().child("tarefas");
    Activity c;
    List<CheckModel> listaChecks;
    Dialog dialogCarregando;

    boolean isSubCheck;

    boolean isAdmin = true;

    public AdapterChecks(Activity c, List<CheckModel> listaChecks, Dialog dialogCarregando, boolean isSubCheck) {
        this.c = c;
        this.listaChecks = listaChecks;
        this.dialogCarregando = dialogCarregando;
        this.isSubCheck = isSubCheck;
    }

    public AdapterChecks(Activity c, List<CheckModel> listaChecks, Dialog dialogCarregando, boolean isSubCheck, boolean isAdmin) {
        this.c = c;
        this.listaChecks = listaChecks;
        this.dialogCarregando = dialogCarregando;
        this.isSubCheck = isSubCheck;
        this.isAdmin = isAdmin;
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

        if ( check.getChecked() ){
            holder.btnCheck.setChecked(true);
        }

        if ( !isAdmin) {
            holder.removeCheck.setVisibility(View.GONE);
        }

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

            atualizarCheckNoBanco(check, holder);


        });

        holder.nomeCheck.setText(
                check.getCheckNome()
        );

        if ( !check.getSubChecks().isEmpty() ){
            holder.recyclerSubChecks.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
            holder.recyclerSubChecks.setHasFixedSize(true);
            holder.recyclerSubChecks.setLayoutManager(new LinearLayoutManager(c));
            AdapterChecks adapterSubChecks = new AdapterChecks(c, check.getSubChecks(), dialogCarregando, true, isAdmin);
            holder.recyclerSubChecks.setAdapter(adapterSubChecks);
        }

        if ( !check.getfilesDoCheck().isEmpty() ){
            holder.recyclerFiles.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.VERTICAL));
            holder.recyclerFiles.setHasFixedSize(true);
            holder.recyclerFiles.setLayoutManager(new LinearLayoutManager(c));
            AdapterArquivos adapterArquivos = new AdapterArquivos(check.getfilesDoCheck(), c, check.getIdTarefa(), dialogCarregando, isAdmin);
            holder.recyclerFiles.setAdapter(adapterArquivos);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void atualizarCheckNoBanco(CheckModel check, MyViewHolder holder){
        dialogCarregando.show();

        refTarefas.child(check.getIdTarefa()).get().addOnCompleteListener(task -> {
            TarefaModel tarefaModel = task.getResult().getValue(TarefaModel.class);

            if ( !check.getSubChecks().isEmpty() ){
                for ( CheckModel checkModel : tarefaModel.getChecks() ){
                    if ( checkModel.getId().equals(check.getId())){
                        for ( CheckModel subCheck : checkModel.getSubChecks()){
                            subCheck.setChecked(holder.btnCheck.isChecked());
                        }
                        for ( CheckModel checkReal : check.getSubChecks() ){
                            checkReal.setChecked(holder.btnCheck.isChecked());
                        }
                        this.notifyDataSetChanged();
                        break;
                    }
                }
            }

            boolean jaAtualizou = false;
            for(CheckModel checkDaTarefa : tarefaModel.getChecks()){
                if ( jaAtualizou ){break;}

                if ( checkDaTarefa.getId().equals(check.getId()) ){
                    checkDaTarefa.setChecked(holder.btnCheck.isChecked());
                    jaAtualizou = true;
                }

                for ( CheckModel subCheckDoCheck : checkDaTarefa.getSubChecks()){
                    if ( subCheckDoCheck.getId().equals(check.getId())){
                        subCheckDoCheck.setChecked(holder.btnCheck.isChecked());
                        jaAtualizou = true;
                    }
                }

            }

            tarefaModel.setStatus(veriificarTodosOsChecados(tarefaModel));

            refTarefas.child(check.getIdTarefa()).setValue(tarefaModel).addOnCompleteListener( attTarefaTask -> {
                dialogCarregando.dismiss();
            });

        });
    }
    private int veriificarTodosOsChecados(TarefaModel tarefaModel){
        int todosChecados = 2;

        for ( CheckModel check : tarefaModel.getChecks()){
            if ( !check.getChecked() ){
                todosChecados = 0;
                break;
            }

            for ( CheckModel subCheck : check.getSubChecks()){
                if ( !subCheck.getChecked() ){
                    todosChecados = 0;
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
