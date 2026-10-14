# Numrise UI (screens, VMs, adapters)
-keep class local.numrise.ui.** { *; }
-keep class local.numrise.engine.** { *; }
# Room (via core-ui) — keep here too in case UI references entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
