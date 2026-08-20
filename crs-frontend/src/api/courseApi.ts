import axiosClient from "./axiosClient";
import type { Course, PageResponse } from "../types/course";

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