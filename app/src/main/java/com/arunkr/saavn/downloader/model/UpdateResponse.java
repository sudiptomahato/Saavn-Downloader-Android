package com.arunkr.saavn.downloader.model;

/**
 * Created by Arun Kumar Shreevastava on 24/10/16.
 */

public class UpdateResponse
{
    int version,type;
    String url,message;

    public UpdateResponse()
    {

    }

    public UpdateResponse(int version, String url,String message,int type)
    {
        this.version = version;
        this.url = url;
        this.type = type;
        this.message = message;
    }

    public int getVersion()
    {
        return version;
    }

    public String getUrl()
    {
        return url;
    }

    public void setVersion(int version)
    {
        this.version = version;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
