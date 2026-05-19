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
import { Check, Shuffle, Trash } from "lucide-react";
import { Dialog, DialogContent, DialogTitle } from "../ui/dialog";
import { Field, FieldGroup, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { useForm } from "react-hook-form";
import UpdateEnumerationValueDto from "@/lib/dto/updateEnumerationValueDto";

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
              ) : enums.length ? (
                enums.map((e) => (
                  <EnumerationItem
                    key={e.id}
                    name={e.name}
                    shortName={e.shortName}
                    id={e.id}
                  />
                ))
              ) : (
                <div className="h-full flex justify-center items-center">
                  <p>Пусто...</p>
                </div>
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
    enumerations: {
      isLoadingValues,
      isLoadingEnums,
      enumValues,
      deleteEnumeration,
      fetchAll,
      fetchEnumerationValues,
      reorderValues,
    },
  } = useStore();
  const [isReorderOpen, setReorderOpen] = useState(false);
  const {
    formState: { errors, isValid },
    register,
    handleSubmit,
    reset,
  } = useForm<{ order: number[] }>({
    defaultValues: {
      order: [],
    },
  });

  const onReorder = async (data: { order: number[] }) => {
    await reorderValues(data.order);
    reset();
    setReorderOpen(false);
    await fetchEnumerationValues();
  };

  const { values, valuesIds } = useMemo(() => {
    const values = enumValues.get(id);
    if (!values)
      return {
        values: [],
        valuesIds: new Set(),
      };
    return {
      values: values.toSorted((a, b) => {
        if (a.position && b.position) {
          return a.position - b.position;
        } else {
          return -1;
        }
      }),
      valuesIds: new Set(values.map((item) => item.id)),
    };
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
            onClick={() => setReorderOpen(true)}
            disabled={values.length === 0 || isLoadingValues}
          >
            <Shuffle />
          </Button>
        </ItemActions>
      </ItemContent>
      <Dialog open={isReorderOpen} onOpenChange={setReorderOpen}>
        <DialogContent>
          <DialogTitle className="text-lg font-bold">
            Изменение порядка значений
          </DialogTitle>
          <form
            className="flex flex-col gap-2"
            onSubmit={handleSubmit(onReorder)}
          >
            <FieldGroup className="flex flex-row">
              <Field>
                <FieldTitle>
                  Идентификаторы значений:{" "}
                  <span className="bg-accent px-1 rounded-md font-bold">
                    {values.map((v) => v.id).join(" , ")}
                  </span>
                </FieldTitle>
                <FieldLabel>
                  Новый порядок (идентификаторы через пробел)
                </FieldLabel>
                <Input
                  type="text"
                  placeholder="Расставьте идентификаторы"
                  {...register("order", {
                    required: true,
                    setValueAs: (value: unknown): number[] => {
                      if (typeof value !== "string" || !value.trim()) {
                        return [];
                      }

                      return value
                        .split(/\s+/)
                        .filter((item) => item !== "")
                        .map(Number);
                    },
                    validate: {
                      notEmpty: (value: number[]): string | boolean =>
                        value.length > 0 || "Введите числа",

                      correctLength: (value: number[]): string | boolean =>
                        value.length === values.length ||
                        `Должно быть ${values.length} чисел`,

                      allNumbers: (value: number[]): string | boolean =>
                        !value.some(isNaN) ||
                        "Все значения должны быть числами",

                      allExist: (value: number[]): string | boolean => {
                        const numbers = value.filter((n) => !isNaN(n));
                        if (numbers.length === 0) return true;

                        const missingIds = numbers.filter(
                          (id) => !valuesIds.has(id),
                        );

                        if (missingIds.length > 0) {
                          return `Не найдены ID: ${missingIds.join(", ")}`;
                        }
                        return true;
                      },

                      noDuplicates: (value: number[]): string | boolean => {
                        const numbers = value.filter((n) => !isNaN(n));
                        const uniqueIds = new Set(numbers);
                        if (uniqueIds.size !== numbers.length) {
                          return "Есть повторяющиеся ID";
                        }
                        return true;
                      },
                    },
                  })}
                />
              </Field>
              <Button
                type="submit"
                variant="secondary"
                className="hover:bg-accent self-end"
                disabled={!isValid}
              >
                <Check />
              </Button>
            </FieldGroup>
            {errors.order && (
              <span className="text-accent text-xs">
                {errors.order.message}
              </span>
            )}
          </form>
        </DialogContent>
      </Dialog>
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
