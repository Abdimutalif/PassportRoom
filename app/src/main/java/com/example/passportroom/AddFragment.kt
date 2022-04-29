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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.passportroom.database.MyDatabase
import com.example.passportroom.databinding.FragmentAddBinding
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
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
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

    lateinit var binding: FragmentAddBinding
    lateinit var myDatabase: MyDatabase
    lateinit var list: java.util.ArrayList<Passport>
    private var fileAbsolutePath = ""
    lateinit var passport: Passport


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
        myDatabase = MyDatabase.getInstance(requireContext())
        list = ArrayList(myDatabase.passportDao().getPassportList())
        binding.apply {
            saveBtn.setOnClickListener {
                if (isValidation()) {
                    myDatabase.passportDao().addPassport(passport)
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
            } else if (fileAbsolutePath.isEmpty()) {
                Toast.makeText(requireContext(), "Rasm kiritilmagan", Toast.LENGTH_SHORT).show()
                return false
            }
            passport = Passport(
                name = name,
                surname = surName,
                fio = fio,
                city = city,
                address = address,
                date = date,
                deadline = deadline,
                region = region,
                gender = gender,
                seriesNumber = getSerialNumber(),
                imagePath = fileAbsolutePath
            )
        }

        return true
    }

    private fun getSerialNumber(): String {
        val a1 = (65..91).random().toChar()
        val a2 = (65..91).random().toChar()
        val number = (1000000..9999999).random()
        val seriesNumber = "$a1$a2 $number"

        val filterList = list.filter {
            it.seriesNumber == seriesNumber
        }
        return if (filterList.isEmpty()) {
            seriesNumber
        } else {
            getSerialNumber()
        }
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
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}