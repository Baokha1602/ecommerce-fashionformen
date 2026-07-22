package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Public endpoints cho payment callbacks (VNPay return, IPN; MoMo IPN).
 * Không cần authentication vì gọi từ bên thứ 3.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/vnpay/return")
    public ApiResponse<Map<String, String>> vnPayReturn(@RequestParam Map<String, String> params) {
        Map<String, String> result = paymentService.processVnPayReturn(params);
        String status = result.getOrDefault("status", "FAILED");
        String message = result.getOrDefault("message", "Xử lý thanh toán");
        return "SUCCESS".equals(status)
                ? ApiResponse.success(message, result)
                : ApiResponse.error(message, result);
    }

    @GetMapping("/vnpay/ipn")
    public Map<String, String> vnPayIpn(@RequestParam Map<String, String> params) {
        // VNPay IPN yêu cầu trả plain JSON { "RspCode": "00", "Message": "..." }
        return paymentService.processVnPayIpn(params);
    }

    @PostMapping("/momo/ipn")
    public Map<String, String> moMoIpn(@RequestBody Map<String, String> params) {
        return paymentService.processMoMoIpn(params);
    }
}
