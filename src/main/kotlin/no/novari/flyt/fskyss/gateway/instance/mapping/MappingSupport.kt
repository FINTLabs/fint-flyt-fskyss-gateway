package no.novari.flyt.fskyss.gateway.instance.mapping

internal fun MutableMap<String, String>.putOrEmpty(
    key: String,
    value: Any?,
) {
    put(key, value?.toString() ?: "")
}
