package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.model.Payment;
import et.com.gebeya.safaricom.coreservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }
}

