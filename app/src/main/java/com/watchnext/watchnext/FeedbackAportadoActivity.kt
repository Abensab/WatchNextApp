package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.UiThread
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_finalizar_tarea.*
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class FeedbackAportadoActivity : AppCompatActivity() {

    private var satisfaccionButton: Button? = null
    var respuesta = ""
    var URL_FINALIZAR_TAREA:String = "https://us-central1-wane-3630f.cloudfunctions.net/finalizarTarea?" //Devuelve el id de la primera tarea que tiene asignada o un texto para decir que no tiene
    var tarea = JSONObject()
    var idOperario = ""
    internal val Background = newFixedThreadPoolContext(2, "bg")
    // TODO - recibir nombre tarea desde actividad anterior

    /* Niveles de satisfacción:
     *
     * DISSATISFIED: -1
     * NEUTRAL: 0
     * SATISFIED: 1
     *
     * Default: NEUTRAL
     */
    private var nivelSatisfaccion = NEUTRAL

    // id operario


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalizar_tarea)
        val objetoIntent: Intent = intent
        tarea = JSONObject(objetoIntent.getStringExtra("tarea"))
        idOperario = objetoIntent.getStringExtra("operario")

//        setContentView(R.layout.activity_finalizar_tarea)
        loadingPanel.visibility = View.INVISIBLE
        // Get nombreTarea y codOperario
        // TODO - get operario, nombreTarea e IDTarea from intent
        /*
        val objetoIntent: Intent = intent
        var codOperario = objetoIntent.getStringExtra("operario")
        */

        // TODO - set tareaTextView = nombreTarea

        // Get resources from R.class

        satisfaccionButton = findViewById(R.id.buttonEnviarNivelSatisfaccion)

        // Set image borders' visibility
        imageViewDissatisfiedBorder.visibility = View.INVISIBLE
        imageViewSatisfiedBorder.visibility = View.INVISIBLE

        // Dissatisfied image selected
        imageViewDissatisfied.setOnClickListener {
            nivelSatisfaccion = DISSATISFIED
            imageViewDissatisfiedBorder.visibility = View.VISIBLE
            imageViewSatisfiedBorder.visibility = View.INVISIBLE
            imageViewNeutralBorder.visibility = View.INVISIBLE
        }

        // Satisfied image selected
        imageViewSatisfied.setOnClickListener {
            nivelSatisfaccion = SATISFIED
            imageViewDissatisfiedBorder.visibility = View.INVISIBLE
            imageViewSatisfiedBorder.visibility = View.VISIBLE
            imageViewNeutralBorder.visibility = View.INVISIBLE
        }

        // Neutral image selected
        imageViewNeutral.setOnClickListener {
            nivelSatisfaccion = NEUTRAL
            imageViewDissatisfiedBorder.visibility = View.INVISIBLE
            tareaTextView.visibility = View.INVISIBLE
            imageViewNeutralBorder.visibility = View.VISIBLE
        }
    }

    fun satisfactionButtonClicked(view: View) {
        // TODO - enviar nivel de satisfacción a firebase usando codOperario
        var intento = this
        doAsync {
            doGet(URL_FINALIZAR_TAREA + "idOperario=" + idOperario+"&idTarea=" + tarea.get("id")+"&satisfaccion="+nivelSatisfaccion)
            uiThread {
                val intent = Intent(intento, AceptarTareaActivity::class.java)
                intent.putExtra("operario", "" + idOperario)
                startActivity(intent, Bundle())
                finish()
            }
        }
        loadingPanel.visibility = View.VISIBLE
        imageViewNeutral.visibility=View.INVISIBLE
        imageViewDissatisfied.visibility=View.INVISIBLE
        imageViewSatisfied.visibility=View.INVISIBLE
        imageViewDissatisfiedBorder.visibility = View.INVISIBLE
        imageViewSatisfiedBorder.visibility = View.INVISIBLE
        imageViewNeutralBorder.visibility = View.INVISIBLE
    }

    companion object {
        private val DISSATISFIED = -1
        private val NEUTRAL = 0
        private val SATISFIED = 1
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
                Log.e("Error" ,"Error with "+ {e.message})
            }
            Log.w("GET RESPONSE:", message.toString())
        }catch (e: Exception) {
            Log.e("Error" ,"Error with Thread: "+{e.message})
        }
        Log.w("GET RESPONSE:", message.toString())
        respuesta=message
    }
}
