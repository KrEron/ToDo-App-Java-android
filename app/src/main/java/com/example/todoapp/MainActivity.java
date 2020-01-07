package com.example.todoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    DbSQLite dbSQLite;
    ArrayAdapter<String> mAdapter;
    ListView lstTask;
    // funkcja tworzaca baze dancyh przy odpaleniu aplikacji
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbSQLite = new DbSQLite(this);
        lstTask = (ListView)findViewById(R.id.lstTask);

        loadTaskList();
    }
    // zaladowanie listy taskow
    public void loadTaskList(){
        ArrayList<String> taskList = dbSQLite.getTaskList();
        if(mAdapter == null){
            mAdapter = new ArrayAdapter<String>(this,R.layout.row,R.id.task_title,taskList);
            lstTask.setAdapter(mAdapter);
        }else{
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }
    // stowrzenie menu gornego aplikacji z przyciskiem do tworzenia taskow
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN));

        return super.onCreateOptionsMenu(menu);
    }
    //  Wywołanie menu do tworzenia tasku
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final EditText editTextName = new EditText(this);
        editTextName.setHint("Task Name");
        editTextName.setFocusable(true);
        editTextName.setClickable(true);
        editTextName.setFocusableInTouchMode(true);
        editTextName.setSelectAllOnFocus(true);
        editTextName.setSingleLine(true);
        editTextName.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        switch (item.getItemId()) {
            case R.id.action_add_task:
                final EditText taskEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add New Task")
                        .setMessage("Enter name for your new task.")
                        .setView(taskEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface diallog, int which) {
                                String task = String.valueOf(taskEditText.getText());
                                dbSQLite.insertNewTask(task);
                                loadTaskList();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //  usuwanie taskow
    public void deleteTask(View view){
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
        //Log.e("String", (String) taskTextView.getText());
        String task = String.valueOf(taskTextView.getText());
        dbSQLite.deleteTask(task);
        loadTaskList();
    }
    //  Podniesienie lvl priority taskow
    public void priority_up(View view){
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        dbSQLite.priority_up(task);
        loadTaskList();
    }
    //  przejscie do activty zawierajacego body taskow
    public void showBody(View view){
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        Intent task_to_send = new Intent(MainActivity.this, BodyActivity.class);
        task_to_send.putExtra("key",task);
        MainActivity.this.startActivityForResult(task_to_send, 708);

    }
    //  Załadowanie taskow od nowa po powrocie
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 708) {
            loadTaskList();
        }
    }
}
