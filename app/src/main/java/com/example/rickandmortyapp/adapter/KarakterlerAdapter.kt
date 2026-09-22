package com.example.rickandmortyapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapp.fragment.AnasayfaFragmentDirections
import com.example.rickandmortyapp.data.CharacterDto
import com.example.rickandmortyapp.data.Karakterler
import com.example.rickandmortyapp.databinding.CardTasarimBinding
import com.google.android.material.snackbar.Snackbar

class KarakterlerAdapter(var mContext: Context, var karakterlerListesi: ArrayList<CharacterDto>)
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
            Navigation.findNavController(it).navigate(AnasayfaFragmentDirections.detayGecis(karakter))
        }
    }

    override fun getItemCount(): Int {
        return karakterlerListesi.size
    }
    fun yeniKarakterlerEkle(yeniListe: List<CharacterDto>) {
        val baslangicPozisyonu = karakterlerListesi.size
        karakterlerListesi.addAll(yeniListe)
        notifyItemRangeInserted(baslangicPozisyonu, yeniListe.size)
    }
    fun listeyiYenile(yeniListe: List<CharacterDto>) {
        karakterlerListesi.clear()
        karakterlerListesi.addAll(yeniListe)
        notifyDataSetChanged()
    }

}