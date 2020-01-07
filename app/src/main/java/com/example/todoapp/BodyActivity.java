package com.example.todoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class BodyActivity extends AppCompatActivity {
    String value;
    DbSQLite dbSQLite;

    // Funckja odsiwrzajaca liste taskow po powrocie do MainActivity
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BodyActivity.this.finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.body_activity);
        dbSQLite = new DbSQLite(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
        }
        TextView TaskName = findViewById(R.id.task_name);
        TaskName.setText(value);

        String BodyValue = dbSQLite.getBody(value);
        TextView Body_view = findViewById(R.id.body_text);
        Body_view.setText(BodyValue);
    }
    // Funkcja edytujaca zawartosc body dla tasku
    public void editBody(View view){
            final EditText taskEditText = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Add the content of the task:")
                    .setMessage("What do you want to add?")
                    .setView(taskEditText)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface diallog, int which) {
                            TextView task_view = findViewById(R.id.task_name);
                            String task = task_view.getText().toString();
                            String text = String.valueOf(taskEditText.getText());
                            dbSQLite.setBody(task,text);
                            TextView Body_view = findViewById(R.id.body_text);
                            Body_view.setText(text);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();

    }
    // funkcja zmieniajaca nazwe tasku
    public void editTaskName(View view){
        final EditText taskEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Task name")
                .setMessage("Enter new name for task:")
                .setView(taskEditText)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface diallog, int which) {
                        TextView task_view = findViewById(R.id.task_name);
                        String task = task_view.getText().toString();
                        String text = String.valueOf(taskEditText.getText());
                        dbSQLite.setTaskName(task,text);
                        TextView taskName = findViewById(R.id.task_name);
                        taskName.setText(text);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

    }
}
