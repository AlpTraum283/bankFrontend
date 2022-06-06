package org.example;

import com.vaadin.flow.server.VaadinServletRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Constants {
    public static final String jwtSecret = new String(Base64.getEncoder().encode("secret".getBytes(StandardCharsets.UTF_8)));
    public static final int BALANCE_ATTRIBUTE_ID = 1;
    public static final int DRAFT_ATTRIBUTE_ID = 2;
    public static final int PASSWORD_ATTRIBUTE_ID = 3;
    public static final int OPERATION_ATTRIBUTE_ID = 4;
    public static final int SUM_ATTRIBUTE_ID = 5;
    public static final int CURRENCY_ATTRIBUTE_ID = 6;
    public static final int SENDER_ATTRIBUTE_ID = 7;
    public static final int RECIPIENT_ATTRIBUTE_ID = 8;
    public static final int STATUS_ATTRIBUTE_ID = 9;
    public static final int MESSAGE_ATTRIBUTE_ID = 10;

    public static final String OBJECT_TYPE_USER = "userentity";
    public static final String OBJECT_TYPE_TRANSFER = "transferentity";
    public static final String OBJECT_TYPE_ACCOUNT = "accountentity";
    public static final String OBJECT_TYPE_TRANSFER_REQUEST = "transferrequestentity";

    public static final String OPERATION_TYPE_IN = "IN";
    public static final String OPERATION_TYPE_OUT = "OUT";
    public static final String TRANSACTION_STATUS_NEW = "New";
    public static final String TRANSACTION_STATUS_IN_PROGRESS = "In Progress";
    public static final String TRANSACTION_STATUS_SUCCESS = "Success";
    public static final String TRANSACTION_STATUS_ERROR = "Error";
    public static int transferRequestId = 0;

    public static String getTokenFromCookie() {
        HttpServletRequest request = VaadinServletRequest.getCurrent().getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
