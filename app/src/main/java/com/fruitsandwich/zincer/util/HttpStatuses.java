package com.fruitsandwich.zincer.util;

import org.apache.http.HttpStatus;

/**
 * Created by nakac on 15/07/23.
 */
public class HttpStatuses {
    public static String valueOf(int statusCode) {
        switch (statusCode) {
            case 400:
                return "Bad Request";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return Integer.toString(statusCode);
        }
    }
}
