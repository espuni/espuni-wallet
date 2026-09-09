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

package eu.europa.ec.resourceslogic.theme.values

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import eu.europa.ec.resourceslogic.theme.templates.ThemeShapesTemplate
import eu.europa.ec.resourceslogic.theme.values.ThemeShapes.Companion.LARGE
import eu.europa.ec.resourceslogic.theme.values.ThemeShapes.Companion.SMALL

/**
 * espuni corner radii, from `apps/portal/app/tokens.css`.
 *
 * The scale is deliberately tighter than both shadcn's 10px default and Material's
 * own 4/8/12/16/28dp, so every component that does not pin its own shape will read
 * rounder than the web unless it goes through [shapes].
 *
 * `--radius-xs` chips, inline code, badges · `--radius-sm` buttons and inputs ·
 * `--radius-md` cards and panels · `--radius-lg` modals and the session widget.
 * The system never goes above 12dp, so `extraLarge` repeats `large`. A fully round
 * shape is reserved for status dots and is never used for a badge, which stays a
 * rectangle so that "status results read as data, not as consumer labels".
 *
 * `small` follows the 6px the design system page documents for buttons and inputs.
 * The shadcn primitives in the same codebase use 12px; the two coexist upstream
 * (§6.8) and the documented value is taken as canon here.
 */
class ThemeShapes {
    companion object {
        const val EXTRA_SMALL = 4.0
        const val SMALL = 6.0
        const val MEDIUM = 8.0
        const val LARGE = 12.0
        const val EXTRA_LARGE = 12.0

        val shapes = ThemeShapesTemplate(
            extraSmall = EXTRA_SMALL,
            small = SMALL,
            medium = MEDIUM,
            large = LARGE,
            extraLarge = EXTRA_LARGE
        )
    }
}

val Shapes.bottomCorneredShapeSmall: Shape
    @Composable get() = RoundedCornerShape(bottomStart = SMALL.dp, bottomEnd = SMALL.dp)

val Shapes.topCorneredShapeSmall: Shape
    @Composable get() = RoundedCornerShape(topStart = SMALL.dp, topEnd = SMALL.dp)

val Shapes.allCorneredShapeSmall: Shape
    @Composable get() = RoundedCornerShape(SMALL.dp)

val Shapes.allCorneredShapeLarge: Shape
    @Composable get() = RoundedCornerShape(LARGE.dp)