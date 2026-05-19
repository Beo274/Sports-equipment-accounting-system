import { API_CONFIG } from "@/lib/api";
import MeasureUnit from "@/types/measureUnit";
import { ApiError } from "next/dist/server/api-utils";
import { useCallback, useState } from "react";

export default function useMeasures() {
  const [items, setItems] = useState<MeasureUnit[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);

  const clearError = () => {
    setError(null);
  };

  const fetchMeasures = useCallback(async () => {
    try {
      setIsLoading(true);

      const response = await fetch(`${API_CONFIG.BASE_URL}/measure`);
      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка при получении е. и.: повторите попытку",
        );
      }

      const data: MeasureUnit[] = await response.json();
      setItems(data);
    } catch (error) {
      console.error(`Fetching measure units error: ${error}`);
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
    } finally {
      setIsLoading(false);
    }
  }, [setItems]);

  return {
    items,

    isLoading,

    error,
    clearError,

    fetchMeasures,
  };
}
