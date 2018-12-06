package mx.edu.iitepic.damd_u4_practica2_inmobiliaria;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    Button regrear;

    EditText identificacion, nombre, domicilio, telefono;
    Button insertar, consultar, eliminar, actualizar;
    BaseDatos base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        regrear = findViewById(R.id.otra);

        identificacion = findViewById(R.id.Ident);
        nombre = findViewById(R.id.Nombre);
        domicilio = findViewById(R.id.Domicilio);
        telefono = findViewById(R.id.Telefono);

        insertar = findViewById(R.id.Insertar);
        consultar = findViewById(R.id.Consultar);
        eliminar = findViewById(R.id.Eliminar);
        actualizar = findViewById(R.id.Actualizar);

        regrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otraVentana = new Intent(Main2Activity.this, MainActivity.class);
                startActivity(otraVentana);
            }
        });


        base = new BaseDatos(this, "inmobiliaria", null, 1);


        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (identificacion.length() == 0 || nombre.length()==0 || domicilio.length() == 0 || telefono.length() == 0){
                    Toast.makeText(Main2Activity.this, "llenar los campos", Toast.LENGTH_LONG).show();
                }else{
                    guardarDatos();
                }

            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(2);
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizar.getText().toString().startsWith("CONFIRMAR")){
                    invocarConfirmacionActualizacion();
                }else{
                    pedirID(3);
                }
            }
        });

    }

    private void invocarConfirmacionActualizacion() {

        AlertDialog.Builder confir = new AlertDialog.Builder(this);

        confir.setTitle("IMPORTANTE").setMessage("¿ Deseas aplicar los cambios?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aplicarActualizacion();
                dialog.dismiss();
            }
        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                dialog.cancel();
            }
        }).show();
    }

    private void habilitarBotonesYLimpiarCampos(){
        identificacion.setText("");
        nombre.setText("");
        domicilio.setText("");
        telefono.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
        identificacion.setEnabled(true);
    }

    private void aplicarActualizacion(){
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "UPDATE PROPIETARIO SET NOMBRE='"+nombre.getText().toString()+"', DOMICILIO='"+domicilio.getText().toString()+"', TELEFONO='"
                    +telefono.getText().toString()+"' WHERE IDP=" +identificacion.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, " se actualizaron correctamente ", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: En ACTUALIZAR", Toast.LENGTH_LONG).show();
        }

        habilitarBotonesYLimpiarCampos();
    }

    private void pedirID(final int origen) {

        final EditText pidoID = new EditText(this);
        pidoID.setInputType(InputType.TYPE_CLASS_NUMBER);
        pidoID.setHint("VALOR ENTERO MAYOR DE 0");

        String mensaje = "";
        String mensajeBoton = "";

        if (origen==1){
            mensaje = "ESCRIBA EL ID A BUSCAR";
            mensajeBoton = "BUSCAR";
        }
        if (origen==2){
            mensaje = "ESCRIBA EL ID QUE SE DESEA ELIMINAR";
            mensajeBoton = "ELIMINAR";
        }
        if (origen==3){
            mensaje = "ESCRIBA EL ID A MODIFICAR";
            mensajeBoton = "MODIFICAR";
        }


        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage(mensaje).setView(pidoID).setPositiveButton(mensajeBoton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pidoID.getText().toString().isEmpty()){
                    Toast.makeText(Main2Activity.this, "DEBES ESCRIBIR LOS DATOS", Toast.LENGTH_LONG).show();
                    return;
                }
                buscarDato(pidoID.getText().toString(), origen);
                dialog.dismiss();
            }
        }).setNegativeButton("CANCELAR", null).show();

    }

    private void buscarDato(String idABuscar, int origen){
        try{
            SQLiteDatabase tabla = base.getReadableDatabase();

            String SQL = "SELECT * FROM PROPIETARIO WHERE IDP="+idABuscar;

            Cursor resultado = tabla.rawQuery(SQL, null);

            if (resultado.moveToFirst()){

                if (origen==2){

                    String datos = idABuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3);
                    invocarConfirmacionEliminacion(datos);
                    return;
                }

                identificacion.setText(resultado.getString(0));
                nombre.setText(resultado.getString(1));
                domicilio.setText(resultado.getString(2));
                telefono.setText(resultado.getString(3));

                if (origen==3){
                    //mo
                    actualizar.setText("CONFIRMAR");
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    identificacion.setEnabled(false);
                }
            }else{
                //No hay!
                Toast.makeText(this, "ERROR: No se pudo buscar ", Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE RESULTADO", Toast.LENGTH_LONG).show();
        }

    }

    private void invocarConfirmacionEliminacion(String datos) {

        String cadenaDatos[] = datos.split("&");
        final String id = cadenaDatos[0];
        String nombre = cadenaDatos[1];
        String domicilio = cadenaDatos[2];
        String telefono = cadenaDatos[3];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("Deseas eliminar \nUsuario: "+id+"\nNombre: "+nombre+" \nDomicilio: "+domicilio+" \nTelefono: "+telefono+" ?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                eliminarDato(id);
                dialog.dismiss();
            }
        }).setNegativeButton("NO", null).show();
    }

    private void eliminarDato(String id) {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "DELETE FROM PROPIETARIO WHERE IDP="+id;

            tabla.execSQL(SQL);

            identificacion.setText("");
            nombre.setText("");
            domicilio.setText("");
            telefono.setText("");

            Toast.makeText(this, "Eliminado", Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }

    private void guardarDatos() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "INSERT INTO PROPIETARIO VALUES(%1, '%2', '%3', '%4')";
            SQL = SQL.replace("%1", identificacion.getText().toString());
            SQL = SQL.replace("%2", nombre.getText().toString());
            SQL = SQL.replace("%3", domicilio.getText().toString());
            SQL = SQL.replace("%4", telefono.getText().toString());

            tabla.execSQL(SQL);

            Toast.makeText(this, "Se guardo exitosamente", Toast.LENGTH_LONG).show();

            identificacion.setText("");
            nombre.setText("");
            domicilio.setText("");
            telefono.setText("");

            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: No se guardó", Toast.LENGTH_LONG).show();
        }
    }
}
