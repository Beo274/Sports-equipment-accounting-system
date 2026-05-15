import { API_CONFIG } from "@/lib/api";
import { Enumeration, EnumerationValue } from "@/types/enumeration";
import { ApiError } from "next/dist/server/api-utils";
import { useCallback, useState } from "react";

type EnumerationValuesMap = Map<number, ReadonlyArray<EnumerationValue>>;

export default function useEnumerations() {
  const [enums, setEnums] = useState<ReadonlyArray<Enumeration>>([]);
  const [enumValues, setEnumValues] = useState<EnumerationValuesMap>(
    new Map<number, ReadonlyArray<EnumerationValue>>(),
  );
  const [isLoadingEnums, setIsLoadingEnums] = useState(false);
  const [isLoadingValues, setIsLoadingValues] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);

  const clearError = () => {
    setError(null);
  };

  const fetchEnumerations = useCallback(async () => {
    try {
      setIsLoadingEnums(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/enumeration`);
      if (!response.ok) {
        throw new ApiError(response.status, "Error getting enumerations list");
      }

      const data: Enumeration[] = await response.json();

      setEnums(data);
      return data;
    } catch (error) {
      console.error(`Error getting enumerations: ${error}`);
      setError(
        error instanceof ApiError
          ? error
          : new ApiError(0, `Unexpected error: ${error}`),
      );
    } finally {
      setIsLoadingEnums(false);
    }
  }, [setEnums]);

  const fetchEnumerationValues = useCallback(
    async (enumerations?: ReadonlyArray<Enumeration>) => {
      const enumsToFetch = enumerations || enums;

      if (enumsToFetch.length === 0) {
        console.warn("No enumerations to fetch values for");
        return;
      }

      try {
        setIsLoadingValues(true);
        const newValuesMap = new Map<number, ReadonlyArray<EnumerationValue>>();
        const promises = enumsToFetch.map(async (e) => {
          const response = await fetch(
            `${API_CONFIG.BASE_URL}/enumeration/${e.id}/values`,
          );
          if (!response.ok) {
            throw new ApiError(
              response.status,
              `Error fetching values for enumeration ${e.id}`,
            );
          }
          const values: EnumerationValue[] = await response.json();
          newValuesMap.set(e.id, values);
        });

        await Promise.all(promises);
        console.log(newValuesMap);
        setEnumValues(newValuesMap);
      } catch (error) {
        console.error(`Error fetching values: ${error}`);
        setError(
          error instanceof ApiError
            ? error
            : new ApiError(0, `Unexpected error: ${error}`),
        );
      } finally {
        setIsLoadingValues(false);
      }
    },
    [enums],
  );

  return {
    enums,
    enumValues,
    isLoadingEnums,
    isLoadingValues,
    error,
    clearError,
    fetchEnumerations,
    fetchEnumerationValues,
  };
}
