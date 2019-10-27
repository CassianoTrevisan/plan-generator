package com.tech.plangenerator;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GeneratePlanControllerRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void postValidPayloadAndGet24PaymentInstallmentsBack() throws JSONException, IOException {

        String loanDetailsInJson = "{\n" +
                "\"loanAmount\": \"5000\",\n" +
                "\"nominalRate\": \"5\",\n" +
                "\"duration\": 24,\n" +
                "\"startDate\": \"2017-06-01T00:00:01Z\"\n" +
                "}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(loanDetailsInJson, headers);

        ResponseEntity<String> response = restTemplate.exchange("/generate-plan", HttpMethod.POST, entity, String.class);

        String expectedJson;
        InputStream in =  this.getClass().getClassLoader().getResourceAsStream("defaultResponse.json");

        expectedJson = IOUtils.toString(in, "UTF-8");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }
}
