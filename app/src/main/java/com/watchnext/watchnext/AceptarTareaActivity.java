package com.watchnext.watchnext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AceptarTareaActivity extends AppCompatActivity {

    TextView nombreTarea_textView, duracionTarea_textView;
    Button  button_aceptarTare;

    /* Por defecto, el botón de 'Aceptar tarea' está inhabilitado,
     * y en nombre tarea y duración tarea se muestran los siguientes mensajes:
     * 'En este momento no hay tareas disponibles' y
     * Tiempo de espera hasta recibir tarea: DESCONOCIDO'.
     *
     * Cuando se recibe la tarea se cambian estos campos con los datos de la tarea
     * y se activa el botón de 'Aceptar tarea'
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aceptar_tarea);
        nombreTarea_textView = findViewById(R.id.nombreTarea_textView);
        nombreTarea_textView.setText("En este momento no hay tareas disponibles");
        duracionTarea_textView = findViewById(R.id.duracionTarea_textView);
        duracionTarea_textView.setText("Tiempo de espera hasta recibir tarea: DESCONOCIDO");
        button_aceptarTare = findViewById(R.id.button_aceptarTarea);
        button_aceptarTare.setActivated(false);
        displayTime();
    }

    private void displayTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strDate = mdformat.format(calendar.getTime());
        TextView textView = findViewById(R.id.time_textView);
        textView.setText(strDate);
    }

    // TODO - conexión a firebase para coger la tarea
    // TODO - si hay tarea, mostrar datos de la tarea y activar 'button_aceptarTarea'
    // button_aceptarTare.setActivated(true);
}
