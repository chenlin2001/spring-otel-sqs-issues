package com.mimecast.architech.springk8sample.rest;

import com.mimecast.architech.springk8sample.model.SqsMessage;
import com.mimecast.architech.springk8sample.service.aws.sqs.SqsMessageHandler;
import io.opentelemetry.api.OpenTelemetry;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Log4j2
@TestPropertySource(properties = {
        "sqs.enabled=true",
        "sqs.queue.name=test-sqs-queue-name"
})
class OtelSqsExampleControllerTest {

    @LocalServerPort
    public int serverPort;

    @Autowired
    private OpenTelemetry openTelemetry;

    @Autowired
    private SqsMessageHandler sqsMessageHandler;

    @PostConstruct
    public void initRestAssured() {
        RestAssured.port = serverPort;
        RestAssured.urlEncodingEnabled = false;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Container
    static LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("localstack/init.sh", 0775),
                    "/etc/localstack/init/ready.d/init.sh"
            )
            .withServices(LocalStackContainer.Service.SQS)
            .waitingFor(Wait.forLogMessage(".*Initialized\\.\n", 1));

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public SqsMessageHandler sqsMessageHandler() {
            return mock(SqsMessageHandler.class);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Spring Cloud AWS credentials
        registry.add("spring.cloud.aws.credentials.access-key", localstack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localstack::getSecretKey);
        registry.add("spring.cloud.aws.region.static", localstack::getRegion);

        // Spring Cloud AWS SQS endpoint
        registry.add("spring.cloud.aws.sqs.endpoint", () ->
                localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString());

        // Queue URL for SqsService
        registry.add("sqs.queue.url", () ->
                localstack.getEndpointOverride(LocalStackContainer.Service.SQS).toString() + "/000000000000/test-sqs-queue-name");
    }

    @Test
    public void callSqsExample_ThisFailsIfSpanContextNotValid() {
        Response response = RestAssured.given()
                .accept(ContentType.JSON)
                .when()
                .get("/otel-sqs/example");


        verifyNoInteractions(sqsMessageHandler);

        assertEquals(200, response.statusCode());


    }
}
