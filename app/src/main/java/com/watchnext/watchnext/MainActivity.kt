package com.watchnext.watchnext

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var failedLogin = true

        button_login.setOnClickListener {

            val alertDialog = AlertDialog.Builder(this).create()

            if( (editText_name.length() <= 0) or (editText_passwd.length() <= 0) ) {
                alertDialog.setTitle("Error al acceder al sistema")
                var errorMessage = "Han ocurrido los siguientes errores:"
                if (editText_name.length() <= 0)
                    errorMessage += "\n\tEl nombre de usuario no puede estar vacío."
                if(editText_passwd.length() <= 0)
                    errorMessage += "\n\tLa contraseña no puede estar vacía."
                alertDialog.setMessage(errorMessage)
            }

            else if(failedLogin) {
                alertDialog.setTitle("Error al acceder al sistema")
                alertDialog.setMessage("Nombre de usuario o contraseña incorrectos")
                failedLogin = false
            }
            else {
                alertDialog.setTitle("Bienvienid@ "+editText_name.text)
                alertDialog.setMessage("Gracias por usar WatchNext")
            }
            alertDialog.show()
        }

    }
}
