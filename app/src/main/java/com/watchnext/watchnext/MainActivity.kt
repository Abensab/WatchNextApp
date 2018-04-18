package com.watchnext.watchnext

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.util.Log


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()//referencia de firestore
        val operariosRef = db.collection("operarios")
        val operarios = operariosRef.get()
        val alertDialogBuilder = AlertDialog.Builder(this)

        button_login.setOnClickListener {
            var CodOperario : String = editText_name.text.toString()
            Log.w("MA-COMPROBANDO", "CodOperario: " +CodOperario)
            Log.w("INTRODUCED", "documento: " + editText_name.text + ", pass: " + editText_passwd.text)
            if ((CodOperario.length > 0) and (editText_passwd.length() > 0)) {
                var codigo = 0 //0->usr y pass incorrecto, 1->pass incorrecto, 2->correcto
                operarios.addOnSuccessListener { snapshot ->
                    for (document in snapshot.documents) {
                        Log.w("ITERACION", "documento: " + document.get("id") + ", pass: " + document.get("pass"))
                        Log.w("COMPROBANDO", "documento: " + editText_name.text.toString().equals(document.get("id").toString()) +
                                ", pass: " + editText_passwd.text.toString().equals(document.get("pass").toString()))
                        if (editText_name.text.toString().equals(document.get("id").toString())) {
                            if (editText_passwd.text.toString().equals(document.get("pass").toString())) {
                                val successDialogBuilder = AlertDialog.Builder(this)
                                Log.w("SUCCESSFUL", "documento: " + document.get("id") + ", pass: " + document.get("pass"))
                                successDialogBuilder.setTitle("Bienvienid@ " + editText_name.text)
                                successDialogBuilder.setMessage("Gracias por usar WatchNext")
                                successDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { button, whichButton ->
                                    val intent = Intent(this, AceptarTareaActivity::class.java)
                                    intent.putExtra("operario", CodOperario)
                                    startActivity(intent, Bundle())

                                })
                                val b = successDialogBuilder.create()
                                b.show()
                                codigo = 2
                                break
                            }
                            codigo = 1
                            break
                        }
                    }
                    if (codigo == 0) {
                        alertDialogBuilder.setMessage("Nombre de usuario incorrecto")
                    } else if (codigo == 1) {
                        alertDialogBuilder.setMessage("Contraseña incorrecta")
                    }
                    if (codigo != 2) {
                        alertDialogBuilder.setTitle("Error al acceder al sistema")
                        var b = alertDialogBuilder.create()
                        b.show()
                    }
                }
            } else {
                alertDialogBuilder.setTitle("Error al acceder al sistema")
                var errorMessage = "Han ocurrido los siguientes errores:"
                if (editText_name.length() <= 0)
                    errorMessage += "\n\tEl nombre de usuario no puede estar vacío."
                if (editText_passwd.length() <= 0)
                    errorMessage += "\n\tLa contraseña no puede estar vacía."
                alertDialogBuilder.setMessage(errorMessage)
                var a = alertDialogBuilder.create()
                a.show()
            }

        }
    }
}

