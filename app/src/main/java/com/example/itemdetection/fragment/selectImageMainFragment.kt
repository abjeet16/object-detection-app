package com.example.itemdetection.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.media.MediaCas
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.itemdetection.R
import com.example.itemdetection.databinding.FragmentSelectImageBinding
import com.example.itemdetection.databinding.FragmentSelectImageMainBinding
import com.example.itemdetection.ml.ObjectDetection
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp

class selectImageMainFragment : Fragment() {

    private val paint = Paint()
    private lateinit var binding:FragmentSelectImageMainBinding
    private lateinit var bitmap: Bitmap
    private lateinit var model:ObjectDetection
    private lateinit var lables:List<String>
    private val imageProcessor = ImageProcessor.Builder().add(ResizeOp(300,300,ResizeOp.ResizeMethod.BILINEAR)).build()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSelectImageMainBinding.inflate(layoutInflater)


        lables = FileUtil.loadLabels(requireContext(),"labels.txt")
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)

        model = ObjectDetection.newInstance(requireContext())

        binding.selectImageButton.setOnClickListener {
            startActivityForResult(intent,101)
        }
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 101){
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,uri)

            getPrediction()
        }
    }

    private fun getPrediction() {

// Creates inputs for reference.
        var image = TensorImage.fromBitmap(bitmap)
        image = imageProcessor.process(image)

// Runs model inference and gets result.
        val outputs = model.process(image)
        val locations = outputs.locationsAsTensorBuffer.floatArray
        val classes = outputs.classesAsTensorBuffer.floatArray
        val scores = outputs.scoresAsTensorBuffer.floatArray
        val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray

        var mutable = bitmap.copy(Bitmap.Config.ARGB_8888,true)
        val canvas = Canvas(mutable)


        val h = mutable.height
        val w = mutable.width

        //setting response text size
        paint.textSize =h/15f
        //setting box width
        paint.strokeWidth = h/85f
        var x = 0
        scores.forEachIndexed{index, fl ->
            x = index
            x*=4
            // if prediction is more then 50 % confident
            if (fl>0.5){
                //stattingdrawing lines
                paint.style = Paint.Style.STROKE
                //draw rectange onto the prediction
                canvas.drawRect(RectF(locations.get(x+1)*w,
                    locations.get(x)*h,
                    locations.get(x+3)*w,
                    locations.get(x+2)*h)
                    ,paint)
                paint.style = Paint.Style.FILL
                canvas.drawText(
                    lables.get(classes.get(index).toInt())+" "+fl.toString(),
                    locations.get(x+1)*w,
                    locations.get(x)*h,
                    paint)
            }
        }

        binding.selectedImage.setImageBitmap(mutable)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Releases model resources if no longer used.
        model.close()
    }

}