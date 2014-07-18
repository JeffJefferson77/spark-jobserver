package ooyala.common.akka.metrics

import com.codahale.metrics._

object MetricsWrapper {
  val registry: MetricRegistry = new MetricRegistry
  val reporter: JmxReporter = JmxReporter.forRegistry(registry).build()
  // TODO: Use the cleaner way if possible
  val shutdownHook: Thread = new Thread {
    override def run {
      reporter.stop
    }
  }
  // Start the reporter and set up shutdown hooks
  reporter.start()
  Runtime.getRuntime.addShutdownHook(shutdownHook)

  def newGauge[T](klass: Class[_], name: String, metric: => T): Gauge[T] =
    registry.register(MetricRegistry.name(klass, name), new Gauge[T] {
      override def getValue(): T = metric
    })

  def newCounter(klass: Class[_], name: String): Counter =
    registry.counter(MetricRegistry.name(klass, name))

  // TODO: Is this necessary???
  def newCounter(klass: Class[_], name: String, scope: String): Counter =
    registry.counter(MetricRegistry.name(klass, name, scope))

  def newTimer(klass: Class[_], name: String): Timer =
    registry.timer(MetricRegistry.name(klass, name))

  def newHistogram(klass: Class[_], name: String): Histogram =
    registry.histogram(MetricRegistry.name(klass, name))

  def newMeter(klass: Class[_], name: String): Meter =
    registry.meter(MetricRegistry.name(klass, name))

  def getRegistry: MetricRegistry = {
    return registry
  }

  def shutdown = {
    reporter.stop
    Runtime.getRuntime.removeShutdownHook(shutdownHook)
  }
}