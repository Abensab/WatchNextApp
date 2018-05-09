package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_aceptar_tarea.*
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar

class AceptarTareaActivity : AppCompatActivity() {

    var URL_ASIGNAR_TAREA:String = " https://us-central1-wane-3630f.cloudfunctions.net/asignarTarea?" //Devuelve el id de la primera tarea que tiene asignada o un texto para decir que no tiene
    var URL_SOLICITAR_TAREA:String = "https://us-central1-wane-3630f.cloudfunctions.net/solicitarDatosTarea3?" //Devuelve el id de la primera tarea que tiene asignada o un texto para decir que no tiene
    internal val Background = newFixedThreadPoolContext(3, "bg")

    var respuesta = ""

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
        loadingPanelAT.visibility = View.INVISIBLE
        button_aceptarTarea.isClickable=false
        button_aceptarTarea.visibility= View.INVISIBLE
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario").toInt()
        Log.w("ATA-COMPROBANDO", "CodOperario: " +CodOperario)
        nombreTarea_textView.text = "Estamos buscando tareas disponibles..."
        duracionTarea_textView.text = "Tiempo de espera hasta recibir tarea: DESCONOCIDO"
        button_aceptarTarea.isActivated = false
        getTarea(CodOperario)
    }


    private fun getTarea(CodOperario:Number) {

        loadingPanelAT.visibility = View.VISIBLE
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var operariosConectadosRef =db.collection("operariosConectados")
        val operariosRef = db.collection("operarios")
        val operarios = operariosRef.get()

        logout_button.setOnClickListener({
            FirebaseFirestore.getInstance().collection("operariosConectados").document(CodOperario.toString()).update("conectado" , false)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent, Bundle())
        })

        //TODO Empezamos por aqui...
        val job = launch(Background) {
           doGet(URL_ASIGNAR_TAREA + "id=" + CodOperario)
        }
        while (job.isActive) {}//TODO: esto esta feo
        try{    //Tarea asignada correctamente
            Log.i("TRY" , "mensaje " + respuesta)
            if(respuesta.contains("type")){
                if(JSONObject(respuesta).get("type")!=null) {
                    Log.i("Mensaje", respuesta.toString())
                    val job2 = launch(Background) {
                        doGet(URL_SOLICITAR_TAREA + "id=" + CodOperario)
                    }
                    while (job2.isActive) {
                    }//TODO: esto esta feo
                    job2.invokeOnCompletion {
                        visualizarTarea(JSONObject(respuesta), CodOperario)
                    }
                }
            }else{
                duracionTarea_textView.visibility=View.INVISIBLE
                Log.i("NOTICE" , "No se ha devuleto ninguna tarea " + respuesta)
                if(JSONObject(respuesta).get("error").equals("No hay tareas")){
                    operariosConectadosRef.document(CodOperario.toString()).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                        loadingPanelAT.visibility = View.VISIBLE
                        if(firebaseFirestoreException!=null) {
                            Log.e("Listen failed: ",firebaseFirestoreException.toString())
                        }
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            Log.w("Current data ", documentSnapshot.getData().toString())
                            doAsync {
                                doGet(URL_SOLICITAR_TAREA + "id=" + CodOperario)
                                uiThread {
                                    if (respuesta!=null && !respuesta.contentEquals("error")&& respuesta!="") {
                                        Log.e("WTF_Entra", respuesta)
                                        System.out.println("Respuesta: " + respuesta)
                                        visualizarTarea(JSONObject(respuesta), CodOperario)//TODO: aqui explota
                                    }
                                }
                            }
                        } else {
                            System.out.print("Current data: null")
                        }

                    }
                }
            }
        }catch (e: Exception){              //No hay tareas -> Pongo escuchador
            Log.e("ERROR", e.printStackTrace().toString())
        }
    }

    private fun mostrarTarea(ta :Tarea, op : Number){
        //TODO arreglar este codio para que acepte la tarea mediante una función en firebase
        val objetoIntent : Intent =intent
        var CodOperario = objetoIntent.getStringExtra("operario")
        var db = FirebaseFirestore.getInstance()//referencia de firestore
        var tareasAsignadasRef =db.collection("asignadas")
        loadingPanelAT.visibility = View.INVISIBLE
        var tarea = ta
        nombreTarea_textView.text = tarea.titulo.toString()
        duracionTarea_textView.visibility=View.VISIBLE
        duracionTarea_textView.text = tarea.descripcion
        button_aceptarTarea.setActivated(true)
        button_aceptarTarea.setOnClickListener {
            var h_inicio = mapOf("h_inicio" to Timestamp(System.currentTimeMillis()).nanos)
            Log.w("ATA-timestamp", "timestamp: "+h_inicio.get("h_inicio"))
            tareasAsignadasRef.document(tarea.id.toString()).update(h_inicio)
            val intent = Intent(this, TareaEnEjecucionActivity::class.java)
            intent.putExtra("operario", CodOperario.toString())
            intent.putExtra("tarea", tarea.toJSONObject().toString())
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
    private fun doGet(url: String) {
        var message = ""
        try {
            try {
                var buffer = URL(url).openStream().bufferedReader()
                var line = buffer.readLine()

                while (line != null) {
                    message += line
                    line = buffer.readLine()
                }
            } catch (e: IOException) {
                Log.e("Error", "Error with " + { e.message })
            }
            Log.w("GET RESPONSE:", message)
        } catch (e: Exception) {
            Log.e("Error", "Error with Thread: " + { e.message })
        }
        Log.w("GET RESPONSE:", message)
        respuesta = message
    }

    private fun visualizarTarea(res :JSONObject, codOperario : Number){
        try{
            var tarea = res.get("tarea") as JSONObject
            if(tarea!=null){
                Log.i("TAREA" , tarea.toString())
                mostrarTarea(Tarea(tarea),codOperario)
            }else{
                Log.e("WTF-ERROR" , "La respuesta no tiene respuesta" + tarea.toString())
            }
        }catch (e: Exception){
            Log.e("ERROR" , "No se ha devuleto ninguna tarea: " + res.get("error"))
        }
    }
}
