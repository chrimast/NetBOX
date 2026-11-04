#!/bin/sh
# Fail if launcher branding is still AsteriskBOX or still using the old raster icon.
set -eu
ROOT=$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)
cd "$ROOT"
grep -q 'PROJECT_NAME = "NetBOX"' buildSrc/src/main/kotlin/BuildConfig.kt
grep -q 'rootProject.name = "NetBOX"' settings.gradle.kts
grep -q '<string name="app_name">NetBOX</string>' app/src/main/res/values/strings.xml
grep -q '<string name="app_name">NetBOX</string>' app/src/main/res/values-zh-rCN/strings.xml
grep -q '<string name="quick_settings_tile_label">NetBOX</string>' app/src/main/res/values/strings.xml
grep -q '<string name="quick_settings_tile_label">NetBOX</string>' app/src/main/res/values-zh-rCN/strings.xml
test -f app/src/main/res/drawable/ic_launcher_foreground.xml
test -f app/src/main/res/drawable/ic_box_background.xml
test ! -f app/src/main/res/drawable-nodpi/ic_launcher_foreground.png
grep -q '@drawable/ic_box_background' app/src/main/res/mipmap-anydpi/ic_launcher.xml
grep -q '@drawable/ic_launcher_foreground' app/src/main/res/mipmap-anydpi/ic_launcher.xml
! grep -q '<string name="app_name">AsteriskBOX</string>' app/src/main/res/values/strings.xml
! grep -q '<string name="app_name">AsteriskBOX</string>' app/src/main/res/values-zh-rCN/strings.xml
echo "netbox brand ok"
