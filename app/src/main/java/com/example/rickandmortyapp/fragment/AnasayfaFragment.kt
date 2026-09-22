package com.example.rickandmortyapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rickandmortyapp.adapter.KarakterlerAdapter
import com.example.rickandmortyapp.data.ApiService
import com.example.rickandmortyapp.databinding.FragmentAnasayfaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnasayfaFragment : Fragment() {

    private lateinit var binding: FragmentAnasayfaBinding
    private lateinit var adapter: KarakterlerAdapter
    private lateinit var apiService: ApiService

    // Sayfalama (Pagination) takibi
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

    // Arama ve Debounce takibi
    private var currentSearchQuery: String? = null
    private var searchJob: Job? = null

    // Durum Filtresi takibi
    private var currentStatus: String? = null

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

        // 1. RecyclerView & Adapter Kurulumu
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.layoutManager = layoutManager
        adapter = KarakterlerAdapter(requireContext(), arrayListOf())
        binding.rcView.adapter = adapter

        // 2. Retrofit Kurulumu
        val retrofit = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        // 3. İlk Sayfayı Yükle
        karakterleriGetir(currentPage)

        // 4. Chip Filtre Dinleyicisi
        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            currentStatus = when {
                checkedIds.contains(binding.chipAlive.id) -> "alive"
                checkedIds.contains(binding.chipDead.id) -> "dead"
                checkedIds.contains(binding.chipUnknown.id) -> "unknown"
                else -> null // Seçim kaldırıldığında tüm durumlar gelir
            }

            // Filtre değiştiğinde sayfayı başa sar ve listeyi yenile
            currentPage = 1
            isLastPage = false
            karakterleriGetir(currentPage, currentSearchQuery, currentStatus, isNewSearch = true)
        }

        // 5. Sonsuz Kaydırma (Infinite Scroll) Dinleyicisi
        binding.rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPage++
                            // Aktif olan hem arama kelimesini hem de durum filtresini koruyarak sıradaki sayfayı çeker
                            karakterleriGetir(currentPage, currentSearchQuery, currentStatus, isNewSearch = false)
                        }
                    }
                }
            }
        })

        // 6. Debounce Destekli Arama Dinleyicisi (300 ms Gecikmeli)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchJob?.cancel()
                aramayiBaslat(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = viewLifecycleOwner.lifecycleScope.launch {
                    delay(300)
                    aramayiBaslat(newText)
                }
                return true
            }
        })
    }

    private fun aramayiBaslat(query: String?) {
        currentPage = 1
        isLastPage = false
        currentSearchQuery = if (query.isNullOrBlank()) null else query.trim()
        karakterleriGetir(currentPage, currentSearchQuery, currentStatus, isNewSearch = true)
    }

    private fun karakterleriGetir(
        page: Int,
        query: String? = null,
        status: String? = null,
        isNewSearch: Boolean = false
    ) {
        isLoading = true

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getCharacters(page = page, name = query, status = status)
                val gelenKarakterler = response.results

                if (response.info.next == null) {
                    isLastPage = true
                }

                withContext(Dispatchers.Main) {
                    if (isNewSearch) {
                        adapter.listeyiYenile(gelenKarakterler)
                    } else {
                        adapter.yeniKarakterlerEkle(gelenKarakterler)
                    }
                }
            } catch (e: Exception) {
                // Eşleşen kayıt bulunamadığında (404) ekranı temizle
                withContext(Dispatchers.Main) {
                    if (isNewSearch) {
                        adapter.listeyiYenile(emptyList())
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }
}