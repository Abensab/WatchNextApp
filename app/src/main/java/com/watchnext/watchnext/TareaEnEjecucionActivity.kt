package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.solver.widgets.Snapshot
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Display
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_aceptar_tarea.*
import kotlinx.android.synthetic.main.activity_tarea_en_ejecucion.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

class TareaEnEjecucionActivity : AppCompatActivity() {

    // private Tarea tarea; // Obtener los datos de la tarea desde la vista AceptarTarea



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tarea_en_ejecucion)
        val objetoIntent: Intent = intent
        var CodOperario = objetoIntent.getStringExtra("operario")
        var IdTarea = objetoIntent.getStringExtra("IDtarea")
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        Log.w("ATA-timestamp", "codOperario"+CodOperario+", idtarea: "+IdTarea)
        var tareaRef=db.collection("asignadas").document(CodOperario).collection("Tareas").document(IdTarea)
        db.collection("asignadas").document(CodOperario).collection("Tareas").document(IdTarea).get().addOnSuccessListener { snapshot ->
            nombreTarea_textView_EnEjecucion.text = snapshot.get("titulo") as String
            duracionTarea_textView_EnEjecucion.text = snapshot.get("descripcion") as String
        }
        

        imageButton_verDetalles.isClickable=false
        imageButton_notificarIncidencia.isClickable=false
        imageButton_verDetalles.visibility= View.VISIBLE

        imageButton_finalizarTarea.setOnClickListener {
            var h_fin = mapOf("h_fin" to Timestamp(System.currentTimeMillis()).nanos )
            tareaRef.update(h_fin)
            val intent = Intent(this, FeedbackAportadoActivity::class.java)
            intent.putExtra("operario", CodOperario)
            intent.putExtra("nombreTarea", nombreTarea_textView_EnEjecucion.text)
            intent.putExtra("IDTarea", IdTarea)
            startActivity(intent, Bundle())
            finish()
        }
        displayTime()
    }

    private fun displayTime() {
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("HH:mm")
        val strDate = mdformat.format(calendar.time)
        time_textView_EnEjecucion.text = strDate
    }
    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("No puedes salir")
        var errorMessage = "Termina la tarea por favor"
        alertDialogBuilder.setMessage(errorMessage)
        var a = alertDialogBuilder.create()
        a.show()
    }
}
