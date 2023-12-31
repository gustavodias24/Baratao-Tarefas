package benicio.solucoes.baratotarefas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import benicio.solucoes.baratotarefas.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mainBinding;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final int SPLASH_DURATION = 3000;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Picasso.get().load(R.raw.logo).into(mainBinding.logo);

        // Animação de fade in
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500); // 1,5 segundos
        mainBinding.logo.startAnimation(fadeIn);

        // Inicia a próxima atividade após 3 segundos
        new Handler().postDelayed(() -> {
            Intent intent;
            if ( user == null ){
                intent = new Intent(getApplicationContext(), LoginActivity.class);
            }else{
                intent = new Intent(getApplicationContext(), TarefasActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);

    }
}