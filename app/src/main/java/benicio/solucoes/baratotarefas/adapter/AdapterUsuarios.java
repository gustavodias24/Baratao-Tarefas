package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.microedition.khronos.egl.EGLSurface;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.model.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.MyViewHolder>{

    List<UserModel> listaDeUsuarios;
    List<UserModel> listaDeUsuariosSelecionados;
    Activity c;

    AdapterUsuarios adapterUsuariosSelecionados;

    boolean exibicao;


    public AdapterUsuarios(List<UserModel> listaDeUsuarios, List<UserModel> listaDeUsuariosSelecionados, Activity c, AdapterUsuarios adapterUsuariosSelecionados, boolean exibicao) {
        this.listaDeUsuarios = listaDeUsuarios;
        this.listaDeUsuariosSelecionados = listaDeUsuariosSelecionados;
        this.c = c;
        this.adapterUsuariosSelecionados = adapterUsuariosSelecionados;
        this.exibicao = exibicao;
    }

    public AdapterUsuarios(List<UserModel> listaDeUsuarios, Activity c, boolean exibicao) {
        this.listaDeUsuarios = listaDeUsuarios;
        this.c = c;
        this.exibicao = exibicao;
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

        holder.profileUser.setVisibility(View.GONE);
        holder.progressProfilePhoto.setVisibility(View.VISIBLE);
        Picasso.get().load(user.getLinkImageProfile()).into(holder.profileUser, new Callback() {
            @Override
            public void onSuccess() {
                holder.profileUser.setVisibility(View.VISIBLE);
                holder.progressProfilePhoto.setVisibility(View.GONE);
            }

            @SuppressLint("ResourceType")
            @Override
            public void onError(Exception e) {
                holder.profileUser.setVisibility(View.VISIBLE);
                holder.progressProfilePhoto.setVisibility(View.GONE);
                Picasso.get().load(R.raw.notloading).into(holder.profileUser);
            }
        });

        holder.nomeUser.setText(user.getNome());

        if ( !exibicao ){
            holder.itemView.getRootView().setOnClickListener(view -> {
                try {
                    boolean select = true;

                    for ( UserModel userJaSelecionado : listaDeUsuariosSelecionados){
                        if ( userJaSelecionado.getEmail().equals(user.getEmail()) ){
                            Toast.makeText(c, "Esse usuário já foi selecionado!", Toast.LENGTH_SHORT).show();
                            select = false;
                            break;
                        }
                    }

                    if ( select ){
                        listaDeUsuariosSelecionados.add(user);
                        listaDeUsuarios.remove(position);
                        Toast.makeText(c, "Usuário adicionado", Toast.LENGTH_SHORT).show();
                        this.notifyDataSetChanged();
                        adapterUsuariosSelecionados.notifyDataSetChanged();
                    }
                }catch (Exception e){}

            });
        }else{
            holder.itemView.getRootView().setClickable(false);
            holder.removeUser.setVisibility(View.VISIBLE);

            holder.removeUser.setOnClickListener(view -> {
                listaDeUsuarios.remove(position);
                this.notifyDataSetChanged();
            });
        }



    }

    @Override
    public int getItemCount() {
        return listaDeUsuarios.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileUser;
        TextView nomeUser;
        ImageButton removeUser;
        ProgressBar progressProfilePhoto;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileUser = itemView.findViewById(R.id.profile_image_usuarios);
            nomeUser = itemView.findViewById(R.id.nome_usuario_selecionar);
            removeUser = itemView.findViewById(R.id.removeUserInRecycler);
            progressProfilePhoto = itemView.findViewById(R.id.progressProdfilePhoto);

        }
    }
}
