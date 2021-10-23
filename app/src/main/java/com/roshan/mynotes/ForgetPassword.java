package com.roshan.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.roshan.mynotes.Auth.sign_in;
import com.roshan.mynotes.databinding.ActivityForgetPasswordBinding;

public class ForgetPassword extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.emailSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.emailForgot.getText().toString();

                if (email.isEmpty()){
                    binding.emailForgot.setError("Email is required");
                }
                else {

                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                new AlertDialog.Builder(ForgetPassword.this)
                                        .setIcon(R.drawable.ic_baseline_send_24)
                                        .setTitle("Password Sent")
                                        .setMessage("Check your email")
                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent intent = new Intent(ForgetPassword.this, sign_in.class);
                                                startActivity(intent);
                                            }
                                        }).show();
                            }
                            else {
                                Toast.makeText(ForgetPassword.this, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}