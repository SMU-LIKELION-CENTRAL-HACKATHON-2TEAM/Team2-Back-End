package org.example.team2backend.global.apiPayload.exception;

import org.example.team2backend.global.apiPayload.code.RouteErrorCode;

public class RouteException extends CustomException{
    public RouteException(RouteErrorCode errorCode){super(errorCode);}
}
