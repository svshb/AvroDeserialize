package tsdb.measurement

import java.util

import tsdb.UnixTimestampMs

case class MeasurementV1(metric: Int, tags: util.Map[Int, Int], logtime: UnixTimestampMs, value: Long)

case class MeasurementV2(metric: Int, tags: Array[Int], logtime: UnixTimestampMs, value: Long)
