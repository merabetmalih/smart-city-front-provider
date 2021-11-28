package com.smartcity.provider.ui.config

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.provider.R
import com.smartcity.provider.models.Store
import com.smartcity.provider.models.StoreAddress
import com.smartcity.provider.ui.AreYouSureCallback
import com.smartcity.provider.ui.config.state.ConfigStateEvent
import com.smartcity.provider.ui.config.viewmodel.*
import com.smartcity.provider.util.*
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_create_store_config.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import me.ibrahimsn.lib.PhoneNumberKit
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class CreateStoreConfigFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseConfigFragment(R.layout.fragment_create_store_config,viewModelFactory){

    lateinit var defaultPhoneNumberKit: PhoneNumberKit
    lateinit var phoneNumberKit: PhoneNumberKit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiCommunicationListener.updateStatusBarColor(R.color.white,false)
        setTextInput()
        subscribeObservers()

        store_image.setOnClickListener {
            if(uiCommunicationListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }

        update_textview_store.setOnClickListener {
            if(uiCommunicationListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }


        val applicationInfo = activity!!.packageManager.getApplicationInfo( activity!!.packageName, PackageManager.GET_META_DATA)
        input_address.setOnClickListener {
            val gpsTracker = GpsTracker(context!!)
            if(uiCommunicationListener.isFineLocationPermissionGranted()){
                gpsTracker.getCurrentLocation()
                val latitude: Double = gpsTracker.getLatitude()
                val longitude: Double = gpsTracker.getLongitude()

                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(latitude, longitude)
                    .showLatLong(true)
                    .setMapZoom(16.0f)
                    .setMapRawResourceStyle(R.raw.map_style)
                    .setMapType(MapType.NORMAL)
                    .setPlaceSearchBar(false, applicationInfo.metaData.getString("com.google.android.geo.API_KEY"))
                    .setMarkerImageImageColor(R.color.bleu)
                    .setFabColor(R.color.bleu)
                    .setPrimaryTextColor(R.color.black)
                    .setSecondaryTextColor(R.color.dark)
                    .build(activity!!)
                startActivityForResult(intent, com.sucho.placepicker.Constants.PLACE_PICKER_REQUEST)
            }else{
                uiCommunicationListener.isFineLocationPermissionGranted()
            }
        }
        next_button.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {

                override fun proceed() {
                    create()
                }

                override fun cancel() {
                    // ignore
                }

            }
            uiCommunicationListener.onResponseReceived(
                response = Response(
                    message = getString(R.string.are_you_sure_publish),
                    uiComponentType = UIComponentType.AreYouSureDialog(callback),
                    messageType = MessageType.Info()
                ),
                stateMessageCallback = object: StateMessageCallback{
                    override fun removeMessageFromStack() {
                        viewModel.clearStateMessage()
                    }
                }
            )
        }
    }

    private fun setTextInput(){
        defaultPhoneNumberKit = PhoneNumberKit(context!!)
        defaultPhoneNumberKit.attachToInput(phone_number, 213)
        defaultPhoneNumberKit.setupCountryPicker(
            activity = activity as AppCompatActivity,
            searchEnabled = true
        )
        default_phone_number.helperText="*Required\nPhone number for SmartCity"

        phoneNumberKit = PhoneNumberKit(context!!)
        phoneNumberKit.attachToInput(default_phone_number, 213)
        phoneNumberKit.setupCountryPicker(
            activity = activity as AppCompatActivity,
            searchEnabled = true
        )
        phone_number.helperText="Phone number for clients"
    }

    private fun validPhoneNumber():Boolean{
        if(!defaultPhoneNumberKit.isValid || !phoneNumberKit.isValid){
            showErrorDialog(ErrorHandling.ERROR_INVALID_PHONE_NUMBER_FORMAT)
            return false
        }
        if(input_address.text.toString().isEmpty()){
            showErrorDialog(ErrorHandling.ERROR_FILL_ALL_INFORMATION)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val permissionsToRequest = ArrayList<String>();
        var i = 0;
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i]);
            i++;
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                activity!!,
                permissionsToRequest.toTypedArray(),
                122);
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(intent, Constants.GALLERY_REQUEST_CODE)
    }

    var beug=true
    override fun onPause() {
        super.onPause()
        beug=false
    }

    private fun create() {
        if(validPhoneNumber()){
            var multipartBody: MultipartBody.Part? = null

            viewModel.viewState.value?.storeFields?.newImageUri?.let{ imageUri ->
                imageUri.path?.let{filePath ->
                    val imageFile = File(filePath)
                    Log.d(TAG, "CreateBlogFragment, imageFile: file: ${imageFile}")
                    val requestBody =
                        RequestBody.create(
                            MediaType.parse("image/*"),
                            imageFile
                        )
                    // name = field name in serializer
                    // filename = name of the image file
                    // requestBody = file with file type information
                    multipartBody = MultipartBody.Part.createFormData(
                        "image",
                        imageFile.name,
                        requestBody
                    )
                }
            }
            multipartBody?.let {
                val store= Store(
                    input_name.text.toString(),
                    input_description.text.toString(),
                    viewModel.getStoreAddress()!!,
                    -1,
                    input_phone_number.text.toString(),
                    input_default_phone_number.text.toString(),
                    getUriName(viewModel.getNewImageUri())
                )


                viewModel.setStateEvent(
                    ConfigStateEvent.CreateStoreAttemptEvent(
                        store,
                        it
                    )
                )
            }
        }
    }

    private fun getUriName(uri:Uri?):String{
        uri?.let {
            return RingtoneManager.getRingtone(context, uri).getTitle(context)
        }
        return ""
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.STORE_CREATION_DONE)){
                    findNavController().navigate(R.id.action_createStoreConfigFragment_to_categoryConfigFragment)
                }

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{
            setStoreProperties(
                viewModel.getStoreName(),
                viewModel.getStoreDescription(),
                viewModel.getStoreAddress(),
                viewModel.getNewImageUri()
            )
        })
    }

    fun setStoreProperties(name: String? ,
                           description: String?,
                           address: StoreAddress?,
                           newImageUri: Uri?){
        if(newImageUri != null){
            requestManager
                .load(newImageUri)
                .into(store_image)
        }
        else{
            requestManager
                .load(R.drawable.ic_baseline_add_photo_alternate_grey)
                .into(store_image)
        }
        input_name.setText(name)
        input_description.setText(description)
        address?.let {
            input_address.setText(it.addressLine())
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "CROP: RESULT OK")
            when (requestCode) {

                Constants.GALLERY_REQUEST_CODE -> {
                    data?.data?.let { uri ->
                        activity?.let{
                            launchImageCrop(uri)
                        }
                    }?: showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE")
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Log.d(TAG, "CROP: CROP_IMAGE_ACTIVITY_REQUEST_CODE: uri: ${resultUri}")
                    viewModel.setNewImageUri(resultUri)
                }

                CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE -> {
                    Log.d(TAG, "CROP: ERROR")
                    showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
                }

                com.sucho.placepicker.Constants.PLACE_PICKER_REQUEST ->{
                    val addressData = data?.getParcelableExtra<AddressData>(com.sucho.placepicker.Constants.ADDRESS_INTENT)
                    var address: Address?=null
                    addressData?.let {
                        it.addressList?.let {
                            address=it.first()
                        }
                    }

                    address?.let {

                        val storeAddress= StoreAddress(
                            it.featureName,
                            it.adminArea,
                            it.subAdminArea,
                            it.locality,
                            it.thoroughfare,
                            it.postalCode,
                            it.countryCode,
                            it.countryName,
                            addressData!!.latitude,
                            addressData!!.longitude,
                            it.getAddressLine(0)
                        )

                        viewModel.setStoreAddress(storeAddress)
                    }





                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri){
        context?.let{
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(it, this)
        }
    }

    fun showErrorDialog(errorMessage: String){
        uiCommunicationListener.onResponseReceived(
            response = Response(
                message = errorMessage,
                uiComponentType = UIComponentType.Dialog(),
                messageType = MessageType.Error()
            ),
            stateMessageCallback = object: StateMessageCallback {
                override fun removeMessageFromStack() {
                    viewModel.clearStateMessage()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setStoreName(input_name.text.toString())
        viewModel.setStoreDescription(input_description.text.toString())
    }
}