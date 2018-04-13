package com.watchnext.watchnext

import com.google.firebase.firestore.CollectionReference


class Operario(var id: Number, var nombre: String?, var apellidos: String?, var etiquetas:ArrayList<String>?, var pass: String?, var tareas:CollectionReference) {
    fun toStringMap(): MutableMap<String, Any> {
        var map = mapOf("id" to id.toString(), "nombre" to nombre.toString(),"apellidos" to apellidos.toString())
        return map.toMutableMap()
    }
    fun toArrayMap(): MutableMap<String,Any> {
        var map = mapOf("etiquetas" to etiquetas as ArrayList<String>)
        return map.toMutableMap()
    }
}

