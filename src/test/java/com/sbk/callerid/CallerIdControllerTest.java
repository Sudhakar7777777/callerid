package com.sbk.callerid;

import com.sbk.callerid.model.CallerId;
import com.sbk.callerid.service.ICallerIdService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CallerIdControllerTest
{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICallerIdService service;

    private CallerId record;
    private List<CallerId> expectedResponse;
    private final String TEST_RECORD_IN_JSON = "{" + "\"name\":\"Sudhakar\"," + "\"number\":\"(423)9611999\"," + "\"context\":\"Unit Testing\"" + "}";
    private final String TEST_RECORD_OUT_JSON = "{" + "\"name\":\"Sudhakar\"," + "\"number\":\"+14239611999\"," + "\"context\":\"Unit Testing\"" + "}";
    private final String TEST_RECORD_ERR_JSON = "{" + "\"name\":\"Sudhakar\"," + "\"number\":\"ALPHA\"," + "\"context\":\"Unit Testing\"" + "}";
    private final String TEST_RECORD_ERR2_JSON = "{" + "\"name\":\"Sudhakar\"," + "\"number\":\"(423)9611999\"," + "\"context2\":\"Unit Testing\"" + "}";

    @Before
    public void setup()
    {
        record = new CallerId("Sudhakar", "+14239611999", "Unit Testing");
        expectedResponse = Arrays.asList(record);
    }

    @Test
    public void getQueryPositiveSuccess() throws Exception
    {
        given(service.get("+14239611337")).willReturn(expectedResponse);
        given(service.contains("+14239611337")).willReturn(true);

        mockMvc.perform(get("/query?number=1(423)9611337")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.results", hasSize(1)))
                .andExpect(jsonPath("$.results[0].name", is(record.getName())));
    }

    @Test
    public void getQueryNegativeNumberNotFound() throws Exception
    {
        given(service.get("+14239611337")).willReturn(expectedResponse);
        given(service.contains("+14239611337")).willReturn(true);

        mockMvc.perform(get("/query?number=1(423)9611555")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    public void getQueryNegativeNumberIsInvalid() throws Exception
    {
        given(service.get("+14239611337")).willReturn(expectedResponse);
        given(service.contains("+14239611337")).willReturn(true);

        mockMvc.perform(get("/query?number=ALPHA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    public void postNumberPositiveCreated() throws Exception
    {
        given(service.add(any(CallerId.class))).willReturn(true);
        given(service.contains("+14239611999")).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/number")
                .content(TEST_RECORD_IN_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(content().json(TEST_RECORD_OUT_JSON));
    }

    @Test
    public void postNumberNegativeDuplicate() throws Exception
    {
        given(service.add(any(CallerId.class))).willReturn(false);
        given(service.contains("+14239611999")).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/number")
                .content(TEST_RECORD_IN_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(406));
    }

    @Test
    public void postNumberNegativeIsInvalidPhoneNumber() throws Exception
    {
        given(service.add(any(CallerId.class))).willReturn(true);
        given(service.contains("+14239611999")).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/number")
                .content(TEST_RECORD_ERR_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }

    @Test
    public void postNumberNegativeIsInvalidRequest() throws Exception
    {
        given(service.add(any(CallerId.class))).willReturn(true);
        given(service.contains("+14239611999")).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/number")
                .content(TEST_RECORD_ERR2_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(400));
    }
}