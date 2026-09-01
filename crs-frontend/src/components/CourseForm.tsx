import { useEffect, useState, type FormEvent } from "react";
import { createCourse, updateCourse } from "../api/courseApi";
import { getApiErrorMessage } from "../types/apiError";
import type { Course, CoursePayload, CourseStatus } from "../types/course";

interface CourseFormProps {
  editingCourse: Course | null;
  onSuccess: (message: string) => Promise<void>;
  onCancelEdit: () => void;
}

interface FormValues {
  courseCode: string;
  courseName: string;
  instructor: string;
  capacity: string;
  availableSeats: string;
  price: string;
  status: CourseStatus;
}

type FormErrors = Partial<Record<keyof FormValues, string>>;

const EMPTY_FORM: FormValues = {
  courseCode: "",
  courseName: "",
  instructor: "",
  capacity: "",
  availableSeats: "",
  price: "",
  status: "OPEN",
};

const CourseForm = ({ editingCourse, onSuccess, onCancelEdit }: CourseFormProps) => {
  const [values, setValues] = useState<FormValues>(EMPTY_FORM);
  const [errors, setErrors] = useState<FormErrors>({});
  const [apiError, setApiError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setValues(editingCourse ? {
      courseCode: editingCourse.courseCode,
      courseName: editingCourse.courseName,
      instructor: editingCourse.instructor,
      capacity: String(editingCourse.capacity),
      availableSeats: String(editingCourse.availableSeats),
      price: String(editingCourse.price),
      status: editingCourse.status,
    } : EMPTY_FORM);
    setErrors({});
    setApiError("");
  }, [editingCourse]);

  const setField = (field: keyof FormValues, value: string) => {
    setValues((current) => ({ ...current, [field]: value }));
    setErrors((current) => ({ ...current, [field]: undefined }));
  };

  const validate = (): FormErrors => {
    const next: FormErrors = {};
    const capacity = Number(values.capacity);
    const availableSeats = Number(values.availableSeats);
    const price = Number(values.price);

    if (!values.courseCode.trim()) next.courseCode = "Mã khóa học là bắt buộc.";
    if (!values.courseName.trim()) next.courseName = "Tên khóa học là bắt buộc.";
    if (!values.instructor.trim()) next.instructor = "Giảng viên là bắt buộc.";
    if (values.capacity === "" || !Number.isInteger(capacity) || capacity < 1) {
      next.capacity = "Sức chứa phải là số nguyên lớn hơn hoặc bằng 1.";
    }
    if (values.availableSeats === "" || !Number.isInteger(availableSeats) || availableSeats < 0) {
      next.availableSeats = "Số chỗ còn lại phải là số nguyên lớn hơn hoặc bằng 0.";
    } else if (!next.capacity && availableSeats > capacity) {
      next.availableSeats = "Số chỗ còn lại không được lớn hơn sức chứa.";
    }
    if (values.price === "" || !Number.isFinite(price) || price < 0) {
      next.price = "Học phí phải là số lớn hơn hoặc bằng 0.";
    }
    if (!["OPEN", "FULL", "CLOSED"].includes(values.status)) {
      next.status = "Trạng thái không hợp lệ.";
    }
    return next;
  };

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    const payload: CoursePayload = {
      courseCode: values.courseCode.trim(),
      courseName: values.courseName.trim(),
      instructor: values.instructor.trim(),
      capacity: Number(values.capacity),
      availableSeats: Number(values.availableSeats),
      price: Number(values.price),
      status: values.status,
    };

    setSubmitting(true);
    setApiError("");
    try {
      if (editingCourse) {
        await updateCourse(editingCourse.id, payload);
        await onSuccess("Cập nhật khóa học thành công.");
      } else {
        await createCourse(payload);
        setValues(EMPTY_FORM);
        await onSuccess("Thêm khóa học thành công.");
      }
    } catch (error: unknown) {
      setApiError(getApiErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="course-form-card">
      <h2>{editingCourse ? "Sửa khóa học" : "Thêm khóa học"}</h2>
      {apiError && <div className="alert error-alert">{apiError}</div>}
      <form onSubmit={handleSubmit} noValidate>
        <div className="form-grid">
          <label>Mã khóa học<input value={values.courseCode} onChange={(e) => setField("courseCode", e.target.value)} />{errors.courseCode && <span className="field-error">{errors.courseCode}</span>}</label>
          <label>Tên khóa học<input value={values.courseName} onChange={(e) => setField("courseName", e.target.value)} />{errors.courseName && <span className="field-error">{errors.courseName}</span>}</label>
          <label>Giảng viên<input value={values.instructor} onChange={(e) => setField("instructor", e.target.value)} />{errors.instructor && <span className="field-error">{errors.instructor}</span>}</label>
          <label>Sức chứa<input type="number" min="1" step="1" value={values.capacity} onChange={(e) => setField("capacity", e.target.value)} />{errors.capacity && <span className="field-error">{errors.capacity}</span>}</label>
          <label>Số chỗ còn lại<input type="number" min="0" step="1" value={values.availableSeats} onChange={(e) => setField("availableSeats", e.target.value)} />{errors.availableSeats && <span className="field-error">{errors.availableSeats}</span>}</label>
          <label>Học phí<input type="number" min="0" step="any" value={values.price} onChange={(e) => setField("price", e.target.value)} />{errors.price && <span className="field-error">{errors.price}</span>}</label>
          <label>Trạng thái<select value={values.status} onChange={(e) => setField("status", e.target.value)}><option value="OPEN">OPEN</option><option value="FULL">FULL</option><option value="CLOSED">CLOSED</option></select>{errors.status && <span className="field-error">{errors.status}</span>}</label>
        </div>
        <div className="form-actions">
          <button className="primary-btn" type="submit" disabled={submitting}>{submitting ? "Đang lưu..." : editingCourse ? "Cập nhật" : "Thêm mới"}</button>
          {editingCourse && <button className="cancel-btn" type="button" onClick={onCancelEdit} disabled={submitting}>Hủy</button>}
        </div>
      </form>
    </section>
  );
};

export default CourseForm;
