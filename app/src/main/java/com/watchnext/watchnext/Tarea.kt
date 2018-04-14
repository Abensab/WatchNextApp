package com.watchnext.watchnext

import com.google.firebase.firestore.CollectionReference

class Tarea(var id: Number, var asiganble: Boolean?, var descripcion: String?, var etiquetas:ArrayList<String>?, var estimado: Number?, var fechaRealizacion: Long, var h_inicio: Long, var h_fin:Long, var pausable:Boolean, var prioridad:Number, var titulo:String) {

}