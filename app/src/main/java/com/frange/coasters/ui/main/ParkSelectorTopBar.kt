package com.frange.coasters.ui.main

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.frange.coasters.domain.model.ParkInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParkSelectorTopBar(
    parks: List<ParkInfo>,
    onParkSelected: (ParkInfo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedName by remember { mutableStateOf(parks.firstOrNull()?.name ?: "Seleccionar Parque") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Parque") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            parks.forEach { park ->
                DropdownMenuItem(
                    text = { Text(park.name) },
                    onClick = {
                        selectedName = park.name
                        expanded = false
                        onParkSelected(park)
                    }
                )
            }
        }
    }
}