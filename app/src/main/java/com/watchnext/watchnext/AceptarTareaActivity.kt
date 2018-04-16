package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
        var CodOperario = objetoIntent.getStringExtra("operario")
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
        val textView = findViewById<TextView>(R.id.time_textView)
        textView.text = strDate
    }

    private fun getTarea(CodOperario:String) {
        var operario:DocumentSnapshot
        var tarea:DocumentSnapshot
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var operariosAsignados =db.collection("asignadas").get()
        val operariosRef = db.collection("operarios")
        val operarios = operariosRef.get()
        var encontrado=false

        var firebaseFirestore :FirebaseFirestoreException

        var doc :DocumentSnapshot
        var tar :DocumentSnapshot
        var queryOperario =db.collection("asignadas").whereEqualTo("id",CodOperario).get().addOnCompleteListener({ task: Task<QuerySnapshot> ->
            doc=task.getResult().documents.get(0) as DocumentSnapshot
            Log.w("ATA-QUERY-OP", "id documento: " + doc.get("id"))

            db.collection("asignadas").document(CodOperario).collection("Tareas").whereEqualTo("pausada",false).get().addOnCompleteListener({ task: Task<QuerySnapshot> ->
                Log.w("ATA-QUERY-Tarea", "Tamaño coleccion tareas: " + task.getResult().documents.isEmpty())
                if(!task.getResult().documents.isEmpty()) {
                    encontrado=true
                    tar = task.getResult().documents.get(0) as DocumentSnapshot
                    Log.w("ATA-QUERY-Tarea", "id tarea: " + tar.get("id"))
                    val intent = Intent(this, TareaEnEjecucionActivity::class.java)
                    intent.putExtra("operario", CodOperario)
                    intent.putExtra("IDtarea", tar.get("id").toString())
                    startActivity(intent, Bundle())
                }
            })
            val childEventListener =  object: EventListener<DocumentSnapshot>{
                override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
                    Log.w("ATA-QUERY-Tarea", "Cambio " + p0?.toString())
                    db.collection("asignadas").document(CodOperario).collection("Tareas").whereEqualTo("pausada",false).get().addOnCompleteListener({task:Task<QuerySnapshot> ->
                        Log.w("ATA-QUERY-Tarea", "Tamaño coleccion tareas: " + task.getResult().documents.isEmpty())
                        if(!task.getResult().documents.isEmpty()) {
                            mostrarTarea(task.getResult().documents.get(0) as DocumentSnapshot, doc)
                        }else{
                            Log.w("ATA-QUERY-Tarea", "No pasa nada.... ")
                        }
                    })
                }
            }
            db.collection("asignadas").document(CodOperario).addSnapshotListener(childEventListener)
        })
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
                        operarios.addOnSuccessListener { snapshot ->
                            for (doc in snapshot.documents) {
                                if (CodOperario.equals(doc.get("id").toString())) {
                                    operario = doc
                                    mostrarTarea(document, operario)
                                    break
                                } else {
                                    nombreTarea_textView.text = "Usuario no encontrado en la base de datos"
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
        var operariosAsignadosRef =db.collection("asignadas")
        var operario = Operario(op.get("id") as Number, true, op.get("etiquetas") as ArrayList<String>, db.collection("operarios").document(CodOperario).collection("Tareas"))
        var tarea = Tarea(ta)
        operariosAsignadosRef.document(operario.id.toString()).set(operario.toStringMap())
        operariosAsignadosRef.document(operario.id.toString()).update(operario.toArrayMap())
        nombreTarea_textView.text = tarea.titulo.toString()
        button_aceptarTarea.setActivated(true)
        button_aceptarTarea.setOnClickListener {
            operariosAsignadosRef.document(operario.id.toString()).collection("Tareas").document(tarea.id.toString()).set(tarea.toMap())
            operariosAsignadosRef.document(operario.id.toString()).collection("Tareas").document(tarea.id.toString()).update(tarea.toArrayMap())
            var h_inicio = mapOf<String,Any>("h_inicio" to Timestamp(System.currentTimeMillis()))
            Log.w("ATA-timestamp", "timestamp: "+h_inicio.get("h_inicio"))
            operariosAsignadosRef.document(operario.id.toString()).collection("Tareas").document(tarea.id.toString()).update(h_inicio)
            val intent = Intent(this, TareaEnEjecucionActivity::class.java)
            intent.putExtra("operario", CodOperario)
            intent.putExtra("IDtarea", tarea.id.toString())
            startActivity(intent, Bundle())
        }
        button_aceptarTarea.isClickable=true
        button_aceptarTarea.visibility= View.VISIBLE


    }
}
