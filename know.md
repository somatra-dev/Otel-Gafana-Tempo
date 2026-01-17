Found the issue. In RestClientConfig.java:18-19, you have a conflicting configuration:

.baseUrl("http://localhost:9002/api/v1/")
.requestInterceptor(new LoadBalancerInterceptor(loadBalancerClient))

The Problem: You're using LoadBalancerInterceptor with a direct localhost URL. The load balancer intercepts the request and tries to resolve "localhost" as a service name, but since Eureka is disabled (commented out in your application.yml), there are no registered instances.

You have two options:

Option 1: Remove Load Balancer (if not using service discovery)

----

The issue was mixing direct URL (localhost:9002) with load balancer (which expects a service name like product-service).

Summary of the fix:
- Removed LoadBalancerInterceptor since you're using a direct URL
- Removed LoadBalancerClient parameter and unused imports

Option 2 (alternative): If you want to use Service Discovery later

Uncomment Eureka in application.yml and change the base URL to use the service name:
.baseUrl("http://product-service")
.requestInterceptor(new LoadBalancerInterceptor(loadBalancerClient))

The load balancer will then resolve product-service to actual instances registered in Eureka.