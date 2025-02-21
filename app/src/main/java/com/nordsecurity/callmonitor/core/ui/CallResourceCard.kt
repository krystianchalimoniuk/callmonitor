package com.nordsecurity.callmonitor.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nordsecurity.callmonitor.core.model.CallResource
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaZoneId

@Composable
fun CallResourceCard(
    modifier: Modifier = Modifier,
    callLogEntry: CallResource,
) {
    OutlinedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row {
                    CallResourceName(
                        callResourceName = callLogEntry.callerName ?: "-",
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    CallResourceNumber(
                        callResourceNumber = callLogEntry.phoneNumber,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row {
                    CallResourceDuration(callResourceDuration = callLogEntry.duration)
                    Spacer(modifier = Modifier.width(4.dp))
                    CallResourceDate(callResourceDate = callLogEntry.startTime)
                }
            }
        }
    }
}

@Composable
fun CallResourceName(
    callResourceName: String,
    modifier: Modifier = Modifier,
) {
    Text(
        callResourceName,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}

@Composable
fun CallResourceNumber(
    callResourceNumber: String,
    modifier: Modifier = Modifier,
) {
    Text(
        callResourceNumber,
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier,
    )
}

@Composable
fun CallResourceDuration(
    callResourceDuration: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        formatDuration(callResourceDuration),
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier,
    )
}

@Composable
fun CallResourceDate(
    callResourceDate: Long,
    modifier: Modifier = Modifier,
) {
    Text(
        dateFormatted(Instant.fromEpochMilliseconds(callResourceDate)),
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier,
    )
}

private fun formatDuration(duration: Int): String {
    val hours = duration / 3600
    val minutes = (duration % 3600) / 60
    val seconds = duration % 60

    return if (hours > 0) {
        String.format(locale = Locale.getDefault(), "%02d:%02d:%02d,", hours, minutes, seconds)
    } else {
        String.format(locale = Locale.getDefault(), "%02d:%02d,", minutes, seconds)
    }
}

@Composable
fun dateFormatted(publishDate: Instant, style: FormatStyle = FormatStyle.MEDIUM): String =
    DateTimeFormatter
        .ofLocalizedDateTime(style)
        .withLocale(Locale.getDefault())
        .withZone(LocalTimeZone.current.toJavaZoneId())
        .format(publishDate.toJavaInstant())

@Composable
@Preview
private fun CallResourceCardPreview() {
    CallResourceCard(
        callLogEntry = CallResource(
            id = "1",
            callerName = "John Doe",
            phoneNumber = "+48 233424123",
            duration = 20,
            startTime = 1739965430L,
            timesQueried = 0
        )
    )
}