package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_tarea_en_ejecucion.*
import org.json.JSONObject
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
        var tarea : JSONObject = JSONObject(objetoIntent.getStringExtra("tarea"))
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var tareaRef=db.collection("asignadas").document(tarea.get("id").toString())
        Log.w("TEE-TAREA",tarea.toString())
//        db.collection("asignadas").document(IdTarea).get().addOnSuccessListener { snapshot ->
//            nombreTarea_textView_EnEjecucion.text = snapshot.get("titulo") as String
//            duracionTarea_textView_EnEjecucion.text = snapshot.get("descripcion") as String
//        }
//

        imageButton_verDetalles.isClickable=true
        imageButton_notificarIncidencia.isClickable=false
        imageButton_verDetalles.visibility= View.VISIBLE

        // Botón de reportar incidencia
        imageButton_notificarIncidencia.visibility= View.INVISIBLE
        textView_Incidencia.visibility=View.INVISIBLE


        imageButton_finalizarTarea.setOnClickListener {
            var h_fin = mapOf("h_fin" to Timestamp(System.currentTimeMillis()).nanos )
            tareaRef.update(h_fin)
            val intent = Intent(this, FeedbackAportadoActivity::class.java)
            intent.putExtra("operario", CodOperario)
            intent.putExtra("tarea", tarea.toString())
            startActivity(intent, Bundle())
            finish()
        }

        imageButton_verDetalles.setOnClickListener {
            val intent = Intent(this, VerDetallesActivity::class.java)
            intent.putExtra("nombreTarea", nombreTarea_textView_EnEjecucion.text)
            intent.putExtra("descripTarea", tarea.getString("descripcion"))
            startActivity(intent, Bundle())
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
