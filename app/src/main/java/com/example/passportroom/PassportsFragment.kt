package com.example.passportroom

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import android.widget.Filter
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.Navigation
import com.example.passportroom.adapters.PassportAdapter
import com.example.passportroom.database.MyDatabase
import com.example.passportroom.databinding.FragmentPassportsBinding
import com.example.passportroom.entity.Passport

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PassportsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PassportsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    lateinit var binding: FragmentPassportsBinding
    lateinit var myDatabase: MyDatabase
    lateinit var list: ArrayList<Passport>
    lateinit var passportAdapter: PassportAdapter
    var list2=ArrayList<Passport>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassportsBinding.inflate(inflater, container, false)
        myDatabase = MyDatabase.getInstance(requireContext())
        list = ArrayList(myDatabase.passportDao().getPassportList())
        passportAdapter = PassportAdapter(list, object : PassportAdapter.OnItemClickPassport {
            override fun onItemClickPassport(passport: Passport, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("passport", passport)
                bundle.putInt("position", position)
                Navigation.findNavController(requireView()).navigate(R.id.aboutFragment, bundle)
            }

            override fun onItemClickDelete(passport: Passport, position: Int) {
                Toast.makeText(requireContext(), "Click Delete", Toast.LENGTH_SHORT).show()
                list.removeAt(position)
                myDatabase.passportDao().deletePassport(passport)
                passportAdapter.notifyItemRemoved(position)
                passportAdapter.notifyItemRangeRemoved(position, list.size)
            }

            override fun onItemClickEdit(passport: Passport, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("edit", passport)
                bundle.putInt("position", position)
                Navigation.findNavController(requireView()).navigate(R.id.editFragment, bundle)
            }


        }, requireContext())


        binding.rv.adapter = passportAdapter


        binding.search.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchDatabase(query)
                }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchDatabase(newText)
                }
                return true
            }

        })
        return binding.root

    }
    fun searchDatabase(query: String) {
        val searchQuery = "%$query%"
        list2 = ArrayList(myDatabase.passportDao().searchDatabase(searchQuery))
        passportAdapter.list=list2
        passportAdapter.notifyDataSetChanged()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PassportsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PassportsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}