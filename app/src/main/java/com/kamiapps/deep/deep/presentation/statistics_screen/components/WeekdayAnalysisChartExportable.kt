package com.kamiapps.deep.deep.presentation.statistics_screen.components


import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.kamiapps.deep.R
import com.kamiapps.deep.deep.util.captureToBitmap
import com.kamiapps.deep.deep.util.shareBitmap

@Composable
fun ExportLayout(
    title: String,
    subtitle: AnnotatedString,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(24.dp))
            .background(color = Color.White)
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.Start)

        )

        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Start,
            modifier = Modifier.align(Alignment.Start)
        )

        content()

        Image(
            painter = painterResource(id = R.drawable.exp_layout_logo), // logon sabit
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp),
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
fun ExportAsBitmap(
    title: String,
    subtitle: AnnotatedString,
    content: @Composable () -> Unit,
    onExported: () -> Unit,

    ) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            ComposeView(ctx).apply {
                visibility = View.INVISIBLE // 👈 görünmez yap ama boyutu bozma
                setContent {
                    ExportLayout(
                        title = title,
                        subtitle = subtitle,
                        content = content
                    )
                }

                postDelayed({
                    val bitmap = this.captureToBitmap()
                    shareBitmap(context, bitmap) // artık galeriye değil, paylaşım ekranına
                    onExported()
                }, 500)
            }
        },
        modifier = Modifier.fillMaxWidth() // 👈 render boyutu tam olsun

    )

}