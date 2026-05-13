import { API_CONFIG } from "@/lib/api";
import MeasureUnit from "@/types/measureUnit";
import { ApiError } from "next/dist/server/api-utils";
import { useCallback, useState } from "react";

export default function useMeasures() {
  const [items, setItems] = useState<MeasureUnit[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchMeasures = useCallback(async () => {
    try {
      setIsLoading(true);

      const response = await fetch(`${API_CONFIG.BASE_URL}/measure`);
      if (!response.ok) {
        throw new ApiError(response.status, "get measures error");
      }

      const data: MeasureUnit[] = await response.json();
      setItems(data);
    } catch (error) {
      console.error(`Fetching measure units error: ${error}`);
    } finally {
      setIsLoading(false);
    }
  }, [setItems]);

  return {
    items,
    fetchMeasures,
    isLoading,
  };
}
