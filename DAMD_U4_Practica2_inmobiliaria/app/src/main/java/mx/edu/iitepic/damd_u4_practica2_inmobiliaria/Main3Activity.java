package mx.edu.iitepic.damd_u4_practica2_inmobiliaria;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {

    EditText identificacion, domicilio, precioVenta, precioRenta, fechaTransaccion;
    Button insertar, consultar, eliminar, actualizar, regrear;
    BaseDatos base;
    Spinner sp;

    String[] nombre;
    String[] id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        regrear = findViewById(R.id.otra);

        regrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent otraVentana = new Intent(Main3Activity.this, MainActivity.class);
                startActivity(otraVentana);
            }
        });


        identificacion = findViewById(R.id.Ident);
        domicilio = findViewById(R.id.Domicilio);
        precioVenta = findViewById(R.id.preventa);
        precioRenta = findViewById(R.id.prerenta);
        fechaTransaccion = findViewById(R.id.Fecha);

        insertar = findViewById(R.id.Insertar);
        consultar = findViewById(R.id.Consultar);
        eliminar = findViewById(R.id.Eliminar);
        actualizar = findViewById(R.id.Actualizar);

        sp = findViewById(R.id.spidforanero);

        try{

            base = new BaseDatos(this, "inmobiliaria", null, 1);


            SQLiteDatabase bd = base.getReadableDatabase();

            String SQL = "SELECT IDP, NOMBRE FROM PROPIETARIO ORDER BY IDP";

            Cursor fila = bd.rawQuery(SQL, null);

            if (fila.moveToFirst()==false){
                Toast.makeText(this, "No hay propietarios agregados", Toast.LENGTH_LONG).show();
            }

            if(fila.getCount()>0){
                id = new String[fila.getCount()];
                nombre = new String[fila.getCount()];
                for(int i=0; i<fila.getCount(); i++){
                    id[i]=fila.getString(0);
                    nombre[i] = fila.getString(1);
                    fila.moveToNext();
                }
            }

            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nombre);
            sp.setAdapter(adaptador);

        }catch (Exception e){

        }

        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (identificacion.length() == 0 || domicilio.length()==0 || precioVenta.length() == 0 || precioRenta.length() == 0 || fechaTransaccion.length() == 0){
                    Toast.makeText(Main3Activity.this, "llenar los campos", Toast.LENGTH_LONG).show();
                }else{
                    guardarDatos();
                }
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(2);
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pedirID(1);
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actualizar.getText().toString().startsWith("CONFIRMAR ")){
                    invocarConfirmacionActualizacion();
                }else{
                    pedirID(3);
                }
            }
        });


    }

    private void invocarConfirmacionActualizacion() {
        AlertDialog.Builder confi = new AlertDialog.Builder(this);

        confi.setTitle("IMPORTANTE").setMessage("¿ Deseas aplicar los cambios?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aplicarActualizacion();
                sp.setEnabled(true);
                dialog.dismiss();
            }
        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                habilitarBotonesYLimpiarCampos();
                sp.setEnabled(true);
                dialog.cancel();
            }
        }).show();
    }

    private void habilitarBotonesYLimpiarCampos(){
        identificacion.setText("");
        domicilio.setText("");
        precioVenta.setText("");
        precioRenta.setText("");
        fechaTransaccion.setText("");
        insertar.setEnabled(true);
        consultar.setEnabled(true);
        eliminar.setEnabled(true);
        actualizar.setText("ACTUALIZAR");
        identificacion.setEnabled(true);
    }

    private void aplicarActualizacion(){
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();
            String SQL = "UPDATE INMUEBLE SET DOMICILIO='"+domicilio.getText().toString()+"', PRECIOVENTA="+precioVenta.getText().toString()+", PRECIORENTA="
                    +precioRenta.getText().toString()+", FECHATRANSACCION='"+fechaTransaccion.getText().toString()+"' WHERE IDINMUEBLE=" +identificacion.getText().toString();
            tabla.execSQL(SQL);
            tabla.close();
            Toast.makeText(this, "Se actualizaron correctamente los datos", Toast.LENGTH_LONG).show();
        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE PUDO ACTUALIZAR", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Main3Activity.this, "DEBES ESCRIBIR LOS DATOS", Toast.LENGTH_LONG).show();
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

            String SQL = "SELECT * FROM INMUEBLE WHERE IDINMUEBLE="+idABuscar;

            Cursor resultado = tabla.rawQuery(SQL, null);

            if (resultado.moveToFirst()){

                if (origen==2){

                    String datos = idABuscar+"&"+resultado.getString(1)+"&"+resultado.getString(2)+"&"+resultado.getString(3)+"&"+resultado.getString(4)+"&"+resultado.getString(5);
                    invocarConfirmacionEliminacion(datos);
                    return;
                }

                identificacion.setText(resultado.getString(0));
                domicilio.setText(resultado.getString(1));
                precioVenta.setText(resultado.getString(2));
                precioRenta.setText(resultado.getString(3));
                fechaTransaccion.setText(resultado.getString(4));
                sp.setSelection(Integer.parseInt(resultado.getString(5))-1);


                if (origen==3){
                    insertar.setEnabled(false);
                    consultar.setEnabled(false);
                    eliminar.setEnabled(false);
                    actualizar.setText("CONFIRMAR ACTUALIZACION");
                    identificacion.setEnabled(false);
                    sp.setEnabled(false);
                }
            }else{
                //No hay!
                Toast.makeText(this, "ERROR: No se pudo buscar", Toast.LENGTH_LONG).show();
            }
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE ENCONTRO DATOS", Toast.LENGTH_LONG).show();
        }

    }

    private void invocarConfirmacionEliminacion(String datos) {

        String cadenaDatos[] = datos.split("&");
        final String id = cadenaDatos[0];
        String domicilio = cadenaDatos[1];
        String precioVenta = cadenaDatos[2];
        String precioRenta = cadenaDatos[3];
        String fechaTransaccion = cadenaDatos[4];
        String propietario = cadenaDatos[5];

        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("ATENCION").setMessage("Deseas eliminar \nDomicilio: "+domicilio+" \nPrecio Venta: "+precioVenta+" \nPrecio Renta: "+precioRenta+"\nFecha Transaccion: "+fechaTransaccion+" ?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
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

            String SQL = "DELETE FROM INMUEBLE WHERE IDINMUEBLE="+id;

            tabla.execSQL(SQL);

            identificacion.setText("");
            domicilio.setText("");
            precioVenta.setText("");
            precioRenta.setText("");
            fechaTransaccion.setText("");

            Toast.makeText(this, "Eliminado correctamente", Toast.LENGTH_LONG).show();
            tabla.close();

        }catch (SQLiteException e){
            Toast.makeText(this, "ERROR: NO SE PUDO ELIMINAR", Toast.LENGTH_LONG).show();
        }
    }

    private void guardarDatos() {
        try{
            SQLiteDatabase tabla = base.getWritableDatabase();

            String SQL = "INSERT INTO INMUEBLE VALUES(%1, '%2', %3, %4, '%5', %6)";
            SQL = SQL.replace("%1", identificacion.getText().toString());
            SQL = SQL.replace("%2", domicilio.getText().toString());
            SQL = SQL.replace("%3", precioVenta.getText().toString());
            SQL = SQL.replace("%4", precioRenta.getText().toString());
            SQL = SQL.replace("%5", fechaTransaccion.getText().toString());
            SQL = SQL.replace("%6", id[sp.getSelectedItemPosition()]);

            tabla.execSQL(SQL);

            Toast.makeText(this, "Se guardó registro", Toast.LENGTH_LONG).show();

            identificacion.setText("");
            domicilio.setText("");
            precioVenta.setText("");
            precioRenta.setText("");
            fechaTransaccion.setText("");

            tabla.close();

        }catch (Exception e){
            Toast.makeText(this, "ERROR: No se pudo guardar", Toast.LENGTH_LONG).show();
        }
    }



}
