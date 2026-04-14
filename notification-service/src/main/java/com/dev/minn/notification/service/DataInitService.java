package com.dev.minn.notification.service;

import com.dev.minn.notification.node.Template;
import com.dev.minn.notification.repository.TemplateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitService {

    TemplateRepository templateRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("Bắt đầu kiểm tra và khởi tạo dữ liệu mẫu...");
        initTemplate();
        log.info("Khởi tạo dữ liệu mẫu hoàn tất!");
    }

    private void initTemplate() {
        // ==========================================
        // 1. TEMPLATE: GỬI MÃ OTP XÁC THỰC
        // ==========================================
        if (!templateRepository.existsByCode("SEND_OTP_VERIFY")) {
            String otpTemplateHtml = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;">
                        <div style="text-align: center; border-bottom: 2px solid #f0f0f0; padding-bottom: 10px; margin-bottom: 20px;">
                            <h2 style="color: #333333; margin: 0;">Xác Thực Tài Khoản</h2>
                        </div>
                        <p style="color: #555555; font-size: 16px;">Xin chào <strong th:text="${name}">Bạn</strong>,</p>
                        <p style="color: #555555; font-size: 16px; line-height: 1.5;">Cảm ơn bạn đã đăng ký tài khoản trên hệ thống của chúng tôi. Vui lòng sử dụng mã OTP dưới đây để hoàn tất việc xác thực email của bạn:</p>
                    
                        <div style="text-align: center; margin: 30px 0;">
                            <span th:text="${otp}" style="display: inline-block; font-size: 32px; font-weight: bold; color: #2E86C1; padding: 15px 30px; background-color: #F2F4F4; border: 2px dashed #AED6F1; border-radius: 8px; letter-spacing: 5px;">
                                000000
                            </span>
                        </div>
                    
                        <p style="color: #555555; font-size: 14px;"><em>* Lưu ý: Mã xác thực này sẽ hết hạn sau 5 phút. Vui lòng <strong>không chia sẻ</strong> mã này cho bất kỳ ai.</em></p>
                        <hr style="border: none; border-top: 1px solid #eeeeee; margin: 30px 0;">
                        <p style="font-size: 12px; color: #999999; text-align: center;">Đây là email tự động từ hệ thống. Vui lòng không trả lời email này.</p>
                    </div>
                    """;

            templateRepository.save(Template.builder()
                    .code("SEND_OTP_VERIFY")
                    .subject("Mã xác thực tài khoản của bạn")
                    .htmlContent(otpTemplateHtml)
                    .build());
            log.info("Đã khởi tạo template: SEND_OTP_VERIFY");
        }

        // ==========================================
        // 2. TEMPLATE: CHÀO MỪNG NGƯỜI DÙNG MỚI
        // ==========================================
        if (!templateRepository.existsByCode("USER_WELCOME")) {
            String welcomeTemplateHtml = """
                    <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px; background-color: #ffffff;">
                        <div style="text-align: center; margin-bottom: 20px;">
                            <h1 style="color: #27AE60; margin: 0;">Chào Mừng Bạn! 🎉</h1>
                        </div>
                    
                        <p style="color: #555555; font-size: 16px;">Xin chào <strong th:text="${name}">Bạn</strong>,</p>
                        <p style="color: #555555; font-size: 16px; line-height: 1.5;">Chúc mừng bạn đã thiết lập tài khoản thành công! Chúng tôi rất vui mừng được chào đón bạn gia nhập hệ thống.</p>
                        <p style="color: #555555; font-size: 16px; line-height: 1.5;">Ngay bây giờ, bạn đã có thể đăng nhập và trải nghiệm toàn bộ các tính năng tuyệt vời mà chúng tôi mang lại.</p>
                    
                        <div style="text-align: center; margin: 40px 0;">
                            <a href="https://yourdomain.com/login" style="background-color: #27AE60; color: #ffffff; padding: 14px 28px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 16px; display: inline-block;">
                                Đăng Nhập Ngay
                            </a>
                        </div>
                    
                        <p style="color: #555555; font-size: 15px;">Nếu bạn cần bất kỳ sự hỗ trợ nào, đội ngũ chăm sóc khách hàng của chúng tôi luôn sẵn sàng lắng nghe và giải đáp.</p>
                    
                        <hr style="border: none; border-top: 1px solid #eeeeee; margin: 30px 0;">
                        <p style="font-size: 13px; color: #777777; text-align: center;">Trân trọng,<br><strong>Đội ngũ phát triển</strong></p>
                    </div>
                    """;

            templateRepository.save(Template.builder()
                    .code("USER_WELCOME")
                    .subject("Chào mừng bạn đã gia nhập hệ thống!")
                    .htmlContent(welcomeTemplateHtml)
                    .build());
            log.info("Đã khởi tạo template: USER_WELCOME");
        }
    }
}