package com.example.fyreapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fyreapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        val textView2: TextView = binding.textHome2
        val textView3: TextView = binding.textHome3
        val textView4: TextView = binding.textHome4
        val textView5: TextView = binding.textHome5
        val textView6: TextView = binding.textHome6
        val textView7: TextView = binding.textHome7
        val textView8: TextView = binding.textHome8
        val textView9: TextView = binding.textHome9
        val textView10: TextView = binding.textHome10
        val textView11: TextView = binding.textHome11
        val textView12: TextView = binding.textHome12
        val textView13: TextView = binding.textHome13
        val textView14: TextView = binding.textHome14
        val textView15: TextView = binding.textHome15
        val textView16: TextView = binding.textHome16
        val textView17: TextView = binding.textHome17
        val textView18: TextView = binding.textHome18
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
            textView2.text = "Nov 11 - 22:30 - Cuca Roseta"
            textView3.text = "Nov 11 - 00:30 - Cuca Roseta"
            textView4.text = "Nov 12 - 19:30 - Cuca Roseta"
            textView5.text = "Nov 12 - 22:30 - Cuca Roseta"
            textView6.text = "Nov 12 - 00:30 - Cuca Roseta"
            textView7.text = "Nov 13 - 16:30 - Cuca Roseta"
            textView8.text = "Nov 13 - 19:30 - Cuca Roseta"
            textView9.text = "Nov 13 - 22:30 - Cuca Roseta"
            textView10.text = "Stage 2"
            textView11.text = "Nov 11 - 22:30 - Marco Paulo"
            textView12.text = "Nov 11 - 00:30 - Marco Paulo"
            textView13.text = "Nov 12 - 19:30 - Marco Paulo"
            textView14.text = "Nov 12 - 22:30 - Marco Paulo"
            textView15.text = "Nov 12 - 00:30 - Marco Paulo"
            textView16.text = "Nov 13 - 16:30 - Marco Paulo"
            textView17.text = "Nov 13 - 19:30 - Marco Paulo"
            textView18.text = "Nov 13 - 22:30 - Marco Paulo"
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}