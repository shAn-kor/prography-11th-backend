CREATE TABLE IF NOT EXISTS `member` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS cohort (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number INT NOT NULL,
    current BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS part (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cohort_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    CONSTRAINT uk_cohort_type UNIQUE (cohort_id, type),
    CONSTRAINT fk_part_cohort FOREIGN KEY (cohort_id) REFERENCES cohort(id)
);

CREATE TABLE IF NOT EXISTS team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cohort_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    CONSTRAINT uk_cohort_name UNIQUE (cohort_id, name),
    CONSTRAINT fk_team_cohort FOREIGN KEY (cohort_id) REFERENCES cohort(id)
);

CREATE TABLE IF NOT EXISTS cohort_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    cohort_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    team_id BIGINT,
    deposit INT NOT NULL,
    excuse_count INT NOT NULL,
    CONSTRAINT uk_member_cohort UNIQUE (member_id, cohort_id),
    CONSTRAINT fk_cohort_member_member FOREIGN KEY (member_id) REFERENCES `member`(id),
    CONSTRAINT fk_cohort_member_cohort FOREIGN KEY (cohort_id) REFERENCES cohort(id),
    CONSTRAINT fk_cohort_member_part FOREIGN KEY (part_id) REFERENCES part(id),
    CONSTRAINT fk_cohort_member_team FOREIGN KEY (team_id) REFERENCES team(id)
);

CREATE TABLE IF NOT EXISTS `session` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cohort_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    session_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_session_cohort FOREIGN KEY (cohort_id) REFERENCES cohort(id)
);

CREATE TABLE IF NOT EXISTS qr_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    hash_value VARCHAR(36) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_qrcode_session FOREIGN KEY (session_id) REFERENCES `session`(id)
);

CREATE TABLE IF NOT EXISTS attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id BIGINT NOT NULL,
    cohort_member_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    late_minutes INT NOT NULL,
    penalty_amount INT NOT NULL,
    checked_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_session_cohort_member UNIQUE (session_id, cohort_member_id),
    CONSTRAINT fk_attendance_session FOREIGN KEY (session_id) REFERENCES `session`(id),
    CONSTRAINT fk_attendance_cohort_member FOREIGN KEY (cohort_member_id) REFERENCES cohort_member(id)
);

CREATE TABLE IF NOT EXISTS deposit_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cohort_member_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount INT NOT NULL,
    balance_after INT NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_deposit_history_cohort_member FOREIGN KEY (cohort_member_id) REFERENCES cohort_member(id)
);
