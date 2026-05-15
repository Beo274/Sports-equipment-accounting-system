"use client";

import useCategories from "@/hooks/use-categories";
import useEnumerations from "@/hooks/use-enumerations";
import useMeasures from "@/hooks/use-measures";
import { createContext, ReactNode, useContext } from "react";

interface CategoriesState {
  categories: ReturnType<typeof useCategories>;
  measures: ReturnType<typeof useMeasures>;
  enumerations: ReturnType<typeof useEnumerations>;
}

const StoreContext = createContext<CategoriesState | null>(null);

interface CategoriesProviderProps {
  children: ReactNode;
}

export function StoreProvider({ children }: CategoriesProviderProps) {
  const categories = useCategories();
  const measures = useMeasures();
  const enumerations = useEnumerations();

  const values = { categories, measures, enumerations };

  return (
    <StoreContext.Provider value={values}>{children}</StoreContext.Provider>
  );
}

export function useStore() {
  const context = useContext(StoreContext);
  if (context === null) {
    throw new Error("Categories context must be used beneath provided");
  }
  return context;
}
