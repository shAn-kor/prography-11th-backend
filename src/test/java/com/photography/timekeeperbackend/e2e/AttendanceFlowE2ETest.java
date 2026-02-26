package com.photography.timekeeperbackend.e2e;

import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.cohort.PartType;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.session.Session;
import com.photography.timekeeperbackend.domain.repository.cohort.CohortRepository;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import com.photography.timekeeperbackend.domain.repository.member.CohortMemberRepository;
import com.photography.timekeeperbackend.domain.repository.member.MemberRepository;
import com.photography.timekeeperbackend.domain.repository.qrcode.QRCodeRepository;
import com.photography.timekeeperbackend.domain.repository.session.SessionRepository;
import com.photography.timekeeperbackend.support.ApiIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceFlowE2ETest extends ApiIntegrationTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private CohortMemberRepository cohortMemberRepository;

    @Autowired
    private QRCodeRepository qrCodeRepository;

    @Test
    void given_운영가능한_초기상태_when_회원등록부터_qr출석까지_진행하면_then_핵심_api_흐름이_모두_성공한다() throws Exception {
        Cohort current = cohortRepository.findByCurrentTrue().orElseThrow();
        Part part = partRepository.findAll().stream()
                .filter(p -> p.getCohort().getId().equals(current.getId()))
                .filter(p -> p.getType() == PartType.SERVER)
                .findFirst()
                .orElseThrow();

        String loginId = "e2e-user-" + System.nanoTime();
        String sessionTitle = "e2e-session-" + System.nanoTime();

        Map<String, Object> createMemberBody = new HashMap<>();
        createMemberBody.put("loginId", loginId);
        createMemberBody.put("password", "pw1234");
        createMemberBody.put("name", "E2E");
        createMemberBody.put("partId", part.getId());
        createMemberBody.put("teamId", null);
        postJson("/admin/members", createMemberBody, 201);

        Member createdMember = memberRepository.findByLoginId(loginId).orElseThrow();

        postJson("/admin/sessions", Map.of(
                "title", sessionTitle,
                "description", "e2e flow",
                "type", "STUDY",
                "sessionDate", "2099-01-01T10:00:00"
        ), 201);
        Session createdSession = sessionRepository.findAll().stream()
                .filter(s -> s.getTitle().equals(sessionTitle))
                .findFirst()
                .orElseThrow();

        putJson("/admin/sessions/" + createdSession.getId() + "/status",
                Map.of("status", "IN_PROGRESS"), 200);

        String hashValue = qrCodeRepository.findActiveBySessionId(createdSession.getId(), java.time.LocalDateTime.now())
                .orElseThrow()
                .getHashValue();

        var attendanceResponse = postJson("/attendances", Map.of(
                "hashValue", hashValue,
                "memberId", createdMember.getId()
        ), 201);
        assertThat(attendanceResponse.get("memberLoginId").asText()).isEqualTo(loginId);

        var myAttendances = getJson("/attendances?memberId=" + createdMember.getId(), 200);
        assertThat(myAttendances.isArray()).isTrue();
        assertThat(myAttendances.size()).isEqualTo(1);

        var summary = getJson("/members/" + createdMember.getId() + "/attendance-summary", 200);
        assertThat(summary.get("PRESENT").asInt()).isEqualTo(1);

        CohortMember cohortMember = cohortMemberRepository.findByMemberIdAndCohortId(createdMember.getId(), current.getId())
                .orElseThrow();
        var depositHistories = getJson("/admin/cohort-members/" + cohortMember.getId() + "/deposits", 200);
        assertThat(depositHistories.isArray()).isTrue();
        assertThat(depositHistories.size()).isGreaterThan(0);
    }
}
