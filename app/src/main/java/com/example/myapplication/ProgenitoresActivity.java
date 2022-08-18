package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.MainActivity.*;

public class ProgenitoresActivity extends AppCompatActivity {

    private String username;
    private int idUsuario;
    private EditText nombrePadre, nombreMadre, lugarNacPadre, lugarNacMadre, fechaNacPadre, fechaNacMadre;
    private RadioGroup padreFallecido, madreFallecida;
    private String np, nm, lnp, lnm, fnp, fnm;
    private int pf, mf;
    private AdminSQLiteOpenHelper admin;
    private SQLiteDatabase db_read, db_write;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progenitores);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        TextView usuario = findViewById(R.id.usuario);
        usuario.setText("Usuario: " + username);
        idUsuario = extras.getInt("user_id");

        // Linking component to logic
        nombrePadre = (EditText) findViewById(R.id.nombrePadre);
        nombreMadre = (EditText) findViewById(R.id.nombreMadre);
        lugarNacPadre = (EditText) findViewById(R.id.lugarNacPadre);
        lugarNacMadre = (EditText) findViewById(R.id.lugarNacMadre);
        fechaNacPadre = (EditText) findViewById(R.id.fechaNacPadre);
        fechaNacMadre = (EditText) findViewById(R.id.fechaNacMadre);
        padreFallecido = (RadioGroup) findViewById(R.id.padreFallecido);
        madreFallecida = (RadioGroup) findViewById(R.id.madreFallecida);

        nombrePadre.setText("Fabio Salvi");
        nombreMadre.setText("Alessia Iorio");
        lugarNacPadre.setText("Napoli");
        lugarNacMadre.setText("Roma");
        fechaNacPadre.setText("09/05/1970");
        fechaNacMadre.setText("05/12/1970");
        padreFallecido.check(R.id.padreFallecidoNo);
        madreFallecida.check(R.id.madreFallecidaNo);

        admin = new AdminSQLiteOpenHelper(this, "administracion", null, 1);
        db_write = admin.getWritableDatabase();
        db_read = admin.getReadableDatabase();

    }

    public void RegistrarProgenitores(View view){
        if(!MainActivity.checkConnection(view.getContext())){
            MainActivity.showAlert(view.getContext(), "CONNECTION ERROR", "Please verify your connection and retry");
            return;
        }
        if(!checkFields()){
            MainActivity.showAlert(view.getContext(), "EMPTY FIELDS ERROR", "Please fill empty fields and retry");
            return;
        }


        /*
        ContentValues usuario = new ContentValues();
        usuario.put("id", idUsuario);
        usuario.put("username", username);


        ContentValues padre = new ContentValues();
        padre.put("nombre",np);
        padre.put("fallecido",pf);
        padre.put("lugar_nac",lnp);
        padre.put("fecha_nac",fnp);
        padre.put("id_usuario",idUsuario);

        ContentValues madre = new ContentValues();
        madre.put("nombre",nm);
        madre.put("fallecido",mf);
        madre.put("lugar_nac",lnm);
        madre.put("fecha_nac",fnm);
        madre.put("id_usuario",idUsuario);*/

        Cursor fila = db_read.rawQuery
                ("select * from progenitores where id_usuario = " + idUsuario, null);

        boolean check = fila.moveToFirst();
        if(check)  MainActivity.showAlert(view.getContext(), "DATABASE INFO MESSAGE", "Progenitores ya cargados en la BD");
        else {
            MainActivity.showAlert(view.getContext(), "DATABASE INFO MESSAGE", "Progenitores insertados exitosamente en la BD");
            cleanFields();
        }
        db_write.execSQL(insertUsuarioString(idUsuario, username));
        db_write.execSQL(insertProgenitorString(np, pf, lnp, fnp, idUsuario));
        db_write.execSQL(insertProgenitorString(nm, mf, lnm, fnm, idUsuario));


        /*
        long usuarioOk = db.insert("usuarios", null, usuario);
        long padreOk = db.insert("progenitores", null, padre);
        long madreOk = db.insert("progenitores", null, madre);
        */

        /*

        if(usuarioOk != -1 && padreOk != -1 && madreOk != -1){
            MainActivity.showAlert(view.getContext(), "DATABASE INFO MESSAGE", "Progenitores correctly inserted into DB");
            cleanFields();
            return;
        }
        else{
            MainActivity.showAlert(view.getContext(), "DATABASE ERROR", "An error occurred when inserting data");
            return;
        }*/

    }

    @Override
    protected void onPause() {
        super.onPause();
        db_write.close();
        db_read.close();
    }

    public boolean checkFields(){
        np = nombrePadre.getText().toString();
        nm = nombreMadre.getText().toString();
        lnp = lugarNacPadre.getText().toString();
        lnm = lugarNacMadre.getText().toString();
        fnp = fechaNacPadre.getText().toString();
        fnm = fechaNacMadre.getText().toString();

        RadioButton padreRadio = (RadioButton) findViewById(padreFallecido.getCheckedRadioButtonId());
        RadioButton madreRadio = (RadioButton) findViewById(madreFallecida.getCheckedRadioButtonId());

        if(padreRadio == null || madreRadio == null) return false;
        else {
             if(padreRadio.getText().toString().equals("Si")) pf = 0;
             else pf = 1;
             if(madreRadio.getText().toString().equals("Si")) mf = 0;
             else mf = 1;
        }

        if(np.equals("") || nm.equals("") || lnp.equals("") || lnm.equals("") || fnp.equals("") || fnm.equals("")) return false;
        else return true;
    }

    public void cleanFields(){
        nombrePadre.setText("");
        nombreMadre.setText("");
        lugarNacPadre.setText("");
        lugarNacMadre.setText("");
        fechaNacPadre.setText("");
        fechaNacMadre.setText("");
        padreFallecido.clearCheck();
        madreFallecida.clearCheck();
    }

    public String insertUsuarioString(int id, String username){
        String cmd;
        cmd = "INSERT OR REPLACE INTO usuarios VALUES (" + id + ", " + "'" + username + "'" + ")";
        return cmd;
    }

    public String insertProgenitorString(String nombre, int fallecido, String lugar, String fecha, int idUsuario){
        String cmd;
        cmd = "INSERT OR REPLACE INTO progenitores VALUES ("
                + "null, "
                + "'" + nombre + "'" + ", "
                + fallecido + ", "
                + "'" + lugar + "'" + ", "
                + "'" + fecha + "'" + ", "
                + idUsuario + ");";
        return cmd;
    }
}