import { API_CONFIG } from "@/lib/api";
import CreateCategoryDto from "@/lib/dto/createCategoryDto";
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
  const [parentId, setParentId] = useState("");
  const [childId, setChildId] = useState("");

  const clearError = () => {
    setError(null);
  };

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
        throw new ApiError(
          response.status,
          "Ошибка при создании: проверьте уникальность имен или повторите попытку",
        );
      }
    } catch (error) {
      console.error(`Creating class error: ${error}`);
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
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
          throw new ApiError(
            response.status,
            "Ошибка при получении списка категорий: повторите попытку",
          );
        }

        const list: CategoryArrayType = await response.json();
        setItems(list);
      } catch (error) {
        console.error(`Error getting all classes: ${error}`);
        if (error instanceof TypeError) {
          setError(new ApiError(503, "Сервер недоступен"));
        } else if (error instanceof ApiError) setError(error);
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
        throw new ApiError(
          response.status,
          "Ошибка удаления: проверьте наличие класса или повторите попытку",
        );
      }
    } catch (error) {
      console.error(`Error deleting class: ${error}`);
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
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
          throw new ApiError(
            response.status,
            "Ошибка обновления: проверьте существование базового класса",
          );
        }
      } catch (error) {
        console.error(`Error changing base class: ${error}`);
        if (error instanceof TypeError) {
          setError(new ApiError(503, "Сервер недоступен"));
        } else if (error instanceof ApiError) setError(error);
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
        throw new ApiError(
          response.status,
          "Ошибка удаления: проверьте существование класса",
        );
      }
    } catch (error) {
      console.error(`Error deleting base class: ${error}`);
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
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
          throw new ApiError(
            response.status,
            "Ошибка обновления: проверьте существование е.и.",
          );
        }
      } catch (error) {
        console.error(`Error changing measure: ${error}`);
        if (error instanceof TypeError) {
          setError(new ApiError(503, "Сервер недоступен"));
        } else if (error instanceof ApiError) setError(error);
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
          "Ошибка удаления: проверьте существование класса",
        );
      }
    } catch (error) {
      console.error(`Error deleting class measure unit: ${error}`);
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const refreshList = useCallback(async () => {
    if (listType === "all" || listType === "leaves") {
      await fetchCategories(listType);
    } else if (listType === "parents" && childId) {
      await fetchCategories(listType, Number(childId));
    } else if (listType === "children" && parentId) {
      await fetchCategories(listType, Number(parentId));
    }
  }, [listType, fetchCategories, childId, parentId]);

  return {
    items,
    listType,
    parentId,
    childId,

    setListType,
    setParentId,
    setChildId,

    isLoading,

    error,
    clearError,

    createCategory,
    fetchCategories,
    deleteCategory,
    changeBaseClass,
    changeMeasure,
    deleteBaseClass,
    deleteMeasure,
    refreshList,
  };
}
