package com.roshan.mynotes.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.roshan.mynotes.ForgetPassword;
import com.roshan.mynotes.MainActivity;
import com.roshan.mynotes.R;
import com.roshan.mynotes.databinding.ActivitySignInBinding;

public class sign_in extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setTitle("Login Account");
        dialog.setMessage("Log in to your account");

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.emailInput.getEditText().getText().toString().isEmpty()){
//                    Toast.makeText(sign_in.this, "Please Enter your email", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(sign_in.this)
                            .setIcon(R.drawable.ic_baseline_cancel_24)
                            .setTitle("Email is required")
                            .setMessage("Please Enter your email")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                else if (binding.passwordInput.getEditText().getText().toString().isEmpty()){
//                    Toast.makeText(sign_in.this, "Please Enter your Password", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(sign_in.this)
                            .setIcon(R.drawable.ic_baseline_cancel_24)
                            .setTitle("Password is required")
                            .setMessage("Please Enter your password")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }
                else {
                    dialog.show();

                    auth.signInWithEmailAndPassword(binding.emailInput.getEditText().getText().toString(),
                            binding.passwordInput.getEditText().getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    dialog.dismiss();
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(sign_in.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });




        binding.forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_in.this, ForgetPassword.class);
                startActivity(intent);
            }
        });





        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_in.this, sign_up.class);
                startActivity(intent);
            }
        });


        if (auth.getCurrentUser() != null){
            Intent intent = new Intent(sign_in.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}