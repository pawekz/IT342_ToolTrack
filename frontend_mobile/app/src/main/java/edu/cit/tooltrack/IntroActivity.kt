package edu.cit.tooltrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ToolTrackTheme {
                IntroScreen(
                    onDoneClick = {
                        // Navigate to LoginActivity when intro is complete
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun IntroScreen(onDoneClick: () -> Unit) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val introSlides = listOf(
        IntroSlide(
            title = stringResource(id = R.string.intro_title_1),
            description = stringResource(id = R.string.intro_description_1),
            backgroundColor = colorResource(id = R.color.intro_color_1),
            imageRes = R.drawable.ic_launcher_foreground
        ),
        IntroSlide(
            title = stringResource(id = R.string.intro_title_2),
            description = stringResource(id = R.string.intro_description_2),
            backgroundColor = colorResource(id = R.color.intro_color_2),
            imageRes = R.drawable.ic_launcher_foreground
        ),
        IntroSlide(
            title = stringResource(id = R.string.intro_title_3),
            description = stringResource(id = R.string.intro_description_3),
            backgroundColor = colorResource(id = R.color.intro_color_3),
            imageRes = R.drawable.ic_launcher_foreground
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = introSlides.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val slide = introSlides[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(slide.backgroundColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    // Image
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color(0xFF2196F3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = slide.imageRes),
                            contentDescription = null,
                            modifier = Modifier.size(180.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Title
                    Text(
                        text = slide.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = slide.description,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // Bottom navigation and indicators
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Page indicators
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 32.dp),
                activeColor = Color.White,
                inactiveColor = colorResource(id = R.color.white_50)
            )

            // Navigation buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Skip button (invisible on last page)
                if (pagerState.currentPage < introSlides.size - 1) {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(introSlides.size - 1)
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text("SKIP")
                    }
                } else {
                    // Empty spacer to maintain layout
                    Spacer(modifier = Modifier.width(64.dp))
                }

                // Next/Done button
                Button(
                    onClick = {
                        if (pagerState.currentPage < introSlides.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onDoneClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage < introSlides.size - 1) "NEXT" else "GET STARTED"
                    )
                }
            }
        }
    }
}

data class IntroSlide(
    val title: String,
    val description: String,
    val backgroundColor: Color,
    val imageRes: Int
)
