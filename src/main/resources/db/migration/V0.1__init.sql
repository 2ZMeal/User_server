-- 회원 테이블
CREATE TABLE p_user (
  id UUID PRIMARY KEY,
  keycloak_id VARCHAR(50) NOT NULL UNIQUE,
  nickname VARCHAR(20) UNIQUE,
  name VARCHAR(10) NOT NULL,
  email VARCHAR(30) NOT NULL UNIQUE,
  role VARCHAR(10) NOT NULL,
  last_login_at TIMESTAMP,
  status VARCHAR(10) NOT NULL,
  created_at TIMESTAMP,
  created_by VARCHAR(50),
  modified_at TIMESTAMP,
  modified_by VARCHAR(50),
  deleted_at TIMESTAMP,
  deleted_by VARCHAR(50)
);

-- 배송지 테이블
CREATE TABLE p_user_address (
  id UUID PRIMARY KEY,
  user_id UUID NOT NULL,
  road_name_address VARCHAR(256) NOT NULL,
  lot_number_address VARCHAR(256),
  zip_code VARCHAR(10) NOT NULL,
  is_default BOOLEAN NOT NULL,
  created_at TIMESTAMP,
  created_by VARCHAR(50),
  modified_at TIMESTAMP,
  modified_by VARCHAR(50),
  deleted_at TIMESTAMP,
  deleted_by VARCHAR(50),
  CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
        REFERENCES p_user(id)
);

-- 배송지 Partial Unique Index
CREATE UNIQUE INDEX uk_user_default_address
ON p_user_address(user_id)
WHERE is_default = true AND deleted_at IS NULL;
