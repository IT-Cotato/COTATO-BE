package org.cotato.csquiz.domain.recruitment.email;

public class RecruitmentEmailFactory {
    private static final String LINK_URL = "https://www.cotato.kr/";

    public static EmailContent createForGeneration(int generation) {
        String subject = "[IT 연합동아리 코테이토] " + generation + "기 모집이 시작되었습니다.";

        String htmlBody = """
                <html>
                  <body style="margin:0;padding:0;font-family:Arial,sans-serif;background:#f9f9f9;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f9f9f9;padding:30px 0;">
                      <tr>
                        <td align="center">
                          <table width="600" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:8px;overflow:hidden;">
                            <!-- BODY -->
                            <tr>
                              <td style="padding:30px;text-align:center;color:#333333;">
                                <h1 style="font-size:24px;margin-bottom:20px;">코테이토 %d기 모집이 시작되었습니다!</h1>
                                <p style="font-size:16px;line-height:1.5;margin:0 0 20px;">
                                  <a href="%s" style="color:#00c471;text-decoration:none;">코테이토 홈페이지</a> 에 접속하여 지원해주세요!
                                </p>
                                <p style="font-size:16px;line-height:1.5;margin:0;">
                                  무엇이라도 해내야겠다는 마음가짐, 발전하고자 하는<br/>
                                  열정이면 충분합니다!
                                </p>
                              </td>
                            </tr>
                            <!-- FOOTER -->
                            <tr>
                              <td style="background:#f1f1f1;padding:15px;text-align:center;color:#999999;font-size:12px;">
                                &copy; 2025 코테이토. All rights reserved.
                              </td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                    </table>
                  </body>
                </html>
                """.formatted(generation, LINK_URL, LINK_URL);

        return new EmailContent(subject, htmlBody);
    }
}
