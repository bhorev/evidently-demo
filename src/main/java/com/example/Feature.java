package com.example;

import java.time.Instant;
import java.util.Collections;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.evidently.EvidentlyClient;
import software.amazon.awssdk.services.evidently.model.*;

public class Feature {

    private static final EvidentlyClient evidently = EvidentlyClient.builder().region(Region.US_EAST_2).build();
    private static final String FEATURE =  "Awesome-Feature";
    private static final String PROJECT = "Project-Demo";
    private final String entityId;

    public void execute() {
        // Ask which feature variation to execute
        EvaluateFeatureRequest request = EvaluateFeatureRequest.builder()
                .entityId(entityId)
                .feature(FEATURE)
                .project(PROJECT)
                .build();
        EvaluateFeatureResponse result = evidently.evaluateFeature(request);
        boolean useNewFeature = result.value().boolValue();

        // Execute feature variation
        int executionTime = compute(useNewFeature);

        // Report how it went
        reportMetric(executionTime);
    }

    private void reportMetric(int executionTime) {
        Event event = Event.builder()
                .type(EventType.AWS_EVIDENTLY_CUSTOM)
                .timestamp(Instant.now())
                .data(jsonString(executionTime))
                .build();

        PutProjectEventsRequest eventPutRequest = PutProjectEventsRequest.builder()
                .events(Collections.singletonList(event))
                .project(PROJECT)
                .build();

        PutProjectEventsResponse response = evidently.putProjectEvents(eventPutRequest);
        System.out.println(" - Failures - " + response.failedEventCount());
    }

    Feature() {
        entityId = RandomStringUtils.randomAlphabetic(10);
    }

    private String jsonString(int executionTime) {
        String json =  "{ \"ExecutionTimeMS\": \""
                + entityId
                + "\", \"ExecutionTimeMSValue\": "
                + executionTime
                + " }";
        System.out.print(json);
        return json;
    }

    private int compute(boolean useNewFeature) {
        Random random = new Random();
        if (useNewFeature) {
            return random.nextInt(100) + 300;
        }
        return random.nextInt(100) + 500;
    }
}
