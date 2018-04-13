package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Display
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_tarea_en_ejecucion.*
import java.text.SimpleDateFormat
import java.util.Calendar

class TareaEnEjecucionActivity : AppCompatActivity() {

    // private Tarea tarea; // Obtener los datos de la tarea desde la vista AceptarTarea



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea_en_ejecucion)
        setContentView(R.layout.activity_aceptar_tarea)
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario")
        var IdTarea = objetoIntent.getStringExtra("IDtarea")

        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var tareasSinAsignarRef = db.collection("sinAsignar")
        var tareasSinAsignar = tareasSinAsignarRef.get()
//        nombreTarea_textView_EnEjecucion.text="HOLA SOY ABRY, TU AMIGO"
//        nombreTarea_textView = findViewById(R.id.nombreTarea_textView_EnEjeucion)
//        duracionTarea_textView = findViewById(R.id.duracionTarea_textView_EnEjeucion)
//        imageButton_verDetalles = findViewById(R.id.imageButton_verDetalles)
//        imageButton_finalizarTarea = findViewById(R.id.imageButton_finalizarTarea)
//        imageButton_notificarIncidencia = findViewById(R.id.imageButton_notificarIncidencia)
//        displayTime()

    }

    private fun displayTime() {
        val calendar = Calendar.getInstance()
        val millis = calendar.timeInMillis

        //        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        //        String strDate = mdformat.format(calendar.getTime());
        val textView = findViewById<TextView>(R.id.time_textView)
        //        textView.setText(strDate);
    }

    fun verDetalles_buttonClicked(view: View) {
        // TODO - navigate to view details o mostrar un pop-up
        // el popup es mejor porque no hace falta navegar a otra pagina y puede mostrar toda la información de una tarea.
    }

    fun notificarIncidencia_buttonClicked(view: View) {
        // TODO - navigate to NotificarIncidencia
    }

    fun finalizarTarea_buttonClicked(view: View) {
        // TODO - navigate to FinalizarTarea o mostrar un pop-up
        // el popup es mejor porque no hace falta navegar a otra pagina y puede mostrar toda la información de una tarea.
    }

}
