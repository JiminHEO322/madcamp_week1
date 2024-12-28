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
            EventItem(R.drawable.photo1, "우연히 웨스 앤더슨 2", "그라운드시소 센트럴", "2024.10.18 - 2025.04.13"),
            EventItem(R.drawable.photo2, "뮤지컬 지킬앤하이드", "블루스퀘어 신한카드홀", "2024.11.29 - 2025.05.18"),
            EventItem(R.drawable.photo3, "피아노 파 드 되", "유니버설아트센터", "2025.02.09"),
            EventItem(R.drawable.photo1, "우연히 웨스 앤더슨 2", "그라운드시소 센트럴", "2024.10.18 - 2025.04.13"),
            EventItem(R.drawable.photo2, "뮤지컬 지킬앤하이드", "블루스퀘어 신한카드홀", "2024.11.29 - 2025.05.18"),
            EventItem(R.drawable.photo3, "피아노 파 드 되", "유니버설아트센터", "2025.02.09")
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
