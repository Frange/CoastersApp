package com.frange.coasters.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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
fun RideCard(ride: Ride) {
    val backgroundColor = if (ride.isFavourite) Color(0xFF006064) else Color(0xFFE0E0E0)
    val contentColor = if (ride.isFavourite) Color.White else Color.Black

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(4.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (ride.isFavourite) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }

                Text(
                    text = ride.name?.uppercase() ?: "",
                    style = TextStyle(
                        color = contentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 1
                )
            }

            if (ride.waitTime >= 5) {
                WaitTimeBadge(ride.waitTime)
            } else {
                Icon(
                    painter = painterResource(id = R.drawable.closed_jm),
                    contentDescription = "Closed",
                    tint = contentColor.copy(alpha = 0.7f),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun WaitTimeBadge(minutes: Int) {
    val color = when {
        minutes < 15 -> Color(0xFF2E7D32)
        minutes < 45 -> Color(0xFFEF6C00)
        else -> Color(0xFFC62828)
    }

    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "$minutes MIN",
            color = Color.White,
            style = TextStyle(
                fontWeight = FontWeight.Black,
                fontSize = 13.sp
            ),
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 2.dp
            )
        )
    }
}