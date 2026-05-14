"use client";

import MeasureUnit from "@/types/measureUnit";
import {
  Item,
  ItemActions,
  ItemDescription,
  ItemGroup,
  ItemHeader,
  ItemTitle,
} from "../ui/item";
import useCategories from "@/hooks/use-categories";
import { useCallback, useEffect, useState } from "react";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldContent, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { EditIcon, TrashIcon } from "lucide-react";
import {
  Command,
  CommandDialog,
  CommandGroup,
  CommandItem,
} from "../ui/command";

export function CategoriesList() {
  const {
    items,
    fetchCategories,
    isLoading,
    listType,
    setListType,
    deleteCategory,
  } = useCategories();
  const [parentId, setParentId] = useState("");
  const [childId, setChildId] = useState("");

  const refreshList = useCallback(() => {
    switch (listType) {
      case "all":
      case "leaves":
        fetchCategories(listType);
      case "parents":
        if (childId) {
          fetchCategories(listType, Number(childId));
        }
        break;
      case "children":
        if (parentId) {
          fetchCategories(listType, Number(parentId));
        }
    }
  }, [listType, fetchCategories, childId, parentId]);

  useEffect(() => {
    refreshList();
  }, [listType, childId, parentId, refreshList]);

  const onDeleteCategory = (classId: number) => {
    return async function () {
      await deleteCategory(classId);
      refreshList();
    };
  };

  return (
    <div className="h-full flex flex-col gap-2">
      <RadioGroup
        className="grid grid-cols-2 gap-2 w-full p-2 border-2 border-accent rounded-lg"
        value={listType}
        onValueChange={setListType}
      >
        <div className="flex items-center justify-between rounded-lg border p-4 bg-dimmedblue text-background">
          <Field className="space-y-0.5">
            <FieldLabel
              htmlFor="all-categories"
              className="text-base flex flex-col items-start cursor-pointer"
            >
              <FieldTitle>Все категории</FieldTitle>
              <FieldContent className="text-xs text-background">
                Вывести все категории
              </FieldContent>
            </FieldLabel>
          </Field>
          <RadioGroupItem
            value="all"
            id="all-categories"
            className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
          />
        </div>

        <div className="flex items-center justify-between rounded-lg border p-4 bg-dimmedblue text-background">
          <Field className="space-y-0.5">
            <FieldLabel
              htmlFor="leaves-categories"
              className="text-base flex flex-col items-start cursor-pointer"
            >
              <FieldTitle>Конечные категории</FieldTitle>
              <FieldContent className="text-xs text-background">
                Вывести категории, не имеющие потомков
              </FieldContent>
            </FieldLabel>
          </Field>
          <RadioGroupItem
            value="leaves"
            id="leaves-categories"
            className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
          />
        </div>

        <div className="flex items-center justify-between rounded-lg gap-3 border p-4 bg-dimmedblue text-background">
          <Field className="space-y-0.5">
            <FieldLabel
              htmlFor="children-categories"
              className="text-base flex flex-col items-start cursor-pointer"
            >
              <FieldTitle>Категории-потомки</FieldTitle>
              <FieldContent className="text-xs text-background">
                Вывести категории-потомки заданного класса
              </FieldContent>
            </FieldLabel>
          </Field>
          <Input
            id="category-children-class-id"
            type="number"
            min={0}
            placeholder="ID класса"
            className="max-w-20"
            value={parentId}
            onChange={(e) => setParentId(e.target.value)}
            disabled={listType !== "children"}
          />
          <RadioGroupItem
            value="children"
            id="children-categories"
            className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
          />
        </div>

        <div className="flex items-center justify-between rounded-lg gap-3 border p-4 bg-dimmedblue text-background">
          <Field className="space-y-0.5">
            <FieldLabel
              htmlFor="parents-categories"
              className="text-base flex flex-col items-start cursor-pointer"
            >
              <FieldTitle>Категории-предки</FieldTitle>
              <FieldContent className="text-xs text-background">
                Вывести категории-предки заданного класса
              </FieldContent>
            </FieldLabel>
          </Field>
          <Input
            id="category-parents-class-id"
            type="number"
            min={0}
            value={childId}
            onChange={(e) => setChildId(e.target.value)}
            placeholder="ID класса"
            className="max-w-20"
            disabled={listType !== "parents"}
          />
          <RadioGroupItem
            value="parents"
            id="parents-categories"
            className="shrink-0 cursor-pointer hover:scale-125 transition-transform"
          />
        </div>
      </RadioGroup>
      {isLoading ? (
        <Item>Загрузка категорий...</Item>
      ) : items.length ? (
        <div className="p-2 border-2 border-accent rounded-lg">
          <div className="overflow-y-auto p-2">
            <ItemGroup className="flex flex-col max-h-96">
              {items.map((c) => (
                <CategoriesListItem
                  key={c.id}
                  id={c.id}
                  name={c.name}
                  shortName={c.shortName}
                  baseClassId={
                    c.baseClass
                      ? c.baseClass.id
                      : c.baseClassId
                        ? c.baseClassId
                        : null
                  }
                  handleDelete={onDeleteCategory(c.id)}
                />
              ))}
            </ItemGroup>
          </div>
        </div>
      ) : (
        <div>
          <p>Пусто...</p>
        </div>
      )}
    </div>
  );
}

interface CategoriesListItemProps {
  id: number;
  name: string;
  shortName: string;
  baseClassId: number | null;
  measureUnit?: MeasureUnit;
  measureUnitId?: number;
  level?: number;

  handleDelete: () => void;
}

export function CategoriesListItem(props: CategoriesListItemProps) {
  const [isEditOpen, setEditOpen] = useState(false);

  return (
    <Item className="grid grid-cols-2 items-start gap-1 border border-accent rounded-lg max-w-sm p-0 hover:-translate-y-0.5 hover:shadow hover:shadow-foreground transition-all">
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
      </ItemActions>
      <CommandDialog open={isEditOpen} onOpenChange={setEditOpen}>
        <Command>
          <CommandGroup heading="Редактирование"></CommandGroup>
        </Command>
      </CommandDialog>
    </Item>
  );
}
