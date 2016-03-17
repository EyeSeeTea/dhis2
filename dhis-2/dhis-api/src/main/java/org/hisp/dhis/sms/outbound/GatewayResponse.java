package org.hisp.dhis.sms.outbound;

public enum GatewayResponse
{
    SUCCESS( "success","" ), 
    SENT( "success" ,""), 
    FAILED( "failed" ,""), 
    PENDING( "pending" ,""), 
    SERVICE_NOT_AVAILABLE("service not available","" ), 
    ENCODING_FAILURE( "encoding failed" ,""), 
    PROCESSING( "processing" ,""), 
    QUEUED("queued" ,""), 
    NO_GATWAY_CONFIGURATION( "no gateway configuration found","" ), 
    AUTENTICATION_FAILED("authentication failed","" ),
    
    // BulkSms Result Codes 
    
    RESULT_CODE_0("success",""),
    RESULT_CODE_1("scheduled",""),
    RESULT_CODE_22("internal fatal error",""),
    RESULT_CODE_23("authentication failure",""),
    RESULT_CODE_24("data validation failed",""),
    RESULT_CODE_25("you do not have sufficient credits",""),
    RESULT_CODE_26("upstream credits not available",""),
    RESULT_CODE_27("you have exceeded your daily quota",""),
    RESULT_CODE_28("pstream quota exceeded",""),
    RESULT_CODE_40("temporarily unavailable",""),
    RESULT_CODE_201("maximum batch size exceeded",""),
    
    // Clickatell Result Codes
    
    RESULT_CODE_200("success","The request was successfully completed"),
    RESULT_CODE_202("accepted","The message(s) will be processed"),
    RESULT_CODE_207("multi-status","More than  one message was submitted to the API; however, not all messages have the same status"),
    RESULT_CODE_400("bad request","Validation failure (such as missing/invalid parameters or headers)"),
    RESULT_CODE_401("unauthorized","Authentication failure. This can also be caused by IP lockdown settings"),
    RESULT_CODE_402("payment required","Not enough credit to send message"),
    RESULT_CODE_404("not found","Resource does not existS"),
    RESULT_CODE_405("method not allowed","Http method is not support on the resource"),
    RESULT_CODE_410("gone","Mobile number is blocked"),
    RESULT_CODE_429("too many requests","Generic rate limiting error"),
    RESULT_CODE_503("service unavailable","A temporary error has occurred on our platform - please retry");



    private final String responseMessage;

    private final String responseMessageDetail;

    private GatewayResponse( String responseMessage, String responseMessageDetail )
    {
        this.responseMessage = responseMessage;
        this.responseMessageDetail = responseMessageDetail;
    }

    public String getResponseMessage()
    {
        return responseMessage;
    }
    
    public String getResponseMessageDetail()
    {
        return responseMessageDetail;
    }
}

