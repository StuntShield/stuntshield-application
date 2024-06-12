package com.geby.stuntshield.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.geby.stuntshield.data.ArticleAdapter
import com.geby.stuntshield.data.response.ResultsItem
import com.geby.stuntshield.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val sharedPref = requireActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

        homeViewModel.listArticle.observe(viewLifecycleOwner) { item ->
            setArticleList(item)
        }
        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvArticles.layoutManager = layoutManager

        return root
    }

    private fun setArticleList(items: List<ResultsItem>) {
        val adapter = ArticleAdapter()
        adapter.submitList(items)
        binding.rvArticles.adapter = adapter
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}