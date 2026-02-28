-- ============================================================
-- PBAC エンジン: 初期データのリセットと再投入
-- 学習しやすいデータ・ポリシーに改善
-- ============================================================

-- 既存データを全削除してシーケンスをリセット
TRUNCATE TABLE
    access_logs,
    policy_conditions,
    policy_targets,
    policies,
    resources,
    users,
    actions
RESTART IDENTITY CASCADE;

-- ============================================================
-- アクション（操作種別）
-- ============================================================
INSERT INTO actions (name) VALUES
    ('READ'),
    ('WRITE'),
    ('DELETE'),
    ('EXECUTE');

-- ============================================================
-- ユーザー（クリアランスレベル 1〜5 の代表例）
--
-- clearance_level = 1: 一般   → 公開情報（Lv.1）のみアクセス可
-- clearance_level = 2: 社員   → 社内限定（Lv.2）以下にアクセス可
-- clearance_level = 3: リーダー → 部門限定（Lv.3）以下にアクセス可
-- clearance_level = 4: 管理職 → 機密（Lv.4）以下にアクセス可
-- clearance_level = 5: 管理者 → すべての情報にアクセス可
-- ============================================================
INSERT INTO users (username, email, department, role, clearance_level) VALUES
    ('tanaka',   'tanaka@example.com',   'sales',       'viewer',    1),
    ('yamada',   'yamada@example.com',   'engineering', 'developer', 2),
    ('nakamura', 'nakamura@example.com', 'finance',     'developer', 3),
    ('sato',     'sato@example.com',     'hr',          'manager',   4),
    ('admin',    'admin@example.com',    'it',          'admin',     5);

-- ============================================================
-- リソース（機密レベル 1〜5 の代表例）
--
-- sensitivity_level = 1: 公開    → 誰でも閲覧可能（製品カタログ等）
-- sensitivity_level = 2: 社内限定 → 全社員が対象（社内規定等）
-- sensitivity_level = 3: 部門限定 → 担当部門のみ（設計書等）
-- sensitivity_level = 4: 機密    → 承認された人のみ（個人情報等）
-- sensitivity_level = 5: 極秘    → 役員・管理者のみ（役員報酬等）
-- ============================================================
INSERT INTO resources (name, resource_type, owner_department, sensitivity_level) VALUES
    ('製品カタログ2024',   'DOCUMENT', 'sales',       1),
    ('社内規定マニュアル', 'DOCUMENT', 'it',          2),
    ('システム設計書',     'DOCUMENT', 'engineering', 3),
    ('従業員個人情報DB',   'DATABASE', 'hr',          4),
    ('役員報酬レポート',   'REPORT',   'finance',     5);

-- ============================================================
-- ポリシー1: 同じ部署のリソースは読める（部署属性によるアクセス制御）
--
-- 【学習ポイント①】
--   「誰が」ではなく「どの部署か」という属性でアクセスを判断するのがPBACの特徴。
--   例: tanaka（営業部）は「製品カタログ」を読めるが「システム設計書」は読めない
--
-- Target:    アクション = READ
-- Condition: ユーザーの部署 == リソースの所有部署
-- Effect:    PERMIT（許可）
-- Priority:  10
-- ============================================================
INSERT INTO policies (name, description, effect, priority) VALUES
    ('同部署リソース読み取り許可',
     '【学習ポイント①】同じ部署が所有するリソースへの読み取りを許可します。'
     'tanaka（営業部）は「製品カタログ」を読めますが、「システム設計書」は読めません。',
     'PERMIT', 10);

INSERT INTO policy_targets (policy_id, target_category, attr_name, operator, attr_value)
    VALUES (1, 'ACTION', 'name', 'EQ', 'READ');

INSERT INTO policy_conditions (policy_id, condition_order, left_attr_source, left_attr_name, operator, right_attr_source, right_attr_name)
    VALUES (1, 1, 'USER_ATTR', 'department', 'EQ', 'RESOURCE_ATTR', 'owner_department');

