package com.example.mobile_hw2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import mobilehw2.composeapp.generated.resources.Res
import mobilehw2.composeapp.generated.resources.compose_multiplatform


@Composable
fun FirstScreen(onNextClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(Color(14, 14, 14))
            .safeContentPadding()
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = "https://avatars.mds.yandex.net/i?id=99cf81596b060af939955e7a896c6d06_l-10256664-images-thumbs&n=13",
                //model = "https://www.techslang.com/wp-content/uploads/2023/01/21113y_atgn8or841mmutj9ck.jpg",
                contentDescription = "Кино постер",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(Res.drawable.compose_multiplatform), // Заглушка во время загрузки
                error = painterResource(Res.drawable.compose_multiplatform), // Заглушка при ошибке
                onLoading = {
                    // Действия при загрузке
                },
                onError = { state ->
                    println("Error loading image: ${state.result.throwable}")
                },
                onSuccess = {
                    // Действия при успешной загрузке
                }
            )
        }
        Text(
            text = "CinemaКино",
            fontSize = 40.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = "Подборка для вечера уже здесь: фильмы, сериалы и новинки.\nНайди что посмотреть — за пару секунд.",
            textAlign = TextAlign.Center,
            color = Color(179, 179, 179),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
        Button(
            onClick = onNextClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier
                .height(50.dp)
                .width(200.dp)
        ) {
            Text("Вперёд!")
        }
    }
}