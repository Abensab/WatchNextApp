package com.watchnext.watchnext

import com.google.firebase.firestore.CollectionReference


class Operario(var id: Number, var conectado: Boolean, var etiquetas:ArrayList<String> = arrayListOf<String>(), var tareas:ArrayList<Number> = arrayListOf<Number>()) {

    fun toStringMap(): MutableMap<String, Any> {
        var map = mapOf("id" to id, "conectado" to conectado,"etiquetas" to etiquetas,"tareas" to tareas)
        return map.toMutableMap()
    }

    fun toArrayMap(): Map<String,Any> {
        var map = mapOf("etiquetas" to etiquetas,"tareas" to tareas)
        return map.toMutableMap()
    }


}

