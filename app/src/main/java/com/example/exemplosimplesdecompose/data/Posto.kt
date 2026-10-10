package com.example.exemplosimplesdecompose.data

import java.util.UUID

data class Historico(
    val data: String,
    val precoAlcool: Double,
    val precoGasolina: Double,
    val melhorOpcao: String,
    val parametro: String
)

data class Posto(
    val nome: String,
    val coordenadas: Coordenadas,
    val id: String = UUID.randomUUID().toString(),
    val precoAlcool: Double = 0.0,
    val precoGasolina: Double = 0.0,
    val dataAtualizacao: String = "",
    val historico: List<Historico> = emptyList()
) {
    constructor(nome: String) : this(
        nome = nome,
        coordenadas = Coordenadas(41.40338, 2.17403)
    )
}