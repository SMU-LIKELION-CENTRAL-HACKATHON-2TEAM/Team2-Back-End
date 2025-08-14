package org.example.team2backend.domain.member.service.command;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MailCommandService {

    private final JavaMailSender javaMailSender;

    //메일 보내기
    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String number = createNumber();  //랜덤 인증번호 생성
        //메일 생성
        MimeMessage message = createMail(sendEmail, number);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다." + e.getMessage());
        }
        //보낸 인증 번호 반환
        return number;
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
        String body = "";
        body += "<h1>" + number + "</h1>";
        message.setText(body, "UTF-8", "html");
        //만든 메일 반환
        return message;
    }
}

