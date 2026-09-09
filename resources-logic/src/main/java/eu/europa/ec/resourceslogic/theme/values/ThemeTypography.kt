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

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import eu.europa.ec.resourceslogic.R
import eu.europa.ec.resourceslogic.theme.templates.ThemeTextStyle
import eu.europa.ec.resourceslogic.theme.templates.ThemeTypographyTemplate
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeFont
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeFontStyle
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeFontWeight
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeTextAlign

/**
 * espuni type scale, from `apps/portal/app/[lang]/design-system/page.tsx` and
 * `landing.module.css`, mapped onto the Material 3 slots per §2.2 of
 * `docs/brand-native-criteria.md`.
 *
 * Two rules travel with the scale:
 *
 *  - **Headlines are tight fragments.** Weight 700–800 with negative tracking that
 *    grows with the size. In CSS the tracking is in `em`, in Compose it is
 *    absolute, so `letterSpacing = fontSize * em` — the values below cannot be
 *    copied between slots.
 *  - **`bodySmall` is W500, not W400.** The system raises the weight as the size
 *    drops instead of lowering the contrast.
 *
 * The mono family is not decorative: it is the register of machine values —
 * session ids, claim values, protocol codes, counters. A claim prints literally
 * (`age_over_18: true`) rather than as an icon, and therefore never goes through
 * the translation system. The mono styles have no M3 slot and live as [Typography]
 * extension properties at the bottom of this file.
 */
internal class ThemeTypography {
    companion object {
        val typo: ThemeTypographyTemplate
            get() {
                return ThemeTypographyTemplate(
                    // hero — clamp(48,7vw,80)/800/-0.04em/1.0
                    displayLarge = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 48,
                        lineHeight = 48,
                        fontWeight = ThemeFontWeight.W800,
                        letterSpacing = -1.92f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // Interpolated: the web has no slot between hero and display.
                    displayMedium = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 46,
                        lineHeight = 48,
                        fontWeight = ThemeFontWeight.W800,
                        letterSpacing = -1.38f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // display — 44/800/-0.03em/1.05
                    displaySmall = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 44,
                        lineHeight = 46,
                        fontWeight = ThemeFontWeight.W800,
                        letterSpacing = -1.32f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // section title — clamp(28,4vw,38)/700/-0.03em/1.1
                    headlineLarge = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 32,
                        lineHeight = 36,
                        fontWeight = ThemeFontWeight.W700,
                        letterSpacing = -0.96f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // heading-lg — 28/700/-0.02em
                    headlineMedium = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 28,
                        lineHeight = 34,
                        fontWeight = ThemeFontWeight.W700,
                        letterSpacing = -0.56f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // Interpolated: the web jumps from 28 straight to 19.
                    headlineSmall = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 24,
                        lineHeight = 30,
                        fontWeight = ThemeFontWeight.W700,
                        letterSpacing = -0.48f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // heading — 19/700
                    titleLarge = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 19,
                        lineHeight = 26,
                        fontWeight = ThemeFontWeight.W700,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // card title — 16/600
                    titleMedium = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 16,
                        lineHeight = 22,
                        fontWeight = ThemeFontWeight.W600,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // item title — 15/600
                    titleSmall = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 15,
                        lineHeight = 20,
                        fontWeight = ThemeFontWeight.W600,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // link / CTA — 13/500
                    labelLarge = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 13,
                        lineHeight = 18,
                        fontWeight = ThemeFontWeight.W500,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // meta — 12/500
                    labelMedium = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 12,
                        lineHeight = 16,
                        fontWeight = ThemeFontWeight.W500,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // caption — 11/500, small caps, +0.06em. Always `textMuted`.
                    labelSmall = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 11,
                        lineHeight = 14,
                        fontWeight = ThemeFontWeight.W500,
                        letterSpacing = 0.66f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // body — 15/400/1.6
                    bodyLarge = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 15,
                        lineHeight = 24,
                        fontWeight = ThemeFontWeight.W400,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // body-2 — 14/400/1.6
                    bodyMedium = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 14,
                        lineHeight = 22,
                        fontWeight = ThemeFontWeight.W400,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    ),
                    // body-sm — 13/500. Weight up, not contrast down.
                    bodySmall = ThemeTextStyle(
                        fontFamily = Geist,
                        fontSize = 13,
                        lineHeight = 20,
                        fontWeight = ThemeFontWeight.W500,
                        letterSpacing = 0f,
                        textAlign = ThemeTextAlign.Start
                    )
                )
            }
    }
}

private fun geist(res: Int, weight: ThemeFontWeight) = ThemeFont(
    res = res,
    weight = weight,
    style = ThemeFontStyle.Normal,
)

/**
 * Every Geist weight the scale uses, so that a slot's `fontWeight` resolves against
 * a real face instead of being synthesised.
 */
internal val Geist: List<ThemeFont> = listOf(
    geist(R.font.geist_regular, ThemeFontWeight.W400),
    geist(R.font.geist_medium, ThemeFontWeight.W500),
    geist(R.font.geist_semibold, ThemeFontWeight.W600),
    geist(R.font.geist_bold, ThemeFontWeight.W700),
    geist(R.font.geist_extrabold, ThemeFontWeight.W800),
)

/** Geist Mono, the register of machine values. */
val GeistMonoFontFamily: FontFamily = FontFamily(
    Font(R.font.geist_mono_regular, FontWeight.W400),
    Font(R.font.geist_mono_medium, FontWeight.W500),
)

/** Tabular figures, so a counter does not jitter as its digits change. */
private const val TABULAR_FIGURES = "tnum"

/** Session ids, claim values, protocol codes. Geist Mono 13. */
val Typography.mono: TextStyle
    get() = TextStyle(
        fontFamily = GeistMonoFontFamily,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.W400,
        fontFeatureSettings = TABULAR_FIGURES,
    )

/** The chip register. Badges are mono by default in the web system. */
val Typography.monoBadge: TextStyle
    get() = TextStyle(
        fontFamily = GeistMonoFontFamily,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W500,
        fontFeatureSettings = TABULAR_FIGURES,
    )

/** Mono caption, small caps, +0.06em. */
val Typography.monoCaption: TextStyle
    get() = TextStyle(
        fontFamily = GeistMonoFontFamily,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.66.sp,
        fontFeatureSettings = TABULAR_FIGURES,
    )

/** Section eyebrow — the `// pricing` register. Mono 10, small caps, +0.06em. */
val Typography.monoEyebrow: TextStyle
    get() = TextStyle(
        fontFamily = GeistMonoFontFamily,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.W500,
        letterSpacing = 0.6.sp,
        fontFeatureSettings = TABULAR_FIGURES,
    )
