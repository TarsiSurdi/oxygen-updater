package com.oxygenupdater.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.oxygenupdater.BuildConfig
import com.oxygenupdater.R
import com.oxygenupdater.internal.settings.SettingsManager
import com.oxygenupdater.models.SystemVersionProperties
import com.oxygenupdater.utils.Logger
import com.oxygenupdater.utils.ThemeUtils
import org.koin.java.KoinJavaComponent.inject

/**
 * @author [Adhiraj Singh Chauhan](https://github.com/adhirajsinghchauhan)
 */
fun Context.openPlayStorePage() {
    val appPackageName = packageName

    try {
        // Try opening Play Store
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appPackageName")
            ).withAppReferrer(this)
        )
    } catch (e: ActivityNotFoundException) {
        try {
            // Try opening browser
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                ).withAppReferrer(this)
            )
        } catch (e1: ActivityNotFoundException) {
            // Give up and cry
            Toast.makeText(
                this,
                getString(R.string.error_unable_to_rate_app),
                Toast.LENGTH_LONG
            ).show()
            Logger.logWarning("AboutActivity", "App rating without google play store support", e1)
        }
    }
}

fun Context.openEmail() {
    val systemVersionProperties by inject(SystemVersionProperties::class.java)
    val oxygenOsVersion = systemVersionProperties.oxygenOSVersion
    val oxygenOsOtaVersion = systemVersionProperties.oxygenOSOTAVersion
    val osType = systemVersionProperties.osType
    val actualDeviceName = systemVersionProperties.oxygenDeviceName
    val appVersion = BuildConfig.VERSION_NAME
    val chosenDeviceName = SettingsManager.getPreference(
        SettingsManager.PROPERTY_DEVICE,
        "<UNKNOWN>"
    )
    val chosenUpdateMethod = SettingsManager.getPreference(
        SettingsManager.PROPERTY_UPDATE_METHOD,
        "<UNKNOWN>"
    )
    val advancedModeEnabled = SettingsManager.getPreference(
        SettingsManager.PROPERTY_ADVANCED_MODE,
        false
    )

    startActivity(
        Intent.createChooser(
            Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_address)))
                // Parts of this should probably be localized but it may pose a
                // problem for us while reading emails
                .putExtra(
                    Intent.EXTRA_TEXT,
                    """
                        <write your query here>

                        DON'T EDIT ANYTHING BELOW, IT'S AUTOGENERATED
                        --------------------
                        • Actual device: $actualDeviceName
                        • Chosen device: $chosenDeviceName
                        • Update method: $chosenUpdateMethod
                        • OS version: $oxygenOsVersion ($osType)
                        • OTA version: $oxygenOsOtaVersion
                        • Advanced mode: $advancedModeEnabled
                        • App version: $appVersion
                        --------------------
                    """.trimIndent()
                ),
            getString(R.string.about_email_button_text)
        )
    )
}

fun Context.openDiscord() = startActivity(
    Intent(
        Intent.ACTION_VIEW,
        Uri.parse(getString(R.string.discord_url))
    ).withAppReferrer(this)
)

fun Context.openGitHub() = startActivity(
    Intent(
        Intent.ACTION_VIEW,
        Uri.parse(getString(R.string.github_url))
    ).withAppReferrer(this)
)

fun Context.openPatreon() = startActivity(
    Intent(
        Intent.ACTION_VIEW,
        Uri.parse(getString(R.string.patreon_url))
    ).withAppReferrer(this)
)

fun Context.openWebsite() = startActivity(
    Intent(
        Intent.ACTION_VIEW,
        Uri.parse(getString(R.string.website_url))
    ).withAppReferrer(this)
)

fun Context.openInCustomTab(url: String) = customTabIntent().launchUrl(
    this,
    Uri.parse(url)
)

private fun Context.customTabIntent() = CustomTabsIntent.Builder()
    .setShowTitle(true)
    .setUrlBarHidingEnabled(true)
    .setDefaultColorSchemeParams(
        CustomTabColorSchemeParams.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.background))
            .setNavigationBarColor(ContextCompat.getColor(this, R.color.background))
            .build()
    )
    .setColorScheme(
        if (ThemeUtils.isNightModeActive(this)) {
            CustomTabsIntent.COLOR_SCHEME_DARK
        } else {
            CustomTabsIntent.COLOR_SCHEME_LIGHT
        }
    ).build().apply {
        intent.withAppReferrer(this@customTabIntent)
    }
