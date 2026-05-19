"use client";

import useCategories from "@/hooks/use-categories";
import useEnumerations from "@/hooks/use-enumerations";
import useMeasures from "@/hooks/use-measures";
import useParameters from "@/hooks/use-parameters";
import { createContext, ReactNode, useContext } from "react";

interface State {
  categories: ReturnType<typeof useCategories>;
  measures: ReturnType<typeof useMeasures>;
  enumerations: ReturnType<typeof useEnumerations>;
  parameters: ReturnType<typeof useParameters>;
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

  const values = { categories, measures, enumerations, parameters };

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
