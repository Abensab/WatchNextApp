package com.watchnext.watchnext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TareaEnEjecucionActivity extends AppCompatActivity {

    // private Tarea tarea; // Obtener los datos de la tarea desde la vista AceptarTarea
    TextView nombreTarea_textView, duracionTarea_textView;
    ImageButton imageButton_verDetalles, imageButton_notificarIncidencia, imageButton_finalizarTarea;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarea_en_ejecucion);
        nombreTarea_textView = findViewById(R.id.nombreTarea_textView_EnEjeucion);
        duracionTarea_textView = findViewById(R.id.duracionTarea_textView_EnEjeucion);
        imageButton_verDetalles = findViewById(R.id.imageButton_verDetalles);
        imageButton_finalizarTarea = findViewById(R.id.imageButton_finalizarTarea);
        imageButton_notificarIncidencia = findViewById(R.id.imageButton_notificarIncidencia);

        displayTime();
    }

    private void displayTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strDate = mdformat.format(calendar.getTime());
        TextView textView = findViewById(R.id.time_textView);
        textView.setText(strDate);
    }

    public void verDetalles_buttonClicked(View view) {
        // TODO - navigate to view details o mostrar un pop-up
        // el popup es mejor porque no hace falta navegar a otra pagina y puede mostrar toda la información de una tarea.
    }

    public void notificarIncidencia_buttonClicked(View view) {
        // TODO - navigate to NotificarIncidencia
    }

    public void finalizarTarea_buttonClicked(View view) {
        // TODO - navigate to FinalizarTarea o mostrar un pop-up
        // el popup es mejor porque no hace falta navegar a otra pagina y puede mostrar toda la información de una tarea.
    }

}
