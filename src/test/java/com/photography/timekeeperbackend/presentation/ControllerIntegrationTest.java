package com.photography.timekeeperbackend.presentation;

import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.cohort.PartType;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.model.session.SessionType;
import com.photography.timekeeperbackend.domain.repository.cohort.CohortRepository;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import com.photography.timekeeperbackend.domain.repository.member.MemberRepository;
import com.photography.timekeeperbackend.domain.repository.session.SessionRepository;
import com.photography.timekeeperbackend.support.ApiIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerIntegrationTest extends ApiIntegrationTestSupport {

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Test
    void given_정상_로그인정보_when_auth_login_then_200과_회원정보를_반환한다() throws Exception {
        var response = postJson("/auth/login", Map.of("loginId", "admin", "password", "admin1234"), 200);

        assertThat(response.get("loginId").asText()).isEqualTo("admin");
        assertThat(response.get("status").asText()).isEqualTo("ACTIVE");
    }

    @Test
    void given_잘못된_비밀번호_when_auth_login_then_400과_에러코드를_반환한다() throws Exception {
        var response = postJson("/auth/login", Map.of("loginId", "admin", "password", "wrong"), 400);

        assertThat(response.get("code").asText()).isEqualTo("PASSWORD_NOT_MATCHED");
    }

    @Test
    void given_유효한_회원등록요청_when_admin_members_post_then_201로_회원이_생성된다() throws Exception {
        Part part = currentCohortPart();
        String loginId = "ctrl-user-" + System.nanoTime();
        Map<String, Object> request = new HashMap<>();
        request.put("loginId", loginId);
        request.put("password", "pw1234");
        request.put("name", "tester");
        request.put("partId", part.getId());
        request.put("teamId", null);

        var response = postJson("/admin/members", request, 201);

        assertThat(response.get("loginId").asText()).isEqualTo(loginId);
        assertThat(memberRepository.findByLoginId(loginId)).isPresent();
    }

    @Test
    void given_세션이_존재할때_when_qrcode_생성을_두번_호출하면_then_두번째는_409다() throws Exception {
        Session session = sessionRepository.save(Session.create(
                cohortRepository.findByCurrentTrue().orElseThrow(),
                "ctrl-session-" + System.nanoTime(),
                "desc",
                SessionType.STUDY,
                LocalDateTime.now().plusDays(1)
        ));

        postJson("/admin/sessions/" + session.getId() + "/qrcodes", Map.of(), 201);
        var conflict = postJson("/admin/sessions/" + session.getId() + "/qrcodes", Map.of(), 409);

        assertThat(conflict.get("code").asText()).isEqualTo("QR_ALREADY_ACTIVE");
    }

    @Test
    void given_요청바디가_유효하지않을때_when_admin_members_post_then_400이다() throws Exception {
        var response = postJson("/admin/members", Map.of(
                "loginId", "",
                "password", "",
                "name", "",
                "partId", 1L
        ), 400);

        assertThat(response.isNull() || response.isObject()).isTrue();
    }

    private Part currentCohortPart() {
        Long currentCohortId = cohortRepository.findByCurrentTrue().orElseThrow().getId();
        return partRepository.findAll().stream()
                .filter(p -> p.getCohort().getId().equals(currentCohortId))
                .filter(p -> p.getType() == PartType.SERVER)
                .findFirst()
                .orElseThrow();
    }
}
