package mx.utng.srcp.fulbito.data.local

import androidx.room.TypeConverter
import mx.utng.srcp.fulbito.data.local.entity.EventType

class Converters {
    @TypeConverter
    fun fromEventType(value: EventType): String {
        return value.name
    }

    @TypeConverter
    fun toEventType(value: String): EventType {
        return EventType.valueOf(value)
    }
}
