export type CourseStatus = "OPEN" | "FULL" | "CLOSED";

export interface Course {
  id: number;
  courseCode: string;
  courseName: string;
  instructor: string;
  capacity: number;
  availableSeats: number;
  price: number;
  status: CourseStatus;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export type CoursePayload = Omit<Course, "id">;
