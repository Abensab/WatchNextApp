package com.watchnext.watchnext

import com.google.firebase.firestore.CollectionReference


class Operario(var id: Number, var conectado: Boolean, var etiquetas:ArrayList<String>, var tareas:CollectionReference) {
    fun toStringMap(): MutableMap<String, Any> {
        var map = mapOf("id" to id, "conectado" to conectado)
        return map.toMutableMap()
    }
    fun toArrayMap(): MutableMap<String,Any> {
        var map = mapOf("etiquetas" to etiquetas)
        return map.toMutableMap()
    }
}

