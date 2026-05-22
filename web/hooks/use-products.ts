import { API_CONFIG } from "@/lib/api";
import CreateProductDto from "@/lib/dto/createProductDto";
import GetAllProductsResponseDto from "@/lib/dto/getAllProductsResponseDto";
import ApiError from "@/types/apiError";
import { NullParameterId } from "@/types/parameter";
import Product from "@/types/product";
import { useCallback, useState } from "react";

type FetchType = "all" | "with-params" | "within-range";

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
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [paramsIds, setParamsIds] = useState<Set<number>>(new Set([]));
  const [paramValueRange, setParamValueRange] = useState({
    minVal: "",
    maxVal: "",
  });
  const [rangeParamId, setRangeParamId] = useState<
    typeof NullParameterId | string
  >(NullParameterId);

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

  const fetchByParamIds = useCallback(async (ids: number[]) => {
    console.log(ids);
    try {
      setFetchLoading(true);
      const query = ids.map((id) => `paramIds=${id}`);
      const response = await fetch(
        `${API_CONFIG.BASE_URL}/product/search-by-params?${query.join("&")}`,
      );

      if (!response.ok) {
        throw new ApiError(
          response.status,
          "Ошибка получения изделий. Проверьте данные или повторите",
        );
      }

      const items: Product[] = await response.json();
      setProducts(items);
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

  const fetchByParamRange = useCallback(
    async (paramId: number, minVal: number, maxVal: number) => {
      try {
        setFetchLoading(true);
        const response = await fetch(
          `${API_CONFIG.BASE_URL}/product?paramId=${paramId}&minVal=${minVal}&maxVal=${maxVal}`,
        );

        if (!response.ok) {
          throw new ApiError(
            response.status,
            "Ошибка получения изделий. Проверьте данные или повторите",
          );
        }

        const items: Product[] = await response.json();
        setProducts(items);
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
    },
    [],
  );

  const fetchProducts = useCallback(async () => {
    switch (fetchType) {
      case "all":
        fetchAll(classId);
        break;
      case "with-params":
        if (paramsIds.size) fetchByParamIds(paramsIds.values().toArray());
        else fetchAll();
        break;
      case "within-range":
        console.log(paramValueRange);
        console.log(rangeParamId);
        if (
          rangeParamId === NullParameterId ||
          paramValueRange.maxVal === "" ||
          paramValueRange.minVal === ""
        ) {
          fetchAll();
        } else {
          fetchByParamRange(
            Number(rangeParamId),
            Number(paramValueRange.minVal),
            Number(paramValueRange.maxVal),
          );
        }
        break;
    }
  }, [fetchType, classId, paramsIds, paramValueRange, rangeParamId]);

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
    editingProduct,
    setEditingProduct,
    paramsIds,
    setParamsIds,
    rangeMinVal: paramValueRange.minVal,
    rangeMaxVal: paramValueRange.maxVal,
    setRangeMinVal: (value: string) => {
      setParamValueRange((prev) => ({ ...prev, minVal: value }));
    },
    setRangeMaxVal: (value: string) => {
      setParamValueRange((prev) => ({ ...prev, maxVal: value }));
    },
    rangeParamId,
    setRangeParamId,

    errors,
    clearError,

    fetchProducts,
    createProduct,
    deleteProduct,
    updateBaseClass,
  };
}
