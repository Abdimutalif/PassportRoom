package com.example.passportroom

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.passportroom.databinding.FragmentAboutBinding
import com.example.passportroom.entity.Passport
import java.io.File

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Passport? = null
    private var param2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable("passport") as Passport
            param2 = it.getInt("position")
        }
    }

    private lateinit var binding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAboutBinding.inflate(inflater, container, false)


        binding.apply {
            tv.text = param1?.name + param1?.surname
            tvFullname.text = param1?.name + param1?.surname + param1?.fio
            region.text = "Viloyati: ${param1?.region}\n\nShahri: ${param1?.city}\n\nYashash manzili:  ${param1?.address}"
            passportDateDeadline?.text = "Date: ${param1?.date}\n\nDeadline: ${param1?.deadline}"
            gender.text = "Gender: ${param1?.gender}"
            tvPassportSeries.text = "SeriesNumber: ${param1?.seriesNumber}"
            image.setImageURI(Uri.parse(param1?.imagePath))
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AboutFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}