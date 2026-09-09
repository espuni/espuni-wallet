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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import eu.europa.ec.resourceslogic.theme.templates.ThemeColorsTemplate

/**
 * espuni colour system, ported from the web design system.
 *
 * The literal values below are the tokens of `apps/portal/app/tokens.css`; the
 * assignment to Material 3 roles follows §1.2 of `docs/brand-native-criteria.md`.
 * Three decisions from §0 of that document are load-bearing and must survive any
 * edit to this file:
 *
 *  1. **The primary colour is the text colour, not the green.** `--verify` is
 *     semantic — it means "this verification passed" — and is never a call to
 *     action.
 *  2. **Three verification states, not five.** `verify` / `alert` / `pending`.
 *     `horizon` and `signal` exist but are never a verification state.
 *  3. **Borders, not shadows.** Surfaces are separated by 1dp of `outlineVariant`;
 *     `surfaceTint` is neutralised so Material's tonal elevation cannot tint them.
 *
 * `verify`, `pending`, `horizon` and `signal` have no Material 3 role. They live
 * as [ColorScheme] extension properties at the bottom of this file, alongside the
 * third text level (`--text-muted`) that M3's two-level `onSurface` /
 * `onSurfaceVariant` pair cannot express.
 */
class ThemeColors {
    companion object {
        private const val black: Long = 0xFF000000

        // region espuni tokens — light (`tokens.css` :root)

        // Neutral ramp.
        private const val espuni_light_bg: Long = 0xFFF5F6F8
        private const val espuni_light_surface: Long = 0xFFFFFFFF
        private const val espuni_light_surface_2: Long = 0xFFF0F1F3
        private const val espuni_light_border: Long = 0xFFE8EAED
        private const val espuni_light_border_strong: Long = 0xFFD8DCE2
        private const val espuni_light_text: Long = 0xFF111827
        internal const val espuni_light_text_dim: Long = 0xFF374151
        internal const val espuni_light_text_muted: Long = 0xFF6B7280

        // Two surface steps M3 requires and the web does not define. Interpolated
        // between the tokens above — see §6.6, still an open decision upstream.
        private const val espuni_light_surface_low: Long = 0xFFFAFBFC
        private const val espuni_light_surface_high: Long = 0xFFE9EBEE

        // Named accents. The `-bg` / `-bd` pairs are the accent at the fixed 8% /
        // 22% opacity the web uses "so that no state reads louder than another".
        internal const val espuni_light_verify: Long = 0xFF16A34A
        internal const val espuni_light_verify_bg: Long = 0x1416A34A
        internal const val espuni_light_verify_bd: Long = 0x3816A34A
        internal const val espuni_light_alert: Long = 0xFFDC2626
        internal const val espuni_light_alert_bg: Long = 0x14DC2626
        internal const val espuni_light_alert_bd: Long = 0x33DC2626
        internal const val espuni_light_pending: Long = 0xFFD97706
        internal const val espuni_light_pending_bg: Long = 0x14D97706
        internal const val espuni_light_pending_bd: Long = 0x38D97706
        internal const val espuni_light_horizon: Long = 0xFF9333EA
        internal const val espuni_light_horizon_bg: Long = 0x149333EA
        internal const val espuni_light_horizon_bd: Long = 0x389333EA
        internal const val espuni_light_signal: Long = 0xFF2563EB
        internal const val espuni_light_signal_bg: Long = 0x142563EB
        internal const val espuni_light_signal_bd: Long = 0x382563EB

        // endregion

        // region espuni tokens — dark (`tokens.css` .dark)

        private const val espuni_dark_bg: Long = 0xFF0A0A0A
        private const val espuni_dark_surface: Long = 0xFF111111
        private const val espuni_dark_surface_2: Long = 0xFF1A1A1A
        private const val espuni_dark_border: Long = 0xFF1F1F1F
        private const val espuni_dark_border_strong: Long = 0xFF2A2A2A
        private const val espuni_dark_text: Long = 0xFFEDEDEC
        internal const val espuni_dark_text_dim: Long = 0xFFA3A3A3
        internal const val espuni_dark_text_muted: Long = 0xFF525252

        private const val espuni_dark_surface_low: Long = 0xFF0E0E0E

        // Dark opacities are 10% / 26–28%, not 8% / 22%.
        internal const val espuni_dark_verify: Long = 0xFF4ADE80
        internal const val espuni_dark_verify_bg: Long = 0x1A4ADE80
        internal const val espuni_dark_verify_bd: Long = 0x474ADE80
        internal const val espuni_dark_alert: Long = 0xFFF87171
        internal const val espuni_dark_alert_bg: Long = 0x1AF87171
        internal const val espuni_dark_alert_bd: Long = 0x42F87171
        internal const val espuni_dark_pending: Long = 0xFFFBBF24
        internal const val espuni_dark_pending_bg: Long = 0x1AFBBF24
        internal const val espuni_dark_pending_bd: Long = 0x47FBBF24
        internal const val espuni_dark_horizon: Long = 0xFFC084FC
        internal const val espuni_dark_horizon_bg: Long = 0x1AC084FC
        internal const val espuni_dark_horizon_bd: Long = 0x47C084FC
        internal const val espuni_dark_signal: Long = 0xFF60A5FA
        internal const val espuni_dark_signal_bg: Long = 0x1A60A5FA
        internal const val espuni_dark_signal_bd: Long = 0x4760A5FA

        // endregion

        // Light theme base colors palette.
        // `primary` is `--text`, not an accent: the CTA is near-black on near-white,
        // mirroring `--primary: var(--text)` / `--primary-foreground: var(--bg)`.
        private const val eudiw_theme_light_primary: Long = espuni_light_text
        private const val eudiw_theme_light_onPrimary: Long = espuni_light_bg
        private const val eudiw_theme_light_primaryContainer: Long = espuni_light_surface_2
        private const val eudiw_theme_light_onPrimaryContainer: Long = espuni_light_text
        private const val eudiw_theme_light_secondary: Long = espuni_light_text_dim
        private const val eudiw_theme_light_onSecondary: Long = espuni_light_surface
        private const val eudiw_theme_light_secondaryContainer: Long = espuni_light_surface_2
        private const val eudiw_theme_light_onSecondaryContainer: Long = espuni_light_text

        // `tertiary` is the only free accent: `signal` is declared "never a state",
        // which is exactly the decorative role M3 gives `tertiary`.
        private const val eudiw_theme_light_tertiary: Long = espuni_light_signal
        private const val eudiw_theme_light_onTertiary: Long = espuni_light_surface
        private const val eudiw_theme_light_tertiaryContainer: Long = espuni_light_signal_bg
        private const val eudiw_theme_light_onTertiaryContainer: Long = espuni_light_signal

        private const val eudiw_theme_light_error: Long = espuni_light_alert
        private const val eudiw_theme_light_onError: Long = espuni_light_surface
        private const val eudiw_theme_light_errorContainer: Long = espuni_light_alert_bg
        private const val eudiw_theme_light_onErrorContainer: Long = espuni_light_alert

        private const val eudiw_theme_light_surface: Long = espuni_light_bg
        private const val eudiw_theme_light_onSurface: Long = espuni_light_text
        private const val eudiw_theme_light_background: Long = eudiw_theme_light_surface
        private const val eudiw_theme_light_onBackground: Long = eudiw_theme_light_onSurface
        private const val eudiw_theme_light_surfaceVariant: Long = espuni_light_surface_2
        private const val eudiw_theme_light_onSurfaceVariant: Long = espuni_light_text_dim
        private const val eudiw_theme_light_outline: Long = espuni_light_border_strong
        private const val eudiw_theme_light_outlineVariant: Long = espuni_light_border
        private const val eudiw_theme_light_scrim: Long = black
        private const val eudiw_theme_light_inverseSurface: Long = espuni_light_text
        private const val eudiw_theme_light_inverseOnSurface: Long = espuni_light_bg
        private const val eudiw_theme_light_inversePrimary: Long = espuni_dark_text
        private const val eudiw_theme_light_surfaceDim: Long = espuni_light_border
        private const val eudiw_theme_light_surfaceBright: Long = espuni_light_surface

        // The surface scale is inverted with respect to M3 in the light theme: the
        // page is #F5F6F8 and a card is #FFFFFF, i.e. *lighter* than the page. Cards
        // therefore belong in `surfaceContainerLowest`, not in the `surfaceContainer`
        // that `Card` and `Surface` reach for by default.
        internal const val eudiw_theme_light_surfaceContainerLowest: Long = espuni_light_surface
        private const val eudiw_theme_light_surfaceContainerLow: Long = espuni_light_surface_low
        private const val eudiw_theme_light_surfaceContainer: Long = espuni_light_surface_2
        private const val eudiw_theme_light_surfaceContainerHigh: Long = espuni_light_surface_high
        private const val eudiw_theme_light_surfaceContainerHighest: Long = espuni_light_border

        // Equal to `surface`, which neutralises Material's tonal elevation: the web
        // system separates surfaces with a border and never tints them by height.
        private const val eudiw_theme_light_surfaceTint: Long = eudiw_theme_light_surface

        // Light theme fixed accent roles (identical in dark as well).
        // The web has no equivalent; they are drawn from the neutral ramp so that no
        // stray hue can re-enter through a component that reads them.
        private const val eudiw_theme_light_primaryFixed: Long = espuni_light_surface_2
        private const val eudiw_theme_light_primaryFixedDim: Long = espuni_light_border
        private const val eudiw_theme_light_onPrimaryFixed: Long = espuni_light_text
        private const val eudiw_theme_light_onPrimaryFixedVariant: Long = espuni_light_text_dim

        private const val eudiw_theme_light_secondaryFixed: Long = espuni_light_surface_2
        private const val eudiw_theme_light_secondaryFixedDim: Long = espuni_light_border
        private const val eudiw_theme_light_onSecondaryFixed: Long = espuni_light_text
        private const val eudiw_theme_light_onSecondaryFixedVariant: Long = espuni_light_text_dim

        private const val eudiw_theme_light_tertiaryFixed: Long = espuni_light_signal_bg
        private const val eudiw_theme_light_tertiaryFixedDim: Long = espuni_light_signal_bd
        private const val eudiw_theme_light_onTertiaryFixed: Long = espuni_light_signal
        private const val eudiw_theme_light_onTertiaryFixedVariant: Long = espuni_light_signal

        // Light theme extra colors palette.
        internal const val eudiw_theme_light_success: Long = espuni_light_verify
        internal const val eudiw_theme_light_warning: Long = espuni_light_pending
        internal const val eudiw_theme_light_pending: Long = espuni_light_pending
        internal const val eudiw_theme_light_divider: Long = espuni_light_border

        // Dark theme base colors palette.
        private const val eudiw_theme_dark_primary: Long = espuni_dark_text
        private const val eudiw_theme_dark_onPrimary: Long = espuni_dark_bg
        private const val eudiw_theme_dark_primaryContainer: Long = espuni_dark_surface_2
        private const val eudiw_theme_dark_onPrimaryContainer: Long = espuni_dark_text
        private const val eudiw_theme_dark_secondary: Long = espuni_dark_text_dim
        private const val eudiw_theme_dark_onSecondary: Long = espuni_dark_bg
        private const val eudiw_theme_dark_secondaryContainer: Long = espuni_dark_surface_2
        private const val eudiw_theme_dark_onSecondaryContainer: Long = espuni_dark_text
        private const val eudiw_theme_dark_tertiary: Long = espuni_dark_signal
        private const val eudiw_theme_dark_onTertiary: Long = espuni_dark_bg
        private const val eudiw_theme_dark_tertiaryContainer: Long = espuni_dark_signal_bg
        private const val eudiw_theme_dark_onTertiaryContainer: Long = espuni_dark_signal
        private const val eudiw_theme_dark_error: Long = espuni_dark_alert
        private const val eudiw_theme_dark_onError: Long = espuni_dark_bg
        private const val eudiw_theme_dark_errorContainer: Long = espuni_dark_alert_bg
        private const val eudiw_theme_dark_onErrorContainer: Long = espuni_dark_alert
        private const val eudiw_theme_dark_surface: Long = espuni_dark_bg
        private const val eudiw_theme_dark_onSurface: Long = espuni_dark_text
        private const val eudiw_theme_dark_background: Long = eudiw_theme_dark_surface
        private const val eudiw_theme_dark_onBackground: Long = eudiw_theme_dark_onSurface
        private const val eudiw_theme_dark_surfaceVariant: Long = espuni_dark_surface_2
        private const val eudiw_theme_dark_onSurfaceVariant: Long = espuni_dark_text_dim
        private const val eudiw_theme_dark_outline: Long = espuni_dark_border_strong
        private const val eudiw_theme_dark_outlineVariant: Long = espuni_dark_border
        private const val eudiw_theme_dark_scrim: Long = black
        private const val eudiw_theme_dark_inverseSurface: Long = espuni_dark_text
        private const val eudiw_theme_dark_inverseOnSurface: Long = espuni_dark_bg
        private const val eudiw_theme_dark_inversePrimary: Long = espuni_light_text
        private const val eudiw_theme_dark_surfaceDim: Long = espuni_dark_bg
        private const val eudiw_theme_dark_surfaceBright: Long = espuni_dark_border_strong

        // In dark the order does match M3 — containers lighten as they rise — so the
        // mapping is direct and cards sit in `surfaceContainer`.
        private const val eudiw_theme_dark_surfaceContainerLowest: Long = espuni_dark_bg
        private const val eudiw_theme_dark_surfaceContainerLow: Long = espuni_dark_surface_low
        internal const val eudiw_theme_dark_surfaceContainer: Long = espuni_dark_surface
        private const val eudiw_theme_dark_surfaceContainerHigh: Long = espuni_dark_surface_2
        private const val eudiw_theme_dark_surfaceContainerHighest: Long = espuni_dark_border_strong
        private const val eudiw_theme_dark_surfaceTint: Long = eudiw_theme_dark_surface

        // Dark theme fixed accent roles (same values as light).
        private const val eudiw_theme_dark_primaryFixed: Long = eudiw_theme_light_primaryFixed
        private const val eudiw_theme_dark_primaryFixedDim: Long = eudiw_theme_light_primaryFixedDim
        private const val eudiw_theme_dark_onPrimaryFixed: Long = eudiw_theme_light_onPrimaryFixed
        private const val eudiw_theme_dark_onPrimaryFixedVariant: Long =
            eudiw_theme_light_onPrimaryFixedVariant

        private const val eudiw_theme_dark_secondaryFixed: Long = eudiw_theme_light_secondaryFixed
        private const val eudiw_theme_dark_secondaryFixedDim: Long =
            eudiw_theme_light_secondaryFixedDim
        private const val eudiw_theme_dark_onSecondaryFixed: Long =
            eudiw_theme_light_onSecondaryFixed
        private const val eudiw_theme_dark_onSecondaryFixedVariant: Long =
            eudiw_theme_light_onSecondaryFixedVariant

        private const val eudiw_theme_dark_tertiaryFixed: Long = eudiw_theme_light_tertiaryFixed
        private const val eudiw_theme_dark_tertiaryFixedDim: Long =
            eudiw_theme_light_tertiaryFixedDim
        private const val eudiw_theme_dark_onTertiaryFixed: Long = eudiw_theme_light_onTertiaryFixed
        private const val eudiw_theme_dark_onTertiaryFixedVariant: Long =
            eudiw_theme_light_onTertiaryFixedVariant

        // Dark theme extra colors palette.
        internal const val eudiw_theme_dark_success: Long = espuni_dark_verify
        internal const val eudiw_theme_dark_warning: Long = espuni_dark_pending
        internal const val eudiw_theme_dark_pending: Long = espuni_dark_pending
        internal const val eudiw_theme_dark_divider: Long = espuni_dark_border

        const val eudiw_theme_light_background_preview: Long =
            eudiw_theme_light_surface
        const val eudiw_theme_dark_background_preview: Long =
            eudiw_theme_dark_surface

        internal val lightColors = ThemeColorsTemplate(
            primary = eudiw_theme_light_primary,
            onPrimary = eudiw_theme_light_onPrimary,
            primaryContainer = eudiw_theme_light_primaryContainer,
            onPrimaryContainer = eudiw_theme_light_onPrimaryContainer,
            secondary = eudiw_theme_light_secondary,
            onSecondary = eudiw_theme_light_onSecondary,
            secondaryContainer = eudiw_theme_light_secondaryContainer,
            onSecondaryContainer = eudiw_theme_light_onSecondaryContainer,
            tertiary = eudiw_theme_light_tertiary,
            onTertiary = eudiw_theme_light_onTertiary,
            tertiaryContainer = eudiw_theme_light_tertiaryContainer,
            onTertiaryContainer = eudiw_theme_light_onTertiaryContainer,
            error = eudiw_theme_light_error,
            errorContainer = eudiw_theme_light_errorContainer,
            onError = eudiw_theme_light_onError,
            onErrorContainer = eudiw_theme_light_onErrorContainer,
            background = eudiw_theme_light_background,
            onBackground = eudiw_theme_light_onBackground,
            surface = eudiw_theme_light_surface,
            onSurface = eudiw_theme_light_onSurface,
            surfaceVariant = eudiw_theme_light_surfaceVariant,
            onSurfaceVariant = eudiw_theme_light_onSurfaceVariant,
            outline = eudiw_theme_light_outline,
            inverseOnSurface = eudiw_theme_light_inverseOnSurface,
            inverseSurface = eudiw_theme_light_inverseSurface,
            inversePrimary = eudiw_theme_light_inversePrimary,
            surfaceTint = eudiw_theme_light_surfaceTint,
            outlineVariant = eudiw_theme_light_outlineVariant,
            scrim = eudiw_theme_light_scrim,
            surfaceBright = eudiw_theme_light_surfaceBright,
            surfaceDim = eudiw_theme_light_surfaceDim,
            surfaceContainer = eudiw_theme_light_surfaceContainer,
            surfaceContainerHigh = eudiw_theme_light_surfaceContainerHigh,
            surfaceContainerHighest = eudiw_theme_light_surfaceContainerHighest,
            surfaceContainerLow = eudiw_theme_light_surfaceContainerLow,
            surfaceContainerLowest = eudiw_theme_light_surfaceContainerLowest,
            primaryFixed = eudiw_theme_light_primaryFixed,
            primaryFixedDim = eudiw_theme_light_primaryFixedDim,
            onPrimaryFixed = eudiw_theme_light_onPrimaryFixed,
            onPrimaryFixedVariant = eudiw_theme_light_onPrimaryFixedVariant,
            secondaryFixed = eudiw_theme_light_secondaryFixed,
            secondaryFixedDim = eudiw_theme_light_secondaryFixedDim,
            onSecondaryFixed = eudiw_theme_light_onSecondaryFixed,
            onSecondaryFixedVariant = eudiw_theme_light_onSecondaryFixedVariant,
            tertiaryFixed = eudiw_theme_light_tertiaryFixed,
            tertiaryFixedDim = eudiw_theme_light_tertiaryFixedDim,
            onTertiaryFixed = eudiw_theme_light_onTertiaryFixed,
            onTertiaryFixedVariant = eudiw_theme_light_onTertiaryFixedVariant,
        )

        internal val darkColors = ThemeColorsTemplate(
            primary = eudiw_theme_dark_primary,
            onPrimary = eudiw_theme_dark_onPrimary,
            primaryContainer = eudiw_theme_dark_primaryContainer,
            onPrimaryContainer = eudiw_theme_dark_onPrimaryContainer,
            secondary = eudiw_theme_dark_secondary,
            onSecondary = eudiw_theme_dark_onSecondary,
            secondaryContainer = eudiw_theme_dark_secondaryContainer,
            onSecondaryContainer = eudiw_theme_dark_onSecondaryContainer,
            tertiary = eudiw_theme_dark_tertiary,
            onTertiary = eudiw_theme_dark_onTertiary,
            tertiaryContainer = eudiw_theme_dark_tertiaryContainer,
            onTertiaryContainer = eudiw_theme_dark_onTertiaryContainer,
            error = eudiw_theme_dark_error,
            errorContainer = eudiw_theme_dark_errorContainer,
            onError = eudiw_theme_dark_onError,
            onErrorContainer = eudiw_theme_dark_onErrorContainer,
            background = eudiw_theme_dark_background,
            onBackground = eudiw_theme_dark_onBackground,
            surface = eudiw_theme_dark_surface,
            onSurface = eudiw_theme_dark_onSurface,
            surfaceVariant = eudiw_theme_dark_surfaceVariant,
            onSurfaceVariant = eudiw_theme_dark_onSurfaceVariant,
            outline = eudiw_theme_dark_outline,
            inverseOnSurface = eudiw_theme_dark_inverseOnSurface,
            inverseSurface = eudiw_theme_dark_inverseSurface,
            inversePrimary = eudiw_theme_dark_inversePrimary,
            surfaceTint = eudiw_theme_dark_surfaceTint,
            outlineVariant = eudiw_theme_dark_outlineVariant,
            scrim = eudiw_theme_dark_scrim,
            surfaceBright = eudiw_theme_dark_surfaceBright,
            surfaceDim = eudiw_theme_dark_surfaceDim,
            surfaceContainer = eudiw_theme_dark_surfaceContainer,
            surfaceContainerHigh = eudiw_theme_dark_surfaceContainerHigh,
            surfaceContainerHighest = eudiw_theme_dark_surfaceContainerHighest,
            surfaceContainerLow = eudiw_theme_dark_surfaceContainerLow,
            surfaceContainerLowest = eudiw_theme_dark_surfaceContainerLowest,
            primaryFixed = eudiw_theme_dark_primaryFixed,
            primaryFixedDim = eudiw_theme_dark_primaryFixedDim,
            onPrimaryFixed = eudiw_theme_dark_onPrimaryFixed,
            onPrimaryFixedVariant = eudiw_theme_dark_onPrimaryFixedVariant,
            secondaryFixed = eudiw_theme_dark_secondaryFixed,
            secondaryFixedDim = eudiw_theme_dark_secondaryFixedDim,
            onSecondaryFixed = eudiw_theme_dark_onSecondaryFixed,
            onSecondaryFixedVariant = eudiw_theme_dark_onSecondaryFixedVariant,
            tertiaryFixed = eudiw_theme_dark_tertiaryFixed,
            tertiaryFixedDim = eudiw_theme_dark_tertiaryFixedDim,
            onTertiaryFixed = eudiw_theme_dark_onTertiaryFixed,
            onTertiaryFixedVariant = eudiw_theme_dark_onTertiaryFixedVariant,
        )
    }
}

