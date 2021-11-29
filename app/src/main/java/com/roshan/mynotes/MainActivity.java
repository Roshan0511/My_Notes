package com.roshan.mynotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.roshan.mynotes.Adapters.NotesAdapter;
import com.roshan.mynotes.Auth.sign_in;
import com.roshan.mynotes.Models.NotesModel;
import com.roshan.mynotes.databinding.ActivityMainBinding;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    NotesAdapter notesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        //Recycler View ---------------------------------------------------------
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        initRecyclerView(Objects.requireNonNull(auth.getCurrentUser()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.recyclerView);


        //Floating Button -------------------------------------------------------
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view  = LayoutInflater.from(MainActivity.this).inflate(R.layout.notes_dialog, null);

                EditText noteEdit = view.findViewById(R.id.addNoteText);
                EditText heading = view.findViewById(R.id.addHeadingText);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addNotes(noteEdit.getText().toString(), heading.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();

                alertDialog.show();
            }
        });
    }




    //Add Notes via Dialog Box ---------------------------------------------------------
    public void addNotes(String text, String title){

        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        NotesModel notes = new NotesModel(text, title, false, new Timestamp(new Date()), userId);

        firebaseFirestore.collection("notes")
                .add(notes)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(MainActivity.this, "Note Added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //Press back button ---------------------------------------------------------
    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }




    //Inflate menu in toolbar ---------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logOut) {
            Toast.makeText(MainActivity.this, "Welcome to Login Activity", Toast.LENGTH_SHORT).show();
            auth.signOut();
            Intent intent = new Intent(MainActivity.this, sign_in.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }



    //OnStart method ---------------------------------------------------------
    @Override
    protected void onStart() {
        super.onStart();

        notesAdapter.startListening();
    }



    //OnStop method ---------------------------------------------------------
    @Override
    protected void onStop() {
        super.onStop();

        if (notesAdapter != null){
            notesAdapter.stopListening();
        }
    }




    //Adapter Items ---------------------------------------------------------
    public void initRecyclerView(FirebaseUser user){
        Query query = firebaseFirestore.collection("notes")
                .whereEqualTo("userId", user.getUid())
                .orderBy("completed" , Query.Direction.ASCENDING)
                .orderBy("time", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<NotesModel> options = new FirestoreRecyclerOptions.Builder<NotesModel>()
                .setQuery(query, NotesModel.class)
                .build();

        notesAdapter = new NotesAdapter(options);
        binding.recyclerView.setAdapter(notesAdapter);
    }




    //Delete items ---------------------------------------------------------
    final ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == ItemTouchHelper.LEFT) {
                NotesAdapter.notesViewHolder notesViewHolder = (NotesAdapter.notesViewHolder) viewHolder;
                notesViewHolder.deleteItem();
                Toast.makeText(MainActivity.this, "Note deleted successfully", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}
