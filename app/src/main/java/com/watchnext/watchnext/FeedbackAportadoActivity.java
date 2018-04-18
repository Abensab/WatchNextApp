package com.watchnext.watchnext;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedbackAportadoActivity extends AppCompatActivity {

    private static int DISSATISFIED = -1;
    private static int NEUTRAL = 0;
    private static int SATISFIED = 1;

    private ImageView dissatisfiedImage, dissatisfiedImageBorder;
    private ImageView neutralImage, neutralImageBorder;
    private ImageView satisfiedImage, satisfiedImageBorder;

    private TextView tareaTextView;
    private Button satisfaccionButton;

    // TODO - recibir nombre tarea desde actividad anterior

    /* Niveles de satisfacción:
     *
     * DISSATISFIED: -1
     * NEUTRAL: 0
     * SATISFIED: 1
     *
     * Default: NEUTRAL
     */
    private int nivelSatisfaccion = NEUTRAL;

    // id operario
    private int codOperario = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_aportado);

        // Get nombreTarea y codOperario
        // TODO - get operario, nombreTarea e IDTarea from intent
        /*
        val objetoIntent: Intent = intent
        var codOperario = objetoIntent.getStringExtra("operario")
        */

        // TODO - set tareaTextView = nombreTarea

        // Get resources from R.class
        dissatisfiedImage = findViewById(R.id.imageViewDissatisfied);
        dissatisfiedImageBorder = findViewById(R.id.imageViewDissatisfiedBorder);
        neutralImage = findViewById(R.id.imageViewNeutral);
        neutralImageBorder = findViewById(R.id.imageViewNeutralBorder);
        satisfiedImage = findViewById(R.id.imageViewSatisfied);
        satisfiedImageBorder = findViewById(R.id.imageViewSatisfiedBorder);
        tareaTextView = findViewById(R.id.tareaTextView);
        satisfaccionButton = findViewById(R.id.buttonEnviarNivelSatisfaccion);

        // Set image borders' visibility
        dissatisfiedImageBorder.setVisibility(View.INVISIBLE);
        satisfiedImageBorder.setVisibility(View.INVISIBLE);

        // Dissatisfied image selected
        dissatisfiedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nivelSatisfaccion = DISSATISFIED;
                dissatisfiedImageBorder.setVisibility(View.VISIBLE);
                satisfiedImageBorder.setVisibility(View.INVISIBLE);
                neutralImageBorder.setVisibility(View.INVISIBLE);
            }
        });

        // Satisfied image selected
        satisfiedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nivelSatisfaccion = SATISFIED;
                dissatisfiedImageBorder.setVisibility(View.INVISIBLE);
                satisfiedImageBorder.setVisibility(View.VISIBLE);
                neutralImageBorder.setVisibility(View.INVISIBLE);
            }
        });

        // Neutral image selected
        neutralImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nivelSatisfaccion = NEUTRAL;
                dissatisfiedImageBorder.setVisibility(View.INVISIBLE);
                satisfiedImageBorder.setVisibility(View.INVISIBLE);
                neutralImageBorder.setVisibility(View.VISIBLE);
            }
        });
    }

    public void satisfactionButtonClicked(View view) {
        // TODO - enviar nivel de satisfacción a firebase usando codOperario
        Intent intent = new Intent(this, AceptarTareaActivity.class);
        intent.putExtra("operario", "" + codOperario);
        startActivity(intent, new Bundle());
    }
}
