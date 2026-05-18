"use client";

import { useStore } from "@/lib/store/store";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemGroup,
  ItemTitle,
} from "../ui/item";
import { useCallback, useEffect, useMemo } from "react";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { EnumerationValue } from "@/types/enumeration";
import Loader from "../Loader/Loader";
import { Button } from "../ui/button";
import { Trash } from "lucide-react";

export default function EnumerationsList() {
  const {
    enumerations: {
      fetchAll,
      isLoadingEnums,
      enums,
      errors: { fetchingError: error },
      clearError,
    },
  } = useStore();

  useEffect(() => {
    fetchAll();
  }, []);

  return (
    <div className="h-full">
      <h3 className="p-1 text-xl">Список перечислений</h3>
      <div className="flex justify-center items-center p-1 overflow-y-auto">
        {error ? (
          <ErrorLabel
            message={error.message}
            onClearError={() => {
              clearError("fetchingError");
              fetchAll();
            }}
          />
        ) : (
          <div className="w-full max-h-150">
            <ItemGroup>
              {isLoadingEnums ? (
                <Item className="h-full w-full justify-center items-center">
                  <Loader />
                </Item>
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
    enumerations: { isLoadingValues, enumValues, deleteEnumeration, fetchAll },
  } = useStore();

  const values = useMemo(() => {
    const values = enumValues.get(id);
    return values ?? [];
  }, [enumValues, id]);

  return (
    <Item className="grid grid-cols-2 border-2 border-dimmedblue rounded-md bg-gray-200">
      <ItemTitle>{`${id}: ${name} (${shortName})`}</ItemTitle>
      <ItemContent className="grid grid-cols-[1fr_max-content]">
        <h4 className="col-start-1">Значения</h4>
        {isLoadingValues ? (
          <Loader />
        ) : (
          <ItemGroup className="col-start-1 bg-white rounded-md p-1 gap-0 border-collapse">
            {values.length ? (
              values
                .toSorted((a, b) => {
                  if (a.position && b.position) {
                    return a.position - b.position;
                  } else {
                    return -1;
                  }
                })
                .map((v) => (
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
        <ItemActions className="flex flex-col justify-center">
          <Button
            className="hover:bg-accent"
            variant="secondary"
            type="button"
            onClick={async () => {
              await deleteEnumeration(id);
              fetchAll();
            }}
          >
            <Trash />
          </Button>
        </ItemActions>
      </ItemContent>
    </Item>
  );
}

interface EnumerationValueItemProps {
  enumerationValue: EnumerationValue;
}

function EnumerationValueItem({ enumerationValue }: EnumerationValueItemProps) {
  const {
    enumerations: { fetchAll, deleteEnumerationValue },
  } = useStore();

  function renderValue() {
    if (enumerationValue.intValue) {
      return (
        <span>{`${enumerationValue.intValue}
          ${enumerationValue.measure && ` ${enumerationValue.measure.name} (${enumerationValue.measure.shortName})`}`}</span>
      );
    } else if (enumerationValue.stringValue) {
      return <span>{enumerationValue.stringValue}</span>;
    } else if (enumerationValue.imageValue) {
      return (
        <a
          href={enumerationValue.imageValue}
          target="_blank"
          className="hover:text-accent"
        >
          Картинка
        </a>
      );
    }
    return <span>Пустое значение, id: {enumerationValue.id}</span>;
  }

  return (
    <Item className="border-y border-x-0 border-gray-300 rounded-none first:border-t-0 last:border-b-0">
      <ItemContent>{renderValue()}</ItemContent>
      <ItemActions>
        <Button
          variant="ghost"
          className="hover:bg-accent"
          onClick={async () => {
            await deleteEnumerationValue(enumerationValue.id);
            fetchAll();
          }}
        >
          <Trash />
        </Button>
      </ItemActions>
    </Item>
  );
}
