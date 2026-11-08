package com.northwilds.app.blocks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bifrostorbit.cms.CmsComponent
import kotlinx.serialization.Serializable

@Serializable
data class Hero(
    val eyebrow: String = "",
    val heading: String = "",
    val headingAccent: String = "",
    val description: String = "",
)

@Composable
fun HeroBlock(block: CmsComponent) {
    val hero = block.decodeFields<Hero>()
    Column(Modifier.padding(horizontal = 20.dp, vertical = 32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (hero.eyebrow.isNotEmpty()) Text(hero.eyebrow, style = MaterialTheme.typography.labelLarge)
        Text(listOf(hero.heading, hero.headingAccent).filter { it.isNotEmpty() }.joinToString(" "), style = MaterialTheme.typography.headlineLarge)
        RichText(hero.description)
        CmsBlocks(block.children["primaryAction"].orEmpty() + block.children["secondaryAction"].orEmpty())
    }
}
