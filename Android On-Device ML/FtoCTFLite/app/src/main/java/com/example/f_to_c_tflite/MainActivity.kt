package com.example.f_to_c_tflite

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import org.tensorflow.lite.Interpreter;
import java.nio.MappedByteBuffer
import android.app.Activity
import android.content.res.AssetFileDescriptor
import android.view.View
import android.widget.Button
import java.io.FileInputStream
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {

    private lateinit var tflite: Interpreter
    private var tfliteoptions: Interpreter.Options = Interpreter.Options()
    private lateinit var tflitemodel: MappedByteBuffer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try{
            tflitemodel = loadModelfile(this)
            tfliteoptions.setNumThreads(1)
            tflite = Interpreter(tflitemodel, tfliteoptions)

        } catch (ex: Exception){
            ex.printStackTrace()
        }
        var btnDoInference: Button = findViewById<Button>(R.id.btnInfer)
        btnDoInference.setOnClickListener{
            doInference()
        }

    }

    private fun doInference(){
        val inputVal: FloatArray = floatArrayOf(100.0f)
        var outputVal: ByteBuffer = ByteBuffer.allocateDirect(4)
        outputVal.order(ByteOrder.nativeOrder())
        tflite.run(inputVal, outputVal)
        outputVal.rewind()
        var f:Float = outputVal.getFloat()
        var x:Int = 1
    }

    private fun loadModelfile(activity: Activity):MappedByteBuffer{
        var fileDescriptor: AssetFileDescriptor = activity.assets.openFd("ftoc.tflite")
        var inputStream:FileInputStream = FileInputStream(fileDescriptor.fileDescriptor);
        var fileChannel:FileChannel = inputStream.channel;
        var startOffset:Long = fileDescriptor.startOffset;
        var declaredLength:Long = fileDescriptor.declaredLength;
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
