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

package eu.europa.ec.businesslogic.config

import eu.europa.ec.eudi.rqesui.infrastructure.theme.ThemeManager as RqesThemeManager
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.ThemeColorsTemplate as RqesColors
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.ThemeTextStyle as RqesTextStyle
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.ThemeTypographyTemplate as RqesTypography
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.structures.ThemeFont as RqesFont
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.structures.ThemeFontStyle as RqesFontStyle
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.structures.ThemeFontWeight as RqesFontWeight
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.structures.ThemeTextAlign as RqesTextAlign
import eu.europa.ec.eudi.rqesui.infrastructure.theme.templates.structures.ThemeTextDecoration as RqesTextDecoration
import eu.europa.ec.resourceslogic.theme.EspuniTokens
import eu.europa.ec.resourceslogic.theme.templates.ThemeColorsTemplate
import eu.europa.ec.resourceslogic.theme.templates.ThemeTextStyle
import eu.europa.ec.resourceslogic.theme.templates.ThemeTypographyTemplate
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeFont
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeFontStyle
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeFontWeight
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeTextAlign
import eu.europa.ec.resourceslogic.theme.templates.structures.ThemeTextDecoration

/**
 * Hands the espuni theme to the embedded RQES signing SDK.
 *
 * That SDK ships a private copy of this project's theme system — same class shapes,
 * different package — together with its own Roboto faces and the reference app's
 * blue palette. Its screens are part of the wallet's remote-signature flow, so
 * leaving them on the defaults would break the brand halfway through a user
 * journey. The SDK exposes `EudiRQESUiConfig.themeManager`, so the fix is to
 * translate our templates into its own and hand them over.
 *
 * The translation is total and mechanical; [EspuniTokens] stays the only place a
 * token value is written down. The SDK's builder takes colours and typography but
 * no shapes, so its corner radii remain its own.
 */
internal fun espuniRqesThemeManager(): RqesThemeManager =
    RqesThemeManager.Builder()
        .withLightColors(EspuniTokens.lightColors.toRqes())
        .withDarkColors(EspuniTokens.darkColors.toRqes())
        .withTypography(EspuniTokens.typography.toRqes())
        .build(buildStatic = false)

private fun ThemeColorsTemplate.toRqes(): RqesColors = RqesColors(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    secondary = secondary,
    onSecondary = onSecondary,
    secondaryContainer = secondaryContainer,
    onSecondaryContainer = onSecondaryContainer,
    tertiary = tertiary,
    onTertiary = onTertiary,
    tertiaryContainer = tertiaryContainer,
    onTertiaryContainer = onTertiaryContainer,
    error = error,
    errorContainer = errorContainer,
    onError = onError,
    onErrorContainer = onErrorContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    inverseOnSurface = inverseOnSurface,
    inverseSurface = inverseSurface,
    inversePrimary = inversePrimary,
    surfaceTint = surfaceTint,
    outlineVariant = outlineVariant,
    scrim = scrim,
    surfaceBright = surfaceBright,
    surfaceDim = surfaceDim,
    surfaceContainer = surfaceContainer,
    surfaceContainerHigh = surfaceContainerHigh,
    surfaceContainerHighest = surfaceContainerHighest,
    surfaceContainerLow = surfaceContainerLow,
    surfaceContainerLowest = surfaceContainerLowest,
    primaryFixed = primaryFixed,
    primaryFixedDim = primaryFixedDim,
    onPrimaryFixed = onPrimaryFixed,
    onPrimaryFixedVariant = onPrimaryFixedVariant,
    secondaryFixed = secondaryFixed,
    secondaryFixedDim = secondaryFixedDim,
    onSecondaryFixed = onSecondaryFixed,
    onSecondaryFixedVariant = onSecondaryFixedVariant,
    tertiaryFixed = tertiaryFixed,
    tertiaryFixedDim = tertiaryFixedDim,
    onTertiaryFixed = onTertiaryFixed,
    onTertiaryFixedVariant = onTertiaryFixedVariant,
)

private fun ThemeTypographyTemplate.toRqes(): RqesTypography = RqesTypography(
    displayLarge = displayLarge.toRqes(),
    displayMedium = displayMedium.toRqes(),
    displaySmall = displaySmall.toRqes(),
    headlineLarge = headlineLarge.toRqes(),
    headlineMedium = headlineMedium.toRqes(),
    headlineSmall = headlineSmall.toRqes(),
    titleLarge = titleLarge.toRqes(),
    titleMedium = titleMedium.toRqes(),
    titleSmall = titleSmall.toRqes(),
    bodyLarge = bodyLarge.toRqes(),
    bodyMedium = bodyMedium.toRqes(),
    bodySmall = bodySmall.toRqes(),
    labelLarge = labelLarge.toRqes(),
    labelMedium = labelMedium.toRqes(),
    labelSmall = labelSmall.toRqes(),
)

private fun ThemeTextStyle.toRqes(): RqesTextStyle = RqesTextStyle(
    color = color,
    fontSize = fontSize,
    lineHeight = lineHeight,
    fontWeight = fontWeight?.toRqes(),
    fontStyle = fontStyle?.toRqes(),
    fontFamily = fontFamily?.map { it.toRqes() },
    letterSpacing = letterSpacing,
    background = background,
    textDecoration = textDecoration?.toRqes(),
    textAlign = textAlign?.toRqes(),
)

private fun ThemeFont.toRqes(): RqesFont = RqesFont(
    res = res,
    weight = weight.toRqes(),
    style = style.toRqes(),
)

private fun ThemeFontWeight.toRqes(): RqesFontWeight = when (this) {
    ThemeFontWeight.W100 -> RqesFontWeight.W100
    ThemeFontWeight.W200 -> RqesFontWeight.W200
    ThemeFontWeight.W300 -> RqesFontWeight.W300
    ThemeFontWeight.W400 -> RqesFontWeight.W400
    ThemeFontWeight.W500 -> RqesFontWeight.W500
    ThemeFontWeight.W600 -> RqesFontWeight.W600
    ThemeFontWeight.W700 -> RqesFontWeight.W700
    ThemeFontWeight.W800 -> RqesFontWeight.W800
    ThemeFontWeight.W900 -> RqesFontWeight.W900
}

private fun ThemeFontStyle.toRqes(): RqesFontStyle = when (this) {
    ThemeFontStyle.Normal -> RqesFontStyle.Normal
    ThemeFontStyle.Italic -> RqesFontStyle.Italic
}

private fun ThemeTextAlign.toRqes(): RqesTextAlign = when (this) {
    ThemeTextAlign.Left -> RqesTextAlign.Left
    ThemeTextAlign.Right -> RqesTextAlign.Right
    ThemeTextAlign.Center -> RqesTextAlign.Center
    ThemeTextAlign.Justify -> RqesTextAlign.Justify
    ThemeTextAlign.Start -> RqesTextAlign.Start
    ThemeTextAlign.End -> RqesTextAlign.End
}

private fun ThemeTextDecoration.toRqes(): RqesTextDecoration = when (this) {
    ThemeTextDecoration.None -> RqesTextDecoration.None
    ThemeTextDecoration.Underline -> RqesTextDecoration.Underline
    ThemeTextDecoration.LineThrough -> RqesTextDecoration.LineThrough
}
