package com.smartcity.provider.ui.main.custom_category.variant

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.smartcity.provider.R
import com.smartcity.provider.models.product.ProductVariants
import com.smartcity.provider.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.provider.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.provider.ui.main.custom_category.viewmodel.*
import com.smartcity.provider.util.*
import kotlinx.android.synthetic.main.fragment_variant.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.io.File
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class VariantFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_variant,viewModelFactory){

    var imageUri:Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CustomCategoryViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()

        updateProductVariant()
        selectImage()
        deleteImage()

        copyImage()
        pasteImage()
        subscribeObservers()
        viewModel.getSelectedProductVariant()?.let {
            imageUri=it.imageUri
        }
    }

    private fun pasteImage() {
        variant_image_paste.setOnClickListener {
            viewModel.getCopyImage()?.let {
                imageUri=it
                requestManager
                    .load(imageUri)
                    .into(variant_image)
                Toast.makeText(context,"pasted",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyImage() {
        variant_image_copy.setOnClickListener {

            imageUri?.let {
                viewModel.setCopyImage(it)
                Toast.makeText(context,"copied",Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun deleteImage() {
        variant_image_delete.setOnClickListener {
            viewModel.getSelectedProductVariant()?.let {productVariant ->
                variant_image.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)
                imageUri=null

                Toast.makeText(context,"deleted",Toast.LENGTH_SHORT).show()

            }

        }
    }

    private fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState.selectedProductVariant.variante?.let {productVariant ->
                setVariantProperties(
                    productVariant.price,
                    productVariant.unit,
                    productVariant.imageUri
                )
            }
        })
    }

    private fun setVariantProperties(price:Double,
                                     quantity:Int,
                                     imageUri: Uri?){
        if(imageUri != null){
            requestManager
                .load(imageUri)
                .into(variant_image)
        }
        input_variant_price.setText(price.toInt().toString())
        input_variant_quantity.setText(quantity.toString())
    }
    private fun selectImage() {
        variant_image.setOnClickListener {
            if(uiCommunicationListener.isStoragePermissionGranted()){
                pickFromGallery()
            }
        }
    }

    private fun pickFromGallery() {
        ImagePicker.with(this)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .saveDir(File(Environment.getExternalStorageDirectory(), "ImagePicker"))
            .galleryMimeTypes(  //Exclude gif images
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "CROP: RESULT OK")

            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            fileUri?.let {
                imageUri=it
                requestManager
                    .load(imageUri)
                    .into(variant_image)
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
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

    private fun setUpUi(variant:ProductVariants) {
        try {
            var optionValues=""
            variant.productVariantAttributeValuesProductVariant.map {
                optionValues= optionValues+" / "+it.attributeValue.value
            }
            toolbarTitle.text=optionValues.drop(2)
        }catch (e:Exception){

        }


    }

    private fun updateProductVariant() {
        setUpUi(viewModel.getSelectedProductVariant()!!)
        variant_save_button.setOnClickListener {

            val variant=viewModel.getSelectedProductVariant()
            val variantList=viewModel.getProductVariantsList()!!.toMutableList()
            variant?.let {
                if (input_variant_price.text.toString().isNotEmpty().and(input_variant_quantity.text.toString().isNotEmpty())){


                    variantList.find { it==variant }.let {productVariant->
                        if (productVariant!=null){
                            productVariant.price=input_variant_price.text.toString().toDouble()
                            productVariant.unit=input_variant_quantity.text.toString().toInt()
                            productVariant.imageUri=imageUri
                            if (imageUri!=null){
                                productVariant.image=RingtoneManager.getRingtone(context, imageUri).getTitle(context)
                            }

                        }
                    }

                    viewModel.setProductVariantsList(variantList)

                    findNavController().navigate(R.id.action_variantFragment_to_createProductFragment)


                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        toolbarTitle.text=""
        viewModel.setSelectedProductVariant(null)
    }
}