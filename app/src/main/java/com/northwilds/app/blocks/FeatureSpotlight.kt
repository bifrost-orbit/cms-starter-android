package com.northwilds.app.blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bifrostorbit.cms.CmsComponent
import com.bifrostorbit.cms.CmsImage
import com.bifrostorbit.cms.CmsImages
import kotlinx.serialization.Serializable

@Serializable
data class FeatureSpotlight(
    val eyebrow: String = "",
    val heading: String = "",
    val body: String = "",
    val image: CmsImage? = null,
)

@Composable
fun FeatureSpotlightBlock(block: CmsComponent) {
    val feature = block.decodeFields<FeatureSpotlight>()
    Column(Modifier.padding(horizontal = 20.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        feature.image?.let {
            AsyncImage(model = CmsImages.url(it.url, width = 1080), contentDescription = it.alt, modifier = Modifier.fillMaxWidth())
        }
        if (feature.eyebrow.isNotEmpty()) Text(feature.eyebrow, style = MaterialTheme.typography.labelLarge)
        Text(feature.heading, style = MaterialTheme.typography.headlineSmall)
        RichText(feature.body)
        CmsBlocks(block.children["action"].orEmpty())
    }
}
