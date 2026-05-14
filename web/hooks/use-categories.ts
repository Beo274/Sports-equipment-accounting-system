import { API_CONFIG } from "@/lib/api";
import { CreateCategoryDto } from "@/lib/dto/createCategoryDto";
import ApiError from "@/types/apiError";
import {
  Category,
  CategoryWithLevel,
  CategoryWithMeasure,
} from "@/types/category";
import { useCallback, useState } from "react";

type CategoryListType = "all" | "leaves" | "parents" | "children";
type CategoryArrayType =
  | ReadonlyArray<Category>
  | ReadonlyArray<CategoryWithLevel>
  | ReadonlyArray<CategoryWithMeasure>;

export default function useCategories() {
  const [isLoading, setIsLoading] = useState(false);
  const [items, setItems] = useState<CategoryArrayType>([]);
  const [listType, setListType] = useState<CategoryListType>("all");

  const createCategory = useCallback(async (cat: CreateCategoryDto) => {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/class`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(cat),
      });

      if (!response.ok) {
        throw new ApiError(response.status, "Error creating class");
      }
    } catch (error) {
      console.error(`Creating class error: ${error}`);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const fetchCategories = useCallback(
    async (listType: CategoryListType, classId?: number) => {
      try {
        setIsLoading(true);
        let url: string;
        switch (listType) {
          case "all":
            url = `${API_CONFIG.BASE_URL}/class`;
            break;
          case "leaves":
            url = `${API_CONFIG.BASE_URL}/class/leaves`;
            break;
          case "parents":
            url = `${API_CONFIG.BASE_URL}/class/${classId}/parents`;
            break;
          case "children":
            url = `${API_CONFIG.BASE_URL}/class/${classId}/children`;
            break;
        }
        const response = await fetch(url);
        if (!response.ok) {
          throw new ApiError(response.status, "Error getting all classes");
        }

        const list: CategoryArrayType = await response.json();
        setItems(list);
      } catch (error) {
        console.error(`Error getting all classes: ${error}`);
      } finally {
        setIsLoading(false);
      }
    },
    [],
  );

  const deleteCategory = useCallback(async (classId: number) => {
    try {
      const response = await fetch(`${API_CONFIG.BASE_URL}/class/${classId}`, {
        method: "DELETE",
      });
      if (!response.ok) {
        throw new ApiError(response.status, "Error deleting class");
      }
    } catch (error) {
      console.error(`Error deleting class: ${error}`);
    }
  }, []);

  return {
    isLoading,
    createCategory,
    fetchCategories,
    deleteCategory,
    items,
    listType,
    setListType,
  };
}
