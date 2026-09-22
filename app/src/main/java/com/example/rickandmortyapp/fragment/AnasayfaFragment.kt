package com.example.rickandmortyapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rickandmortyapp.adapter.KarakterlerAdapter
import com.example.rickandmortyapp.data.ApiService
import com.example.rickandmortyapp.databinding.FragmentAnasayfaBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnasayfaFragment : Fragment() {

    private lateinit var binding: FragmentAnasayfaBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnasayfaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView için alt alta dizilim ayarı
        binding.rcView.layoutManager = LinearLayoutManager(requireContext())

        val retrofit = Retrofit.Builder()
                .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCharacters()
                val karakterListesi = response.results

                withContext(Dispatchers.Main) {
                    // API'den gelen listeyi Adapter'a verip RecyclerView'a bağlıyoruz
                    val adapter = KarakterlerAdapter(requireContext(), karakterListesi)
                    binding.rcView.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}