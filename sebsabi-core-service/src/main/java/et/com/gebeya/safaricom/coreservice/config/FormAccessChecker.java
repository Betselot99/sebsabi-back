//package et.com.gebeya.safaricom.coreservice.config;
//import et.com.gebeya.safaricom.coreservice.model.Form;
//import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//@Component
//public class FormAccessChecker {
//
//    @Autowired
//    private FormRepository formRepository;
//
//    public boolean hasAccess(Authentication authentication, HttpServletRequest request) {
//        // Get the gig worker's ID from the authentication object
//        Long gigWorkerId = extractGigWorkerId(request);
//
//        // Extract the form ID from the request path
//        Long formId = extractFormIdFromRequest(request);
//
//        // Check if the gig worker is assigned to the requested form
//        return isGigWorkerAssignedToForm(gigWorkerId, formId);
//    }
//
//    private Long extractGigWorkerId(HttpServletRequest request) {
//        // Extract the gig worker's ID from the request header
//        String header = request.getHeader("UserId");
//        // Parse the header value to get the gig worker ID
//        return Long.parseLong(header);
//    }
//
//    private Long extractFormIdFromRequest(HttpServletRequest request) {
//        // Extract the form ID from the request path
//        // You need to customize this based on your application's URL structure
//        // Here's an example assuming the form ID is part of the path:
//        String path = request.getRequestURI();
//        String[] pathSegments = path.split("/");
//        // Assuming the form ID is the last segment of the path
//        return Long.parseLong(pathSegments[pathSegments.length - 1]);
//    }
//
//    private boolean isGigWorkerAssignedToForm(Long gigWorkerId, Long formId) {
//        // Check if the gig worker is assigned to the requested form
//        // You need to implement this logic based on your application's business rules
//        // For example, you can query the database to check if the gig worker is assigned to the form
//        // Here's a hypothetical example using a repository:
//        Form form = formRepository.findById(formId).orElse(null);
//        return form != null && form.getAssignedGigWorker().getId().equals(gigWorkerId);
//    }
//}
