import { API_CONFIG } from "@/lib/api";
import { CreateCategoryDto } from "@/lib/dto/createCategoryDto";
import ApiError from "@/types/apiError";
import { Category, CategoryWithLevel } from "@/types/category";
import { useCallback, useState } from "react";

export type CategoryListType = "all" | "leaves" | "parents" | "children";

export type CategoryArrayType =
  | ReadonlyArray<Category>
  | ReadonlyArray<CategoryWithLevel>;

export default function useCategories() {
  const [isLoading, setIsLoading] = useState(false);
  const [items, setItems] = useState<CategoryArrayType>([]);
  const [listType, setListType] = useState<CategoryListType>("all");
  const [error, setError] = useState<ApiError | null>(null);

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
      setError(
        error instanceof ApiError
          ? error
          : new ApiError(0, `Unexpected error: ${error}`),
      );
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
        setError(
          error instanceof ApiError
            ? error
            : new ApiError(0, `Unexpected error: ${error}`),
        );
      } finally {
        setIsLoading(false);
      }
    },
    [],
  );

  const deleteCategory = useCallback(async (classId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/class/${classId}`, {
        method: "DELETE",
      });
      if (!response.ok) {
        throw new ApiError(response.status, "Error deleting class");
      }
    } catch (error) {
      console.error(`Error deleting class: ${error}`);
      setError(
        error instanceof ApiError
          ? error
          : new ApiError(0, `Unexpected error: ${error}`),
      );
    } finally {
      setIsLoading(false);
    }
  }, []);

  const changeBaseClass = useCallback(
    async (classId: number, newBaseClassId: number) => {
      try {
        setIsLoading(true);
        const response = await fetch(
          `${API_CONFIG.BASE_URL}/class/${classId}/parent?new=${newBaseClassId}`,
          { method: "PUT" },
        );

        if (!response.ok) {
          throw new ApiError(response.status, "Error changing base class");
        }
      } catch (error) {
        console.error(`Error changing base class: ${error}`);
        setError(
          error instanceof ApiError
            ? error
            : new ApiError(0, `Unexpected error: ${error}`),
        );
      } finally {
        setIsLoading(false);
      }
    },
    [],
  );

  const deleteBaseClass = useCallback(async (classId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/class/${classId}/parent`,
        { method: "DELETE" },
      );
      if (!response.ok) {
        throw new ApiError(response.status, "Error deleting base class");
      }
    } catch (error) {
      console.error(`Error deleting base class: ${error}`);
      setError(
        error instanceof ApiError
          ? error
          : new ApiError(0, `Unexpected error: ${error}`),
      );
    } finally {
      setIsLoading(false);
    }
  }, []);

  const changeMeasure = useCallback(
    async (classId: number, measureId: number) => {
      try {
        setIsLoading(true);
        const response = await fetch(
          `${API_CONFIG.BASE_URL}/class/${classId}/measure?measureId=${measureId}`,
          {
            method: "PUT",
          },
        );
        if (!response.ok) {
          throw new ApiError(response.status, "Error changing measure");
        }
      } catch (error) {
        console.error(`Error changing measure: ${error}`);
        setError(
          error instanceof ApiError
            ? error
            : new ApiError(0, `Unexpected error: ${error}`),
        );
      } finally {
        setIsLoading(false);
      }
    },
    [],
  );

  const deleteMeasure = useCallback(async (classId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/class/${classId}/measure`,
        { method: "DELETE" },
      );
      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Error deleting class measure unit",
        );
      }
    } catch (error) {
      console.error(`Error deleting class measure unit: ${error}`);
      setError(
        error instanceof ApiError
          ? error
          : new ApiError(0, `Unexpected error: ${error}`),
      );
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    isLoading,
    createCategory,
    fetchCategories,
    deleteCategory,
    changeBaseClass,
    changeMeasure,
    deleteBaseClass,
    deleteMeasure,
    items,
    listType,
    setListType,
    error,
  };
}
