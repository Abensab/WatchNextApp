package com.watchnext.watchnext

import com.google.firebase.firestore.DocumentSnapshot


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
        var map = mapOf("id" to id.toString(),
                "asignable" to asignable,
                "descripcion" to descripcion.toString(),
                "estimado" to estimado.toString(),
                "fecha_realizacion" to fechaRealizacion.toString(),
                "h_inicio" to h_inicio.toString(),
                "h_fin" to h_fin.toString(),
                "pausable" to pausable.toString(),
                "prioridad" to prioridad.toString(),
                "titulo" to titulo.toString(), "pausada" to pausada)
        return map.toMutableMap()
    }
    fun toArrayMap(): MutableMap<String,Any> {
        var map = mapOf("etiquetas" to etiquetas as ArrayList<String>)
        return map.toMutableMap()
    }

}