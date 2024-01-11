package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.VisualizarTarefaActivity;
import benicio.solucoes.baratotarefas.model.CheckModel;
import benicio.solucoes.baratotarefas.model.TarefaModel;

public class AdapterTarefas extends RecyclerView.Adapter<AdapterTarefas.MyViewHolder>{

    List<TarefaModel> tarefas;
    Activity c;

    public AdapterTarefas(List<TarefaModel> tarefas, Activity c) {
        this.tarefas = tarefas;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tarefa, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TarefaModel tarefaModel = tarefas.get(position);
        holder.tituloTarefa.setText(tarefaModel.getNomeTarefa());

        String statusTarefa = "";
        if ( tarefaModel.getStatus() == 0){
            statusTarefa = "Pendente";
            holder.status.setBackgroundColor(Color.YELLOW);
        }else if ( tarefaModel.getStatus() == 1){
            statusTarefa = "Vencida";
            holder.status.setBackgroundColor(Color.RED);
        }else{
            statusTarefa = "Concluída";
            holder.status.setBackgroundColor(Color.GREEN);
        }

        holder.prazo.setText( tarefaModel.getData()+ " às " + tarefaModel.getHora());

        holder.status.setText(statusTarefa);

        verificarChecks(tarefaModel, holder.concluidas);

        holder.recyclerResponsaveis.setLayoutManager(new LinearLayoutManager(c, RecyclerView.HORIZONTAL, false));
        holder.recyclerResponsaveis.setHasFixedSize(true);
//        holder.recyclerResponsaveis.addItemDecoration(new DividerItemDecoration(c, DividerItemDecoration.HORIZONTAL));
        AdapterProfile adapterProfile = new AdapterProfile(tarefaModel.getUsuariosResponsaveis());
        holder.recyclerResponsaveis.setAdapter(adapterProfile);

        holder.imageMore.setOnClickListener( view -> {
            Intent i = new Intent(c, VisualizarTarefaActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("idTarefa", tarefaModel.getId());
            c.startActivity(i);
        });
    }

    @SuppressLint("DefaultLocale")
    private void verificarChecks(TarefaModel tarefaModel, TextView textoHolder){
        int qtdCheck = 0;
        int qtdCheckConcluidos = 0;


        for (CheckModel check : tarefaModel.getChecks()){
            qtdCheck++;
            if ( check.getChecked() ){
                qtdCheckConcluidos++;
            }

            for ( CheckModel subCheck : check.getSubChecks()){
                qtdCheck++;
                if ( subCheck.getChecked() ){
                    qtdCheckConcluidos++;
                }
            }
        }

        textoHolder.setText(
                String.format("%d/%d",
                        qtdCheckConcluidos,
                        qtdCheck)
                );
    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tituloTarefa, status, prazo, concluidas;
        ImageView imageMore;
        RecyclerView recyclerResponsaveis;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloTarefa = itemView.findViewById(R.id.titulo_tarefa);
            imageMore = itemView.findViewById(R.id.image_more);
            recyclerResponsaveis = itemView.findViewById(R.id.recyclerUsuariosResponsaveis);
            status = itemView.findViewById(R.id.status_text);
            prazo = itemView.findViewById(R.id.prazo_text);
            concluidas = itemView.findViewById(R.id.concluidas_text);

        }
    }
}
