"use client";

import MeasureUnit from "@/types/measureUnit";
import { Item, ItemDescription, ItemGroup, ItemTitle } from "../ui/item";
import useCategories from "@/hooks/use-categories";
import { useEffect, useState } from "react";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldContent, FieldLabel, FieldTitle } from "../ui/field";
import { Input } from "../ui/input";

export function CategoriesList() {
  const { items, fetchCategories, isLoading, listType, setListType } =
    useCategories();
  const [parentId, setParentId] = useState("");
  const [childId, setChildId] = useState("");

  useEffect(() => {
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
  }, [fetchCategories, listType, childId, parentId]);

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
          <div className="overflow-y-auto">
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
}

export function CategoriesListItem(props: CategoriesListItemProps) {
  return (
    <Item className="flex flex-col border border-accent rounded-lg max-w-xs hover:-translate-y-0.5 hover:shadow hover:shadow-foreground transition-all">
      <ItemTitle>{`${props.id}: ${props.name} (${props.shortName})`}</ItemTitle>
      <ItemDescription>
        {props.baseClassId &&
          `Идентификатор базовой категории: ${props.baseClassId}`}
      </ItemDescription>
    </Item>
  );
}
