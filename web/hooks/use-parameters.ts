import { API_CONFIG } from "@/lib/api";
import {
  CreateClassParameterDto,
  CreateProductParameterDto,
} from "@/lib/dto/createEntityParameterDto";
import CreateParamDto from "@/lib/dto/createParamDto";
import UpdateParamDto from "@/lib/dto/updateParamDto";
import { ClassParameter, ProductParameter } from "@/types/entityParameter";
import Parameter from "@/types/parameter";
import { ApiError } from "next/dist/server/api-utils";
import { useCallback, useState } from "react";

export default function useParameters() {
  const [items, setItems] = useState<ReadonlyArray<Parameter>>([]);
  const [classParameters, setClassParameters] = useState<
    ReadonlyArray<ClassParameter>
  >([]);
  const [productParameters, setProductParameters] = useState<
    ReadonlyArray<ProductParameter>
  >([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<ApiError | null>(null);

  // Edit dialog-window props
  const [isEditOpen, setEditOpen] = useState(false);
  const [editedParam, setEditedParam] = useState<Parameter | null>(null);

  const clearError = () => {
    setError(null);
  };

  const clearItems = (key: "all" | "class" | "product") => {
    switch (key) {
      case "all":
        setItems([]);
        break;
      case "class":
        setClassParameters([]);
        break;
      case "product":
        setProductParameters([]);
    }
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

  const addParameterToClass = useCallback(
    async (dto: CreateClassParameterDto) => {
      try {
        setIsLoading(true);
        const response = await fetch(`${API_CONFIG.BASE_URL}/param/class`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(dto),
        });

        if (!response.ok) {
          throw new ApiError(
            response.status,
            "Ошибка при создании параметра. Проверьте данные и повторите",
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

  const addParameterToProduct = useCallback(
    async (dto: CreateProductParameterDto) => {
      try {
        setIsLoading(true);
        const response = await fetch(`${API_CONFIG.BASE_URL}/param/product`, {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(dto),
        });

        if (!response.ok) {
          throw new ApiError(
            response.status,
            "Ошибка при создании параметра. Проверьте данные и повторите",
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

  const fetchClassParameters = useCallback(async (classId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/param/class?classId=${classId}`,
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка получения параметров, проверьте данные и повторите",
        );
      }

      const params: ClassParameter[] = await response.json();
      setClassParameters(params);
    } catch (error) {
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
    } finally {
      setIsLoading(false);
    }
    return [];
  }, []);

  const fetchProductParameters = useCallback(async (productId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/param/product?productId=${productId}`,
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка получения параметров, проверьте данные и повторите",
        );
      }

      const params: ProductParameter[] = await response.json();
      setProductParameters(params);
    } catch (error) {
      if (error instanceof TypeError) {
        setError(new ApiError(503, "Сервер недоступен"));
      } else if (error instanceof ApiError) setError(error);
    } finally {
      setIsLoading(false);
    }
    return [];
  }, []);

  const deleteClassParameter = useCallback(async (classParamId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/param/class/${classParamId}`,
        {
          method: "DELETE",
        },
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка удаления, проверьте актуальность данных или повторите",
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

  const deleteProductParameter = useCallback(async (productParamId: number) => {
    try {
      setIsLoading(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/param/product/${productParamId}`,
        {
          method: "DELETE",
        },
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка удаления, проверьте актуальность данных или повторите",
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
    classParameters,
    productParameters,
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
    addParameterToClass,
    addParameterToProduct,
    fetchClassParameters,
    fetchProductParameters,
    deleteClassParameter,
    deleteProductParameter,
  };
}
