import { useState, useEffect, useCallback } from "react";
import { getCourses } from "../api/courseApi";
import type { Course } from "../types/course";

export type UseCoursesState = "loading" | "success" | "empty" | "error";

interface UseCoursesResult {
  courses: Course[];
  totalPages: number;
  state: UseCoursesState;
  errorMessage: string;
  refetch: () => void;
}

export const useCourses = (
  keyword: string,
  page: number,
  size: number
): UseCoursesResult => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [state, setState] = useState<UseCoursesState>("loading");
  const [errorMessage, setErrorMessage] = useState<string>("");

  const fetchCourses = useCallback(async () => {
    setState("loading");
    setErrorMessage("");

    try {
      const response = await getCourses(keyword, page, size);
      
      if (response.content.length === 0) {
        setState("empty");
        setCourses([]);
        setTotalPages(response.totalPages);
      } else {
        setState("success");
        setCourses(response.content);
        setTotalPages(response.totalPages);
      }
    } catch (error: unknown) {
      console.error("Failed to fetch courses:", error);
      setState("error");
      if (error instanceof Error) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage("Không thể tải danh sách khóa học");
      }
    }
  }, [keyword, page, size]);

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchCourses();
  }, [fetchCourses]);

  return { courses, totalPages, state, errorMessage, refetch: fetchCourses };
};
