-- ============================================================
-- PBAC エンジン: 初期データ
-- ============================================================

-- アクション
INSERT INTO actions (name) VALUES
    ('READ'),
    ('WRITE'),
    ('DELETE'),
    ('EXECUTE');

-- ユーザー（PDP動作確認用サンプル）
INSERT INTO users (username, email, department, role, clearance_level) VALUES
    ('alice', 'alice@example.com', 'engineering', 'developer', 2),
    ('bob',   'bob@example.com',   'hr',          'manager',   4),
    ('carol', 'carol@example.com', 'finance',     'viewer',    1),
    ('admin', 'admin@example.com', 'it',          'admin',     5);

-- リソース（PDP動作確認用サンプル）
INSERT INTO resources (name, resource_type, owner_department, sensitivity_level) VALUES
    ('Engineering Design Doc', 'DOCUMENT', 'engineering', 2),
    ('HR Employee Report',     'REPORT',   'hr',          4),
    ('Finance Database',       'DATABASE', 'finance',     3),
    ('Public API Docs',        'API',      'engineering', 1);

-- ============================================================
-- ポリシー例1: 同じ部署のリソースを読める
-- Target:    ACTION.name == READ
-- Condition: user.department == resource.owner_department
-- Effect:    PERMIT
-- ============================================================
INSERT INTO policies (name, description, effect, priority) VALUES
    ('同部署リソース読み取り許可',
     '同じ部署が所有するリソースへの読み取りアクセスを許可する',
     'PERMIT', 10);

INSERT INTO policy_targets (policy_id, target_category, attr_name, operator, attr_value)
    VALUES (1, 'ACTION', 'name', 'EQ', 'READ');

INSERT INTO policy_conditions (policy_id, condition_order, left_attr_source, left_attr_name, operator, right_attr_source, right_attr_name)
    VALUES (1, 1, 'USER_ATTR', 'department', 'EQ', 'RESOURCE_ATTR', 'owner_department');

-- ============================================================
-- ポリシー例2: クリアランスレベルが機密レベル以上なら読める
-- Target:    ACTION.name == READ
-- Condition: user.clearance_level >= resource.sensitivity_level
-- Effect:    PERMIT
-- ============================================================
INSERT INTO policies (name, description, effect, priority) VALUES
    ('クリアランスレベル読み取り許可',
     'ユーザーのクリアランスレベルがリソースの機密レベル以上の場合に読み取りを許可する',
     'PERMIT', 5);

INSERT INTO policy_targets (policy_id, target_category, attr_name, operator, attr_value)
    VALUES (2, 'ACTION', 'name', 'EQ', 'READ');

INSERT INTO policy_conditions (policy_id, condition_order, left_attr_source, left_attr_name, operator, right_attr_source, right_attr_name)
    VALUES (2, 1, 'USER_ATTR', 'clearance_level', 'GTE', 'RESOURCE_ATTR', 'sensitivity_level');

-- ============================================================
-- ポリシー例3: 管理者はすべてのリソースに書き込める
-- Target:    ACTION.name == WRITE
-- Condition: user.role == "admin"
-- Effect:    PERMIT
-- ============================================================
INSERT INTO policies (name, description, effect, priority) VALUES
    ('管理者書き込み許可',
     '管理者ロールのユーザーに全リソースへの書き込みを許可する',
     'PERMIT', 20);

INSERT INTO policy_targets (policy_id, target_category, attr_name, operator, attr_value)
    VALUES (3, 'ACTION', 'name', 'EQ', 'WRITE');

INSERT INTO policy_conditions (policy_id, condition_order, left_attr_source, left_attr_name, operator, right_attr_source, right_attr_name)
    VALUES (3, 1, 'USER_ATTR', 'role', 'EQ', 'CONST', 'admin');
