package org.cotato.csquiz.domain.recruitment.email;

import lombok.RequiredArgsConstructor;
import org.cotato.csquiz.common.config.property.CotatoProperties;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecruitmentEmailFactory {

    private final CotatoProperties properties;

    public EmailContent getRecruitmentEmailContent(int generation) {

        String subject = "[IT 연합동아리 코테이토] " + generation + "기 모집이 시작되었습니다.";

        String htmlBody = """
                <html>
                         <body
                           style="
                             margin: 0;
                             padding: 0;
                             font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial,
                               sans-serif;
                             font-size: 16px;
                             font-weight: 400;
                             color: #000000;
                           "
                         >
                           <table width="640" cellpadding="0" cellspacing="0" border="0" align="center">
                             <tr
                               style="
                                 background: #fff;
                                 display: flex;
                                 flex-direction: column;
                                 align-items: center;
                                 justify-content: center;
                                 gap: 28px;
                                 padding: 34px 0;
                               "
                             >
                               <td>
                                 <h1 style="font-size: 24px; font-weight: 600; margin: 0">
                                   코테이토 <span style="color: #ffa000">%d기</span> 모집이 시작되었습니다!
                                 </h1>
                               </td>
                               <td style="text-align: center">
                                 <p style="margin: 4px">
                                   무엇이라도 해내야겠다는 마음가짐, 발전하고자 하는 열정이면 충분합니다.
                                 </p>
                                 <p style="margin: 4px">
                                   <a href="%s" style="color: #ffa000; text-decoration: underline">코테이토 홈페이지</a>
                                   에 접속하여 지원해주세요!
                                 </p>
                               </td>
                               <td>
                                 <a
                                   href="%s"
                                   style="
                                     width: 282px;
                                     height: 48px;
                                     padding : 12px 36px;
                                     display: flex;
                                     justify-content: center;
                                     align-items: center;
                                     border: #ffa000 1px solid;
                                     background-color: #ffc700;
                                     border-radius: 16px;
                                     color: #000000;
                                     text-decoration: none;
                                   "
                                 >
                                   홈페이지 바로가기
                                 </a>
                               </td>
                             </tr>
                             <tr
                               style="
                                 display: flex;
                                 justify-content: center;
                                 align-items: center;
                                 padding: 20px;
                                 background: #f4f4f4;
                               "
                             >
                               <td>
                                 <p style="font-size: 12px; color: #c6c4c1; margin: 0">
                                   © Cotato. 2024 All rights reserved.
                                 </p>
                               </td>
                             </tr>
                           </table>
                         </body>
                       </html>
                """.formatted(
                generation,
                properties.getBaseUrl(),
                properties.getBaseUrl()
        );

        return new EmailContent(subject, htmlBody);
    }
}
