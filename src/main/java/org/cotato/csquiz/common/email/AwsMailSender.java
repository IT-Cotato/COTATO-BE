package org.cotato.csquiz.common.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AwsMailSender {

    private final AmazonSimpleEmailService ses;

    @Value("${cloud.aws.ses.emailAddress}")
    private String from;

    public void sendRawMessageBody(String recipient, String htmlBody, String subject) {
        Content subjectContent = new Content(subject);
        subjectContent.setCharset("UTF-8");

        Content bodyContent = new Content(htmlBody);
        bodyContent.setCharset("UTF-8");
        Body messageBody = createHtmlBody(bodyContent);

        SendEmailRequest req = new SendEmailRequest(
                from,
                new Destination(List.of(recipient)),
                new Message(
                        new Content(subject),
                        messageBody
                )
        );
        ses.sendEmail(req);
    }

    private Body createHtmlBody(Content content) {
        Body messageBody = new Body();
        messageBody.setHtml(content);
        return messageBody;
    }
}
