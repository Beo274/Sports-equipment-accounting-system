import { API_CONFIG } from "@/lib/api";
import CreateProductDto from "@/lib/dto/createProductDto";
import GetAllProductsResponseDto from "@/lib/dto/getAllProductsResponseDto";
import ApiError from "@/types/apiError";
import Product from "@/types/product";
import { useCallback, useState } from "react";

type FetchType = "all" | "with-params";

interface Errors {
  fetchingError: ApiError | null;
  modifyingError: ApiError | null;
}

export default function useProducts() {
  const [products, setProducts] = useState<ReadonlyArray<Product>>([]);

  const [errors, setErrors] = useState<Errors>({
    fetchingError: null,
    modifyingError: null,
  });
  const [isFetchLoading, setFetchLoading] = useState(false);
  const [isModifyLoading, setModifyLoading] = useState(false);

  const [fetchType, setFetchType] = useState<FetchType>("all");
  const [classId, setClassId] = useState<number | undefined>(undefined);

  const clearError = (errorType?: keyof Errors) => {
    if (errorType) {
      setErrors((prev) => ({ ...prev, [errorType]: null }));
    } else {
      setErrors({
        fetchingError: null,
        modifyingError: null,
      });
    }
  };

  const fetchAll = useCallback(async (classId?: number) => {
    try {
      setFetchLoading(true);

      const query: string[] = [];
      if (classId) {
        query.push(`classId=${classId}`);
      }

      const url = `${API_CONFIG.BASE_URL}/product/getAll?${query.join("&")}`;
      const response = await fetch(url);

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка запроса изделий. Повторите попытку или проверьте данные",
        );
      }

      const page: GetAllProductsResponseDto = await response.json();

      setProducts(page.items);
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
      setFetchLoading(false);
    }
  }, []);

  const fetchProducts = useCallback(async () => {
    switch (fetchType) {
      case "all":
        fetchAll(classId);
        break;
    }
  }, [fetchAll, fetchType, classId]);

  const createProduct = useCallback(async (dto: CreateProductDto) => {
    try {
      setModifyLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/product`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(dto),
      });

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка создания. Проверьте уникальность и ID класса",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, modifyingError: error }));
      }
    } finally {
      setModifyLoading(false);
    }
  }, []);

  const deleteProduct = useCallback(async (id: number) => {
    try {
      setModifyLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/product/${id}`, {
        method: "DELETE",
      });

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка удаления. Проверьте актуальность данных или повторите",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, modifyingError: error }));
      }
    } finally {
      setModifyLoading(false);
    }
  }, []);

  const updateBaseClass = useCallback(async (id: number, classId: number) => {
    try {
      setModifyLoading(true);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/product/${id}/baseClass?new=${classId}`,
        { method: "PUT" },
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка обновления. Проверьте существование класса или повторите",
        );
      }
    } catch (error) {
      if (error instanceof TypeError) {
        setErrors((prev) => ({
          ...prev,
          fetchingError: new ApiError(503, "Сервер недоступен"),
        }));
      } else if (error instanceof ApiError) {
        setErrors((prev) => ({ ...prev, modifyingError: error }));
      }
    } finally {
      setModifyLoading(false);
    }
  }, []);

  return {
    products,

    isFetchLoading,
    isModifyLoading,

    fetchType,
    setFetchType,

    classId,
    setClassId,

    errors,
    clearError,

    fetchProducts,
    createProduct,
    deleteProduct,
    updateBaseClass,
  };
}
