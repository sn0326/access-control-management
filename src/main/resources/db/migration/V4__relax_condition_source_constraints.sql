-- ============================================================
-- エンジン拡張性対応: policy_conditions のソース名 CHECK 制約を撤廃
-- ============================================================
--
-- Subject / AccessResource インターフェースの導入により、
-- ポリシー条件の left_attr_source / right_attr_source は
-- アプリ側の実装（UserSubject, GroupSubject 等）が自由に定義できるようになった。
--
-- CHECK 制約でソース名を列挙するとアプリの拡張時に毎回マイグレーションが必要になるため、
-- 制約を削除し、バリデーションはアプリケーション層（Subject / AccessResource 実装）に委譲する。
--
-- また、ソース名の最大長を VARCHAR(20) から VARCHAR(100) に拡張する。
-- ============================================================

ALTER TABLE policy_conditions DROP CONSTRAINT IF EXISTS policy_conditions_left_attr_source_check;
ALTER TABLE policy_conditions DROP CONSTRAINT IF EXISTS policy_conditions_right_attr_source_check;

ALTER TABLE policy_conditions ALTER COLUMN left_attr_source  TYPE VARCHAR(100);
ALTER TABLE policy_conditions ALTER COLUMN right_attr_source TYPE VARCHAR(100);
