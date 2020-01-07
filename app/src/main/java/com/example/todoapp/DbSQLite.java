package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DbSQLite extends SQLiteOpenHelper {

    private static final String DB_NAME="EDMTDev";
    private static final int DB_VER = 1;
    public static final String DB_TABLE="ToDoTasks";
    public static final String DB_COLUMN_TASK_NAME = "TaskName";
    public static final String DB_COLUMN_TASK_LVL = "Level";
    public static final String DB_COLUMN_TASK_BODY = "Body";

    public DbSQLite(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }
    // towrzy baze danych
    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = String.format("CREATE TABLE "+ DB_TABLE + " (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DB_COLUMN_TASK_NAME + " TEXT NOT NULL, " +
                DB_COLUMN_TASK_LVL + " INTEGER NOT NULL, " +
                DB_COLUMN_TASK_BODY + " TEXT NOT NULL);"
        );
        db.execSQL(query);
    }
    // updatetuje baze danych
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = String.format("DELETE TABLE IF EXISTS %s",DB_TABLE);
        db.execSQL(query);
        onCreate(db);
    }
    // dodawnie tasku
    public void insertNewTask(String task){

        SQLiteDatabase db = this.getWritableDatabase();
        int lvl = (int)DatabaseUtils.queryNumEntries(db,DB_TABLE);
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_TASK_NAME, task);
        values.put(DB_COLUMN_TASK_LVL, lvl);
        values.put(DB_COLUMN_TASK_BODY, "Edit your body to make new one.");
        db.insertWithOnConflict(DB_TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }
    // zwiekszenie priority tasku
    public void  priority_up(String task){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT Level FROM ToDoTasks WHERE TaskName = '"+task+"'", null);
        c.moveToFirst();
        String Level = c.getString(c.getColumnIndex("Level"));
        if(Integer.parseInt(Level) != ((int)DatabaseUtils.queryNumEntries(db,DB_TABLE))-1) {
            //Log.e("String", task);
            String strSQL1 = "UPDATE ToDoTasks SET Level = (SELECT Level FROM ToDoTasks WHERE TaskName ='" + task + "') WHERE Level = ((SELECT Level from ToDoTasks WHERE TaskName = '" + task + "') + 1)";
            db.execSQL(strSQL1);
            String strSQL2 = "UPDATE " + DB_TABLE + " SET " + DB_COLUMN_TASK_LVL + " = (SELECT Level from ToDoTasks WHERE TaskName = '" + task + "') + 1 WHERE TaskName = '" + task + "'";
            db.execSQL(strSQL2);
            db.close();
        }

    }
    // Funkcja zwarcajaca zawartosc body tasku
    public String getBody(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT Body FROM ToDoTasks WHERE TaskName = '"+task+"'", null);
        c.moveToFirst();
        String task_body = c.getString(c.getColumnIndex("Body"));
        db.close();
        return task_body;
    }
    // funckja ustawiajaca zawartosc body
    public void setBody(String task, String text){
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL1 = "UPDATE ToDoTasks SET Body = '"+ text +"' WHERE TaskName = '"+task+"'";
        db.execSQL(strSQL1);
        db.close();
    }
    // funkcja zmieniajaca nazwe tasku
    public void setTaskName(String task, String text){
        SQLiteDatabase db = this.getWritableDatabase();
        String strSQL1 = "UPDATE ToDoTasks SET TaskName = '"+ text +"' WHERE TaskName = '"+task+"'";
        db.execSQL(strSQL1);
        db.close();
    }
    //  funkcja usuwajaca task
    public void deleteTask(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT Level FROM ToDoTasks WHERE TaskName ='"+ task +"'", null);
        cursor.moveToFirst();
        String LevelOfDeletedTask = cursor.getString(cursor.getColumnIndex("Level"));
        Cursor c = db.rawQuery("SELECT * FROM ToDoTasks WHERE Level > " + LevelOfDeletedTask,  null);
        if (c.moveToFirst()) {
            do {
                String taskname = c.getString(c.getColumnIndex("TaskName"));
                String lvl = c.getString(c.getColumnIndex("Level"));
                int lvlup = Integer.parseInt(lvl) - 1;
                //Log.e("String", "Task " + taskname + " lvl " + lvl + " > " +  Integer.toString(lvlup));
                String strSQL1 = "UPDATE ToDoTasks SET Level = " + Integer.toString(lvlup) + " WHERE TaskName = '"+taskname+"'";
                db.execSQL(strSQL1);
            } while (c.moveToNext());
        }
        Cursor cursor1 = db.rawQuery("SELECT Level FROM ToDoTasks WHERE TaskName ='"+ task +"'", null);
        cursor1.moveToFirst();
        String LevelOf = cursor.getString(cursor1.getColumnIndex("Level"));
        //Log.e("String", LevelOf);
        db.delete(DB_TABLE,DB_COLUMN_TASK_NAME + " = ?",new String[]{task});
        db.close();
    }
    //funkcja zwracjaca liste taskow
    public ArrayList<String> getTaskList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,new String[]{DB_COLUMN_TASK_NAME},null,null,null,null,DB_COLUMN_TASK_LVL + " DESC");
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(DB_COLUMN_TASK_NAME);
            taskList.add(cursor.getString(index));
        }
        cursor.close();
        db.close();
        return taskList;
    }
}