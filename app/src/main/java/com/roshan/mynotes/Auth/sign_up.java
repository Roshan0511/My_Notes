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
import com.roshan.mynotes.MainActivity;
import com.roshan.mynotes.R;
import com.roshan.mynotes.databinding.ActivitySignUpBinding;

public class sign_up extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setTitle("Creating your account");
        dialog.setMessage("Please wait we're creating your account");

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.emailSignUp.getText().toString().isEmpty()){
//                    Toast.makeText(sign_up.this, "Email is required", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(sign_up.this)
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
                else if (binding.passSignUp.getText().toString().isEmpty()){
//                    Toast.makeText(sign_up.this, "Password is required", Toast.LENGTH_SHORT).show();

                    new AlertDialog.Builder(sign_up.this)
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

                    auth.createUserWithEmailAndPassword(binding.emailSignUp.getText().toString(), binding.passSignUp.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    dialog.dismiss();
                                    if (task.isSuccessful()){
                                        Toast.makeText(sign_up.this, "Successfully Created Your account", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(sign_up.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(sign_up.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sign_up.this, sign_in.class);
                startActivity(intent);
                finish();
            }
        });
    }
}