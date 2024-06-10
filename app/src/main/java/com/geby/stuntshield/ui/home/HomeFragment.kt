package com.geby.stuntshield.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.geby.stuntshield.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel: HomeViewModel
//    private  lateinit var articleAdapter: ArticleAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        setupRecyclerView()

//        homeViewModel.articles.observe(viewLifecycleOwner, Observer {
//            articleAdapter.submitlist(it)
//        })
//        return root
    }

//    private fun setupRecyclerView() {
//        articleAdapter = ArticleAdapter()
//        binding.recyclerViewArticles.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = articleAdapter
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}