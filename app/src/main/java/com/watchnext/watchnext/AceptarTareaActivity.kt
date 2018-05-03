package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_aceptar_tarea.*
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Document
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

class AceptarTareaActivity : AppCompatActivity() {

    /* Por defecto, el botón de 'Aceptar tarea' está inhabilitado,
     * y en nombre tarea y duración tarea se muestran los siguientes mensajes:
     * 'En este momento no hay tareas disponibles' y
     * Tiempo de espera hasta recibir tarea: DESCONOCIDO'.
     *
     * Cuando se recibe la tarea se cambian estos campos con los datos de la tarea
     * y se activa el botón de 'Aceptar tarea'
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aceptar_tarea)
        button_aceptarTarea.isClickable=false
        button_aceptarTarea.visibility= View.INVISIBLE
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario").toInt()
        Log.w("ATA-COMPROBANDO", "CodOperario: " +CodOperario)
        nombreTarea_textView.text = "Estamos buscando tareas disponibles..."
        duracionTarea_textView.text = "Tiempo de espera hasta recibir tarea: DESCONOCIDO"
        button_aceptarTarea.isActivated = false
        displayTime()
        getTarea(CodOperario)
    }

    private fun displayTime() {
        val calendar = Calendar.getInstance()
        val mdformat = SimpleDateFormat("HH:mm")
        val strDate = mdformat.format(calendar.time)
        time_textView.text = strDate
    }

    private fun getTarea(CodOperario:Number) {

        var operario:DocumentSnapshot
        var tarea:DocumentSnapshot
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var operariosConectados =db.collection("operariosConectados").get()
        val operariosRef = db.collection("operarios")
        val operarios = operariosRef.get()
        var encontrado=false

        var firebaseFirestore :FirebaseFirestoreException

        var doc :DocumentSnapshot
        var tar :DocumentSnapshot
        logout_button.setOnClickListener({
            FirebaseFirestore.getInstance().collection("operariosConectados").document(CodOperario.toString()).update("conectado" , false)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent, Bundle())
        })
        var queryOperario =db.collection("operariosConectados").whereEqualTo("id",CodOperario).get().addOnCompleteListener({ task: Task<QuerySnapshot> ->
            Log.w("ATA-Tarea", "tarea: " +task.getResult().isEmpty())

            if(!task.getResult().isEmpty()){
                FirebaseFirestore.getInstance().collection("operariosConectados").document(CodOperario.toString()).update("conectado" , true)
                doc=task.getResult().documents.get(0) as DocumentSnapshot
                Log.w("ATA-QUERY-OP", "id documento: " + doc.get("id"))

                db.collection("operariosConectados").document(CodOperario.toString()).collection("Tareas").whereEqualTo("pausada",false).whereEqualTo("h_fin",0).get().addOnCompleteListener({ task: Task<QuerySnapshot> ->
                    Log.w("ATA-QUERY-Tarea", "Tamaño coleccion tareas: " + task.getResult().documents.size)
                    if (!task.getResult().documents.isEmpty()) {
                        encontrado = true
                        tar = task.getResult().documents.get(0) as DocumentSnapshot
                        Log.w("ATA-QUERY-Tarea", "id tarea: " + tar.get("id"))
                        val intent = Intent(this, TareaEnEjecucionActivity::class.java)
                        intent.putExtra("operario", CodOperario.toString())
                        intent.putExtra("IDtarea", tar.get("id").toString())
                        startActivity(intent, Bundle())
                        finish()
                    }
                })
                val childEventListener =  object: EventListener<DocumentSnapshot>{
                    override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                        Log.w("ATA-QUERY-Tarea", "Cambio " + p0?.toString())
                        var numTareas = 0
                        db.collection("operariosConectado").document(CodOperario.toString()).collection("Tareas").whereEqualTo("pausada",false).whereEqualTo("h_fin",0).get().addOnCompleteListener({task:Task<QuerySnapshot> ->
                            Log.w("ATA-QUERY-Tarea", "Tamaño coleccion tareas: " + task.getResult().documents.size)
                            if(task.getResult().documents.size>numTareas) {
                                mostrarTarea(task.result.documents.get(0) as DocumentSnapshot, doc)
                                numTareas=task.getResult().documents.size
                            }else{
                                Log.w("ATA-QUERY-Tarea", "No pasa nada.... ")
                            }
                        })
                    }
                }
            db.collection("operariosConectados").document(CodOperario.toString()).addSnapshotListener(childEventListener)
        }})
        if(!encontrado) {
            var tareasSinAsignarRef = db.collection("sinAsignar")
            var tareasSinAsignar = tareasSinAsignarRef.get()
            tareasSinAsignar.addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    var etiquetas = document.get("etiquetas") as ArrayList<String> //Esto es porque el tipo de dato en firebase es "Array"
                    Log.w("ATA-ETIQUETAS", "etiquetas: " + etiquetas)
                    if (etiquetas.size == 0 && document.get("asignable") as Boolean) { // Esto es porque el tipo de dato en Firebase es "Boolean"
                        //TODO consultar los operarios que tienen etiquetas con las faenas que tienen etiqueta
                        Log.w("ATA-MODIFICAMOS", "Documento: " + document.get("id") + " Modificamos con exito el valor?" + tareasSinAsignarRef.document(document.get("id").toString()).update("asignable", false))
                        operarios.addOnSuccessListener { snap ->
                            for (doc in snap.documents) {
                                Log.w("ATA-MODIFICAMOS", "Documento: " +CodOperario.javaClass +", " + doc.get("id").javaClass +", " + (CodOperario==(doc.get("id"))))
                                if (CodOperario.toString().equals(doc.get("id").toString())) {
                                    var operariosConectadosRef =db.collection("operariosConectados")
                                    var tareas = doc.get("tareas") as ArrayList<Number>
                                    tareas.add(document.get("id") as Number)
                                    operariosConectadosRef.document(doc.id).update(mapOf("tareas" to tareas))
                                    mostrarTarea(document, doc)
                                    break
                                } else {
                                    nombreTarea_textView.text = "Usuario no encontrado en la base de datos"
                                    Log.w("ATA-MODIFICAMOS", "Documento: " + document.get("id") + " Modificamos con exito el valor?" + tareasSinAsignarRef.document(document.get("id").toString()).update("asignable", true))
                                }
                            }
                        }
                        break
                    } else {
                        nombreTarea_textView.text = "En este momento no hay tareas disponibles"
                    }

                }
            }
        }
    }

    private fun mostrarTarea(ta :DocumentSnapshot, op :DocumentSnapshot){
        //TODO arreglar este codio para que pueda almacenar bien las tareas de un operario ya en funcionamiento
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario")
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var operariosAsignadosRef =db.collection("operariosConectados")
        var tarea = Tarea(ta)
        nombreTarea_textView.text = tarea.titulo.toString()
        duracionTarea_textView.text = tarea.descripcion
        button_aceptarTarea.setActivated(true)
        button_aceptarTarea.setOnClickListener {
            operariosAsignadosRef.document(op.get("id").toString()).collection("Tareas").document(tarea.id.toString()).set(tarea.toMap())
            operariosAsignadosRef.document(op.get("id").toString()).collection("Tareas").document(tarea.id.toString()).update(tarea.toArrayMap())
            var h_inicio = mapOf("h_inicio" to Timestamp(System.currentTimeMillis()).nanos)
            Log.w("ATA-timestamp", "timestamp: "+h_inicio.get("h_inicio"))
            operariosAsignadosRef.document(op.get("id").toString()).collection("Tareas").document(tarea.id.toString()).update(h_inicio)
            val intent = Intent(this, TareaEnEjecucionActivity::class.java)
            intent.putExtra("operario", CodOperario.toString())
            intent.putExtra("IDtarea", tarea.id.toString())
            startActivity(intent, Bundle())
            finish()
        }
        button_aceptarTarea.isClickable=true
        button_aceptarTarea.visibility= View.VISIBLE
    }

    override fun onBackPressed() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("No puedes salir así")
        var errorMessage = "Pulsa en Logout antes por favor"
        alertDialogBuilder.setMessage(errorMessage)
        var a = alertDialogBuilder.create()
        a.show()
    }
}
