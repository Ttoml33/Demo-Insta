package com.tomlee.demoinsta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
  private EditText edtUser_name,name,edtEmail,edtPassword;
  private Button btnRegister;
  private TextView tvAlreadyUser;

  private DatabaseReference mRootRef;
  private FirebaseAuth mAuth;
//    ImageView
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUser_name=findViewById(R.id.edtUser_name);
        name=findViewById(R.id.name);
        edtEmail=findViewById(R.id.edtEmail);
        edtPassword=findViewById(R.id.edtPassword);
        btnRegister=findViewById(R.id.btnRegister);
        tvAlreadyUser=findViewById(R.id.tvAlreadyUser);

        mRootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();

        tvAlreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,Login_Activity.class));
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=edtUser_name.getText().toString();
                String Name=name.getText().toString();
                String Email=edtEmail.getText().toString();
                String Password=edtPassword.getText().toString();

                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(Name) ||
                        TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)){
                    Toast.makeText(RegisterActivity.this, "fill in the blanks", Toast.LENGTH_SHORT).show();
                } else if (edtPassword.length()<6) {
                    Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
                }else {
                    // Register user
                    registerUser(userName,Name,Email,Password);
                }
            }
        });
    }

    private void registerUser(String userName, String name, String email, String password) {

     mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {

         @Override
         public void onSuccess(AuthResult authResult) {
             HashMap<String,Object>map=new HashMap<>();
             map.put("Name",name);
             map.put("Email",email);
             map.put("UserName",userName);
             map.put("ID",mAuth.getCurrentUser().getUid());
             map.put("Bio","");
             map.put("imageUrl","default");

             mRootRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).
                     addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Welcome to demo Insta Gram " +
                                "now set your profile", Toast.LENGTH_LONG).show();

                        // Main Activity
                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                    }
                     });
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Toast.makeText(RegisterActivity.this, "Error" +
                     e.getMessage()+ "Retry", Toast.LENGTH_SHORT).show();
         }
     });
    }
}