import { API_CONFIG } from "@/lib/api";
import { CreateCategoryDto } from "@/lib/dto/createCategoryDto";
import ApiError from "@/types/apiError";
import { useCallback, useState } from "react";

export default function useCategories() {
  const [isLoading, setIsLoading] = useState(false);

  const createCategory = useCallback(async (cat: CreateCategoryDto) => {
    try {
      setIsLoading(true);
      const response = await fetch(`${API_CONFIG.BASE_URL}/class`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(cat),
      });

      if (!response.ok) {
        throw new ApiError(response.status, "Error creating class");
      }
    } catch (error) {
      console.error(`Creating class error: ${error}`);
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    isLoading,
    createCategory,
  };
}
