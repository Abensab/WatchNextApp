package com.watchnext.watchnext

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_ver_detalles.*

class VerDetallesActivity : AppCompatActivity() {

    @Override
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_detalles)

        val objetoIntent: Intent = intent
        var nombreTarea = objetoIntent.getStringExtra("nombreTarea")
        var descripTarea = objetoIntent.getStringExtra("descripTarea")

        nombreTarea_textView_verDetalles.text=nombreTarea
        detallesTarea_textView.text=descripTarea
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
