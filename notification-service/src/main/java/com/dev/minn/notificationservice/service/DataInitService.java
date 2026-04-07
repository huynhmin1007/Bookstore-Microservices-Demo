package com.dev.minn.notificationservice.service;

import com.dev.minn.notificationservice.node.EventTemplateMapping;
import com.dev.minn.notificationservice.node.Template;
import com.dev.minn.notificationservice.repository.EventTemplateMappingRepository;
import com.dev.minn.notificationservice.repository.TemplateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DataInitService {

    TemplateRepository templateRepository;
    EventTemplateMappingRepository eventTemplateMappingRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("Bắt đầu kiểm tra và khởi tạo dữ liệu mẫu...");
        initTemplate();
        initEventTemplateMapping();
        log.info("Khởi tạo dữ liệu mẫu hoàn tất!");
    }

    public void initTemplate() {
        if (templateRepository.count() == 0) {
            log.info("Chưa có Template nào, tiến hành tạo mới...");

            Template welcomeTemplate = Template.builder()
                    .code("WELCOME_EMAIL")
                    .subject("Chào mừng bạn đến với hệ thống Bookstore!")
                    .htmlContent("<!DOCTYPE html><html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">" +
                            "<div style=\"max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 8px;\">" +
                            "<h2 style=\"color: #333; text-align: center;\">Xin chào <span th:text=\"${firstName}\">Tên</span>! 🎉</h2>" +
                            "<p style=\"color: #555; font-size: 16px;\">Chúc mừng bạn đã tạo tài khoản thành công. Không gian làm việc của bạn đã được thiết lập sẵn sàng.</p>" +
                            "<p style=\"font-size: 12px; color: #aaa; text-align: center;\">Đây là email tự động từ hệ thống.</p>" +
                            "</div></body></html>")
                    .channel("EMAIL")
                    .isActive(true)
                    .build();

            Template invoiceTemplate = Template.builder()
                    .code("ORDER_INVOICE")
                    .subject("Hóa đơn thanh toán thành công #[[${orderId}]]")
                    .htmlContent("<!DOCTYPE html><html><body><h2>Cảm ơn <span th:text=\"${firstName}\">Khách hàng</span>!</h2><p>Đơn hàng <b><span th:text=\"${orderId}\">Mã đơn</span></b> của bạn đã được thanh toán.</p><p>Tổng tiền: <span th:text=\"${totalAmount}\">0</span> VND</p><p>Chúc bạn một ngày vui vẻ!</p></body></html>")
                    .channel("EMAIL")
                    .isActive(true)
                    .build();

            Template resetPwdTemplate = Template.builder()
                    .code("RESET_PASSWORD")
                    .subject("Yêu cầu đặt lại mật khẩu")
                    .htmlContent("<!DOCTYPE html><html><body><h2>Xin chào,</h2><p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản <b><span th:text=\"${email}\">email</span></b>. Mã OTP của bạn là: <h3 th:text=\"${otpCode}\">000000</h3></p></body></html>")
                    .channel("EMAIL")
                    .isActive(true)
                    .build();

            Template otpTemplate = Template.builder()
                    .code("OTP_VERIFICATION")
                    .subject("Mã xác thực (OTP) đăng ký tài khoản")
                    .htmlContent("<!DOCTYPE html><html><body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">" +
                            "<div style=\"max-width: 600px; margin: auto; background: white; padding: 30px; border-radius: 8px;\">" +
                            "<h2>Xác thực email của bạn</h2>" +
                            "<p>Mã OTP để hoàn tất đăng ký tài khoản của bạn là:</p>" +
                            "<div style=\"font-size: 32px; font-weight: bold; color: #2c3e50; text-align: center; margin: 20px 0; background: #ecf0f1; padding: 15px; border-radius: 8px; letter-spacing: 5px;\">" +
                            "<span th:text=\"${otpCode}\">000000</span>" +
                            "</div>" +
                            "<p>Mã này có hiệu lực trong vòng 5 phút. Vui lòng không chia sẻ mã này.</p>" +
                            "</div></body></html>")
                    .channel("EMAIL")
                    .isActive(true)
                    .build();

            templateRepository.saveAll(List.of(welcomeTemplate, invoiceTemplate, resetPwdTemplate, otpTemplate));
        }
    }

    public void initEventTemplateMapping() {
        if (eventTemplateMappingRepository.count() == 0) {
            log.info("Chưa có Event Mapping nào, tiến hành tạo mới...");

            EventTemplateMapping accountCreatedMapping = EventTemplateMapping.builder()
                    .event("account.created")
                    .templateCode("WELCOME_EMAIL")
                    .isActive(true)
                    .build();

            EventTemplateMapping orderPaidMapping = EventTemplateMapping.builder()
                    .event("order.paid")
                    .templateCode("ORDER_INVOICE")
                    .isActive(true)
                    .build();

            EventTemplateMapping forgotPwdMapping = EventTemplateMapping.builder()
                    .event("account.forgot_password")
                    .templateCode("RESET_PASSWORD")
                    .isActive(true)
                    .build();

            // Mẫu này cố tình để isActive = false để test luồng: Nhận được event nhưng không gửi mail
            EventTemplateMapping accountDeletedMapping = EventTemplateMapping.builder()
                    .event("account.deleted")
                    .templateCode("GOODBYE_EMAIL")
                    .isActive(false)
                    .build();

            EventTemplateMapping otpMapping = EventTemplateMapping.builder()
                    .event("otp.account.verify")
                    .templateCode("OTP_VERIFICATION")
                    .isActive(true)
                    .build();

            eventTemplateMappingRepository.saveAll(List.of(
                    accountCreatedMapping,
                    orderPaidMapping,
                    forgotPwdMapping,
                    accountDeletedMapping,
                    otpMapping
            ));
        }
    }
}