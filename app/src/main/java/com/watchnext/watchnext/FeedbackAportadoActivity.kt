package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class FeedbackAportadoActivity : AppCompatActivity() {

    private var dissatisfiedImage: ImageView? = null
    private var dissatisfiedImageBorder: ImageView? = null
    private var neutralImage: ImageView? = null
    private var neutralImageBorder: ImageView? = null
    private var satisfiedImage: ImageView? = null
    private var satisfiedImageBorder: ImageView? = null

    private var tareaTextView: TextView? = null
    private var satisfaccionButton: Button? = null

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
    private val codOperario = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalizar_tarea)

        // Get nombreTarea y codOperario
        // TODO - get operario, nombreTarea e IDTarea from intent
        /*
        val objetoIntent: Intent = intent
        var codOperario = objetoIntent.getStringExtra("operario")
        */

        // TODO - set tareaTextView = nombreTarea

        // Get resources from R.class
        dissatisfiedImage = findViewById(R.id.imageViewDissatisfied)
        dissatisfiedImageBorder = findViewById(R.id.imageViewDissatisfiedBorder)
        neutralImage = findViewById(R.id.imageViewNeutral)
        neutralImageBorder = findViewById(R.id.imageViewNeutralBorder)
        satisfiedImage = findViewById(R.id.imageViewSatisfied)
        satisfiedImageBorder = findViewById(R.id.imageViewSatisfiedBorder)
        tareaTextView = findViewById(R.id.tareaTextView)
        satisfaccionButton = findViewById(R.id.buttonEnviarNivelSatisfaccion)

        // Set image borders' visibility
        dissatisfiedImageBorder!!.visibility = View.INVISIBLE
        satisfiedImageBorder!!.visibility = View.INVISIBLE

        // Dissatisfied image selected
        dissatisfiedImage!!.setOnClickListener {
            nivelSatisfaccion = DISSATISFIED
            dissatisfiedImageBorder!!.visibility = View.VISIBLE
            satisfiedImageBorder!!.visibility = View.INVISIBLE
            neutralImageBorder!!.visibility = View.INVISIBLE
        }

        // Satisfied image selected
        satisfiedImage!!.setOnClickListener {
            nivelSatisfaccion = SATISFIED
            dissatisfiedImageBorder!!.visibility = View.INVISIBLE
            satisfiedImageBorder!!.visibility = View.VISIBLE
            neutralImageBorder!!.visibility = View.INVISIBLE
        }

        // Neutral image selected
        neutralImage!!.setOnClickListener {
            nivelSatisfaccion = NEUTRAL
            dissatisfiedImageBorder!!.visibility = View.INVISIBLE
            satisfiedImageBorder!!.visibility = View.INVISIBLE
            neutralImageBorder!!.visibility = View.VISIBLE
        }
    }

    fun satisfactionButtonClicked(view: View) {
        // TODO - enviar nivel de satisfacción a firebase usando codOperario
        val intent = Intent(this, AceptarTareaActivity::class.java)
        intent.putExtra("operario", "" + codOperario)
        startActivity(intent, Bundle())
    }

    companion object {
        private val DISSATISFIED = -1
        private val NEUTRAL = 0
        private val SATISFIED = 1
    }
}
