package edu.cit.tooltrack

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState



class IntroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ToolTrackTheme {
                introScreen(
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

@Preview
@PreviewParameter(IntroSlidePreviewProvider::class)
@Composable
fun introScreen(onDoneClick: () -> Unit = {}) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    val introSlides = listOf(
        IntroSlide(
            title = stringResource(id = R.string.intro_title_1),
            description = stringResource(id = R.string.intro_description_1),
            backgroundColor = colorResource(id = R.color.white_tooltrack),
            imageRes = R.drawable.tooltrack_slide_1

        ),
        IntroSlide(
            title = stringResource(id = R.string.intro_title_2),
            description = stringResource(id = R.string.intro_description_2),
            backgroundColor = colorResource(id = R.color.white_tooltrack),
            imageRes = R.drawable.tooltrack_slide_2
        ),
        IntroSlide(
            title = stringResource(id = R.string.intro_title_3),
            description = stringResource(id = R.string.intro_description_3),
            backgroundColor = colorResource(id = R.color.white_tooltrack),
            imageRes = R.drawable.tooltrack_slide_3
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
                            .size(240.dp)
                            .background(Color(0xFFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = slide.imageRes),
                            contentDescription = "woman with tools",
                            modifier = Modifier.size(300.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Title
                    Text(
                        text = slide.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    Text(
                        text = slide.description,
                        fontSize = 16.sp,
                        color = colorResource(id = R.color.intro_slide_decription),
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
                .padding(bottom = 48.dp)
        ) {
            // Navigation buttons - centered
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Page indicators
                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    pageCount = introSlides.size,
                    modifier = Modifier.padding(bottom = 16.dp),
                    activeColor = colorResource(id = R.color.green_tooltrack),
                    inactiveColor = colorResource(id = R.color.lightgreen_tooltrack),
                    indicatorWidth = 8.dp,
                    indicatorHeight = 8.dp,
                    spacing = 8.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Next/Done button with specific styling
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
                    modifier = Modifier
                        .width(213.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.green_tooltrack),
                        contentColor = colorResource(id = R.color.white_tooltrack)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (pagerState.currentPage < introSlides.size - 1) "NEXT" else "GET STARTED",
                        color = colorResource(id = R.color.white_tooltrack),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
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

class IntroSlidePreviewProvider : PreviewParameterProvider<IntroSlide> {
    override val values = sequenceOf(
        IntroSlide(
            title = "Welcome to ToolTrack",
            description = "The easiest way to manage your tools",
            backgroundColor = Color(474747),
            imageRes = R.drawable.tooltrack_slide_1
        )
    )
}
