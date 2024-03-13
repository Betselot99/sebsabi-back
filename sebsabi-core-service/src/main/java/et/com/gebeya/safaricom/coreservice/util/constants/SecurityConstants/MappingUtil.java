package et.com.gebeya.safaricom.coreservice.util.constants.SecurityConstants;

import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.PaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentDto;
import et.com.gebeya.safaricom.coreservice.dto.PaymentDto.TransferPaymentResponseDto;
import et.com.gebeya.safaricom.coreservice.model.Payment;

public class MappingUtil {

    public static Payment mapBalanceRequestDtoToBalance(PaymentDto dto){
        return Payment.builder().id(Long.valueOf(dto.getUserId())).amount(dto.getBalance()).build();
    }

    public static TransferPaymentResponseDto mapBalanceToBalanceResponseDto(Payment balance){
        return TransferPaymentResponseDto.builder().gigWorkerId(String.valueOf(balance.getId())).amountTransferred(balance.getAmount()).build();
    }
}
