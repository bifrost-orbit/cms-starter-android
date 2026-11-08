package com.northwilds.app.blocks

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.bifrostorbit.cms.CmsComponent
import com.bifrostorbit.cms.CmsLink
import com.northwilds.app.LocalOpenLink
import kotlinx.serialization.Serializable

@Serializable
data class Action(val label: String = "", val link: CmsLink? = null, val style: String = "primary")

@Composable
fun ActionBlock(block: CmsComponent) {
    val action = block.decodeFields<Action>()
    val href = action.link?.href ?: return
    val openLink = LocalOpenLink.current
    if (action.style == "primary") {
        Button(onClick = { openLink(href) }) { Text(action.label) }
    } else {
        OutlinedButton(onClick = { openLink(href) }) { Text(action.label) }
    }
}
