package com.photography.timekeeperbackend.config;

import com.photography.timekeeperbackend.domain.model.cohort.Cohort;
import com.photography.timekeeperbackend.domain.model.cohort.Part;
import com.photography.timekeeperbackend.domain.model.cohort.PartType;
import com.photography.timekeeperbackend.domain.model.cohort.Team;
import com.photography.timekeeperbackend.domain.repository.cohort.CohortRepository;
import com.photography.timekeeperbackend.domain.repository.cohort.PartRepository;
import com.photography.timekeeperbackend.domain.repository.cohort.TeamRepository;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistory;
import com.photography.timekeeperbackend.domain.model.deposit.DepositHistoryType;
import com.photography.timekeeperbackend.domain.repository.deposit.DepositHistoryRepository;
import com.photography.timekeeperbackend.domain.model.member.CohortMember;
import com.photography.timekeeperbackend.domain.model.member.Member;
import com.photography.timekeeperbackend.domain.model.member.MemberRole;
import com.photography.timekeeperbackend.domain.repository.member.CohortMemberRepository;
import com.photography.timekeeperbackend.domain.repository.member.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CohortRepository cohortRepository;
    private final PartRepository partRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final CohortMemberRepository cohortMemberRepository;
    private final DepositHistoryRepository depositHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CohortRepository cohortRepository,
                           PartRepository partRepository,
                           TeamRepository teamRepository,
                           MemberRepository memberRepository,
                           CohortMemberRepository cohortMemberRepository,
                           DepositHistoryRepository depositHistoryRepository,
                           PasswordEncoder passwordEncoder) {
        this.cohortRepository = cohortRepository;
        this.partRepository = partRepository;
        this.teamRepository = teamRepository;
        this.memberRepository = memberRepository;
        this.cohortMemberRepository = cohortMemberRepository;
        this.depositHistoryRepository = depositHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (cohortRepository.count() > 0) {
            return;
        }

        Cohort c10 = cohortRepository.save(Cohort.of(10, false));
        Cohort c11 = cohortRepository.save(Cohort.of(11, true));

        for (PartType partType : Arrays.asList(PartType.SERVER, PartType.WEB, PartType.IOS, PartType.ANDROID, PartType.DESIGN)) {
            partRepository.save(Part.of(c10, partType));
            partRepository.save(Part.of(c11, partType));
        }

        Team teamA = teamRepository.save(Team.of(c11, "Team A"));
        teamRepository.save(Team.of(c11, "Team B"));
        teamRepository.save(Team.of(c11, "Team C"));

        Member admin = memberRepository.save(Member.create("admin", passwordEncoder.encode("admin1234"), "관리자", MemberRole.ADMIN));
        Part adminPart = partRepository.findAll().stream().filter(p -> p.getCohort().getId().equals(c11.getId()) && p.getType() == PartType.SERVER).findFirst().orElseThrow();
        CohortMember adminCohortMember = cohortMemberRepository.save(
                CohortMember.create(admin.getId(), c11.getId(), adminPart.getId(), teamA.getId(), 100000)
        );
        depositHistoryRepository.save(DepositHistory.of(adminCohortMember.getId(), DepositHistoryType.INITIAL, 100000, 100000, "관리자 초기 보증금"));
    }
}
