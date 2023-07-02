package com.tomlee.demoinsta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tomlee.demoinsta.Fragments.PostDetailFragment;
import com.tomlee.demoinsta.Fragments.ProfileFragment;
import com.tomlee.demoinsta.Model.User;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView imgClose;
    private Context mContext;
    private CircleImageView imgProfile;
    private TextView save,changePhoto;
    private MaterialEditText fullName,userName,bio;
    private ProgressBar progressbar;

    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgClose=findViewById(R.id.imgClose);
        imgProfile=findViewById(R.id.imgProfile);
        save=findViewById(R.id.save);
        changePhoto=findViewById(R.id.changePhoto);
        fullName=findViewById(R.id.fullName);
        userName=findViewById(R.id.userName);
        bio=findViewById(R.id.bio);
        progressbar=findViewById(R.id.progressbar);

        fUser= FirebaseAuth.getInstance().getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference().child("Uploads");

        FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user =snapshot.getValue(User.class);
                fullName.setText(user.getName());
                userName.setText(user.getUserName());
                bio.setText(user.getBio());
                Picasso.get().load(user.getImageUrl()).into(imgProfile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).
                        start(EditProfileActivity.this);
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).
                        start(EditProfileActivity.this);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
                finish();
            }
        });
    }

    private void updateProfile() {
        HashMap<String,Object>map=new HashMap<>();
        map.put("Name",fullName.getText().toString());
        map.put("UserName",userName.getText().toString());
        map.put("Bio",bio.getText().toString());

        FirebaseDatabase.getInstance().getReference().child("Users").
                child(fUser.getUid()).updateChildren(map);
    }

    private void uploadImage() {
        progressbar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (imageUri!=null){
            StorageReference fileRef=storageReference.child(System.currentTimeMillis()+".jpeg");
            uploadTask=fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return  fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                       Uri downLoadUri= (Uri) task.getResult();
                       String url=downLoadUri.toString();
                       FirebaseDatabase.getInstance().getReference().child("Users").
                               child(fUser.getUid()).child("imageUrl").setValue(url);
                        progressbar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }else{
                        Toast.makeText(EditProfileActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            uploadImage();
        }else{
            Toast.makeText(this, "Failed Something Went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}