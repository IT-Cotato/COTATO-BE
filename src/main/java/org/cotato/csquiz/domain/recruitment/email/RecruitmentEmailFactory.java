package org.cotato.csquiz.domain.recruitment.email;

public class RecruitmentEmailFactory {
    private static final String LINK_URL = "https://www.cotato.kr";

    public static EmailContent createForGeneration(int generation) {
        String subject = generation + "기 모집이 시작됐습니다.";

        String htmlBody = """
                <html>
                  <body style="margin:0;padding:0;font-family:Arial,sans-serif;background:#f9f9f9;">
                    <!-- HEADER -->
                    <div style="background:#004cab;color:#fff;padding:20px;text-align:center;">
                      <h1>코테이토 %d기 모집 안내</h1>
                    </div>
                    <!-- BODY -->
                    <div style="padding:20px;background:#fff;">
                      <p style="font-size:16px;line-height:1.5;">
                        코테이토 %d기 모집이 시작됐습니다!
                      </p>
                      <p style="font-size:14px;">
                        아래 버튼을 눌러 모집 신청을 진행해주세요.
                      </p>
                      <div style="text-align:center;margin:30px 0;">
                        <a href="%s"
                           style="display:inline-block;
                                  padding:12px 24px;
                                  background:#0066cc;
                                  color:#fff;
                                  text-decoration:none;
                                  border-radius:4px;
                                  font-weight:bold;">
                          모집 신청하러 가기
                        </a>
                      </div>
                    </div>
                    <!-- FOOTER -->
                    <div style="background:#f1f1f1;color:#777;padding:10px;text-align:center;font-size:12px;">
                      &copy; 2025 코테이토. All rights reserved.
                    </div>
                  </body>
                </html>
                """.formatted(generation, generation, LINK_URL);

        return new EmailContent(subject, htmlBody);
    }
}
