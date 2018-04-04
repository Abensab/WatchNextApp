package com.watchnext.watchnext

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent




class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val firestore = FirebaseFirestore.getInstance()//referencia de firestore

        var failedLogin = true

        button_login.setOnClickListener {

            val alertDialog = AlertDialog.Builder(this).create()
            if( (editText_name.length()>0) and (editText_passwd.length()>0)){
                firestore.collection("operarios").get().addOnSuccessListener { snapshot->
                    for(document in snapshot.documents){
                        if(editText_name.text.equals(document.id) and (editText_name.text.equals(document.data["pass"]))) {
                            failedLogin = false
                            alertDialog.setTitle("Bienvienid@ " + editText_name.text)
                            alertDialog.setMessage("Gracias por usar WatchNext")
                        }
                        else{
                            alertDialog.setTitle("Error al acceder al sistema")
                            alertDialog.setMessage("Nombre de usuario o contraseña incorrectos")
                        }
                    }
                }
            }
            else {
                alertDialog.setTitle("Error al acceder al sistema")
                var errorMessage = "Han ocurrido los siguientes errores:"
                if (editText_name.length() <= 0)
                    errorMessage += "\n\tEl nombre de usuario no puede estar vacío."
                if(editText_passwd.length() <= 0)
                    errorMessage += "\n\tLa contraseña no puede estar vacía."
                alertDialog.setMessage(errorMessage)
            }
            alertDialog.show()
            if(!failedLogin){
                //TODO redireccionamos a la otra pagina
                val intent = Intent(this, AceptarTareaActivity::class.java)
                startActivity(intent)
            }
        }

    }
}
