package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.model.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.MyViewHolder>{

    List<UserModel> listaDeUsuarios;
    List<UserModel> listaDeUsuariosSelecionados;
    Activity c;


    public AdapterUsuarios(List<UserModel> listaDeUsuarios, List<UserModel> listaDeUsuariosSelecionados, Activity c) {
        this.listaDeUsuarios = listaDeUsuarios;
        this.listaDeUsuariosSelecionados = listaDeUsuariosSelecionados;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_usuario,parent, false));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel user = listaDeUsuarios.get(position);

        Picasso.get().load(user.getLinkImageProfile()).into(holder.profileUser);
        holder.nomeUser.setText(user.getNome());

        holder.itemView.getRootView().setOnClickListener(view -> {
            listaDeUsuariosSelecionados.add(user);
            listaDeUsuarios.remove(position);
            Toast.makeText(c, "Usuário adicionado", Toast.LENGTH_SHORT).show();
            this.notifyDataSetChanged();
        });


    }

    @Override
    public int getItemCount() {
        return listaDeUsuarios.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileUser;
        TextView nomeUser;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileUser = itemView.findViewById(R.id.profile_image_usuarios);
            nomeUser = itemView.findViewById(R.id.nome_usuario_selecionar);

        }
    }
}
