package com.example.ecommerce_fashionformen.controllers;

import com.example.ecommerce_fashionformen.controllers.common.ApiResponse;
import com.example.ecommerce_fashionformen.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Public endpoints cho payment callbacks (VNPay return, IPN; MoMo IPN, Return).
 * Không cần authentication vì gọi từ bên thứ 3 hoặc redirect trình duyệt.
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    // ===================== VNPay =====================

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

    // ===================== MoMo =====================

    /**
     * Webhook IPN: MoMo gọi server-to-server để thông báo kết quả giao dịch.
     *
     * Quan trọng: LUÔN trả HTTP 200 cho MoMo dù xử lý thành công hay thất bại.
     * Nếu server trả HTTP != 200, MoMo sẽ retry IPN nhiều lần gây xử lý trùng.
     *
     * Exception từ Service (do @Transactional rollback) được bắt tại đây,
     * đảm bảo DB rollback đúng mà vẫn trả HTTP 200 cho MoMo.
     */
    @PostMapping("/momo/ipn")
    public ResponseEntity<Map<String, String>> moMoIpn(@RequestBody Map<String, Object> params) {
        try {
            Map<String, String> result = paymentService.processMoMoIpn(params);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[MoMo IPN] Lỗi xử lý, DB đã rollback: {}", e.getMessage());
            // Vẫn trả HTTP 200 để MoMo không retry, kèm thông báo lỗi
            return ResponseEntity.ok(Map.of(
                    "status", "ERROR",
                    "message", "Lỗi xử lý IPN: " + e.getMessage()
            ));
        }
    }

    /**
     * Redirect Return: MoMo chuyển hướng trình duyệt người dùng về đây sau khi thanh toán.
     * Trả HTML trực tiếp để hiển thị kết quả cho người dùng.
     */
    @GetMapping("/momo/return")
    public ResponseEntity<String> moMoReturn(@RequestParam Map<String, String> params) {
        String html = paymentService.processMoMoReturn(params);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }
}

