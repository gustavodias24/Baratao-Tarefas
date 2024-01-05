package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.model.CheckModel;

public class AdapterChecks extends RecyclerView.Adapter<AdapterChecks.MyViewHolder> {

    Activity c;
    List<CheckModel> listaChecks;

    public AdapterChecks(Activity c, List<CheckModel> listaChecks) {
        this.c = c;
        this.listaChecks = listaChecks;
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
    }

    @Override
    public int getItemCount() {
        return listaChecks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        RadioButton btnCheck;
        ImageButton removeCheck;
        TextView nomeCheck;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btnCheck = itemView.findViewById(R.id.radioCheck);
            removeCheck = itemView.findViewById(R.id.removeCheck);
            nomeCheck = itemView.findViewById(R.id.nomeCheckText);
        }
    }
}
