package com.example.maison.objeto

import java.io.Serializable

class Comida (
    var comida: String ="",
    var tipo: String ="",
    var tiempo: Int =0,
    var duplicable: Boolean = false
): Serializable