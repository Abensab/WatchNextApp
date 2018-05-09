package com.watchnext.watchnext

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.util.Log
import android.view.View
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject

import java.io.IOException

import java.net.URL


class MainActivity : AppCompatActivity() {

    internal val Background = newFixedThreadPoolContext(2, "bg")
    var URL_SOLICITAR_TAREA:String = "https://us-central1-wane-3630f.cloudfunctions.net/solicitarDatosTarea3?" //Devuelve el id de la primera tarea que tiene asignada o un texto para decir que no tiene

    var respuesta = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        var alertDialogBuilder  = AlertDialog.Builder(this)
        doGet(URL_SOLICITAR_TAREA)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadingPanel.visibility=View.INVISIBLE
        val db = FirebaseFirestore.getInstance()//referencia de firestore
        val operariosRef = db.collection("operarios")
        val operariosConectadosRef = db.collection("operariosConectados")

        button_login.setOnClickListener {
            loadingPanel.visibility = View.VISIBLE
            button_login.isClickable = false
            button_login.visibility = View.INVISIBLE
            editText_passwd.isClickable = false
            editText_passwd.visibility = View.INVISIBLE
            editText_name.isClickable = false
            editText_name.visibility = View.INVISIBLE
            var CodOperario : String = editText_name.text.toString()
            Log.w("MA-COMPROBANDO", "CodOperario: " +CodOperario)
            Log.w("INTRODUCED", "documento: " + editText_name.text + ", pass: " + editText_passwd.text)
            var codigo = -1 //0->usr y pass incorrecto, 1->pass incorrecto, 2->correcto
            if ((CodOperario.length > 0) and (editText_passwd.length() > 0)) { //Si los campos no estan vacíos
                val successDialogBuilder = AlertDialog.Builder(this)
//                val job1 = launch(Background) {
                    val operacion = operariosRef.document(editText_name.text.toString()).get().addOnSuccessListener { operario->
                        if(operario.exists()) {
                            Log.w("QUERY", "Documento: " + operario.get("id"))
                            if (editText_passwd.text.toString().equals(operario.get("pass").toString())) {
                                operariosConectadosRef.document(editText_name.text.toString()).update(mapOf( "conectado" to true ))
                                Log.w("QUERY", "Password: " + operario.get("pass"))
                                Log.w("SUCCESSFUL", "documento: " + operario.get("id") + ", pass: " + operario.get("pass"))
                                successDialogBuilder.setTitle("Bienvienid@ " + editText_name.text)
                                successDialogBuilder.setMessage("Gracias por usar WatchNext")
                                codigo = 2
                                operariosConectadosRef.document(editText_name.text.toString()).get().addOnSuccessListener { operarioConectado->
                                    if(operarioConectado.exists()) {
                                        doAsync {
                                            doGet(URL_SOLICITAR_TAREA + "id=" + operarioConectado.get("id"))
                                            uiThread {
                                                Log.w("WORK END", "res:" + respuesta.toString())

                                                if (respuesta.contentEquals("no hay tareas") || respuesta.equals("")) {
                                                    Log.w("NO TAREA FOUND", "No hay tareas disponibles " + respuesta)
                                                    ejecutarRedireccion(false, operarioConectado.get("id").toString(), Tarea(), successDialogBuilder)
                                                } else {
                                                    var tarea = Tarea(JSONObject(respuesta).get("tarea") as JSONObject)
                                                    Log.w("TAREA FOUND", "hay tarea disponible " + respuesta)
                                                    ejecutarRedireccion(true, operarioConectado.get("id").toString(), tarea, successDialogBuilder)
                                                }
                                            }
                                        }

                                    }else{
                                        creaOperario(operario, operariosConectadosRef)
                                        ejecutarRedireccion(false, operario.get("id").toString(), Tarea(), successDialogBuilder)
                                    }
                                }

                            }else{//La contraseña no corresponde
                                Log.e("ERROR", "La contraseña es incorrecta")
                                codigo=1
                            }
                        }else{
                            Log.w("QUERY", "No existe")
                            codigo=0
                        }
                    }
                    operacion.addOnCompleteListener{
                        Log.w("CREATING ALERT", "" + codigo)
                        creaAlerta(alertDialogBuilder, codigo)
                    }
//                }


            } else {
                    alertDialogBuilder.setTitle("Error al acceder al sistema")
                    var errorMessage = "Han ocurrido los siguientes errores:"
                    if (editText_name.length() <= 0)
                        errorMessage += "\n\tEl nombre de usuario no puede estar vacío."
                    if (editText_passwd.length() <= 0)
                        errorMessage += "\n\tLa contraseña no puede estar vacía."
                    alertDialogBuilder.setMessage(errorMessage)
                    var a = alertDialogBuilder.create()
                    loadingPanel.visibility=View.INVISIBLE
                    button_login.isClickable=true
                    button_login.visibility= View.VISIBLE
                    editText_passwd.isClickable=true
                    editText_passwd.visibility= View.VISIBLE
                    editText_name.isClickable=true
                    editText_name.visibility= View.VISIBLE
                    a.show()
                }
        }
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

    private fun creaAlerta(alertDialogBuilder: AlertDialog.Builder, codigo : Int){
        if (codigo == 0) {
            alertDialogBuilder.setMessage("Nombre de usuario incorrecto")
        } else if (codigo == 1) {
            alertDialogBuilder.setMessage("Contraseña incorrecta")
        }
        if (codigo!= -1 && codigo != 2) {
            alertDialogBuilder.setTitle("Error al acceder al sistema")
            var b = alertDialogBuilder.create()
            loadingPanel.visibility=View.INVISIBLE
            button_login.isClickable=true
            button_login.visibility= View.VISIBLE
            editText_passwd.isClickable=true
            editText_passwd.visibility= View.VISIBLE
            editText_name.isClickable=true
            editText_name.visibility= View.VISIBLE
            b.show()
        }

    }
    private fun ejecutarRedireccion(tieneTarea:Boolean, codOperario: String, tarea: Tarea, successDialogBuilder: AlertDialog.Builder) {
        if(tieneTarea){
            Log.w("WORK END", respuesta)
            successDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { button, whichButton ->
                val intent = Intent(this, TareaEnEjecucionActivity::class.java)
                intent.putExtra("operario", codOperario.toString())
                intent.putExtra("tarea", tarea.toJSONObject().toString())
                startActivity(intent, Bundle())
            })
        }else {
            successDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { button, whichButton ->
                val intent = Intent(this, AceptarTareaActivity::class.java)
                intent.putExtra("operario", codOperario)
                startActivity(intent, Bundle())
            })
        }
        val b = successDialogBuilder.create()
        b.show()
    }
    private fun creaOperario(document: DocumentSnapshot,operariosConectadosRef:CollectionReference) {
        var operario = Operario(document.get("id") as Number, true, document.get("etiquetas") as ArrayList<String>, ArrayList<Number>())
        operariosConectadosRef.document(operario.id.toString()).set(operario.toStringMap())
    }


}

