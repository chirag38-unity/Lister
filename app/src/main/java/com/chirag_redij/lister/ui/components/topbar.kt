package com.chirag_redij.lister.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.chirag_redij.lister.R
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListerTopBar(
    userData: FirebaseUser?,
    onLogoutClick: (DropDownItem) -> Unit
) {

    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }

    TopAppBar(
        title = {
            Text(text = "Hello " + (userData?.displayName ?: "User"))
        },
        actions = {
            AsyncImage(
                model = userData?.photoUrl ?: R.drawable.person,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable {
                        isContextMenuVisible = true
                    },
                contentScale = ContentScale.Crop
            )
            DropdownMenu(
                offset = DpOffset(8.dp, 8.dp),
                expanded = isContextMenuVisible,
                onDismissRequest = { isContextMenuVisible = false }
            ) {
                for (item in DropDownItem.entries) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(imageVector = item.icon, contentDescription = item.title)
                        },
                        text = {
                            Text(text = item.title)
                        },
                        onClick = {
                            isContextMenuVisible = false
                            onLogoutClick(item)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = Color.Unspecified
        )
    )
}

enum class DropDownItem(val title: String, val icon: ImageVector) {
    Settings("Settings", Icons.Filled.Settings),
    Logout("Logout", Icons.Filled.Logout)
}