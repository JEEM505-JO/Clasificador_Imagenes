package com.devjeem.clasificadordeimagenes.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.devjeem.clasificadordeimagenes.R
import com.devjeem.clasificadordeimagenes.camera.PhotoUtilities
import com.devjeem.clasificadordeimagenes.ml.CategoryClassification
import com.devjeem.clasificadordeimagenes.ml.UiState
import com.devjeem.clasificadordeimagenes.ui.theme.ClasificadorDeImagenesTheme
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.task.vision.classifier.ImageClassifier.*
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var photoUtilities: PhotoUtilities


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClasificadorDeImagenesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(photoUtilities, returnIntent(), getScreenOrientation())
                }
            }
        }
    }

    private fun getScreenOrientation(): Int {
        val outMetrics = DisplayMetrics()
        val display: Display?
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            display = this.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            display = this.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }

        return display?.rotation ?: 0
    }

    private fun returnIntent(): Intent {
        return Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
            it.resolveActivity(applicationContext.packageManager).also { _ ->
                photoUtilities.createPhotoFile(
                    "Prueba"
                )
                val photoUri: Uri =
                    FileProvider.getUriForFile(
                        this,
                        photoUtilities.authority,
                        photoUtilities.file
                    )
                it.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
        }
    }
}


@Composable
fun Greeting(photoUtilities: PhotoUtilities, intent: Intent, rotationScreen: Int) {
    val context = LocalContext.current
    val viewModel: MainViewModel = hiltViewModel()
    val state: UiState by viewModel.dataCharacterList.collectAsState()



    val launcherCamera =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val photoBitmap = BitmapFactory.decodeFile(photoUtilities.file.toString())
                val byte = photoUtilities.getByteArrayOfPhoto(photoBitmap)
                if (byte.isNotEmpty()) {
                    Toast.makeText(context, "Se ha capturado la fotografia", Toast.LENGTH_SHORT)
                        .show()
                    viewModel.classifyFinalVersionTwo(photoBitmap, rotationScreen)
                } else {
                    Toast.makeText(context, "No se pudo capturar la fotografia", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    val rememberPermission =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission())
        { isGradient ->
            if (isGradient) {
                Toast.makeText(context, "Acepto los permisos", Toast.LENGTH_SHORT).show()
                launcherCamera.launch(intent)
            } else {
                Toast.makeText(context, "No acepto los permisos", Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(10.dp)
        )
        Text(text = "Clasificador de imagen", fontFamily = FontFamily.Serif, fontSize = 20.sp)

        Text(text = "Herramientas", fontFamily = FontFamily.Serif, fontSize = 15.sp)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text(
                text = "TensorFlow Lite",
                fontFamily = FontFamily.SansSerif,
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(
                text = "Kotlin",
                fontFamily = FontFamily.SansSerif,
                fontSize = 15.sp,
                modifier = Modifier.padding(end = 10.dp)
            )
            Text(text = "Android Studio", fontFamily = FontFamily.SansSerif, fontSize = 15.sp)
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(10.dp)
        )
        Card(
            shape = RoundedCornerShape(10.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            border = BorderStroke(1.dp, Color(255, 136, 44, 255)),
            modifier = Modifier.size(width = 200.dp, height = 200.dp)
        ) {
            state.image.let { image ->
                AnimatedVisibility(
                    visible = image == null,
                    enter = fadeIn(initialAlpha = 0.4f),
                    exit = fadeOut(animationSpec = tween(durationMillis = 250))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = "ImageNull",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                }
                if (image != null) {
                    Image(
                        bitmap = image, contentDescription = "ImageBit",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .rotate(90f),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(10.dp)
        )
        Text(text = "Resultado del analisis", fontSize = 20.sp, fontFamily = FontFamily.Serif)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(5.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.loading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .padding(start = 35.dp, end = 35.dp),
                    color = Color(255, 136, 44, 255),
                )
            } else {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    state.success?.map { data ->
                        item {
                            CardClassify(model = data)
                        }
                    }
                }
            }
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(5.dp)
        )

        Row(horizontalArrangement = Arrangement.Center) {
            Button(
                onClick = {
                    rememberPermission.launch(Manifest.permission.CAMERA)
                    state.invokeClick()
                },
                contentPadding = PaddingValues(all = 5.dp),
                shape = ButtonDefaults.shape,
                modifier = Modifier.size(width = 300.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(Color(255, 136, 44, 255))
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera), contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = "Capturar fotografia")
            }
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(10.dp)
        )
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            val composabl1 by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.neurallink))
            LottieAnimation(composition = composabl1)
        }

    }
}


@Composable
fun CardClassify(model: CategoryClassification) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .size(width = 350.dp, height = 115.dp)
            .padding(top = 10.dp)
            .clickable {
                isExpanded = !isExpanded
            },
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(2.dp, Color(255, 136, 44, 255)),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            val offset = Offset(5.0f, 10.0f)
            Column(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Objeto predecido",
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color(255, 136, 44, 255),
                                offset = offset,
                                blurRadius = 3f
                            )
                        ),
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        color = Color(53, 53, 53, 255)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column {
                        Text(
                            fontFamily = FontFamily.Monospace,
                            text = "Objeto",
                            color = Color(53, 53, 53, 255),
                            fontSize = 17.sp
                        )
                        Text(
                            fontFamily = FontFamily.Monospace,
                            text = model.label,
                            color = Color(77, 76, 76, 255),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp, start = 5.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(width = 40.dp, height = 20.dp))

                    Column {
                        Text(
                            fontFamily = FontFamily.Monospace,
                            text = "Exactitud",
                            color = Color(53, 53, 53, 255),
                            fontSize = 17.sp
                        )
                        Text(
                            fontFamily = FontFamily.Monospace,
                            text = model.score.toString(),
                            color = Color(77, 76, 76, 255),
                            fontSize = 15.sp,
                            modifier = Modifier.padding(top = 2.dp, start = 5.dp)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center, modifier = Modifier
                        .fillMaxWidth()
                ) {
                    AnimatedVisibility(
                        visible = isExpanded,
                        enter = fadeIn(initialAlpha = 0.4f),
                        exit = fadeOut(animationSpec = tween(durationMillis = 250))
                    ) {
                        Text(
                            text = "Index ${model.index}",
                            fontFamily = FontFamily.Serif,
                            color = Color(77, 76, 76, 255),
                        )
                    }
                }
            }
        }
    }
}



