"use client";

import { useStore } from "@/lib/store/store";
import {
  Item,
  ItemActions,
  ItemContent,
  ItemGroup,
  ItemTitle,
} from "../ui/item";
import { KeyboardEvent, useEffect, useMemo, useState } from "react";
import ErrorLabel from "../ErrorLabel/ErrorLabel";
import { EnumerationValue } from "@/types/enumeration";
import Loader from "../Loader/Loader";
import { Button } from "../ui/button";
import { Shuffle, Trash } from "lucide-react";
import UpdateEnumerationValueDto from "@/lib/dto/updateEnumerationValueDto";
import { ScrollArea } from "../ui/scroll-area";
import ReorderEnumValuesDialog from "../dialog/ReorderEnumValuesDialog";

export default function EnumerationsList() {
  const {
    enumerations: {
      fetchAll,
      isLoadingEnums,
      enums,
      errors: { fetchingError: error },
      clearError,
      clearAll,
    },
  } = useStore();

  useEffect(() => {
    fetchAll();

    return () => {
      clearAll();
    };
  }, []);

  return (
    <div className="h-full flex flex-col min-h-0">
      <h3 className="p-1 text-xl">Список перечислений</h3>
      <div className="flex-1 min-h-0">
        {error ? (
          <div className="h-full flex justify-center items-center">
            <ErrorLabel
              message={error.message}
              onClearError={() => {
                clearError("fetchingError");
                fetchAll();
              }}
            />
          </div>
        ) : (
          <ScrollArea className="h-full max-h-155">
            {isLoadingEnums ? (
              <div className="h-full w-full flex justify-center items-center">
                <Loader />
              </div>
            ) : enums.length ? (
              <ItemGroup className="pr-4">
                {enums.map((e) => (
                  <EnumerationItem
                    key={e.id}
                    name={e.name}
                    shortName={e.shortName}
                    id={e.id}
                  />
                ))}
              </ItemGroup>
            ) : (
              <div className="h-full flex justify-center items-center">
                <p>Пусто...</p>
              </div>
            )}
          </ScrollArea>
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
    enumerations: {
      isLoadingValues,
      isLoadingEnums,
      enumValues,
      deleteEnumeration,
      fetchAll,
      reorder: { reorderingEnum, setReorderingEnum },
    },
  } = useStore();
  const [isReorderOpen, setReorderOpen] = useState(false);

  const values = useMemo(() => {
    return enumValues.get(id) ?? ([] as ReadonlyArray<EnumerationValue>);
  }, [enumValues, id]);

  return (
    <Item className="grid grid-cols-2 border-2 border-dimmedblue rounded-md bg-gray-200">
      <ItemTitle>{`${id}: ${name} (${shortName})`}</ItemTitle>
      <ItemContent className="grid grid-cols-[1fr_max-content]">
        <h4 className="col-start-1">Значения</h4>
        {isLoadingValues ? (
          <Loader />
        ) : (
          <ItemGroup className="col-start-1 self-center bg-white rounded-md p-1 gap-0 border-collapse">
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
        <ItemActions className="flex flex-col justify-center">
          <Button
            className="hover:bg-accent"
            variant="secondary"
            type="button"
            onClick={async () => {
              await deleteEnumeration(id);
              fetchAll();
            }}
            disabled={isLoadingEnums}
          >
            <Trash />
          </Button>
          <Button
            className="hover:bg-accent"
            variant="secondary"
            type="button"
            onClick={() => {
              setReorderOpen(true);
              setReorderingEnum({ id, name, shortName });
            }}
            disabled={values.length === 0 || isLoadingValues}
          >
            <Shuffle />
          </Button>
        </ItemActions>
      </ItemContent>
      <ReorderEnumValuesDialog
        open={isReorderOpen}
        onOpenChange={setReorderOpen}
        editingEnum={reorderingEnum}
      />
    </Item>
  );
}

interface EnumerationValueItemProps {
  enumerationValue: EnumerationValue;
}

function EnumerationValueItem({ enumerationValue }: EnumerationValueItemProps) {
  const {
    enumerations: {
      fetchEnumerationValues,
      deleteEnumerationValue,
      updateEnumerationValue,
      isLoadingValues,
    },
  } = useStore();

  const [isEditing, setIsEditing] = useState(false);
  const [editValue, setEditValue] = useState("");

  const startEditing = () => {
    if (enumerationValue.intValue) {
      setEditValue(enumerationValue.intValue.toString());
    } else if (enumerationValue.stringValue) {
      setEditValue(enumerationValue.stringValue);
    } else if (enumerationValue.imageValue) {
      setEditValue(enumerationValue.imageValue);
    } else {
      setEditValue("");
    }
    setIsEditing(true);
  };

  const saveEdit = async () => {
    const updatedValue: UpdateEnumerationValueDto = {
      intValue: null,
      stringValue: null,
      imageValue: null,
    };

    if (enumerationValue.intValue) {
      updatedValue.intValue = parseInt(editValue) || 0;
    } else if (enumerationValue.stringValue) {
      updatedValue.stringValue = editValue;
    } else if (enumerationValue.imageValue) {
      updatedValue.imageValue = editValue;
    } else {
      updatedValue.stringValue = editValue;
    }

    await updateEnumerationValue(enumerationValue.id, updatedValue);
    fetchEnumerationValues();
    setIsEditing(false);
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") {
      saveEdit();
    } else if (e.key === "Escape") {
      setIsEditing(false);
    }
  };

  function renderValue() {
    if (enumerationValue.intValue) {
      return (
        <span>
          {enumerationValue.intValue}
          {enumerationValue.measure &&
            ` ${enumerationValue.measure.name} (${enumerationValue.measure.shortName})`}
        </span>
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

  const renderEditableValue = () => {
    if (isEditing) {
      return (
        <input
          type={enumerationValue.intValue ? "number" : "text"}
          value={editValue}
          onChange={(e) => setEditValue(e.target.value)}
          onBlur={saveEdit}
          onKeyDown={handleKeyDown}
          className="border border-gray-300 rounded px-2 py-1 w-full focus:outline-none focus:border-accent"
          autoFocus
        />
      );
    }

    return (
      <div
        onClick={startEditing}
        className="cursor-pointer hover:bg-gray-50 rounded px-2 py-1 -mx-2 -my-1"
        title="Нажмите для редактирования"
      >
        {renderValue()}
      </div>
    );
  };

  return (
    <Item className="border-y border-x-0 border-gray-300 rounded-none first:border-t-0 last:border-b-0">
      <ItemContent>{renderEditableValue()}</ItemContent>
      <ItemActions>
        <Button
          variant="ghost"
          className="hover:bg-accent"
          onClick={async () => {
            await deleteEnumerationValue(enumerationValue.id);
            fetchEnumerationValues();
          }}
          disabled={isLoadingValues}
        >
          <Trash />
        </Button>
      </ItemActions>
    </Item>
  );
}
