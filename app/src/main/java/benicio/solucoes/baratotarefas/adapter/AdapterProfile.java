package benicio.solucoes.baratotarefas.adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import benicio.solucoes.baratotarefas.R;
import benicio.solucoes.baratotarefas.model.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterProfile extends RecyclerView.Adapter<AdapterProfile.MyViewHolder> {

    List<UserModel> users;

    public AdapterProfile(List<UserModel> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_only_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        UserModel user = users.get(position);


        holder.profileUser.setVisibility(View.GONE);
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
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileUser;
        ProgressBar progressProfilePhoto;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            profileUser = itemView.findViewById(R.id.profile_image_only);
            progressProfilePhoto = itemView.findViewById(R.id.progressProdfilePhotoOnly);
        }
    }
}
