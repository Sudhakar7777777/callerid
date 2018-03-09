package com.sbk.callerid;

import com.sbk.callerid.model.CallerId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CallerIdIT
{
    @Autowired
    private TestRestTemplate testRestTemplate;

    private String QUERY_URL = "/query?number=1(423)9611337";
    private String NUMBER_URL = "/number";

    @Test
    public void shouldReturn200OnQuery() throws Exception
    {
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> entity = this.testRestTemplate.getForEntity(QUERY_URL, Map.class);

        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
        then(entity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        then(entity.getBody().values().size()).isEqualTo(1);
    }

    @Test
    public void shouldReturn200OnNumber() throws Exception
    {
        HttpEntity<CallerId> request = new HttpEntity<>(new CallerId("Sudhakar", "(412)1231234", "IT Testing"));

        @SuppressWarnings("rawtypes")
        ResponseEntity<CallerId> entity = this.testRestTemplate.postForEntity(NUMBER_URL, request, CallerId.class);
        then(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        then(entity.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON));
        then(entity.getBody().getName()).isEqualTo("Sudhakar");
    }
}