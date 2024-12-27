package com.example.exhibition.ui.event

//import EventDetailFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.exhibition.databinding.FragmentEventBinding
import com.example.exhibition.R
import android.content.Intent


class EventFragment : Fragment() {
    private var _binding: FragmentEventBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_event, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        val events = listOf(
            EventItem(R.drawable.cat1, "도예은 피아노 리사이틀", "조이마루아트홀", "2024.12.26 - 2024.12.26"),
            EventItem(R.drawable.cat2, "뮤지컬 지킬앤하이드", "블루스퀘어 신한카드홀", "2024.11.29 - 2025.05.18"),
            EventItem(R.drawable.cat3, "클래식 음악회", "예술의전당", "2024.12.01 - 2024.12.02"),
            EventItem(R.drawable.cat1, "도예은 피아노 리사이틀", "조이마루아트홀", "2024.12.26 - 2024.12.26"),
            EventItem(R.drawable.cat2, "뮤지컬 지킬앤하이드", "블루스퀘어 신한카드홀", "2024.11.29 - 2025.05.18"),
            EventItem(R.drawable.cat3, "클래식 음악회", "예술의전당", "2024.12.01 - 2024.12.02")
        )

        val adapter = EventAdapter(events) { selectedEvent ->
            val intent = Intent(requireContext(), EventDetailActivity::class.java).apply {
                putExtra("EVENT_IMAGE", selectedEvent.imageResId)
                putExtra("EVENT_TITLE", selectedEvent.title)
                putExtra("EVENT_LOCATION", selectedEvent.location)
                putExtra("EVENT_DATE", selectedEvent.date)
            }
            startActivity(intent)
        }

        // RecyclerView 설정
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.adapter = adapter

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
