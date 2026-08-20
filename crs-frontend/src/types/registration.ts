export interface RegistrationRequest {
  studentId: number;
  courseId: number;
}

export interface Registration {
  id: number;
  studentId: number;
  courseId: number;
  status: string;
  registeredAt: string;
}