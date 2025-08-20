package org.example.team2backend.domain.member.service.command;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.team2backend.domain.member.converter.MemberConverter;
import org.example.team2backend.domain.member.entity.EmailVerification;
import org.example.team2backend.domain.member.repository.EmailVerificationRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MailCommandService {

    private final JavaMailSender javaMailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    //메일 보내기
    public void sendSimpleMessage(String sendEmail) throws MessagingException {
        String number = createNumber();  //랜덤 인증번호 생성
        //메일 생성
        MimeMessage message = createMail(sendEmail, number);
        try {
            // 메일 보내기
            javaMailSender.send(message);

            // 이메일 검증 객체 생성
            EmailVerification emailVerification = MemberConverter.toEmailVerification(sendEmail, number);
            emailVerificationRepository.save(emailVerification);
        } catch (MailException e) {
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다." + e.getMessage());
        }
    }

    //인증코드 생성
    public String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97));

                case 1 -> key.append((char) (random.nextInt(26) + 65));

                case 2 -> key.append(random.nextInt(10));
            }
        }
        return key.toString();
    }

    //메일 생성
    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        //보내는 메일에 들어가는 내용들
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = generateEmailBody(number);
        message.setText(body, "UTF-8", "html");
        //만든 메일 반환
        return message;
    }

    private String generateEmailBody(String authCode) {
        return "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta content=\"IE=edge\" http-equiv=\"X-UA-Compatible\">" +
                "<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\">" +
                "<title>Another Art</title>" +
                "</head>" +
                "<body>" +
                "<p style=\"font-size:10pt; font-family:sans-serif; padding:0 0 0 10pt\"><br><br></p>" +
                "<div style=\"width:440px; margin:30px auto; padding:40px 0 60px; background-color:#fff; border:1px solid #ddd; text-align:center; font-size:16px; font-family:malgun gothic,serif;\">" +
                "<h3 style=\"font-weight:bold; font-size:20px; margin:28px auto;\">Ieum 이메일 본인인증</h3>" +
                "<div style=\"width:200px; margin:28px auto; padding:8px 0 9px; background-color:#f4f4f4; border-radius:3px;\">" +
                "<span style=\"display:inline-block; vertical-align:middle; font-size:13px; color:#666;\">인증번호</span>" +
                "<span style=\"display:inline-block; margin-left:16px; vertical-align:middle; font-size:21px; font-weight:bold; color:#4d5642;\">" + authCode + "</span>" +
                "</div>" +
                "<p style=\"text-align:center; font-size:13px; color:#000; line-height:1.6; margin-top:40px; margin-bottom:0;\">" +
                "해당 인증번호를 인증 번호 확인란에 기입하여 주세요.<br>" +
                "Ieum를 이용해 주셔서 감사합니다.<br>" +
                "</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

