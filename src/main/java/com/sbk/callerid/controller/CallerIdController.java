package com.sbk.callerid.controller;

import com.sbk.callerid.service.ICallerIdService;
import com.sbk.callerid.service.PhoneNumberHelper;
import com.sbk.callerid.model.CallerId;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CallerIdController
{

    @Autowired
    public ICallerIdService idStore;

    @RequestMapping(value = "/query", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    @ApiOperation(value = "Fetches caller id information for the requested phone number.", notes = "Example: GET http://localhost:9090/query?number=(423)961-1337")
    //@ResponseStatus(value = HttpStatus.OK)  //Default response code is 200 for all controllers.  No need to do this.
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success.  CallerId record found."),
            @ApiResponse(code = 400, message = "Bad Request.  Invalid input phone number format."),
            @ApiResponse(code = 404, message = "Not Found.  Phone number is not found on the Server.")
    })
    public ResponseEntity<?> queryCallerId(@ApiParam(value = "Number with E.164 format like +1-400-300-2000", required = true) @RequestParam(value="number") String number)
    {
        String searchNumber = PhoneNumberHelper.convertPhoneNumber(number);
        if(searchNumber.equals(PhoneNumberHelper.INVALID_INPUT))
        {
            System.err.println("Search Number " + number + " is not a valid format.");
            String errorMsg = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid 'number' format\",\"path\":\"/query\"}";
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }
        if(idStore.contains(searchNumber))
        {
            System.out.println("Search Number " + searchNumber + " is found.");
            Map<String, List<CallerId>> response = new LinkedHashMap<>();
            response.put("results", idStore.get(searchNumber));
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else
        {
            System.err.println("Search Number " + searchNumber + " is not found.");
            String errorMsg = "{\"status\":404,\"error\":\"Not Found\",\"message\":\"Phone number is not found on the Server\",\"path\":\"/query\"}";
            return new ResponseEntity<>(errorMsg, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value = "/number", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ApiOperation(value ="Adds a new caller Id record to the service.") //Adding param "code=201" changes the default return code of 200.  Alternative to @ResponseStatus annotation.
    @ResponseStatus(value = HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created. New CallerId record saved successfully."),
            @ApiResponse(code = 400, message = "Bad Request.  Invalid input phone number format."),
            @ApiResponse(code = 406, message = "Duplicated Record.  Caller Id for given context already exists.")
    })
    public ResponseEntity<?> createCallerId(@RequestBody final @Valid @ApiParam(value = "CallerId object, new Record to be created.", required = true) CallerId newRecord)
    {
        //reformat phone number to E.164 format and check its validity
        newRecord.setNumber(PhoneNumberHelper.convertPhoneNumber(newRecord.getNumber()));
        if(newRecord.getNumber().equals(PhoneNumberHelper.INVALID_INPUT))
        {
            System.err.println("Search Number " + newRecord.getNumber() + " is not a valid format.");
            String errorMsg = "{\"status\":400,\"error\":\"Bad Request\",\"message\":\"Invalid 'number' format\",\"path\":\"/number\"}";
            return new ResponseEntity<>(errorMsg, HttpStatus.BAD_REQUEST);
        }

        //store the new record
        boolean saveStatus = idStore.add(newRecord);

        //send response
        if(saveStatus)
        {
            System.out.println("CallerID record " + newRecord + " saved.");
            return new ResponseEntity<>(newRecord, HttpStatus.CREATED);
        }
        else
        {
            System.err.println("CallerID record " + newRecord + " already exists.");
            String errorMsg = "{\"status\":406,\"error\":\"Not Acceptable\",\"message\":\"Phone number with the given context already exists.\",\"path\":\"/query\"}";
            return new ResponseEntity<>(errorMsg, HttpStatus.NOT_ACCEPTABLE);
        }
    }
}