-- ============================================================
-- ポリシー2: クリアランスレベルが機密レベル以上なら読める（数値属性によるアクセス制御）
--
-- 【学習ポイント②】
--   数値属性の大小比較でアクセスを制御できる。レベルが高いほど多くのリソースを読める。
--   例: sato（管理職 Lv.4）は機密レベル4以下のリソースをすべて読める
--
-- Target:    アクション = READ
-- Condition: ユーザーのクリアランスレベル >= リソースの機密レベル
-- Effect:    PERMIT（許可）
-- Priority:  5
-- ============================================================
INSERT INTO policies (name, description, effect, priority) VALUES
    ('クリアランスレベル読み取り許可',
     '【学習ポイント②】ユーザーのクリアランスレベルがリソースの機密レベル以上の場合に読み取りを許可します。'
     'Lv.4（管理職）は機密レベル4以下のリソースを読めます。',
     'PERMIT', 5);

INSERT INTO policy_targets (policy_id, target_category, attr_name, operator, attr_value)
    VALUES (2, 'ACTION', 'name', 'EQ', 'READ');

INSERT INTO policy_conditions (policy_id, condition_order, left_attr_source, left_attr_name, operator, right_attr_source, right_attr_name)
    VALUES (2, 1, 'USER_ATTR', 'clearance_level', 'GTE', 'RESOURCE_ATTR', 'sensitivity_level');

-- ============================================================
-- ポリシー3: 管理者はすべてのリソースに書き込める（ロール属性によるアクセス制御）
--
-- 【学習ポイント③】
--   ロール（役割）属性を使って特定のユーザーグループに権限を付与できる。
--   例: admin（管理者）はすべてのリソースへ書き込みができる
--
-- Target:    アクション = WRITE
-- Condition: ユーザーのロール == "admin"
-- Effect:    PERMIT（許可）
-- Priority:  20
-- ============================================================
INSERT INTO policies (name, description, effect, priority) VALUES
    ('管理者書き込み許可',
     '【学習ポイント③】管理者（admin）ロールのユーザーに全リソースへの書き込みを許可します。'
     'ロール属性を条件にした制御の例です。',
     'PERMIT', 20);

INSERT INTO policy_targets (policy_id, target_category, attr_name, operator, attr_value)
    VALUES (3, 'ACTION', 'name', 'EQ', 'WRITE');

INSERT INTO policy_conditions (policy_id, condition_order, left_attr_source, left_attr_name, operator, right_attr_source, right_attr_name)
    VALUES (3, 1, 'USER_ATTR', 'role', 'EQ', 'CONST', 'admin');

-- ============================================================
-- ポリシー4: 低クリアランスユーザーによる極秘リソースへのアクセスを明示的に拒否
--
-- 【学習ポイント④】
--   DENY（拒否）ポリシーの例。DENY_OVERRIDESアルゴリズムでは、
--   DENYが1つでも適用されると、PERMITが何件あっても最終結果はDENYになる。
--   例: tanaka/yamada/nakamura（Lv.1〜3）が「役員報酬レポート（Lv.5）」にアクセスしようとすると
--       このポリシーが明示的に拒否する
--
-- Target:    アクション = READ
-- Condition: ユーザーのクリアランスレベル <= 3
--        AND リソースの機密レベル == 5
-- Effect:    DENY（拒否）
-- Priority:  30（最優先: DENYが他のPERMITより先に評価される）
-- ============================================================
INSERT INTO policies (name, description, effect, priority) VALUES
    ('極秘リソースへの低クリアランスアクセス拒否',
     '【学習ポイント④】DENY（拒否）ポリシーの例です。クリアランスLv.3以下のユーザーが'
     '極秘（Lv.5）リソースにアクセスしようとすると、他のPERMITポリシーがあっても明示的に拒否されます。',
     'DENY', 30);

INSERT INTO policy_targets (policy_id, target_category, attr_name, operator, attr_value)
    VALUES (4, 'ACTION', 'name', 'EQ', 'READ');

INSERT INTO policy_conditions (policy_id, condition_order, left_attr_source, left_attr_name, operator, right_attr_source, right_attr_name)
    VALUES (4, 1, 'USER_ATTR',     'clearance_level',  'LTE', 'CONST',         '3'),
           (4, 2, 'RESOURCE_ATTR', 'sensitivity_level', 'EQ', 'CONST',         '5');
