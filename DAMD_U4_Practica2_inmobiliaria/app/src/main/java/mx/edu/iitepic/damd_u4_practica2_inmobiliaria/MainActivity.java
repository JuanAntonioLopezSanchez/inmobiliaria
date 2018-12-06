package mx.edu.iitepic.damd_u4_practica2_inmobiliaria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button propietario,  inmueble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        propietario = findViewById(R.id.propietario);
        inmueble = findViewById(R.id.inmobiliaria);

        propietario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent otraVentana = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(otraVentana);

            }
        });

        inmueble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent otraVentana = new Intent(MainActivity.this, Main3Activity.class);
                startActivity(otraVentana);

            }
        });

    }
}
