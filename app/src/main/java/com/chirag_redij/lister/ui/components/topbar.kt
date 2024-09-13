package com.chirag_redij.lister.ui.components

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shortcut
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
import io.github.jan.supabase.gotrue.user.UserInfo
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListerTopBar(
    userData: UserInfo?,
    onLogoutClick: (DropDownItem) -> Unit
) {

    var isContextMenuVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Timber.tag("User Metadata").d(userData?.userMetadata.toString())
    Timber.tag("User Info").d(userData?.toString())

    val userName = userData?.userMetadata?.getValue("name").toString().replace("\"","")
    val userProfile = userData?.userMetadata?.getValue("avatar_url").toString().replace("\"","")

    Timber.tag("Data").d("%s %s", userName, userProfile)

    TopAppBar(
        title = {
            Text(text = "Hello " + (userName ?: "User"))
        },
        actions = {
            AsyncImage(
                model = userProfile ?: R.drawable.person,
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

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(imageVector = DropDownItem.Shortcut.icon, contentDescription = DropDownItem.Shortcut.title)
                        },
                        text = {
                            Text(text = DropDownItem.Shortcut.title)
                        },
                        onClick = {
                            isContextMenuVisible = false
                            onLogoutClick(DropDownItem.Shortcut)
                        }
                    )
                }

                for (item in DropDownItem.entries.dropLast(1)) {
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
    DeleteAccount("Delete Account", Icons.Filled.AccountBox),
    Logout("Logout", Icons.Filled.Logout),
    Shortcut("Pin Shortcut", Icons.Filled.Shortcut)
}