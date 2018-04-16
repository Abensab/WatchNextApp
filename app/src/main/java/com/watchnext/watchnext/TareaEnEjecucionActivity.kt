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
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario")
        var IdTarea = objetoIntent.getStringExtra("IDtarea")

        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var tareasSinAsignarRef = db.collection("sinAsignar")
        var tareasSinAsignar = tareasSinAsignarRef.get()
        displayTime()

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
