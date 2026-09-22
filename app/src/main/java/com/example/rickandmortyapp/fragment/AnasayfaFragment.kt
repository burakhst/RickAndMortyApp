package com.example.rickandmortyapp.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var adapter: KarakterlerAdapter
    private lateinit var apiService: ApiService
    private var currentSearchQuery: String? = null

    // Sonsuz kaydırma için takip değişkenleri
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false

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

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {

            // Klavyeden "Ara" butonuna basıldığında tetiklenir
            override fun onQueryTextSubmit(query: String?): Boolean {
                aramayiBaslat(query)
                return true
            }

            // Kullanıcı her harf yazdığında veya sildiğinde tetiklenir
            override fun onQueryTextChange(newText: String?): Boolean {
                aramayiBaslat(newText)
                return true
            }
        })
        // 1. LayoutManager kurulumu
        val layoutManager = LinearLayoutManager(requireContext())
        binding.rcView.layoutManager = layoutManager

        // 2. Adapter'ı başta boş bir liste ile bağla
        adapter = KarakterlerAdapter(requireContext(), arrayListOf())
        binding.rcView.adapter = adapter

        // 3. Retrofit servisini hazırla
        val retrofit = Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // 4. İlk 20 karakteri yükle (Sayfa 1)
        karakterleriGetir(currentPage)

        // 5. Scroll Dinleyicisi: Kullanıcı aşağı indikçe sonraki sayfayı çağırır
        binding.rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Sadece aşağı doğru kaydırırken tetiklensin
                if (dy > 0) {
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    // Listenin sonuna yaklaşıldıysa ve o an yeni istek atılmıyorsa
                    if (!isLoading && !isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                            currentPage++
                            karakterleriGetir(currentPage)
                        }
                    }
                }
            }
        })
    }
    private fun aramayiBaslat(query: String?) {
        currentPage = 1
        isLastPage = false
        currentSearchQuery = if (query.isNullOrBlank()) null else query.trim()
        karakterleriGetir(currentPage, currentSearchQuery, isNewSearch = true)
    }
    private fun karakterleriGetir(page: Int, query: String? = null, isNewSearch: Boolean = false) {
        isLoading = true

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCharacters(page = page, name = query)
                val gelenKarakterler = response.results

                if (response.info.next == null) {
                    isLastPage = true
                }

                withContext(Dispatchers.Main) {
                    if (isNewSearch) {
                        // Yeni bir arama yapıldıysa listeyi sıfırdan doldur
                        adapter.listeyiYenile(gelenKarakterler)
                    } else {
                        // Sayfa aşağı kaydırıldıysa mevcut listenin altına ekle
                        adapter.yeniKarakterlerEkle(gelenKarakterler)
                    }
                }
            } catch (e: Exception) {
                // Rick and Morty API eşleşen karakter bulamazsa 404 fırlatır
                withContext(Dispatchers.Main) {
                    if (isNewSearch) {
                        adapter.listeyiYenile(emptyList()) // Bulunamadıysa ekranı temizle
                    }
                }
            } finally {
                isLoading = false
            }
        }
    }
}