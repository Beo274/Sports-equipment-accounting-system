import { API_CONFIG } from "@/lib/api";
import CreateParamDto from "@/lib/dto/createParamDto";
import Parameter from "@/types/parameter";
import { ApiError } from "next/dist/server/api-utils";
import { useCallback, useState } from "react";

export default function useParameters() {
  const [items, setItems] = useState<ReadonlyArray<Parameter>>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);

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

  return {
    items,
    clearItems,

    isLoading,

    error,
    clearError,

    fetchParameters,
    createParameter,
  };
}
