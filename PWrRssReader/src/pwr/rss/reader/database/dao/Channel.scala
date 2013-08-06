package pwr.rss.reader.database.dao

import scala.collection.mutable.MutableList
import java.lang.Long

class Channel(
		val ID: Long,
		val name: String,
		val logo: Long,
		val site: String,
		private var selected: Long = 1L) {

	def select(asSelect: Boolean) = selected = if (asSelect) 1L else 0L
	def isSelected = selected == 1L
	def selectedValue = selected
}