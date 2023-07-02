package com.tomlee.demoinsta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {
  private   ImageView icon_image;
    private Button login,register;
    private LinearLayout linear_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        linear_layout=findViewById(R.id.linear_layout);
        icon_image=findViewById(R.id.icon_image);

        linear_layout.animate().alpha(0f).setDuration(1);

            TranslateAnimation animation=new TranslateAnimation(0,0,0,-1000);
                animation.setDuration(1000);
                animation.setFillAfter(false);
                animation.setAnimationListener(new myAnimeListener());

                icon_image.setAnimation(animation);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(StartActivity.this,RegisterActivity.class).
                       addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this,Login_Activity.class).
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }
    private class myAnimeListener implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            icon_image.clearAnimation();
            icon_image.setVisibility(View.INVISIBLE);
            linear_layout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        if (FirebaseAuth.getInstance().getCurrentUser() !=null){
            startActivity(new Intent(StartActivity.this,MainActivity.class));
            finish();
        }
        super.onStart();
    }
}