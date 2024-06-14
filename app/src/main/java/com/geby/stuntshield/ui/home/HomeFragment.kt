package com.geby.stuntshield.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.geby.stuntshield.R
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

        homeViewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.retryButton.visibility = if (isError) View.VISIBLE else View.GONE
        }

        binding.retryButton.setOnClickListener {
            homeViewModel.showArticleList()
        }

        homeViewModel.username.observe(viewLifecycleOwner) { username ->
            binding.username.text = username
            Log.d("HomeFragment", "Username displayed: $username")
        }

        homeViewModel.profilePictureUri.observe(viewLifecycleOwner, Observer { uri ->
            Glide.with(this)
                .load(uri)
                .circleCrop()
                .into(binding.btnImgProfile)
            Log.d("HomeFragment", "Profile picture URI: $uri")
        })

        binding.btnImgProfile.setOnClickListener {
            if (homeViewModel.isError.value == true) {
                Toast.makeText(context, "Error occurred. Please try again.", Toast.LENGTH_SHORT).show()
            } else {
                findNavController().navigate(R.id.action_navigation_home_to_navigation_profile)
            }
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