/**
 * Resolves [light] in the light theme and [dark] in the dark one, following the
 * convention the extension properties below already used.
 */
@Composable
private fun themed(light: Long, dark: Long): Color =
    if (isSystemInDarkTheme()) Color(dark) else Color(light)

val ColorScheme.success: Color
    @Composable get() = themed(
        ThemeColors.eudiw_theme_light_success,
        ThemeColors.eudiw_theme_dark_success
    )

val ColorScheme.warning: Color
    @Composable get() = themed(
        ThemeColors.eudiw_theme_light_warning,
        ThemeColors.eudiw_theme_dark_warning
    )

val ColorScheme.pending: Color
    @Composable get() = themed(
        ThemeColors.eudiw_theme_light_pending,
        ThemeColors.eudiw_theme_dark_pending
    )

val ColorScheme.divider: Color
    @Composable get() = themed(
        ThemeColors.eudiw_theme_light_divider,
        ThemeColors.eudiw_theme_dark_divider
    )

val ColorScheme.surfaceAtElevation1: Color
    @Composable get() = themed(
        ThemeColors.eudiw_theme_light_surfaceContainerLowest,
        ThemeColors.eudiw_theme_dark_surfaceContainer
    )

// region espuni semantic accents
//
// Roles Material 3 has no slot for. `verify` answers "did *this* verification
// pass?"; it is not a call to action and must never become one. `horizon` marks
// roadmap capability and `signal` is analytics only — neither is ever a
// verification state.
//
// Each accent comes as a triple: the accent itself for text and glyphs, `…Bg` for
// the tinted fill behind it, and `…Bd` for the 1dp border around that fill. They
// carry fixed opacities so that no state reads louder than another.

