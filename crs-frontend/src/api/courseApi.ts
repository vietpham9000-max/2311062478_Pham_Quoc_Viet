import axiosClient from "./axiosClient";
import type { Course, CoursePayload, PageResponse } from "../types/course";

export const getCourses = async (
  keyword = "",
  page = 0,
  size = 10
): Promise<PageResponse<Course>> => {
  const response = await axiosClient.get<PageResponse<Course>>(
    "/api/courses",
    {
      params: {
        keyword,
        page,
        size,
      },
    }
  );

  return response.data;
};

export const getCourseById = async (id: number): Promise<Course> => {
  const response = await axiosClient.get<Course>(`/api/courses/${id}`);
  return response.data;
};

export const createCourse = async (data: CoursePayload): Promise<Course> => {
  const response = await axiosClient.post<Course>("/api/courses", data);
  return response.data;
};

export const updateCourse = async (
  id: number,
  data: CoursePayload
): Promise<Course> => {
  const response = await axiosClient.put<Course>(`/api/courses/${id}`, data);
  return response.data;
};

export const deleteCourse = async (id: number): Promise<void> => {
  await axiosClient.delete(`/api/courses/${id}`);
};
