package local.numrise.shop.infrastructure.persistence

import androidx.datastore.preferences.core.Preferences
import local.core.ui.data.SettingsRepositoryImpl

/**
 * Game-owned inventory map, passed into core [SettingsRepositoryImpl].
 * Lives here (game module) — never as a new function in core's companion.
 * Key *names* are frozen once shipped: existing users keep their inventory.
 * Coin-direct variant: nothing reads these keys, but the wiring is kept so
 * existing installs keep their data.
 */
object NumriseInventoryKeys {
    fun map(): Map<String, Preferences.Key<Int>> = SettingsRepositoryImpl.inventoryKeysFor(
        "extra_time" to "extra_time_count",
        "hint" to "hint_count",
        "undo" to "undo_count"
    )
}
