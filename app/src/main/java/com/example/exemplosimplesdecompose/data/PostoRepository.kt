package com.example.exemplosimplesdecompose.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostoRepository(context: Context) {
    private val prefs = context.getSharedPreferences("postos_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getPostos(): List<Posto> {
        val json = prefs.getString("postos_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Posto>>() {}.type
        return gson.fromJson(json, type)
    }

    fun salvarPosto(posto: Posto) {
        val postos = getPostos().toMutableList()
        val index = postos.indexOfFirst { it.id == posto.id }
        if (index != -1) {
            postos[index] = posto // Atualiza
        } else {
            postos.add(posto) // Novo
        }
        salvarLista(postos)
    }

    fun excluirPosto(id: String) {
        val postos = getPostos().filterNot { it.id == id }
        salvarLista(postos)
    }

    private fun salvarLista(postos: List<Posto>) {
        val json = gson.toJson(postos)
        prefs.edit().putString("postos_list", json).apply()
    }
}
