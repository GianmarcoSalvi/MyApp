package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {
    public static final String
            CREATE_USUARIOS = "CREATE TABLE IF NOT EXISTS usuarios (" +
            "id INTEGER primary key," +
            "username TEXT" +
            ");";
    public static final String
            CREATE_PROGENITORES = "CREATE TABLE IF NOT EXISTS progenitores (" +
            "id INTEGER primary key autoincrement," +
            "nombre TEXT," +
            "fallecido INTEGER," +
            "lugar_nac TEXT," +
            "fecha_nac DATE," +
            "id_usuario INTEGER," +
            "foreign key (id_usuario) references usuarios(id)" +
            ");";


    public static final String
            DROP_USUARIOS = "DROP TABLE IF EXISTS usuarios";

    public static final String
            DROP_PROGENITORES = "DROP TABLE IF EXISTS progenitores";


    public AdminSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USUARIOS);
        db.execSQL(CREATE_PROGENITORES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}