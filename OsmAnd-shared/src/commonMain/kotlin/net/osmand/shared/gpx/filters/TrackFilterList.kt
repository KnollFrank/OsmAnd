package net.osmand.shared.gpx.filters

import kotlinx.serialization.json.Json
import net.osmand.shared.gpx.data.SmartFolder
import net.osmand.shared.util.KAlgorithms
import net.osmand.shared.util.LoggerFactory

object TrackFilterList {
	val log = LoggerFactory.getLogger("TrackFilterList")

	fun parseFilters(str: String): List<SmartFolder>? {
		var savedFilters: List<SmartFolder>? = null
		if (!KAlgorithms.isEmpty(str)) {
			try {
				savedFilters = Json.decodeFromString<List<SmartFolder>?>(str)
			} catch (e: Throwable) {
				log.error("Failed to parse track filter: '$str'", e)
			}
		}
		return savedFilters
	}
}
