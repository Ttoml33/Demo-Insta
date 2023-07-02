package com.tomlee.demoinsta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 2002212;
    private Uri imageUri;
    private String imageUrl;
    private ImageView imgAdded,imgClose;
    private TextView txtPost;
    private EditText txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        txtDescription=findViewById(R.id.txtDescription);
        txtPost=findViewById(R.id.txtPost);
        imgAdded=findViewById(R.id.imgAdded);
        imgClose=findViewById(R.id.imgClose);
        txtDescription=findViewById(R.id.txtDescription);

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this,MainActivity.class));
                finish();
            }
        });
        CropImage.activity().start(PostActivity.this);

        txtPost.setOnClickListener(view -> {
            //uploading to firebase dp so it can be read on home fragment
            Upload();
        });
        imgAdded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Upload();
            }
        });
    }

    private void Upload() {
    //  TODO implement  progress bar instead
//        ProgressDialog progressDialog= new ProgressDialog(this);
//        progressDialog.setMessage("uploading");
//        progressDialog.show();

        if (imageUri!=null){
            StorageReference filePath= FirebaseStorage.getInstance().getReference("POSTS").
                    child(System.currentTimeMillis()+"."+ getFileExtension(imageUri));
            StorageTask uploadTask=filePath.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return  filePath.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                Uri downloadUri = task.getResult();
                imageUrl=downloadUri.toString();
// TODO make uid as the main item with other map items and post id as child
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference("POSTS");
                String postID=ref.push().getKey();
                HashMap<String,Object>map=new HashMap<>();
                map.put("Publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                map.put("postID",postID);
                map.put("imageUrl",imageUrl);
                map.put("description",txtDescription.getText().toString());

                ref.child(postID).setValue(map);

            }).addOnFailureListener(e -> Toast.makeText(PostActivity.this, e.getMessage()+"is the error", Toast.LENGTH_SHORT).show());
//            progressDialog.dismiss();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }else {
            Toast.makeText(this, "No Image was Selected", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {
    return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }


    private void PickImage() {
        // TODO  code to open camera/gallery
//        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.setType(ContactsContract.CommonDataKinds.Photo.PHOTO);
//        ActivityCompat.startActivityForResult(PostActivity.this,intent,0,null);
//        startActivity(intent, PICK_IMAGE);
//        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();
            imgAdded.setImageURI(imageUri);
        }else {
            Toast.makeText(this, "Unsuccessful please try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}