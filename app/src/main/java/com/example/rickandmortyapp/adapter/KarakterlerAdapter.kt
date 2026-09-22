package com.example.rickandmortyapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapp.data.CharacterDto
import com.example.rickandmortyapp.data.Karakterler
import com.example.rickandmortyapp.databinding.CardTasarimBinding
import com.google.android.material.snackbar.Snackbar

class KarakterlerAdapter(var mContext: Context, var karakterlerListesi: List<CharacterDto>)
    : RecyclerView.Adapter<KarakterlerAdapter.CardTasarimTutucu>() {
    inner class CardTasarimTutucu(var tasarim: CardTasarimBinding): RecyclerView.ViewHolder(tasarim.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val layoutInflater = LayoutInflater.from(mContext)
        val binding = CardTasarimBinding.inflate(layoutInflater, parent, false)
        return CardTasarimTutucu(binding)
    }

    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val karakter = karakterlerListesi[position]
        val t = holder.tasarim

        t.textViewKullanici.text = karakter.name

        t.root.setOnClickListener {
            Snackbar.make(it, "${karakter.name} seçildi", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return karakterlerListesi.size
    }

}