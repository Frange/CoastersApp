package com.frange.coasters.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.frange.coasters.R
import com.frange.coasters.domain.model.Ride

@Composable
fun RideCard(
    ride: Ride,
    onToggleFavorite: (Ride) -> Unit
) {
    val isFav = ride.isFavourite
    val backgroundColor = if (isFav) Color(0xFF006064) else Color(0xFFE0E0E0)
    val contentColor = Color.White
    val accentColor = Color(0xFF00BCD4)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(
                    onClick = { onToggleFavorite(ride) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = if (isFav) accentColor else contentColor.copy(alpha = 0.5f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = ride.name?.uppercase() ?: "",
                    style = TextStyle(
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    ),
                    maxLines = 2,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                if (ride.isOpen) {
                    WaitTimeBadge(ride.waitTime)
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.closed_jm),
                        contentDescription = "Closed",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WaitTimeBadge(minutes: Int) {
    val badgeColor = when {
        minutes < 15 -> Color(0xFF43A047)
        minutes < 45 -> Color(0xFFFB8C00)
        else -> Color(0xFFE53935)
    }

    Surface(
        color = badgeColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = "$minutes MIN",
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 12.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}