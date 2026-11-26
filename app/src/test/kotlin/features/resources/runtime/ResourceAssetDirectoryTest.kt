// Copyright 2026, AsteriskBOX contributors
// SPDX-License-Identifier: GPL-3.0

package features.resources.runtime

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ResourceAssetDirectoryTest {
    @get:Rule val folder = TemporaryFolder()

    @Test
    fun scanPreservesCoreDexFilesWithoutListingThem() {
        val directory = ResourceAssetDirectory(folder.root)
        check(directory.assetsDir.mkdir())
        val helper = directory.file("system-dns-helper.dex").apply { writeText("helper") }
        val staging = directory.file(".system-dns-helper-update.dex").apply { writeText("staging") }
        val unrelated = directory.file("unrelated.dex").apply { writeText("discard") }
        val ruleSet = directory.file("direct.srs").apply { writeText("rules") }

        assertEquals(listOf(ruleSet), directory.scan { it.endsWith(".srs") })
        assertEquals("helper", helper.readText())
        assertEquals("staging", staging.readText())
        assertFalse(unrelated.exists())
    }
}
