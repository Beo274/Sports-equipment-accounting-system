"use client";

import { useStore } from "@/lib/store/store";
import { Item, ItemContent, ItemGroup, ItemTitle } from "../ui/item";
import { useCallback, useEffect, useMemo } from "react";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { Loader } from "lucide-react";
import { EnumerationValue } from "@/types/enumeration";

export default function EnumerationsList() {
  const {
    enumerations: {
      fetchEnumerations,
      fetchEnumerationValues,
      isLoadingEnums,
      enums,
      error,
      clearError,
    },
  } = useStore();

  const fetchList = useCallback(async () => {
    const fetchedEnums = await fetchEnumerations();
    if (fetchedEnums!.length > 0) {
      await fetchEnumerationValues(fetchedEnums);
    }
  }, [fetchEnumerationValues, fetchEnumerations]);

  useEffect(() => {
    fetchList();
  }, []);

  return (
    <div className="h-full border-2 border-accent rounded-md">
      <h3 className="p-1 text-xl">Список перечислений</h3>
      <div className="flex justify-center items-center p-1 overflow-y-auto">
        {error ? (
          <ErrorLabel
            message={error.message}
            onClearError={() => {
              clearError();
              fetchEnumerations();
            }}
          />
        ) : (
          <div className="w-full max-h-140">
            <ItemGroup>
              {isLoadingEnums ? (
                <Item>Загрузка списка...</Item>
              ) : (
                enums.map((e) => (
                  <EnumerationItem
                    key={e.id}
                    name={e.name}
                    shortName={e.shortName}
                    id={e.id}
                  />
                ))
              )}
            </ItemGroup>
          </div>
        )}
      </div>
    </div>
  );
}

interface EnumerationItemProps {
  id: number;
  name: string;
  shortName: string;
}

function EnumerationItem({ id, name, shortName }: EnumerationItemProps) {
  const {
    enumerations: { isLoadingValues, enumValues },
  } = useStore();

  const values = useMemo(() => {
    const values = enumValues.get(id);
    return values ?? [];
  }, [enumValues, id]);

  return (
    <Item className="grid grid-cols-2 border-2 border-dimmedblue rounded-md bg-gray-200">
      <ItemTitle>{`${id}: ${name} (${shortName})`}</ItemTitle>
      <ItemContent>
        <h4>Значения</h4>
        {isLoadingValues ? (
          <Loader />
        ) : (
          <ItemGroup className="bg-white rounded-md p-1 gap-0 border-collapse">
            {values.length ? (
              values.map((v) => (
                <EnumerationValueItem
                  key={`${id}_${v.id}`}
                  enumerationValue={v}
                />
              ))
            ) : (
              <span className="p-1 text-gray-400">Значений не задано</span>
            )}
          </ItemGroup>
        )}
      </ItemContent>
    </Item>
  );
}

interface EnumerationValueItemProps {
  enumerationValue: EnumerationValue;
}

function EnumerationValueItem({ enumerationValue }: EnumerationValueItemProps) {
  function renderValue() {
    if (enumerationValue.intValue) {
      return (
        <span>{`${enumerationValue.intValue}
          ${enumerationValue.measure && ` ${enumerationValue.measure.name} (${enumerationValue.measure.shortName})`}`}</span>
      );
    } else if (enumerationValue.stringValue) {
      return <span>{enumerationValue.stringValue}</span>;
    } else if (enumerationValue.imageValue) {
      return <a href={enumerationValue.imageValue}></a>;
    }
    return <span>Пустое значение, id: {enumerationValue.id}</span>;
  }

  return (
    <Item className="border-y border-x-0 border-gray-300 rounded-none first:border-t-0 last:border-b-0">
      <ItemContent>{renderValue()}</ItemContent>
    </Item>
  );
}
