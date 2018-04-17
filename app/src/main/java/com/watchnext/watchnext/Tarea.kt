package com.watchnext.watchnext

import com.google.firebase.firestore.DocumentSnapshot
import java.util.*


class Tarea(var id: Number, var asignable: Boolean,
            var descripcion: String, var etiquetas:ArrayList<String>,
            var estimado: Number, var fechaRealizacion: Number,
            var h_inicio: Number, var h_fin:Number, var pausable:Boolean,
            var prioridad:Number, var titulo:String,
            var pausada:Boolean) {
    constructor (t: DocumentSnapshot) :
            this(t.get("id") as Number,t.get("asignable") as Boolean,t.get("descripcion") as String,
                    t.get("etiquetas") as ArrayList<String>, t.get("estimado") as Number,
                    t.get("fecha_realizacion") as Number, t.get("h_inicio") as Number,
                    t.get("h_fin") as Number, t.get("pausable") as Boolean, t.get("prioridad") as Number,
                    t.get("titulo") as String, false)

    fun toMap(): MutableMap<String, Any> {
        var map = mapOf("id" to id,
        "asignable" to asignable,
        "descripcion" to descripcion,
        "estimado" to estimado,
        "fecha_realizacion" to fechaRealizacion,
        "h_inicio" to h_inicio,
        "h_fin" to h_fin,
        "pausable" to pausable,
        "prioridad" to prioridad,
        "titulo" to titulo, "pausada" to pausada)
        return map.toMutableMap()
    }
    fun toArrayMap(): MutableMap<String,Any> {
        var map = mapOf("etiquetas" to etiquetas)
        return map.toMutableMap()
    }

}