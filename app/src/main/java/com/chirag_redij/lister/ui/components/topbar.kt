package com.chirag_redij.lister.ui.components

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Shortcut
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Logout
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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

    val context = LocalContext.current

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
            Text(text = buildString {
                append(stringResource(R.string.hello))
                append(" ")
                append((userName ?: "User"))
            })
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
                            Icon(imageVector = DropDownItem.Shortcut.icon, contentDescription = DropDownItem.Shortcut.getTitle(context))
                        },
                        text = {
                            Text(text = DropDownItem.Shortcut.getTitle(context))
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
                            Icon(imageVector = item.icon, contentDescription = item.getTitle(context))
                        },
                        text = {
                            Text(text = item.getTitle(context))
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
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = Color.Unspecified
        )
    )
}

//enum class DropDownItem(val title: String, val icon: ImageVector) {
//    DeleteAccount(context.getString(R.string.delete_account), Icons.Filled.AccountBox),
//    Logout(context.getString(R.string.logout), Icons.Filled.Logout),
//    Shortcut(context.getString(R.string.pin_shortcut), Icons.Filled.Shortcut)
//}

enum class DropDownItem(val icon: ImageVector) {
    DeleteAccount(Icons.Filled.AccountBox),
    Logout(Icons.AutoMirrored.Filled.Logout),
    Shortcut(Icons.AutoMirrored.Filled.Shortcut);

    fun getTitle(context: Context): String {
        return when (this) {
            DeleteAccount -> context.getString(R.string.delete_account)
            Logout -> context.getString(R.string.logout) // You may define a resource for this as well
            Shortcut -> context.getString(R.string.pin_shortcut) // You may define a resource for this as well
        }
    }
}