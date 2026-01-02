package com.ifthen.app.data.local.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ifthen.app.domain.model.Category
import com.ifthen.app.domain.model.DelegateChannel
import com.ifthen.app.domain.model.LogStatus
import com.ifthen.app.domain.model.Priority
import com.ifthen.app.domain.model.PriorityStatus
import com.ifthen.app.domain.model.SkipReason
import com.ifthen.app.domain.model.StateType
import com.ifthen.app.domain.model.TriggerConfig
import com.ifthen.app.domain.model.TriggerType
import com.ifthen.app.domain.model.ModeType

class Converters {
    private val gsonInstance = Gson()

    // ModeType
    @TypeConverter
    fun fromModeType(value: ModeType): String = value.name

    @TypeConverter
    fun toModeType(value: String): ModeType = ModeType.valueOf(value)

    // List<ModeType> for applicable modes in rules
    @TypeConverter
    fun fromModeTypeList(value: List<ModeType>): String = value.joinToString(",") { it.name }

    @TypeConverter
    fun toModeTypeList(value: String): List<ModeType> {
        if (value.isBlank()) return ModeType.entries.toList()
        return value.split(",").map { ModeType.valueOf(it.trim()) }
    }
    private val gson = Gson()

    // Category
    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    // Priority
    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)

    // TriggerType
    @TypeConverter
    fun fromTriggerType(value: TriggerType): String = value.name

    @TypeConverter
    fun toTriggerType(value: String): TriggerType = TriggerType.valueOf(value)

    // LogStatus
    @TypeConverter
    fun fromLogStatus(value: LogStatus): String = value.name

    @TypeConverter
    fun toLogStatus(value: String): LogStatus = LogStatus.valueOf(value)

    // SkipReason
    @TypeConverter
    fun fromSkipReason(value: SkipReason?): String? = value?.name

    @TypeConverter
    fun toSkipReason(value: String?): SkipReason? = value?.let { SkipReason.valueOf(it) }

    // StateType
    @TypeConverter
    fun fromStateType(value: StateType): String = value.name

    @TypeConverter
    fun toStateType(value: String): StateType = StateType.valueOf(value)

    // DelegateChannel
    @TypeConverter
    fun fromDelegateChannel(value: DelegateChannel): String = value.name

    @TypeConverter
    fun toDelegateChannel(value: String): DelegateChannel = DelegateChannel.valueOf(value)

    // PriorityStatus
    @TypeConverter
    fun fromPriorityStatus(value: PriorityStatus): String = value.name

    @TypeConverter
    fun toPriorityStatus(value: String): PriorityStatus = PriorityStatus.valueOf(value)

    // List<String> for delegate areas
    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString("|||")

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split("|||")
    }

    // TriggerConfig serialization/deserialization
    companion object {
        private val gson = Gson()

        fun serializeTriggerConfig(config: TriggerConfig): String {
            val jsonObject = JsonObject()
            when (config) {
                is TriggerConfig.TimeTrigger -> {
                    jsonObject.addProperty("type", "TIME")
                    jsonObject.addProperty("hour", config.hour)
                    jsonObject.addProperty("minute", config.minute)
                    jsonObject.add("daysOfWeek", gson.toJsonTree(config.daysOfWeek))
                }
                is TriggerConfig.CalendarTrigger -> {
                    jsonObject.addProperty("type", "CALENDAR")
                    jsonObject.addProperty("checkTime", config.checkTime)
                    jsonObject.addProperty("requiresFreeUntil", config.requiresFreeUntil)
                    jsonObject.add("daysOfWeek", gson.toJsonTree(config.daysOfWeek))
                }
                is TriggerConfig.PatternTrigger -> {
                    jsonObject.addProperty("type", "PATTERN")
                    jsonObject.addProperty("relatedRuleId", config.relatedRuleId)
                    jsonObject.addProperty("daysWithoutCompletion", config.daysWithoutCompletion)
                    jsonObject.addProperty("category", config.category?.name)
                }
                is TriggerConfig.ManualTrigger -> {
                    jsonObject.addProperty("type", "MANUAL")
                    jsonObject.addProperty("stateRequired", config.stateRequired?.name)
                }
                is TriggerConfig.EventTrigger -> {
                    jsonObject.addProperty("type", "EVENT")
                    jsonObject.addProperty("eventName", config.eventName)
                }
            }
            return jsonObject.toString()
        }

        fun deserializeTriggerConfig(json: String): TriggerConfig {
            val jsonObject = JsonParser.parseString(json).asJsonObject
            val type = jsonObject.get("type").asString

            return when (type) {
                "TIME" -> TriggerConfig.TimeTrigger(
                    hour = jsonObject.get("hour").asInt,
                    minute = jsonObject.get("minute").asInt,
                    daysOfWeek = gson.fromJson(
                        jsonObject.get("daysOfWeek"),
                        Array<Int>::class.java
                    ).toList()
                )
                "CALENDAR" -> TriggerConfig.CalendarTrigger(
                    checkTime = jsonObject.get("checkTime").asString,
                    requiresFreeUntil = jsonObject.get("requiresFreeUntil").asString,
                    daysOfWeek = gson.fromJson(
                        jsonObject.get("daysOfWeek"),
                        Array<Int>::class.java
                    ).toList()
                )
                "PATTERN" -> TriggerConfig.PatternTrigger(
                    relatedRuleId = jsonObject.get("relatedRuleId")?.takeIf { !it.isJsonNull }?.asString,
                    daysWithoutCompletion = jsonObject.get("daysWithoutCompletion").asInt,
                    category = jsonObject.get("category")?.takeIf { !it.isJsonNull }?.asString?.let { Category.valueOf(it) }
                )
                "MANUAL" -> TriggerConfig.ManualTrigger(
                    stateRequired = jsonObject.get("stateRequired")?.takeIf { !it.isJsonNull }?.asString?.let { StateType.valueOf(it) }
                )
                "EVENT" -> TriggerConfig.EventTrigger(
                    eventName = jsonObject.get("eventName").asString
                )
                else -> throw IllegalArgumentException("Unknown trigger type: $type")
            }
        }
    }
}
