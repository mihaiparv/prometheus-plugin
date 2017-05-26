package org.jenkinsci.plugins.prometheus.rest;

import org.jenkinsci.plugins.prometheus.JobCollector;
import org.jenkinsci.plugins.prometheus.MetricsRequest;
import org.jenkinsci.plugins.prometheus.config.PrometheusConfiguration;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.model.UnprotectedRootAction;
import hudson.util.HttpResponses;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.DefaultExports;

@Extension
public class PrometheusAction implements UnprotectedRootAction {
    private CollectorRegistry collectorRegistry;
    private JobCollector jobCollector = new JobCollector();

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Prometheus Metrics Exporter";
    }

    @Override
    public String getUrlName() {
        return PrometheusConfiguration.get().getUrlName();
    }

    public Object doDynamic(StaplerRequest request) {
        if (request.getRestOfPath().equals(PrometheusConfiguration.get().getAdditionalPath())) {
            if (collectorRegistry == null) {
                collectorRegistry = CollectorRegistry.defaultRegistry;
                collectorRegistry.register(jobCollector);
                DefaultExports.initialize();
            }
            return MetricsRequest.prometheusResponse(collectorRegistry);
        }
        throw HttpResponses.notFound();
    }
}
