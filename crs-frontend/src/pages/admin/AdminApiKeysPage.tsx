import { useCallback, useEffect, useState, type FormEvent } from "react";
import { createApiKey, getApiKeys, revokeApiKey } from "../../api/apiKeyApi";
import Toast from "../../components/Toast";
import { useToast } from "../../hooks/useToast";
import { getApiErrorMessage } from "../../types/apiError";
import type { ApiKeyRecord } from "../../types/apiKey";

const formatDate = (value: string | null) => value
  ? new Intl.DateTimeFormat("vi-VN", { dateStyle: "short", timeStyle: "short" }).format(new Date(value))
  : "Không hết hạn";

const AdminApiKeysPage = () => {
  const [apiKeys, setApiKeys] = useState<ApiKeyRecord[]>([]);
  const [ownerName, setOwnerName] = useState("");
  const [scopes, setScopes] = useState<string[]>(["COURSE_READ"]);
  const [validDays, setValidDays] = useState("");
  const [createdKey, setCreatedKey] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const { toast, showToast, clearToast } = useToast();

  const loadKeys = useCallback(async () => {
    setLoading(true);
    try { setApiKeys(await getApiKeys()); }
    catch (error: unknown) { showToast(getApiErrorMessage(error), "error"); }
    finally { setLoading(false); }
  }, [showToast]);
  useEffect(() => {
    let active = true;
    void getApiKeys()
      .then((keys) => { if (active) setApiKeys(keys); })
      .catch((error: unknown) => { if (active) showToast(getApiErrorMessage(error), "error"); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; };
  }, [showToast]);

  const toggleScope = (scope: string) => setScopes((current) => current.includes(scope)
    ? current.filter((item) => item !== scope) : [...current, scope]);

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    if (!ownerName.trim() || scopes.length === 0) return showToast("Vui lòng nhập owner và chọn scope.", "error");
    setSubmitting(true);
    try {
      const created = await createApiKey({ ownerName: ownerName.trim(), scopes: scopes.join(","), validDays: validDays ? Number(validDays) : null });
      setCreatedKey(created.keyValue);
      setOwnerName(""); setScopes(["COURSE_READ"]); setValidDays("");
      await loadKeys(); showToast("Tạo API Key thành công.", "success");
    } catch (error: unknown) { showToast(getApiErrorMessage(error), "error"); }
    finally { setSubmitting(false); }
  };

  const handleRevoke = async (apiKey: ApiKeyRecord) => {
    if (!window.confirm(`Thu hồi API Key của ${apiKey.ownerName}?`)) return;
    try { await revokeApiKey(apiKey.id); await loadKeys(); showToast("Thu hồi API Key thành công.", "success"); }
    catch (error: unknown) { showToast(getApiErrorMessage(error), "error"); }
  };

  const copyCreatedKey = async () => {
    if (!createdKey) return;
    try { await navigator.clipboard.writeText(createdKey); showToast("Đã sao chép API Key.", "success"); }
    catch { showToast("Không thể sao chép tự động. Hãy sao chép thủ công.", "error"); }
  };

  return <div className="app-container">
    <header className="app-header"><h1>Quản lý API Key</h1><p>Cấp và thu hồi quyền truy cập cho đối tác</p></header>
    <main className="app-content">
      <section className="course-form-card"><h2>Cấp API Key mới</h2><form onSubmit={handleSubmit}>
        <div className="form-grid api-key-form-grid">
          <label>Tên đối tác / Owner name<input value={ownerName} onChange={(e) => setOwnerName(e.target.value)} required /></label>
          <fieldset className="scope-field"><legend>Scopes</legend>{["COURSE_READ", "COURSE_WRITE"].map((scope) =>
            <label key={scope} className="checkbox-label"><input type="checkbox" checked={scopes.includes(scope)} onChange={() => toggleScope(scope)} />{scope}</label>)}</fieldset>
          <label>Thời hạn (ngày)<input type="number" min="1" value={validDays} onChange={(e) => setValidDays(e.target.value)} placeholder="Để trống = không hết hạn" /></label>
        </div><div className="form-actions"><button className="primary-btn" disabled={submitting}>{submitting ? "Đang tạo..." : "Tạo API Key"}</button></div>
      </form></section>
      {createdKey && <section className="new-key-panel" role="alert"><strong>API Key vừa tạo</strong><p>Hãy sao chép và lưu key này ở nơi an toàn.</p>
        <div><code>{createdKey}</code><button type="button" className="primary-btn" onClick={() => void copyCreatedKey()}>Copy</button></div></section>}
      <section className="card"><div className="table-container"><table><thead><tr>
        <th>STT</th><th>Owner</th><th>API Key</th><th>Scopes</th><th>Trạng thái</th><th>Hết hạn</th><th>Ngày tạo</th><th>Thao tác</th>
      </tr></thead><tbody>{loading ? <tr><td colSpan={8}>Đang tải...</td></tr> : apiKeys.length === 0 ? <tr><td colSpan={8}>Chưa có API Key.</td></tr> : apiKeys.map((apiKey, index) =>
        <tr key={apiKey.id}><td>{index + 1}</td><td>{apiKey.ownerName}</td><td><code className="api-key-value">{apiKey.keyValue}</code></td><td>{apiKey.scopes}</td>
          <td><span className={`api-key-status ${apiKey.status.toLowerCase()}`}>{apiKey.status}</span></td><td>{formatDate(apiKey.expiresAt)}</td><td>{formatDate(apiKey.createdAt)}</td>
          <td><button type="button" className="delete-btn" disabled={apiKey.status === "REVOKED"} onClick={() => void handleRevoke(apiKey)}>{apiKey.status === "REVOKED" ? "Đã thu hồi" : "Thu hồi"}</button></td></tr>)}</tbody></table></div></section>
    </main>{toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
  </div>;
};

export default AdminApiKeysPage;
