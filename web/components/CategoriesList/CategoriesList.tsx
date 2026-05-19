"use client";

import {
  Item,
  ItemActions,
  ItemDescription,
  ItemGroup,
  ItemHeader,
  ItemTitle,
} from "../ui/item";
import { useEffect, useRef, useState } from "react";
import { Field, FieldLabel } from "../ui/field";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { EditIcon, TrashIcon } from "lucide-react";
import { useStore } from "@/lib/store/store";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "../ui/dialog";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import Loader from "../Loader/Loader";
import SetListType from "../forms/SetListType";
import ErrorLabel from "../ErrorLabel/ErrorLabel";

export function CategoriesList() {
  const {
    items,
    isLoading,
    listType,
    deleteCategory,
    refreshList,
    error,
    clearError,
  } = useStore().categories;

  useEffect(() => {
    refreshList();
  }, [listType, refreshList]);

  const onDeleteCategory = (classId: number) => {
    return async () => {
      await deleteCategory(classId);
      await refreshList();
    };
  };

  if (error) {
    return (
      <div className="h-full flex justify-center items-center">
        <ErrorLabel
          message={error.message}
          onClearError={() => {
            clearError();
            refreshList();
          }}
        />
      </div>
    );
  }

  return (
    <div className="h-full grid grid-cols-[1fr_max(350px)] gap-2">
      <SetListType />
      <div className="col-start-1 row-start-1 p-2 border-2 border-accent rounded-lg">
        {isLoading ? (
          <Item className="h-full justify-center items-center">
            <Loader />
          </Item>
        ) : items.length ? (
          <div className="overflow-y-auto">
            <ItemGroup className="flex flex-col max-h-96 p-2">
              {items.map((c) => (
                <CategoriesListItem
                  key={c.id}
                  id={c.id}
                  name={c.name}
                  shortName={c.shortName}
                  baseClassId={c.baseClassId}
                  measureUnitId={c.mUnitId ?? null}
                  refresh={refreshList}
                  handleDelete={onDeleteCategory(c.id)}
                />
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

interface CategoriesListItemProps {
  id: number;
  name: string;
  shortName: string;
  baseClassId: number | null;
  measureUnitId: number | null;
  level?: number;

  handleDelete: () => Promise<void>;
  refresh: () => Promise<void>;
}

const NullMeasureUnit = "Без е. и." as const;

export function CategoriesListItem(props: CategoriesListItemProps) {
  const [isEditOpen, setEditOpen] = useState(false);
  const [measureUnit, setMeasureUnit] = useState<string>(
    props.measureUnitId !== null
      ? String(props.measureUnitId)
      : NullMeasureUnit,
  );
  const {
    measures: { items: measures },
    categories: {
      changeBaseClass,
      changeMeasure,
      isLoading,
      deleteBaseClass,
      deleteMeasure,
    },
  } = useStore();
  const [baseClassId, setBaseClassId] = useState(
    props.baseClassId !== null ? String(props.baseClassId) : "",
  );

  const buttonRef = useRef<HTMLButtonElement>(null);

  const handleMeasureUnitChange = (value: string | null) => {
    setMeasureUnit(value ?? NullMeasureUnit);
  };

  const handleEdit = async () => {
    const newMeasureUnit =
      measureUnit === NullMeasureUnit ? null : Number(measureUnit);
    const newBaseClassId = baseClassId === "" ? null : Number(baseClassId);
    let changed = false;

    if (newMeasureUnit !== props.measureUnitId) {
      if (newMeasureUnit !== null && !isNaN(newMeasureUnit)) {
        await changeMeasure(props.id, newMeasureUnit);
      } else {
        await deleteMeasure(props.id);
      }
      changed = true;
    }

    if (newBaseClassId !== props.baseClassId) {
      if (newBaseClassId !== null && !isNaN(newBaseClassId)) {
        await changeBaseClass(props.id, newBaseClassId);
      } else {
        await deleteBaseClass(props.id);
      }
      changed = true;
    }

    if (changed) {
      await props.refresh();
      setEditOpen(false);
    } else {
      if (buttonRef.current) {
        buttonRef.current.textContent = "Изменять нечего";
        setTimeout(() => {
          if (buttonRef.current) {
            buttonRef.current.textContent = "Сохранить";
          }
        }, 3000);
      }
    }
  };

  return (
    <Item className="grid grid-cols-2 items-start gap-1 border border-accent rounded-lg max-w-sm p-0 hover:shadow-lg hover:shadow-foreground transition-all">
      <ItemHeader className="col-span-2 p-0 border border-accent rounded-t-md bg-accent">
        <ItemTitle className="p-1 w-full font-bold">{`${props.id}: ${props.name} (${props.shortName})`}</ItemTitle>
      </ItemHeader>
      <ItemDescription className="col-start-1 p-1">
        {props.baseClassId
          ? `Идентификатор базовой категории: ${props.baseClassId}`
          : "Корневая категория"}
      </ItemDescription>
      <ItemActions className="col-start-2 justify-self-end p-1">
        <Button
          onClick={props.handleDelete}
          variant={"ghost"}
          className="hover:bg-accent"
        >
          <TrashIcon />
        </Button>
        <Button
          onClick={() => setEditOpen(true)}
          variant={"ghost"}
          className="hover:bg-accent"
        >
          <EditIcon />
        </Button>
        <Dialog open={isEditOpen} onOpenChange={setEditOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Редактирование класса</DialogTitle>
            </DialogHeader>
            <Field>
              <FieldLabel>Выбор единицы измерения</FieldLabel>
              <Select
                onValueChange={handleMeasureUnitChange}
                value={measureUnit}
              >
                <SelectTrigger>
                  <SelectValue
                    placeholder="Единица измерения"
                    className="text-gray-400"
                  >
                    {measureUnit === NullMeasureUnit ? (
                      <span>{NullMeasureUnit}</span>
                    ) : (
                      (() => {
                        const selected = measures.find(
                          (m) => String(m.id) === measureUnit,
                        );
                        return selected ? (
                          <span>{`${selected.name} (${selected.shortName})`}</span>
                        ) : null;
                      })()
                    )}
                  </SelectValue>
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    <SelectLabel>Единицы измерения категории</SelectLabel>
                    <SelectItem value={NullMeasureUnit}>Без е. и.</SelectItem>
                    {measures.map((m) => (
                      <SelectItem key={m.id} value={String(m.id)}>
                        {`${m.name} (${m.shortName})`}
                      </SelectItem>
                    ))}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </Field>
            <Field>
              <Input
                id="category-edit-baseClass-id"
                type="number"
                min={1}
                value={baseClassId ?? ""}
                onChange={(e) => setBaseClassId(e.target.value)}
                placeholder="Нет базовой категории"
                className="placeholder:text-gray-400"
              />
            </Field>
            <Button
              variant="secondary"
              className="hover:bg-accent"
              type="submit"
              disabled={isLoading}
              ref={buttonRef}
              onClick={handleEdit}
            >
              Сохранить
            </Button>
          </DialogContent>
        </Dialog>
      </ItemActions>
    </Item>
  );
}
