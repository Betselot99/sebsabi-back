package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/payment")

public class PaymentController {

    private final PaymentService paymentService;

//    @PostMapping("/client-to-admin")
//    public ResponseEntity<TransferPaymentResponseDto> transferFromClientToAdmin(@RequestBody @Valid TransferPaymentDto transferPaymentDto) {
//        TransferPaymentResponseDto response = paymentService.transferPaymentFromClientToAdmin(transferPaymentDto);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

//    @PostMapping("/admin-to-gig-worker")
//    public ResponseEntity<TransferPaymentResponseDto> transferFromAdminToGigWorker(@RequestBody @Valid TransferPaymentDto transferPaymentDto) {
//        TransferPaymentResponseDto response = paymentService.transferPaymentFromAdminToGigWorker(transferPaymentDto);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
}

