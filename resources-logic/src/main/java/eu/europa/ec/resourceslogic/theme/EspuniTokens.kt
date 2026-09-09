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

package eu.europa.ec.resourceslogic.theme

import eu.europa.ec.resourceslogic.theme.templates.ThemeColorsTemplate
import eu.europa.ec.resourceslogic.theme.templates.ThemeTypographyTemplate
import eu.europa.ec.resourceslogic.theme.values.ThemeColors
import eu.europa.ec.resourceslogic.theme.values.ThemeShapes
import eu.europa.ec.resourceslogic.theme.values.ThemeTypography

/**
 * The espuni palette and type scale, published for the modules that have to hand
 * them to something other than [ThemeManager] — today the embedded RQES signing
 * SDK, which carries its own copy of this theme system and would otherwise render
 * its screens in the reference app's blue on Roboto.
 *
 * This is the single source of truth. Nothing should restate a token value.
 */
object EspuniTokens {

    val lightColors: ThemeColorsTemplate get() = ThemeColors.lightColors

    val darkColors: ThemeColorsTemplate get() = ThemeColors.darkColors

    val typography: ThemeTypographyTemplate get() = ThemeTypography.typo

    /** `--radius-md`, the radius of a card or panel. */
    const val RADIUS_MEDIUM: Double = ThemeShapes.MEDIUM
}
