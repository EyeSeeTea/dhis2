package org.hisp.dhis.sms.outbound;

public enum GatewayResponse
{
    SUCCESS( "success" ), SENT( "success" ), FAILED( "failed" ), PENDING( "pending" ), PROCESSING(
        "processing" ), QUEUED( "queued" );

    private final String responseMessage;

    private GatewayResponse( String responseMessage )
    {
        this.responseMessage = responseMessage;
    }

    public String getResponseMessage()
    {
        return responseMessage;
    }
}
