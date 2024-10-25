package io.opentelemetry.example.logappender;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.logging.SystemOutLogRecordExporter;
import io.opentelemetry.instrumentation.log4j.appender.v2_17.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;

public class Application {

  public static void main(String[] args) {

    Configurator.reconfigure();

    LoggerContext context = (LoggerContext) LogManager.getContext(false);
    Configuration config = context.getConfiguration();

    OpenTelemetryAppender otelAppender = OpenTelemetryAppender.builder()
            .setName("MyOtelAppender")
            .build();
    otelAppender.start();

    config.getRootLogger().addAppender(otelAppender, null, null);

    context.updateLoggers();

    // Initialize OpenTelemetry as early as possible
    OpenTelemetry openTelemetry = initializeOpenTelemetry();

//    OpenTelemetryAppender.install(openTelemetry);
    otelAppender.setOpenTelemetry(openTelemetry);

    Logger logger = LogManager.getLogger("My-notebook");
    logger.error("My test log");
  }

  private static OpenTelemetry initializeOpenTelemetry() {
    OpenTelemetrySdk sdk =
        OpenTelemetrySdk.builder()
            .setTracerProvider(SdkTracerProvider.builder().setSampler(Sampler.alwaysOn()).build())
            .setLoggerProvider(
                SdkLoggerProvider.builder()
                    .addLogRecordProcessor(
                        BatchLogRecordProcessor.builder(SystemOutLogRecordExporter.create())
                            .build())
                    .build())
            .build();

    // Add hook to close SDK, which flushes logs
    Runtime.getRuntime().addShutdownHook(new Thread(sdk::close));

    return sdk;
  }
}
