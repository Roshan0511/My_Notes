package com.roshan.mynotes.Adapters;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.roshan.mynotes.Models.NotesModel;
import com.roshan.mynotes.R;

public class NotesAdapter extends FirestoreRecyclerAdapter<NotesModel, NotesAdapter.notesViewHolder> {

    public NotesAdapter(@NonNull FirestoreRecyclerOptions<NotesModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull notesViewHolder holder, int position, @NonNull NotesModel notes) {
        holder.noteRv.setText(notes.getNote());
        holder.headingRV.setText(notes.getHeading());
        holder.checkBox.setChecked(notes.isCompleted());

        CharSequence dateSequence = DateFormat.format("EEEE, MMM d, yyyy  h:mm:ss a", notes.getTime().toDate());
        holder.date.setText(dateSequence);
    }

    @NonNull
    @Override
    public notesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout, parent, false);
        return new notesViewHolder(view);
    }

    public class notesViewHolder extends RecyclerView.ViewHolder {

        TextView noteRv, headingRV, date;
        CheckBox checkBox;

        public notesViewHolder(@NonNull View itemView) {
            super(itemView);

            noteRv = itemView.findViewById(R.id.noteRV);
            headingRV = itemView.findViewById(R.id.heading);
            date = itemView.findViewById(R.id.dateRV);
            checkBox = itemView.findViewById(R.id.checkBox);


            //Check Box click ---------------------------------------------------------
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());

                NotesModel notesModel = getItem(getAdapterPosition());

                if (notesModel.isCompleted() != isChecked){
                    snapshot.getReference().update("completed", isChecked);
                }
            });


            //Edit Note ---------------------------------------------------------
            itemView.setOnClickListener(v -> {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
                View view = LayoutInflater.from((Activity) v.getContext()).inflate(R.layout.edit_notes_dialog, null);

                EditText editTextNote = view.findViewById(R.id.editNoteText);
                EditText editTextHeading = view.findViewById(R.id.editHeadingText);
                NotesModel notesModel = snapshot.toObject(NotesModel.class);
                assert notesModel != null;
                editTextNote.setText(notesModel.getNote());
                editTextNote.setSelection(notesModel.getNote().length());

                editTextHeading.setText(notesModel.getHeading());
                editTextHeading.setSelection(notesModel.getHeading().length());

                AlertDialog alertDialog = new AlertDialog.Builder((Activity) v.getContext())
                        .setView(view)
                        .setPositiveButton("Save", (dialog, which) -> {
                            String newText = editTextNote.getText().toString();
                            String newHeading = editTextHeading.getText().toString();
                            notesModel.setNote(newText);
                            notesModel.setHeading(newHeading);
                            snapshot.getReference().set(notesModel);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .create();

                alertDialog.show();
            });
        }



        //Delete notes ---------------------------------------------------------
        public void deleteItem(){

            DocumentSnapshot snapshot = getSnapshots().getSnapshot(getAdapterPosition());
            DocumentReference reference = snapshot.getReference();

            reference.delete();
        }
    }
}
