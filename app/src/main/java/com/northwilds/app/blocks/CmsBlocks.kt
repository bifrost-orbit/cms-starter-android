package com.northwilds.app.blocks

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bifrostorbit.cms.CmsComponent
import com.northwilds.app.BuildConfig

val components: Map<String, @Composable (CmsComponent) -> Unit> = mapOf(
    "hero" to { HeroBlock(it) },
    "featureSpotlight" to { FeatureSpotlightBlock(it) },
    "action" to { ActionBlock(it) },
)

@Composable
fun CmsBlock(block: CmsComponent) {
    val draw = components[block.type]
    when {
        draw != null -> draw(block)
        BuildConfig.DEBUG -> MissingBlock(block)
    }
}

@Composable
fun CmsBlocks(blocks: List<CmsComponent>, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        blocks.forEach { block -> key(block.id) { CmsBlock(block) } }
    }
}

@Composable
private fun MissingBlock(block: CmsComponent) {
    Text(
        "No composable for \"${block.type}\" yet",
        Modifier.fillMaxWidth().padding(20.dp, 8.dp).border(1.dp, MaterialTheme.colorScheme.outline).padding(12.dp),
        style = MaterialTheme.typography.bodySmall,
    )
}
