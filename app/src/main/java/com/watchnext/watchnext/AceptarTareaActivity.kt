package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_aceptar_tarea.*
import kotlinx.android.synthetic.main.activity_main.*
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

//        button_aceptarTarea.isEnabled=false;
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aceptar_tarea)
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario")
        Log.w("ATA-COMPROBANDO", "CodOperario: " +CodOperario)
//        var nombreTarea_textView: TextView
//        var duracionTarea_textView: TextView
//        var button_aceptarTare: Button
//        nombreTarea_textView = findViewById(R.id.nombreTarea_textView)
        nombreTarea_textView.text = "Estamos buscando tareas disponibles..."
//        duracionTarea_textView = findViewById(R.id.duracionTarea_textView)
        duracionTarea_textView.text = "Tiempo de espera hasta recibir tarea: DESCONOCIDO"
//        var button_aceptarTarea = findViewById(R.id.button_aceptarTarea)
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
        operariosAsignados.addOnSuccessListener { snapshot ->
            for (document in snapshot.documents  ) {
                Log.w("ATA-ITERACION", "documento: " + document.get("id"))
                if (CodOperario.equals(document.get("id"))) {
                    Log.w("ATA-OPERARIO-ENCONTRADO", "documento: " + document.get("id"))
                    encontrado = true
                    operario=document
                    operariosRef.document(operario.get("id").toString()).collection("tareas").get().addOnSuccessListener { snap->
                        for (doc in snap.documents){
                            mostrarTarea(doc,operario)
                            break
                        }
                    }
                    break
                }
            }
        }
        if(encontrado){
            //TODO lanzar a la pantalla de la tarea mostrar la tarea que habia antes y mostrar el boton de aceptar tarea
        }else{
            var tareasSinAsignarRef = db.collection("sinAsignar")
            var tareasSinAsignar = tareasSinAsignarRef.get()
            tareasSinAsignar.addOnSuccessListener { snapshot->
                for (document in snapshot.documents){
                    var etiquetas= document.get("etiquetas") as ArrayList<String> //Esto es porque el tipo de dato en firebase es "Array"
                    Log.w("ATA-ETIQUETAS", "etiquetas: " + etiquetas)
                    if(etiquetas.size==0 && document.get("asignable") as Boolean){ // Esto es porque el tipo de dato en Firebase es "Boolean"
                        //TODO consultar los operarios que tienen etiquetas con las faenas que tienen etiqueta
                        Log.w("ATA-MODIFICAMOS", "Documento: "+document.get("id")+" Modificamos con exito el valor?"+ tareasSinAsignarRef.document(document.get("id").toString()).update("asignable",false))
                        operarios.addOnSuccessListener { snapshot ->
                            for (doc in snapshot.documents) {
                                if (CodOperario.equals(doc.get("id").toString())) {
                                    operario = doc
                                    mostrarTarea(document,operario)
                                    break
                                }else{
                                    nombreTarea_textView.text = "Usuario no encontrado en la base de datos"
                                }
                            }
                        }
                        break
                    }else{
                        nombreTarea_textView.text = "En este momento no hay tareas disponibles"
                    }
                }
            }
        }
    }
    private fun mostrarTarea(tarea :DocumentSnapshot, op :DocumentSnapshot){
        //TODO arreglar este codio para que pueda almacenar bien las tareas de un operario ya en funcionamiento
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario")
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var operariosAsignadosRef =db.collection("asignadas")
        var operario = Operario(op.get("id") as Number, op.get("nombre") as String, op.get("apellidos") as String, op.get("etiquetas") as ArrayList<String>, op.get("pass") as String, db.collection("operarios").document(CodOperario).collection("Tareas"))

        operariosAsignadosRef.document(operario.id.toString()).set(operario.toStringMap())
        operariosAsignadosRef.document(operario.id.toString()).update(operario.toArrayMap())
        operariosAsignadosRef.document(operario.id.toString()).collection("Tareas").add(tarea)

        nombreTarea_textView.text = tarea.get("titulo").toString()
        button_aceptarTarea.setActivated(true)
        button_aceptarTarea.setOnClickListener {

            val intent = Intent(this, TareaEnEjecucionActivity::class.java)
            intent.putExtra("operario", CodOperario)
            intent.putExtra("IDtarea", tarea.get("id").toString())
            startActivity(intent, Bundle())
        }


    }

    //TODO darle funcionalidad al boton

}
