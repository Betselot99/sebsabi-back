package et.com.gebeya.safaricom.coreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient
//@OpenAPIDefinition(info = @Info(title = "Sebsabi System",description = "Done by the UnBeatables"))
public class SebsabiCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(SebsabiCoreApplication.class,args);
    }

    @LoadBalanced
    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}