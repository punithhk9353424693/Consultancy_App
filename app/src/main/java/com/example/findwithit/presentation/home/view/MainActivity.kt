package com.example.findwithit.presentation.home.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.findwithit.R
import com.example.findwithit.presentation.home.viewmodel.InquiryViewModel
import com.example.findwithit.presentation.theme.FindWithItTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val inquiryViewModel: InquiryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)

    }


}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FindWithItTheme {
        Greeting("Android")
    }
}