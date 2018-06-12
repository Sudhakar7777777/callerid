package com.sbk.callerid.model;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;

@ApiModel(value="CallerId", description="CallerId model")
public class CallerId
{
    @NotNull
    private String name;
    @NotNull
    private String number;
    @NotNull
    private String context;

    public CallerId()
    {

    }

    public CallerId(String name, String number, String context)
    {
        this.name = name;
        this.number = number;
        this.context = context;
    }

    public String getNumber()
    {
        return number;
    }

    public void setNumber(String number)
    {
        this.number = number;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "CallerId{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", context='" + context + '\'' +
                '}';
    }
}
