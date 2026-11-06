English | [简体中文](README_zh_CN.md)

# NetBOX

Fork of [AsteriskBOX](https://github.com/Asterisk4Magisk/AsteriskBOX) with a NetSBX-style xhttp overlay on libbox (Reality, VLESS encryption, Vision, optional pqv). Application ID remains `org.asterisk.zcc.abox`; uninstall official AsteriskBOX before installing this APK.

Android sing-box GUI client.

## Related repositories (keep these forks only)

Do **not** fork `reF1nd/sing-box`, `Leadaxe/sing-box-lx`, `Fanju6/NetProxy-Magisk`, or `AsteriskNG`. Protocol overlay is copied from NetSBX; Magisk lives in NetSBX, not here.

| Repository | GitHub fork of | Why it exists |
|---|---|---|
| [chrimast/NetBOX](https://github.com/chrimast/NetBOX) (`feat/netbox`) | [Asterisk4Magisk/AsteriskBOX](https://github.com/Asterisk4Magisk/AsteriskBOX) | This app. Track upstream with `git merge upstream/main`; never push to Asterisk4Magisk. |
| [chrimast/AndroidLibBoxLite](https://github.com/chrimast/AndroidLibBoxLite) (`feat/xhttp-overlay`) | [Asterisk4Magisk/AndroidLibBoxLite](https://github.com/Asterisk4Magisk/AndroidLibBoxLite) | Overlaid `libbox.so` / ROOT CLI. Same reF1nd pin + lx **client** files as NetSBX. JNI and CLI must come from this tree. |
| [chrimast/NetSBX](https://github.com/chrimast/NetSBX) | *(not a GitHub fork)* | Magisk sister product (`id=NetSBX`). Same overlay pin; ships `sing-box` + `netproxyctl` with `with_ebpf`, not this AAR. |

Do not copy a Magisk ELF onto `libbox.so`, or an AAR / `linux-arm64` binary onto NetSBX `bin/sing-box`. Do not run NetBOX/NG ROOT together with the NetSBX module.

## Telegram Channel

[Asterisk4Magisk](https://t.me/Asterisk4Magisk)

## Run Modes

### VPN Service

- Works without root permission.
- Uses Android `VpnService`.

### TPROXY(ROOT)

- Runs the local sing-box executable directly with libsu.
- Uses iptables and policy routing for transparent proxy traffic.

### TUN(ROOT)

- Runs the local sing-box executable directly with libsu.
- Uses `auto_route` and `auto_redirect` in the sing-box TUN inbound to manage routing.

### eBPF(ROOT)

- Runs the local sing-box executable directly with libsu.
- Uses the sing-box eBPF inbound to capture traffic.
- Availability depends on eBPF support in the device kernel.

### TUN2SOCKS(ROOT)

- Runs the local sing-box executable directly with libsu.
- Uses `hev-socks5-tunnel` to create a TUN device and send traffic to the sing-box SOCKS5 inbound.

### BPF2SOCKS(ROOT)

- Runs the local sing-box executable directly with libsu.
- Uses `bpf2socks` to capture traffic and send it to the sing-box SOCKS5 inbound.
- Availability depends on eBPF support in the device kernel.

### asteriskd

- Watches local IPv4/IPv6 addresses and tethering interfaces, then refreshes the relevant iptables rules or BPF maps.
- Cleans up networking rules owned by the active ROOT mode when the service stops.

## Enhancement Module

[AsteriskEnhance](https://github.com/Asterisk4Magisk/AsteriskEnhance) is an optional module that routes system DNS according to the application list and isolates resolver caches. It requires ROOT and Zygisk Next and supports TPROXY, TUN2SOCKS, and BPF2SOCKS. Enable **Application list enhancement** in settings and follow the installation prompts; reboot after installation or updates.

## Resource Files

- Resource files are stored in the app-private `files/sing-box` directory.
- Custom resources can be added or replaced locally and updated from configured URLs.

## Broadcast Control

Enable **Broadcast Control** in settings, then send an explicit broadcast to the receiver below. Actions use the `org.asterisk.zcc.abox.action.` prefix.

| Operation | Action suffix |
| --- | --- |
| Start proxy | `PROXY_START` |
| Stop proxy | `PROXY_STOP` |
| Toggle proxy | `PROXY_TOGGLE` |
| Update all URL subscriptions | `SUBSCRIPTION_UPDATE` |
| Cancel broadcast subscription update | `SUBSCRIPTION_UPDATE_CANCEL` |
| Update all resources | `RESOURCE_UPDATE` |
| Cancel resource updates | `RESOURCE_UPDATE_CANCEL` |

```sh
adb shell am broadcast -n org.asterisk.zcc.abox/features.automation.BroadcastControlReceiver -a org.asterisk.zcc.abox.action.SUBSCRIPTION_UPDATE
```

Subscription updates skip local entries; cancellation preserves completed results and scheduled update settings. Resources use the current Resource Management configuration; resource cancellation also clears its shared queue. Repeated update commands of the same kind are merged while running.

Updates run in the background without starting the proxy. Broadcast delivery does not mean the update has finished; check the `BroadcastControl` app logs for results.

## Development

Initialize submodules before building:

```bash
git submodule update --init --recursive
```

Build with Android Studio or the Gradle wrapper:

```powershell
.\gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
./gradlew assembleDebug
```

The build downloads the pinned AndroidLibBoxLite AAR, builds the native submodules, and produces ABI split APKs plus a universal APK.

If Gradle cannot find the Android NDK, configure it through Android Studio, `ndk.dir` in `local.properties`, or `ANDROID_NDK_HOME`.

## License

[GPL-3.0](LICENSE)

## Credits

- [@SagerNet/sing-box](https://github.com/SagerNet/sing-box)
- [@reF1nd/sing-box-releases](https://github.com/reF1nd/sing-box-releases)
- [@Asterisk4Magisk/AndroidLibBoxLite](https://github.com/Asterisk4Magisk/AndroidLibBoxLite) (this fork uses [chrimast/AndroidLibBoxLite](https://github.com/chrimast/AndroidLibBoxLite) `feat/xhttp-overlay`)
- [@heiher/hev-socks5-tunnel](https://github.com/heiher/hev-socks5-tunnel)
- [@topjohnwu/libsu](https://github.com/topjohnwu/libsu)
- [@android/material3](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [@mayaxcn/china-ip-list](https://github.com/mayaxcn/china-ip-list)
- [@xchacha20-poly1305/husi](https://github.com/xchacha20-poly1305/husi)
