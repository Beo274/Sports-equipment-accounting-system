import { API_CONFIG } from "@/lib/api";
import CreateEnumerationDto from "@/lib/dto/createEnumerationDto";
import CreateEnumerationValueDto from "@/lib/dto/createEnumerationValueDto";
import UpdateEnumerationValueDto from "@/lib/dto/updateEnumerationValueDto";
import { Enumeration, EnumerationValue } from "@/types/enumeration";
import { ApiError } from "next/dist/server/api-utils";
import { useCallback, useState } from "react";

type EnumerationValuesMap = Map<number, ReadonlyArray<EnumerationValue>>;

interface Errors {
  fetchingError: ApiError | null;
  creatingEnumError: ApiError | null;
  creatingEnumValueError: ApiError | null;
}

export default function useEnumerations() {
  const [enums, setEnums] = useState<ReadonlyArray<Enumeration>>([]);
  const [enumValues, setEnumValues] = useState<EnumerationValuesMap>(
    new Map<number, ReadonlyArray<EnumerationValue>>(),
  );
  const [isLoadingEnums, setIsLoadingEnums] = useState(false);
  const [isLoadingValues, setIsLoadingValues] = useState(false);
  const [errors, setErrors] = useState<Errors>({
    fetchingError: null,
    creatingEnumError: null,
    creatingEnumValueError: null,
  });
  const [reorderingEnum, setReorderingEnum] = useState<Enumeration | null>(
    null,
  );

  const clearError = (errorType?: keyof Errors) => {
    if (errorType) {
      setErrors((prev) => ({ ...prev, [errorType]: null }));
    } else {
      setErrors({
        fetchingError: null,
        creatingEnumError: null,
        creatingEnumValueError: null,
      });
    }
  };

  const clearAll = () => {
    setEnums([]);
    setEnumValues(new Map<number, ReadonlyArray<EnumerationValue>>());
  };

  const fetchEnumerations = useCallback(async () => {
    try {
      setIsLoadingEnums(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/enumeration`);
      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка при получении перечислений, повторите попытку",
        );
      }

      const data: Enumeration[] = await response.json();

      setEnums(data);
      return data;
    } catch (error) {
      console.error(`Error getting enumerations: ${error}`);
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, fetchingError: error }));
      }
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
              `Ошибка при получении значения, повторите попытку`,
            );
          }
          const values: EnumerationValue[] = await response.json();
          newValuesMap.set(
            e.id,
            values.toSorted((a, b) => {
              if (a.position && b.position) {
                return a.position - b.position;
              } else {
                return -1;
              }
            }),
          );
        });

        await Promise.all(promises);
        setEnumValues(newValuesMap);
      } catch (error) {
        console.error(`Error fetching values: ${error}`);
        if (error instanceof TypeError) {
          setErrors((prev) => ({
            ...prev,
            fetchingError: new ApiError(503, "Сервер недоступен"),
          }));
        } else if (error instanceof ApiError) {
          setErrors((prev) => ({ ...prev, fetchingError: error }));
        }
      } finally {
        setIsLoadingValues(false);
      }
    },
    [enums],
  );

  const fetchValuesForEnum = useCallback(async (id: number) => {
    try {
      setIsLoadingValues(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/enumeration/${id}/values`,
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка получения значений. Проверьте данные или повторите попытку",
        );
      }

      const values: EnumerationValue[] = await response.json();

      setEnumValues((prev) => {
        const next = new Map<number, ReadonlyArray<EnumerationValue>>(prev);
        next.set(
          id,
          values.toSorted((a, b) => {
            if (a.position && b.position) {
              return a.position - b.position;
            } else {
              return -1;
            }
          }),
        );
        return next;
      });
    } catch (error) {
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, fetchingError: error }));
      }
    } finally {
      setIsLoadingValues(false);
    }
  }, []);

  const fetchAll = useCallback(async () => {
    const fetchedEnums = await fetchEnumerations();
    if (fetchedEnums && fetchedEnums.length > 0) {
      await fetchEnumerationValues(fetchedEnums);
    }
  }, [fetchEnumerationValues, fetchEnumerations]);

  const createEnumeration = useCallback(async (dto: CreateEnumerationDto) => {
    try {
      setIsLoadingEnums(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/enumeration`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto),
      });

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка при создании перечисления: проверьте уникальность или повторите попытку",
        );
      }
    } catch (error) {
      console.error(`Error creating enumeration: ${error}`);
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          creatingEnumError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, creatingEnumError: error }));
      }
    } finally {
      setIsLoadingEnums(false);
    }
  }, []);

  const addEnumerationValue = useCallback(
    async (dto: CreateEnumerationValueDto) => {
      try {
        setIsLoadingValues(true);
        const response = await fetch(
          `${API_CONFIG.BASE_URL}/enumeration/value`,
          {
            method: "POST",
            body: JSON.stringify(dto),
            headers: { "Content-Type": "application/json" },
          },
        );

        if (!response.ok) {
          throw new ApiError(
            response.status,
            "Ошибка при создании значения: проверьте наличие перечисления или повторите попытку",
          );
        }
      } catch (error) {
        console.error(`Error creating enumeration: ${error}`);
        if (error instanceof TypeError) {
          setErrors((prev) => ({
            ...prev,
            creatingEnumValueError: new ApiError(503, "Сервер недоступен"),
          }));
        } else if (error instanceof ApiError) {
          setErrors((prev) => ({
            ...prev,
            creatingEnumValueError: error,
          }));
        }
      } finally {
        setIsLoadingValues(false);
      }
    },
    [],
  );

  const deleteEnumeration = useCallback(async (enumId: number) => {
    try {
      setIsLoadingEnums(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/enumeration/${enumId}`,
        { method: "DELETE" },
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка при удалении перечисления. Повторите попытку",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, fetchingError: error }));
      }
    } finally {
      setIsLoadingEnums(false);
    }
  }, []);

  const deleteEnumerationValue = useCallback(async (enumValueId: number) => {
    try {
      setIsLoadingValues(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/enumeration/value/${enumValueId}?option=FULL`,
        {
          method: "DELETE",
        },
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка при удалении значения перечисления. Повторите попытку",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, fetchingError: error }));
      }
    } finally {
      setIsLoadingValues(false);
    }
  }, []);

  const reorderValues = useCallback(async (ids: number[]) => {
    try {
      setIsLoadingValues(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/enumeration/reorder`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ order: ids }),
        },
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка изменения порядка значений. Проверьте данные",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, fetchingError: error }));
      }
    } finally {
      setIsLoadingValues(false);
    }
  }, []);

  const updateEnumerationValue = useCallback(
    async (id: number, dto: UpdateEnumerationValueDto) => {
      try {
        setIsLoadingValues(true);
        const response = await fetch(
          `${API_CONFIG.BASE_URL}/enumeration/value/${id}`,
          {
            method: "PATCH",
            headers: {
              "Content-Type": "application/json",
            },
            body: JSON.stringify(dto),
          },
        );

        if (!response.ok) {
          throw new ApiError(
            response.status,
            "Ошибка при обновлении значения. Проверьте данные и повторите попытку",
          );
        }
      } catch (error) {
        if (error instanceof TypeError) {
          setErrors((prev) => ({
            ...prev,
            fetchingError: new ApiError(503, "Сервер недоступен"),
          }));
        } else if (error instanceof ApiError) {
          setErrors((prev) => ({ ...prev, fetchingError: error }));
        }
      } finally {
        setIsLoadingValues(false);
      }
    },
    [],
  );

  return {
    enums,
    enumValues,
    clearAll,

    isLoadingEnums,
    isLoadingValues,

    errors,
    clearError,

    reorder: {
      reorderingEnum,
      setReorderingEnum,
    },

    fetchEnumerations,
    fetchEnumerationValues,
    fetchValuesForEnum,
    fetchAll,
    createEnumeration,
    addEnumerationValue,
    deleteEnumeration,
    deleteEnumerationValue,
    reorderValues,
    updateEnumerationValue,
  };
}
