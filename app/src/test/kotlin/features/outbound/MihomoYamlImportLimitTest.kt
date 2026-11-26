// Copyright 2026, AsteriskBOX contributors
// SPDX-License-Identifier: GPL-3.0

package features.outbound

import features.importing.ImportIssueReason
import features.importing.ImportLimitException
import features.importing.MaxImportBytes
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class MihomoYamlImportLimitTest {
    @Test
    fun importsYamlBeyondSnakeYamlDefaultLimit() = runBlocking {
        val yaml = "# ${"a".repeat(3 * 1024 * 1024)}\n" +
            "proxies:\n  - { name: test, type: socks5, server: example.invalid, port: 1080 }\n"

        val result = OutboundImportPipeline.parse(yaml)

        assertEquals(OutboundImportFormat.YAML, result.format)
        assertEquals(1, result.outbounds.size)
    }

    @Test
    fun recognizesProviderSectionBeyondSnakeYamlDefaultLimit() {
        val yaml = "# ${"a".repeat(3 * 1024 * 1024)}\nproxy-providers: {}\n"

        assertTrue(containsMihomoProviders(yaml))
    }

    @Test
    fun rejectsTextBeyondNetBoxImportLimit() {
        val result = assertThrows(ImportLimitException::class.java) {
            runBlocking { OutboundImportPipeline.parse("a".repeat(MaxImportBytes + 1)) }
        }

        assertEquals(ImportIssueReason.INPUT_TOO_LARGE, result.reason)
    }
}
