package com.northwilds.app.blocks

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.fromHtml
import com.northwilds.app.LocalOpenLink

@Composable
fun RichText(html: String) {
    val openLink = LocalOpenLink.current
    val text = remember(html) {
        AnnotatedString.fromHtml(html.trim(), linkInteractionListener = { link ->
            (link as? LinkAnnotation.Url)?.let { openLink(it.url) }
        })
    }
    Text(text, style = MaterialTheme.typography.bodyLarge)
}
