package CentralAPIAudit.CentralAPIAudit.intercepter;

import CentralAPIAudit.CentralAPIAudit.model.StarEntity;
import CentralAPIAudit.CentralAPIAudit.service.StarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;


@Component
public class Intercepter implements HandlerInterceptor {
    private long startTime;


    @Autowired
    private StarService starService;

    Date requestTime = new Date(); // Capture the current date and time

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        startTime = System.currentTimeMillis();
        Date requestTime = new Date(); // Capture the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Request Time: " + dateFormat.format(requestTime));
        request.setAttribute("startTime", startTime);
        return true;

    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
    }




    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                @Nullable Exception ex) throws Exception {

        long endTime = System.currentTimeMillis();
        long timeTaken = endTime - startTime;
        Date responseTime = new Date(); // Capture the current date and time for response
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        StarEntity starEntity = new StarEntity();

        //for error trace
        String errorStackTrace = null;
        if (ex != null) {
            // Capture the exception stack trace in a variable
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            errorStackTrace = sw.toString();
            System.out.println(" error trace : " + errorStackTrace);
//            starEntity.setErrorTrace(errorStackTrace);

        }

        System.out.println("Response Time: " + dateFormat.format(responseTime));
        System.out.println("Status Code :" + response.getStatus());
        System.out.println("Time Taken : " + timeTaken + " ms");
        System.out.println("Context path : " + request.getRequestURI());
        System.out.println("Method Used : " + request.getMethod());
        System.out.println("Header Name : " + request.getHeaderNames().toString());
        System.out.println("Content Type : " + request.getContentType());
        System.out.println("Request ID : " + request.getRequestId());
        System.out.println("Host Name : " + request.getServerName());


        ContentCachingResponseWrapper wrapper;
        if (response instanceof ContentCachingResponseWrapper) {
            wrapper = (ContentCachingResponseWrapper) response;
        } else {
            wrapper = new ContentCachingResponseWrapper(response);
        }
        getResponse(wrapper);


        //for error trace


        //for storing into database
        starEntity.setRequestTime(dateFormat.format(requestTime));
        starEntity.setResponseTime(dateFormat.format(responseTime));
        starEntity.setStatusCode(response.getStatus());
        starEntity.setTimeTaken(String.valueOf(timeTaken));
        starEntity.setRequestURI(request.getRequestURI());
        starEntity.setRequestMethod(request.getMethod());
        starEntity.setRequestHeaderName(getRequestHeaderNames(request));
        starEntity.setContentType(request.getContentType());
        starEntity.setRequestID(request.getRequestId()); // Replace with the actual request ID
        starEntity.setHostName(request.getServerName());
        starEntity.setResponse(getResponse(new ContentCachingResponseWrapper(response)));
        starEntity.setErrorTrace(errorStackTrace);


        starService.saveEntity(starEntity);


    }





//    Response
//    Status
//    Request body
//    Error message (Stack trace)
//    Header String

    private String getRequestHeaderNames(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        StringBuilder headerNamesStr = new StringBuilder();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerNamesStr.append(headerName).append(", ");
        }
        return headerNamesStr.toString();
    }

    private String getResponse(ContentCachingResponseWrapper contentCachingResponseWrapper) {

        String response = IOUtils.toString(contentCachingResponseWrapper.getContentAsByteArray(), contentCachingResponseWrapper.getCharacterEncoding());
        return response;
    }


}


