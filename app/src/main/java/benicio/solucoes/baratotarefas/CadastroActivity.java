package benicio.solucoes.baratotarefas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import benicio.solucoes.baratotarefas.databinding.ActivityCadastroBinding;
import benicio.solucoes.baratotarefas.databinding.ActivityLoginBinding;
import benicio.solucoes.baratotarefas.databinding.LoadingScreenBinding;
import benicio.solucoes.baratotarefas.databinding.SelectCameraOrGaleryLayoutBinding;
import benicio.solucoes.baratotarefas.model.UserModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CadastroActivity extends AppCompatActivity {

    private StorageReference imgRef = FirebaseStorage.getInstance().getReference().getRoot().child("imagesProfile");
    private Dialog dialogCarregando, dialogSelecionarFoto;
    private Uri imageUri;
    private static final String TAG = "mayara";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSON_CODE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private ActivityCadastroBinding mainBinding;
    private DatabaseReference refUsuarios = FirebaseDatabase.getInstance().getReference().getRoot().child("usuarios");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        configurarDialogCarregando();

        getSupportActionBar().setTitle("CADASTRO");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        configurarDialogSelecionarFoto();

        mainBinding.entrar.setOnClickListener( view -> {
            mainBinding.textInfo.setVisibility(View.GONE);
            UserModel novoUsuario = new UserModel();

            String id = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
            Log.d(TAG, "onCreate: "+ id);
            novoUsuario.setId(id);
            novoUsuario.setEmail(mainBinding.emailField.getEditText().getText().toString().trim());
            novoUsuario.setLogin(mainBinding.loginField.getEditText().getText().toString().trim());
            novoUsuario.setNome(mainBinding.nomeField.getEditText().getText().toString().trim());
            novoUsuario.setSenha(mainBinding.senhaField.getEditText().getText().toString().trim());

            if ( !novoUsuario.getEmail().isEmpty() && !novoUsuario.getSenha().isEmpty() ){

                dialogCarregando.show();

                auth.createUserWithEmailAndPassword(novoUsuario.getEmail(), novoUsuario.getSenha()).addOnCompleteListener(criarTask -> {
                    if (criarTask.isSuccessful()) {
                        if (imageUri != null){
                            UploadTask uploadTask = imgRef.child(novoUsuario.getId() + ".jpg").putFile(imageUri);

                            uploadTask.addOnCompleteListener( uploadImageTask -> {
                                if (uploadImageTask.isSuccessful()){
                                    imgRef.child(novoUsuario.getId() + ".jpg")
                                            .getDownloadUrl().addOnCompleteListener(uri ->{
                                                String linkImagePerfil =  uri.getResult().toString();
                                                novoUsuario.setLinkImageProfile(linkImagePerfil);
                                                refUsuarios.child(novoUsuario.getId()).setValue(novoUsuario).addOnCompleteListener( taskUsuario -> {
                                                    if ( taskUsuario.isSuccessful() ){
                                                        auth.signInWithEmailAndPassword(novoUsuario.getEmail(), novoUsuario.getSenha()).addOnCompleteListener( logarTask -> {
                                                            if ( logarTask.isSuccessful()){
                                                                Toast.makeText(CadastroActivity.this, "Usuário criado com sucesso.", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(this, TarefasActivity.class));
                                                            }else{
                                                                exibirError("Conta criada, volte e faça login!");
                                                            }
                                                        });
                                                    }else{
                                                        exibirError("Erro de conexão, tente novamente.");
                                                    }
                                                });
                                            });
                                }else{
                                    exibirError("Erro ao salvar imagem de perfil.");
                                }
                            });
                        }else{
                            exibirError("Adicione uma imagem de perfil.");
                        }
                    } else {
                        try {
                            throw criarTask.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            exibirError("Senha fraca!");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            exibirError("Email inválido!");
                        } catch (FirebaseAuthUserCollisionException e) {
                            exibirError("Já existe um usuário com o mesmo email!");
                        } catch (Exception e) {
                            exibirError(e.getMessage());
                        }
                    }
                });
            }

        });

        mainBinding.profileImage.setOnClickListener( view -> dialogSelecionarFoto.show());

    }

    private void configurarDialogCarregando() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setCancelable(false);
        LoadingScreenBinding dialogBinding = LoadingScreenBinding.inflate(getLayoutInflater());
        dialogCarregando = b.setView(dialogBinding.getRoot()).create();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }

    public void exibirError(String erro){
        dialogCarregando.dismiss();
        mainBinding.textInfo.setVisibility(View.VISIBLE);
        mainBinding.textInfo.setText(erro);
        Toast.makeText(this, erro, Toast.LENGTH_SHORT).show();
    }

    private void configurarDialogSelecionarFoto() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        SelectCameraOrGaleryLayoutBinding cameraOrGalery = SelectCameraOrGaleryLayoutBinding.inflate(getLayoutInflater());
        b.setTitle("Selecione: ");

        cameraOrGalery.btnCamera.setOnClickListener( view -> {
            baterFoto();
            dialogSelecionarFoto.dismiss();
        });

        cameraOrGalery.btnGaleria.setOnClickListener( view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            dialogSelecionarFoto.dismiss();
        });

        b.setView(cameraOrGalery.getRoot());
        dialogSelecionarFoto = b.create();
    }

    public void baterFoto(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ( checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                String[] permissions = {android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSON_CODE);
            }
            else {
                // already permisson
                openCamera();
            }
        }
        else{
            // system < M
            openCamera();
        }
    }

    public void openCamera(){
        ContentValues values  = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "nova picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem tirada da câmera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intentCamera =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentCamera, REQUEST_IMAGE_CAPTURE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Picasso.get().load(imageUri).into(mainBinding.profileImage);
        }
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(mainBinding.profileImage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == PERMISSON_CODE){
            if( grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }else{
                finish();
                Toast.makeText(this, "Conceda as permissões!", Toast.LENGTH_SHORT).show();
            }
        }
    }

}