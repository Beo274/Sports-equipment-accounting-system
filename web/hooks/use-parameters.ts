import { API_CONFIG } from "@/lib/api";
import CreateParamDto from "@/lib/dto/createParamDto";
import UpdateParamDto from "@/lib/dto/updateParamDto";
import Parameter from "@/types/parameter";
import { ApiError } from "next/dist/server/api-utils";
import { useCallback, useState } from "react";

export default function useParameters() {
  const [items, setItems] = useState<ReadonlyArray<Parameter>>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);

  // Edit dialog-window props
  const [isEditOpen, setEditOpen] = useState(false);
  const [editedParam, setEditedParam] = useState<Parameter | null>(null);

  const clearError = () => {
    setError(null);
  };

  const clearItems = () => {
    setItems([]);
  };

  const fetchParameters = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/param`);
      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка при получении параметров. Повторите попытку",
        );
      }

      const data: Parameter[] = await response.json();
      setItems(data);
    } catch (error) {
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const createParameter = useCallback(async (dto: CreateParamDto) => {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/param`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto),
      });

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка создания параметра. Проверьте уникальность или повторите попытку",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const deleteParameter = useCallback(async (id: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/param/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка удаления параметра. Проверьте актуальность данных или повторите",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const updateParameter = useCallback(
    async (id: number, dto: UpdateParamDto) => {
      try {
        setIsLoading(true);
        const response = await fetch(`${API_CONFIG.BASE_URL}/param/${id}`, {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(dto),
        });

        if (!response.ok) {
          throw new ApiError(
            response.status,
            "Ошибка обновления параметра. Проверьте уникальность или повторите попытку",
          );
        }
      } catch (error) {
        if (error instanceof TypeError) {
          setError(new ApiError(503, "Сервер недоступен"));
        } else if (error instanceof ApiError) setError(error);
      } finally {
        setIsLoading(false);
      }
    },
    [],
  );

  return {
    items,
    clearItems,

    dialog: {
      isEditOpen,
      setEditOpen,
      editedParam,
      setEditedParam,
    },

    isLoading,

    error,
    clearError,

    fetchParameters,
    createParameter,
    deleteParameter,
    updateParameter,
  };
}
