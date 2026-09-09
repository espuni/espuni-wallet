/*
 * Copyright (c) 2026 European Commission
 *
 * Licensed under the EUPL, Version 1.2 or - as soon they will be approved by the European
 * Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work
 * except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF
 * ANY KIND, either express or implied. See the Licence for the specific language
 * governing permissions and limitations under the Licence.
 */

package eu.europa.ec.uilogic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.europa.ec.resourceslogic.theme.values.verify
import eu.europa.ec.uilogic.component.preview.PreviewTheme
import eu.europa.ec.uilogic.component.preview.ThemeModePreviews

/** The brand name is lowercase everywhere, without exception. */
private const val WORDMARK = "espuni"

/**
 * The espuni wordmark: the word, then the verify square.
 *
 * In product the wordmark is typographic, not an image — the word in Geist 700 with
 * `-0.02em` of tracking, followed by a square of `0.4em` sitting just above the
 * baseline. Composing it rather than shipping a PNG is what keeps it sharp at any
 * size and lets the word follow the theme while the square keeps the brand green.
 *
 * The square is one of only two places where the green appears without meaning
 * "this verification passed" — the other is [EspuniTopAccent].
 */
@Composable
fun EspuniWordmark(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    color: Color = MaterialTheme.colorScheme.onSurface,
    markColor: Color = MaterialTheme.colorScheme.verify,
) {
    val density = LocalDensity.current
    // The web sizes the mark in `em`, so every part scales with the word.
    val em = with(density) { fontSize.toDp() }
    val liftPx = with(density) { (em * VERTICAL_ALIGN_EM).roundToPx() }

    Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
        Text(
            text = WORDMARK,
            modifier = Modifier.alignByBaseline(),
            color = color,
            fontSize = fontSize,
            fontWeight = FontWeight.W700,
            letterSpacing = fontSize * TRACKING_EM,
        )
        Spacer(modifier = Modifier.width(em * GAP_EM))
        Box(
            // Aligning the square's bottom edge to the text baseline, then raising
            // it by 0.06em, is what `vertical-align` does on the web.
            modifier = Modifier
                .alignBy { it.measuredHeight + liftPx }
                .size(em * MARK_EM)
                .clip(RoundedCornerShape(1.dp))
                .background(markColor)
        )
    }
}

/**
 * The 3dp rule of verify green that sits above every page of the web product.
 *
 * On Android it is drawn *below* the status bar, at the top of the app content,
 * rather than by tinting the status bar itself: a green system bar would read as
 * the app's primary colour, and the primary is the text colour, not the green.
 */
@Composable
fun EspuniTopAccent(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(TOP_ACCENT_HEIGHT)
            .background(MaterialTheme.colorScheme.verify)
    )
}

/** `--radius-xs`-sized square of 0.4em, per `.espuniMark`. */
private const val MARK_EM = 0.4f

/** `letter-spacing: -0.02em` on the word. */
private const val TRACKING_EM = -0.02f

/** `margin-left: 2px` at the canonical 16px size. */
private const val GAP_EM = 0.125f

/** `vertical-align: 0.06em`. */
private const val VERTICAL_ALIGN_EM = 0.06f

/** `.siteTopAccent` is 3px on the web. */
private val TOP_ACCENT_HEIGHT = 3.dp

@ThemeModePreviews
@Composable
private fun EspuniWordmarkPreview() {
    PreviewTheme {
        Column {
            EspuniTopAccent()
            EspuniWordmark(modifier = Modifier.padding(16.dp))
            EspuniWordmark(modifier = Modifier.padding(16.dp), fontSize = 32.sp)
        }
    }
}
