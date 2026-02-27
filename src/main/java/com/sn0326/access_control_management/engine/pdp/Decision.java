package com.sn0326.access_control_management.engine.pdp;

/**
 * PDP が返すアクセス決定。
 * <ul>
 *   <li>PERMIT         - アクセスを許可する</li>
 *   <li>DENY           - アクセスを拒否する</li>
 *   <li>NOT_APPLICABLE - 適用可能なポリシーが存在しない（デフォルト拒否として扱う）</li>
 * </ul>
 */
public enum Decision {
    PERMIT,
    DENY,
    NOT_APPLICABLE
}
