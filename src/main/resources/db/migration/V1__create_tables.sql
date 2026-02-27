-- ============================================================
-- PBAC エンジン: テーブル定義
-- ============================================================

-- ユーザー（Subject）
-- 固定カラム: department, role, clearance_level でポリシーを評価する
CREATE TABLE users (
    id              BIGSERIAL    PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    department      VARCHAR(100) NOT NULL,           -- engineering / hr / finance / sales / it
    role            VARCHAR(50)  NOT NULL,           -- admin / manager / developer / viewer
    clearance_level INT          NOT NULL DEFAULT 1, -- 1(低) ~ 5(高)
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- リソース
-- 固定カラム: resource_type, owner_department, sensitivity_level でポリシーを評価する
CREATE TABLE resources (
    id                BIGSERIAL    PRIMARY KEY,
    name              VARCHAR(200) NOT NULL,
    resource_type     VARCHAR(100) NOT NULL,           -- DOCUMENT / API / REPORT / DATABASE
    owner_department  VARCHAR(100) NOT NULL,           -- このリソースを所有する部署
    sensitivity_level INT          NOT NULL DEFAULT 1, -- 1(低) ~ 5(高)
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- アクション
CREATE TABLE actions (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE -- READ / WRITE / DELETE / EXECUTE
);

-- ポリシー
CREATE TABLE policies (
    id          BIGSERIAL   PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    effect      VARCHAR(10)  NOT NULL CHECK (effect IN ('PERMIT', 'DENY')),
    priority    INT          NOT NULL DEFAULT 0, -- 値が大きいほど優先
    enabled     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ポリシーターゲット（適用範囲）
-- このポリシーがどの Subject / Resource / Action に適用されるかを定義する
-- 同一カテゴリの複数行は OR 条件、異なるカテゴリ間は AND 条件
-- 定義のないカテゴリはワイルドカード（全対象）
--
-- 例: ACTION の name が READ → READアクションのみに適用
-- 例: SUBJECT の department が EQ engineering → engineeringユーザーのみに適用
--
-- attr_name に指定できる値:
--   SUBJECT  → department / role / clearance_level
--   RESOURCE → resource_type / owner_department / sensitivity_level
--   ACTION   → name
CREATE TABLE policy_targets (
    id              BIGSERIAL    PRIMARY KEY,
    policy_id       BIGINT       NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
    target_category VARCHAR(20)  NOT NULL CHECK (target_category IN ('SUBJECT', 'RESOURCE', 'ACTION')),
    attr_name       VARCHAR(100) NOT NULL,
    operator        VARCHAR(20)  NOT NULL CHECK (operator IN ('EQ', 'NEQ', 'GT', 'LT', 'GTE', 'LTE', 'IN')),
    attr_value      VARCHAR(500) NOT NULL
);

-- ポリシー条件（評価条件）
-- ターゲットにマッチした後、実際のアクセス可否を決定する条件
-- 同一ポリシー内の複数条件は AND 結合
--
-- left_attr_source / right_attr_source に指定できる値:
--   USER_ATTR     → users テーブルの固定カラム名（department, role, clearance_level）
--   RESOURCE_ATTR → resources テーブルの固定カラム名（resource_type, owner_department, sensitivity_level）
--   ENV_ATTR      → 実行時環境値（hour, ip_address）
--   CONST         → 定数値（right_attr_name に値を直接記述）
--
-- 例: user.department == resource.owner_department
--   left_attr_source=USER_ATTR,     left_attr_name=department
--   operator=EQ
--   right_attr_source=RESOURCE_ATTR, right_attr_name=owner_department
--
-- 例: user.role == "admin"
--   left_attr_source=USER_ATTR, left_attr_name=role
--   operator=EQ
--   right_attr_source=CONST,    right_attr_name=admin
CREATE TABLE policy_conditions (
    id                BIGSERIAL   PRIMARY KEY,
    policy_id         BIGINT      NOT NULL REFERENCES policies(id) ON DELETE CASCADE,
    condition_order   INT         NOT NULL DEFAULT 0,
    left_attr_source  VARCHAR(20) NOT NULL CHECK (left_attr_source  IN ('USER_ATTR', 'RESOURCE_ATTR', 'ENV_ATTR', 'CONST')),
    left_attr_name    VARCHAR(100) NOT NULL,
    operator          VARCHAR(20) NOT NULL CHECK (operator IN ('EQ', 'NEQ', 'GT', 'LT', 'GTE', 'LTE', 'CONTAINS')),
    right_attr_source VARCHAR(20) NOT NULL CHECK (right_attr_source IN ('USER_ATTR', 'RESOURCE_ATTR', 'ENV_ATTR', 'CONST')),
    right_attr_name   VARCHAR(100) NOT NULL -- CONST の場合はここに値を直接記述
);

-- 監査ログ
CREATE TABLE access_logs (
    id                BIGSERIAL    PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    resource_id       BIGINT       NOT NULL,
    action_name       VARCHAR(100) NOT NULL,
    decision          VARCHAR(20)  NOT NULL CHECK (decision IN ('PERMIT', 'DENY', 'NOT_APPLICABLE')),
    matched_policy_id BIGINT,      -- PERMIT/DENY を決定したポリシー（DENY_OVERRIDES等で複数ある場合は最初のDENY）
    evaluated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
