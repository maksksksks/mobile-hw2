package com.example.spacex.GUI

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spacex.Data.Launch
import com.example.spacex.Data.Links
import com.example.spacex.Data.Rockets

@Composable
fun FirstScreen(
    onNext: () -> Unit,
    onCapabilityChange: (Boolean) -> Unit
) {
    val localLaunches: List<Launch> = listOf(
        Launch(
            flight_number = 1,
            mission_name = "FalconSat",
            launch_year = "2006",
            is_favorite = true,
            links = Links(
                mission_patch_small = "https://images2.imgbox.com/3c/0e/T8iJcSN3_o.png"
            )
        ),
        Launch(
            flight_number = 2,
            mission_name = "DemoSat",
            launch_year = "2007",
            is_favorite = false,
            links = Links(
                mission_patch_small = "https://images2.imgbox.com/4f/e3/I0lkuJ2e_o.png"
            )
        )
    )
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Просмотр списка запусков SpaceX",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = localLaunches,
                key = { launch -> launch.flight_number ?: launch.hashCode() }
            ) { launch ->
                LaunchItem(
                    launch = launch,
                    onClick = {},
                    onFavoriteClick = {}
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onCapabilityChange(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
            ) {
                Text("Пропустить")
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
            ) {
                Text("Дальше")
            }
        }
    }
}

@Composable
fun SecondScreen(
    onNext: () -> Unit,
    onCapabilityChange: (Boolean) -> Unit
) {
    val localRockets: List<Rockets> = listOf(
        Rockets(
            flickr_images = listOf(
                "https://imgur.com/DaCfMsj.jpg",
                "https://imgur.com/azYafd8.jpg"
            ),
            active = false,
            rocket_name = "Falcon 1",
            rocket_id = "falcon1",
            success_rate_pct = 40,
            cost_per_launch = 6700000,
            is_favorite = false
        ),
        Rockets(
            flickr_images = listOf(
                "https://farm1.staticflickr.com/929/28787338307_3453a11a77_b.jpg",
                "https://farm4.staticflickr.com/3955/32915197674_eee74d81bb_b.jpg"
            ),
            active = true,
            rocket_name = "Falcon 9",
            rocket_id = "falcon9",
            success_rate_pct = 97,
            cost_per_launch = 50000000,
            is_favorite = true
        )
    )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "Просмотр списка ракет SpaceX",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(
                items = localRockets,
                key = { rocket -> rocket.id ?: rocket.hashCode() }
            ) { rocket ->
                RocketItem(
                    rocket = rocket,
                    onClick = { },
                    onFavoriteClick = { }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onCapabilityChange(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
            ) {
                Text("Пропустить")
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
            ) {
                Text("Дальше")
            }
        }
    }
}

@Composable
fun ThirdScreen(
    onNext: () -> Unit,
    onCapabilityChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "О компании SpaceX",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                DetailCard("Основные факты") {
                    DetailRow("Основана", "2002 год")
                    DetailRow("Основатель", "Илон Маск")
                    DetailRow("Штаб-квартира", "Хоторн, Калифорния")
                    DetailRow("Сотрудников", "> 13 000")
                }
            }

            item {
                DetailCard("Достижения") {
                    Text(
                        "• Первый частный корабль, доставивший груз на МКС",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• Первая частная компания, отправившая астронавтов на МКС",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "• Создание многоразовых ракет-носителей",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp
                    )
                }
            }

            item {
                DetailCard("Миссия") {
                    Text(
                        "Сделать человечество мультипланетным видом и снизить стоимость космических перевозок.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { onCapabilityChange(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
            ) {
                Text("Пропустить")
            }
            Button(
                onClick = onNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .width(140.dp)
            ) {
                Text("Дальше")
            }
        }
    }
}

@Composable
fun FortyScreen(
    onNext: () -> Unit,
    onCapabilityChange: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
    ) {
        Spacer(Modifier.height(50.dp))
        Text(
            text = "Личный профиль",
            fontSize = 28.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "U",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            "Имя Пользователя",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "user@example.com",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                DetailCard("Настройки") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Тема приложения", color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            "Системная",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            item {
                DetailCard("Избранное (Превью)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Запусков",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "1",
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Ракет",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { onCapabilityChange(false) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(0.8f)
            ) {
                Text("Начать работу")
            }
        }
    }
}