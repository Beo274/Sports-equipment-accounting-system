"use client";

import { useStore } from "@/lib/store/store";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { useEffect } from "react";
import { Item, ItemGroup, ItemTitle } from "../ui/item";
import Parameter from "@/types/parameter";
import Loader from "../Loader/Loader";

export default function ParametersList() {
  const {
    parameters: {
      items,
      clearItems,
      fetchParameters,
      error,
      clearError,
      isLoading,
    },
  } = useStore();

  useEffect(() => {
    fetchParameters();
    return () => {
      clearItems();
    };
  }, []);

  if (error) {
    return (
      <div className="h-full flex justify-center items-center">
        <ErrorLabel
          message={error.message}
          onClearError={() => {
            clearError();
            fetchParameters();
          }}
        />
      </div>
    );
  }

  return (
    <div className="h-full">
      <h3 className="p-1 text-xl">Список параметров</h3>
      <div className="flex justify-center items-center p-1 overflow-y-auto">
        {isLoading ? (
          <Item className="h-full justify-center items-center">
            <Loader />
          </Item>
        ) : items.length ? (
          <div className="w-full">
            <ItemGroup>
              {items.map((p) => (
                <ParameterItem key={p.id} data={p} />
              ))}
            </ItemGroup>
          </div>
        ) : (
          <div className="h-full flex justify-center items-center">
            <p>Пусто...</p>
          </div>
        )}
      </div>
    </div>
  );
}

interface ParameterItemProps {
  data: Parameter;
}

function ParameterItem({ data }: ParameterItemProps) {
  return (
    <Item className="border-2 border-dimmedblue rounded-md bg-gray-200">
      <ItemTitle>{`${data.id}: ${data.name} (${data.shortName})`}</ItemTitle>
    </Item>
  );
}
