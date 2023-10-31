package CentralAPIAudit.CentralAPIAudit.intercepter;

import CentralAPIAudit.CentralAPIAudit.model.StarEntity;
import CentralAPIAudit.CentralAPIAudit.repo.StarRepo;
import CentralAPIAudit.CentralAPIAudit.service.StarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.text.SimpleDateFormat;
import java.util.Date;


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
//        logRequestData(request);
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

        System.out.println("Response Time: " + dateFormat.format(responseTime));
        System.out.println("Status Code :"+response.getStatus());
        System.out.println("Time Taken : " + timeTaken + " ms");
        System.out.println("Context path : "+request.getRequestURI());
        System.out.println("Method Used : "+request.getMethod());
        System.out.println("Header Name : "+ request.getHeaderNames());
        System.out.println("Content Type : "+request.getContentType());
        System.out.println("Request ID : "+request.getRequestId());
        System.out.println("Host Name : "+request.getServerName());

        //for storing into database
        StarEntity starEntity = new StarEntity();
        starEntity.setRequestTime(dateFormat.format(requestTime));
        starEntity.setResponseTime(dateFormat.format(responseTime));
        starEntity.setStatusCode(response.getStatus());
        starEntity.setTimeTaken(String.valueOf(timeTaken));
        starEntity.setRequestURI(request.getRequestURI());
        starEntity.setRequestMethod(request.getMethod());
        starEntity.setRequestHeaderName(request.getHeaderNames().toString()); // Consider looping through headers
        starEntity.setContentType(request.getContentType());
        starEntity.setRequestID(request.getRequestId()); // Replace with the actual request ID
        starEntity.setHostName(request.getServerName());


        starService.saveEntity(starEntity);



//        logResponseData(response);
    }
}
