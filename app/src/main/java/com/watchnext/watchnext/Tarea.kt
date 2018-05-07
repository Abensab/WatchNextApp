package com.watchnext.watchnext

import com.google.firebase.firestore.DocumentSnapshot
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class Tarea(var id: Number,
            var descripcion: String, var etiquetas:ArrayList<String>,
            var estimado: Number, var fechaRealizacion: Number,
            var h_inicio: Number, var h_fin:Number,
            var prioridad:Number, var titulo:String){
    constructor (t: DocumentSnapshot) :
        this(t.get("id") as Number,t.get("descripcion") as String,
                t.get("etiquetas") as ArrayList<String>, t.get("estimado") as Number,
                t.get("fecha_realizacion") as Number, t.get("h_inicio") as Number,
                t.get("h_fin") as Number, t.get("prioridad") as Number,
                t.get("titulo") as String)

    constructor (t: JSONObject) :
        this(t.get("id") as Number,t.get("descripcion") as String,
                    arrayListOf<String>((t.get("etiquetas")  as JSONArray).toString()), t.get("estimado") as Number,
                    t.get("fecha_realizacion") as Number, t.get("h_inicio") as Number,
                    t.get("h_fin") as Number, t.get("prioridad") as Number,
                    t.get("titulo") as String)

    constructor () :
            this(0,"",
                    arrayListOf(""), 0,
                    0, 0,
                    0, 0,
                   "" )

    fun toMap(): MutableMap<String, Any> {
        var map = mapOf("id" to id,
        "descripcion" to descripcion,
        "estimado" to estimado,
        "fecha_realizacion" to fechaRealizacion,
        "h_inicio" to h_inicio,
        "h_fin" to h_fin,
        "prioridad" to prioridad,
        "titulo" to titulo)
        return map.toMutableMap()
    }
    fun toArrayMap(): MutableMap<String,Any> {
        var map = mapOf("etiquetas" to etiquetas)
        return map.toMutableMap()
    }
    fun toJSONObject(): JSONObject {
        return JSONObject(mapOf("id" to id,
                "descripcion" to descripcion,
                "estimado" to estimado,
                "fecha_realizacion" to fechaRealizacion,
                "h_inicio" to h_inicio,
                "h_fin" to h_fin,
                "prioridad" to prioridad,
                "titulo" to titulo))
    }
}