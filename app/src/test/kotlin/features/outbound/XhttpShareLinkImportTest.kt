package features.outbound

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class XhttpShareLinkImportTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parseKeepsXhttpEncryptionRealityAndOptionalPqv() = runBlocking {
        val encryption =
            "mlkem768x25519plus.native.0rtt.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA.X25519"
        val link = "vless://00000000-0000-0000-0000-000000000000@example.invalid:443" +
            "?encryption=$encryption" +
            "&flow=xtls-rprx-vision" +
            "&security=reality" +
            "&sni=www.example.com" +
            "&fp=chrome" +
            "&pbk=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "&sid=ab" +
            "&pqv=dW3KmTdkxdnW2nSvu_qY88c7ibp2-wPik9mKBf0kvA" +
            "&type=xhttp" +
            "&host=www.example.com" +
            "&path=/" +
            "&mode=auto" +
            "#xhttp-node"

        val result = OutboundImportPipeline.parse(link)
        assertEquals(1, result.outbounds.size)
        val outbound = json.parseToJsonElement(result.outbounds.single().json).jsonObject

        assertEquals("vless", outbound.string("type"))
        assertEquals(encryption, outbound.string("encryption"))
        assertEquals("xtls-rprx-vision", outbound.string("flow"))

        val transport = outbound.obj("transport")
        assertNotNull(transport)
        assertEquals("xhttp", transport!!.string("type"))
        assertEquals("/", transport.string("path"))
        assertEquals("www.example.com", transport.string("host"))
        assertEquals("auto", transport.string("mode"))
        assertTrue(transport["host"] is JsonPrimitive)

        val tls = outbound.obj("tls")
        assertNotNull(tls)
        assertEquals("www.example.com", tls!!.string("server_name"))
        assertEquals("chrome", tls.obj("utls")?.string("fingerprint"))
        val reality = tls.obj("reality")
        assertNotNull(reality)
        assertEquals("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", reality!!.string("public_key"))
        assertEquals("ab", reality.string("short_id"))
        assertEquals(
            "dW3KmTdkxdnW2nSvu_qY88c7ibp2-wPik9mKBf0kvA",
            reality.string("mldsa65_verify"),
        )
    }

    @Test
    fun parseClearsVisionWhenEncryptionIsNone() = runBlocking {
        val link = "vless://00000000-0000-0000-0000-000000000000@example.invalid:443" +
            "?encryption=none" +
            "&flow=xtls-rprx-vision" +
            "&security=reality" +
            "&sni=www.example.com" +
            "&fp=chrome" +
            "&pbk=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
            "&sid=ab" +
            "&type=xhttp" +
            "&host=www.example.com" +
            "&path=/" +
            "#no-enc"

        val outbound = json.parseToJsonElement(
            OutboundImportPipeline.parse(link).outbounds.single().json,
        ).jsonObject
        assertEquals("xhttp", outbound.obj("transport")?.string("type"))
        assertTrue(outbound.string("encryption").isBlank() || outbound.string("encryption") == "none")
        assertTrue(outbound.string("flow").isBlank())
    }

    @Test
    fun editorSchemaIncludesXhttpAndVlessEncryption() {
        val schema = OutboundEditorRegistry.schema("vless")
        val transport = schema.fields.first { it.path == "transport.type" }
        assertTrue(transport.options.contains("xhttp"))
        val protocolPaths = schema.fields.map { it.path }
        assertTrue(protocolPaths.contains("encryption"))
        val tlsPaths = schema.fields.map { it.path }
        assertTrue(tlsPaths.contains("tls.reality.mldsa65_verify"))
    }
}

private fun JsonObject.string(key: String): String =
    (this[key] as? JsonPrimitive)?.contentOrNull.orEmpty()

private fun JsonObject.obj(key: String): JsonObject? = this[key] as? JsonObject
