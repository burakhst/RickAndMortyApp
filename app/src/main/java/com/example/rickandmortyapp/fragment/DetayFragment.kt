package com.example.rickandmortyapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rickandmortyapp.R
import com.example.rickandmortyapp.databinding.FragmentAnasayfaBinding
import com.example.rickandmortyapp.databinding.FragmentDetayBinding

class DetayFragment : Fragment() {
    private lateinit var binding : FragmentDetayBinding
    private val args: DetayFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDetayBinding.inflate(inflater, container, false)

        val gelenKarakter = args.karakterDetay

        binding.toolbar.title =  gelenKarakter.name
        if (gelenKarakter.status == "Alive") {
            binding.imageViewDurumIcon.setImageResource(R.drawable.kalp)
        } else if (gelenKarakter.status == "Dead") {
            binding.imageViewDurumIcon.setImageResource(R.drawable.rip)
        }

        val turBilgisi = if (gelenKarakter.type.isNullOrEmpty()) "-" else gelenKarakter.type
        binding.textViewTuru.text = "Tür: $turBilgisi"

        binding.textViewCinsiyet.text = "Cinsiyet: ${gelenKarakter.gender}"
        binding.textViewKokeni.text = "Köken: ${gelenKarakter.origin.name}"
        binding.textViewEpisode.text = "Bölüm Sayısı: ${gelenKarakter.episode.size}"



        Glide.with(requireContext())
            .load(gelenKarakter.image)
            .into(binding.imageView)
        return binding.root
    }
}