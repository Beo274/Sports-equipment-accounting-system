"use client";

import { useEffect } from "react";
import { Field, FieldGroup, FieldLabel } from "../ui/field";
import { Input } from "../ui/input";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectLabel,
  SelectTrigger,
  SelectValue,
} from "../ui/select";
import useMeasures from "@/hooks/use-measures";
import { Item, ItemContent } from "../ui/item";
import { Button } from "../ui/button";
import { Controller, useForm } from "react-hook-form";
import { CreateCategoryDto } from "@/lib/dto/createCategoryDto";
import useCategories from "@/hooks/use-categories";

const NullMeasureUnit = null;

interface CreateCategoryFormData {
  name: string;
  shortName: string;
  measureUnitId: number | null;
  baseClassId: string | number;
}

export default function AddCategory() {
  const { items, isLoading: isLoadingMeasures, fetchMeasures } = useMeasures();
  const { register, handleSubmit, control } = useForm({
    defaultValues: {
      name: "",
      shortName: "",
      measureUnitId: NullMeasureUnit,
      baseClassId: "",
    },
  });
  const { isLoading: isLoadingCategory, createCategory } = useCategories();

  useEffect(() => {
    fetchMeasures();
  }, [fetchMeasures]);

  const handleCreate = (data: CreateCategoryFormData) => {
    const dto: CreateCategoryDto = {
      name: data.name,
      shortName: data.shortName,
      measureUnitId: data.measureUnitId,
      baseClassId: data.baseClassId === "" ? null : Number(data.baseClassId),
    };

    createCategory(dto);
  };

  return (
    <form
      className="min-w-xs flex flex-col gap-2 bg-dimmedblue p-2 rounded-md text-background"
      onSubmit={handleSubmit(handleCreate)}
    >
      <FieldGroup>
        <Field>
          <FieldLabel htmlFor="category-name">Имя категории</FieldLabel>
          <Input
            id="category-name"
            placeholder="Имя"
            required
            {...register("name", { required: true })}
          ></Input>
        </Field>
        <Field>
          <FieldLabel>Краткое имя</FieldLabel>
          <Input
            id="category-shortName"
            placeholder="Короткое имя"
            {...register("shortName", { required: true })}
            required
          ></Input>
        </Field>
        {isLoadingMeasures ? (
          <Item>
            <ItemContent>Единицы измерения загружаются...</ItemContent>
          </Item>
        ) : (
          <Field>
            <FieldLabel>Единица измерения</FieldLabel>
            <Controller
              name="measureUnitId"
              control={control}
              defaultValue={NullMeasureUnit}
              render={({ field }) => (
                <Select onValueChange={field.onChange} value={field.value}>
                  <SelectTrigger>
                    <SelectValue placeholder="Единица измерения"></SelectValue>
                  </SelectTrigger>
                  <SelectContent>
                    <SelectGroup>
                      <SelectLabel>Единицы измерения категории</SelectLabel>
                      <SelectItem value={NullMeasureUnit}>Без е. и.</SelectItem>
                      {items.map((m) => (
                        <SelectItem key={m.id} value={m.id}>
                          {`${m.name} (${m.shortName})`}
                        </SelectItem>
                      ))}
                    </SelectGroup>
                  </SelectContent>
                </Select>
              )}
            ></Controller>
          </Field>
        )}
        <Field>
          <FieldLabel htmlFor="category-baseClass-id">
            Идентификатор родительской категории
          </FieldLabel>
          <Input
            id="category-baseClass-id"
            type="number"
            min={1}
            placeholder="Нет базовой категории"
            {...register("baseClassId", {
              valueAsNumber: true,
              setValueAs: (value) => {
                if (value === "" || value === null || value === undefined)
                  return "";
                const num = Number(value);
                return isNaN(num) ? "" : num;
              },
            })}
          ></Input>
        </Field>
      </FieldGroup>
      <Button
        className="w-full disabled:opacity-40 hover:bg-accent"
        variant="secondary"
        type="submit"
        disabled={isLoadingCategory}
      >
        Создать
      </Button>
    </form>
  );
}
