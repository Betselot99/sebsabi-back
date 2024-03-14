package et.com.gebeya.Safaricom.sebsabi;

import com.fasterxml.jackson.databind.ObjectMapper;
import et.com.gebeya.safaricom.coreservice.SebsabiCoreApplication;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.ClientRequest;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.GigWorkerRequest;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.service.FormService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SebsabiCoreApplication.class)
@Testcontainers
@AutoConfigureMockMvc
public class SebsabiCoreApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private FormRepository formRepository;

    @Autowired
    private FormService formService;
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void shouldCreateClients() throws Exception {
        ClientRequest clientRequest= getClientRequest();
        String prodcutRequestString=objectMapper.writeValueAsString(clientRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/core/signup/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(prodcutRequestString))
                .andExpect(status().isCreated());
    }

    private ClientRequest getClientRequest() {
        return ClientRequest.builder()
                .firstName("Alazar")
                .lastName("Lastname") // You can set other properties as well
                .email("example@email.com")
                .companyName("Sample Company")
                .companyType("Type")
                .occupation("Occupation")
                .isActive(Status.Active)
                .build();
    }

    @Test
    void shouldCreateGigWorker() throws Exception {
        // Create a sample GigWorkerRequest object
        GigWorkerRequest gigWorkerRequest = getGigWorkerRequest();

        // Convert the GigWorkerRequest object to JSON string
        String gigWorkerRequestString = objectMapper.writeValueAsString(gigWorkerRequest);

        // Perform the POST request to the create endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/api/core/gig-worker/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gigWorkerRequestString))
                .andExpect(status().isCreated());
    }

    private GigWorkerRequest getGigWorkerRequest() {
        // Build and return a sample GigWorkerRequest object
        return GigWorkerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .qualification("Sample Qualification")
                .dob(new Date())
                .age(30)
                .isActive(Status.Active)
                .build();
    }


//    @Test
//    void shouldAddQuestionToForm() throws InvocationTargetException, IllegalAccessException {
//        // Create a sample FormQuestionDto
//        // Mock the behavior of the formService method
//        when(formService.addQuestionToForm(anyLong(), any(FormQuestionDto.class)))
//                .thenReturn(new Form());  //
//        FormQuestionDto questionDto = new FormQuestionDto();
//        questionDto.setQuestionText("Sample Question");
//        questionDto.setQuestionType(String.valueOf(QuestionType.TEXT));
//
//        // Mock the repository findById and save methods
//        when(formRepository.findById(anyLong())).thenReturn(java.util.Optional.of(new Form()));
//        when(formRepository.save(any(Form.class))).thenReturn(new Form());
//
//        // Call the service method
//        formService.addQuestionToForm(1L, questionDto);
//
//        // Verify that the formRepository.findById() and formRepository.save() methods were called
//        verify(formRepository).findById(anyLong());
//        verify(formRepository).save(any(Form.class));
//    }

}