/** `age_over_18: true` · validated issuer · completed session. */
val ColorScheme.verify: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_verify,
        ThemeColors.espuni_dark_verify
    )

val ColorScheme.verifyBg: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_verify_bg,
        ThemeColors.espuni_dark_verify_bg
    )

val ColorScheme.verifyBd: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_verify_bd,
        ThemeColors.espuni_dark_verify_bd
    )

/** `age_over_18: false` · rejected presentation · expired session. */
val ColorScheme.alert: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_alert,
        ThemeColors.espuni_dark_alert
    )

val ColorScheme.alertBg: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_alert_bg,
        ThemeColors.espuni_dark_alert_bg
    )

val ColorScheme.alertBd: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_alert_bd,
        ThemeColors.espuni_dark_alert_bd
    )

val ColorScheme.pendingBg: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_pending_bg,
        ThemeColors.espuni_dark_pending_bg
    )

val ColorScheme.pendingBd: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_pending_bd,
        ThemeColors.espuni_dark_pending_bd
    )

/** Roadmap capability. Never a verification state. */
val ColorScheme.horizon: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_horizon,
        ThemeColors.espuni_dark_horizon
    )

val ColorScheme.horizonBg: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_horizon_bg,
        ThemeColors.espuni_dark_horizon_bg
    )

