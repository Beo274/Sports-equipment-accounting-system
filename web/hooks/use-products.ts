import { API_CONFIG } from "@/lib/api";
import GetAllProductsResponseDto from "@/lib/dto/getAllProductsResponseDto";
import ApiError from "@/types/apiError";
import Product from "@/types/product";
import { useCallback, useState } from "react";

type FetchType = "all";

interface Errors {
  fetchingError: ApiError | null;
}

export default function useProducts() {
  const [products, setProducts] = useState<ReadonlyArray<Product>>([]);

  const [errors, setErrors] = useState<Errors>({
    fetchingError: null,
  });
  const [isFetchLoading, setFetchLoading] = useState(false);

  const [fetchType, setFetchType] = useState<FetchType>("all");
  const [classId, setClassId] = useState<number | undefined>(undefined);

  const clearError = (errorType?: keyof Errors) => {
    if (errorType) {
      setErrors((prev) => ({ ...prev, [errorType]: null }));
    } else {
      setErrors({
        fetchingError: null,
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

  return {
    products,

    isFetchLoading,

    fetchType,
    setFetchType,

    setClassId,

    errors,
    clearError,

    fetchProducts,
  };
}
