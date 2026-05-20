"use client";

import useCategories from "@/hooks/use-categories";
import useEnumerations from "@/hooks/use-enumerations";
import useMeasures from "@/hooks/use-measures";
import useParameters from "@/hooks/use-parameters";
import useProducts from "@/hooks/use-products";
import { createContext, ReactNode, useContext } from "react";

interface State {
  categories: ReturnType<typeof useCategories>;
  measures: ReturnType<typeof useMeasures>;
  enumerations: ReturnType<typeof useEnumerations>;
  parameters: ReturnType<typeof useParameters>;
  products: ReturnType<typeof useProducts>;
}

const StoreContext = createContext<State | null>(null);

interface StoreProviderProps {
  children: ReactNode;
}

export function StoreProvider({ children }: StoreProviderProps) {
  const categories = useCategories();
  const measures = useMeasures();
  const enumerations = useEnumerations();
  const parameters = useParameters();
  const products = useProducts();

  const values = { categories, measures, enumerations, parameters, products };

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