val ColorScheme.horizonBd: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_horizon_bd,
        ThemeColors.espuni_dark_horizon_bd
    )

/** Analytics only — trend lines, conversion charts. Never a verification state. */
val ColorScheme.signal: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_signal,
        ThemeColors.espuni_dark_signal
    )

val ColorScheme.signalBg: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_signal_bg,
        ThemeColors.espuni_dark_signal_bg
    )

val ColorScheme.signalBd: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_signal_bd,
        ThemeColors.espuni_dark_signal_bd
    )

/**
 * Chart series order, fixed by the web system: verify, signal, pending, horizon,
 * alert.
 */
val ColorScheme.chartSeries: List<Color>
    @Composable get() = listOf(verify, signal, pending, horizon, alert)

// endregion

// region espuni text and border levels
//
// The system has three text levels and M3 offers two. `onSurface` is `--text` and
// `onSurfaceVariant` is `--text-dim`; the third one lives here.

/** Captions, mono labels. The quietest text in the system. */
val ColorScheme.textMuted: Color
    @Composable get() = themed(
        ThemeColors.espuni_light_text_muted,
        ThemeColors.espuni_dark_text_muted
    )

/** Control border and focus ring. Mirrors `outline`; named for the web token. */
val ColorScheme.borderStrong: Color
    @Composable get() = outline

// endregion
