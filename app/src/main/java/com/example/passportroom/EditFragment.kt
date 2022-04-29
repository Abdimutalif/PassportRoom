package com.example.passportroom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.example.passportroom.database.MyDatabase
import com.example.passportroom.databinding.FragmentEditBinding
import com.example.passportroom.entity.Passport
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Passport? = null
    private var param2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable("edit") as Passport?
            param2 = it.getInt("position")
        }
    }

    private lateinit var binding: FragmentEditBinding
    private var positionRegion = 0
    private var positiongender = 0
    private var fileAbsolutePath = ""
    lateinit var myDatabase: MyDatabase


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBinding.inflate(inflater, container, false)
        myDatabase = MyDatabase.getInstance(requireContext())

        val regions = resources.getStringArray(R.array.region)
        val genders = resources.getStringArray(R.array.gender)
        for (i in regions.indices) {
            if (regions[i] == param1?.region) {
                positionRegion = i
            }
        }
        for (i in genders.indices) {
            if (genders[i] == param1?.gender) {
                positiongender = i
            }
        }

        binding.apply {
            edName.setText(param1?.name)
            edSurname.setText(param1?.surname)
            edFio.setText(param1?.fio)
            country.setSelection(positionRegion)
            edAddress.setText(param1?.address)
            gender.setSelection(positiongender)
            edCity.setText(param1?.city)
            edPassportDate.setText(param1?.date)
            edPassportDeadline.setText(param1?.deadline)
            image.setImageURI(Uri.parse(param1?.imagePath))
            fileAbsolutePath=param1?.imagePath.toString()
        }
        binding.apply {
            editBtn.setOnClickListener {
                if (isValidation()) {
                    myDatabase.passportDao().editPassport(param1!!)
                    findNavController().popBackStack()
                }
            }
            image.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Tanlang!!!")
                builder.setMessage("Cameradan rasm olasizmi yoki Galeriyadanmi rasm olasizmi ?")
                builder.setPositiveButton("Cameradan")
                { dialog, which ->
                    onResultCamera()
                }
                builder.setNegativeButton("Galeriyadan") { dialog, which -> onResultGallery() }
                builder.show()
            }
        }



        return binding.root

    }
    private fun isValidation(): Boolean {
        binding.apply {
            val name = edName.text.toString().trim()
            val surName = edSurname.text.toString().trim()
            val fio = edFio.text.toString().trim()
            val city = edCity.text.toString().trim()
            val address = edAddress.text.toString().trim()
            val date = edPassportDate.text.toString().trim()
            val deadline = edPassportDeadline.text.toString().trim()
            val regions = resources.getStringArray(R.array.region)
            val region: String = regions[binding.country.selectedItemPosition].trim()
            val genders = resources.getStringArray(R.array.gender)
            val gender: String = genders[binding.gender.selectedItemPosition].trim()
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Ismingiz kiritilmagan", Toast.LENGTH_SHORT).show()
                return false
            } else if (surName.isEmpty()) {
                Toast.makeText(requireContext(), "Familyangiz kiritilmagan", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else if (fio.isEmpty()) {
                Toast.makeText(requireContext(), "Otangizni ismi  kiritlmagan", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else if (city.isEmpty()) {
                Toast.makeText(requireContext(), "Shahringiz kiritilmagan", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else if (address.isEmpty()) {
                Toast.makeText(requireContext(), "Address  kiritilmagan", Toast.LENGTH_SHORT)
                    .show()
                return false
            } else if (date.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Passport olgan sanangiz  kiritilmagan",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            } else if (deadline.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Passport tugash sanasi kiritilmagan",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return false
            }
                param1?.name=name
                param1?.surname=surName
                param1?.fio=fio
                param1?.city=city
                param1?.address=address
                param1?.date=date
                param1?.deadline=deadline
                param1?.region=region
                param1?.gender=gender
                param1?.imagePath=fileAbsolutePath

        }

        return true
    }
    private fun onResultCamera() {
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    val photoFile = try {
                        createImageFile()
                    } catch (e: Exception) {
                        null
                    }
                    photoFile?.also {
                        val uri = FileProvider.getUriForFile(
                            requireContext(),
                            BuildConfig.APPLICATION_ID,
                            it
                        )
                        getCameraImage.launch(uri)
                    }

                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        var uri: Uri =
                            Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } else {
                        response.requestedPermission
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Permission!!!")
                    builder.setMessage("Ruxsat berishni surayabdi!!!")
                    builder.setPositiveButton("Ruxsat surash")
                    { dialog, which ->
                        p1?.continuePermissionRequest()
                    }
                    builder.setNegativeButton("Ruxsat suramaslik!!!") { dialog, which -> p1?.cancelPermissionRequest() }
                    builder.show()

                }

            }).check()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val m = System.currentTimeMillis()
        val externalFilesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("G21_$m", ".jpg", externalFilesDir)
            .apply {
                fileAbsolutePath = absolutePath
            }
    }

    private val getCameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            binding.image.setImageURI(Uri.parse(fileAbsolutePath))
        }
    }

    private fun onResultGallery() {
        Dexter.withContext(requireContext())
            .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    getImageContent.launch("image/*")
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        var uri: Uri =
                            Uri.fromParts("package", requireActivity().packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    } else {
                        response.requestedPermission
                    }

                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: com.karumi.dexter.listener.PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Permission!!!")
                    builder.setMessage("Ruxsat berishni surayabdi!!!")
                    builder.setPositiveButton("Ruxsat surash")
                    { dialog, which ->
                        p1?.continuePermissionRequest()
                    }
                    builder.setNegativeButton("Ruxsat suramaslik!!!") { dialog, which -> p1?.cancelPermissionRequest() }
                    builder.show()

                }

            }).check()
    }

    private val getImageContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            binding.image.setImageURI(uri)
            val openInputStream = requireActivity().contentResolver?.openInputStream(uri)
            val m = System.currentTimeMillis()
            val file = File(requireActivity().filesDir, "$m.jpg")
            val fileOutputStream = FileOutputStream(file)
            openInputStream?.copyTo(fileOutputStream)
            openInputStream?.close()
            fileOutputStream.close()
            fileAbsolutePath = file.absolutePath
        }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}